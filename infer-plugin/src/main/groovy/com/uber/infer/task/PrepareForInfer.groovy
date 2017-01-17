package com.uber.infer.task

import com.uber.infer.util.JavacUtils
import com.uber.infer.util.RunCommandUtils
import groovy.json.JsonBuilder
import org.gradle.api.DefaultTask
import org.gradle.api.artifacts.UnknownConfigurationException
import org.gradle.api.file.FileCollection
import org.gradle.api.internal.file.TmpDirTemporaryFileProvider
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

import java.nio.file.Path
import java.nio.file.Paths
import java.util.concurrent.TimeUnit

/**
 * Responsible for capturing Infer metadata for future analysis.
 */
public class PrepareForInfer extends DefaultTask {

    // For creating a config...
    @Input Closure<FileCollection> eradicateExclude
    @Input Closure<FileCollection> eradicateInclude
    @Input Closure<FileCollection> inferExclude
    @Input Closure<FileCollection> inferInclude

    // For capturing...
    @Input Closure<FileCollection> bootClasspath
    @Input Closure<FileCollection> compileDependencies
    @Input Closure<FileCollection> processorDependencies
    @Input Closure<FileCollection> providedDependencies
    @Input Closure<FileCollection> sourceFiles
    @Input Closure<String> sourceJavaVersion
    @Input Closure<String> targetJavaVersion

    File classOutputDirectory = new File(getTemporaryDir(), "classes")
    File generatedSourceOutputDirectory = new File(getTemporaryDir(), "generated-source")
    FileCollection additionalClasses = project.files();

    @TaskAction
    def prepareForInfer() {
        captureAdditionalDependencies()
        createInferConfig()
        captureInferData()
    }

    private def captureAdditionalDependencies() {
        compileDependencies().each { dependency ->
            if (dependency.absolutePath.endsWith(".aar")) {
                "unzip ${dependency.absolutePath} -d ${dependency.parentFile.toString()}".execute().waitFor(2000, TimeUnit.MILLISECONDS)
                if (new File(dependency.parentFile.toString() + "/classes.jar").exists()) {
                    additionalClasses += (project.files(dependency.parentFile.toString() + "/classes.jar"))
                }
            }
        }
    }

    private def captureInferData() {
        classOutputDirectory.mkdirs()
        generatedSourceOutputDirectory.mkdirs()

        def outputDir = new File(project.getBuildDir(), "infer-out")
        outputDir.mkdirs()
        def result = RunCommandUtils.run("infer -a capture --out ${outputDir.absolutePath}"
                + " -- javac -source ${sourceJavaVersion()} -target ${targetJavaVersion()} " +
                "-d ${classOutputDirectory.absolutePath} "
                + "-s ${generatedSourceOutputDirectory.absolutePath} ${getJavacArguments()}", project.projectDir)

        if (!result.success) {
            throw new RuntimeException("Error capturing Infer data: " + result.stderr)
        }
    }

    private def createInferConfig() {
        def eradicateExcludeWithGenerated = eradicateExclude().plus(generatedSourceOutputDirectory)
        def inferExcludeWithGenerated = inferExclude().plus(generatedSourceOutputDirectory)

        Map<String, Object> config = new HashMap<String, Object>()
        config.put("eradicate_blacklist", getPathsArrayInInferFormat(eradicateExcludeWithGenerated))
        config.put("eradicate_whitelist", getPathsArrayInInferFormat(eradicateInclude().files))
        config.put("infer_blacklist", getPathsArrayInInferFormat(inferExcludeWithGenerated))
        config.put("infer_whitelist", getPathsArrayInInferFormat(inferInclude().files))

        project.file('.inferConfig').text = new JsonBuilder(config).toPrettyString() + "\n"
        project.file('.inferConfig').deleteOnExit()
    }

    private List<String> getPathsArrayInInferFormat(Collection<File> files) {
        List<String> paths = new ArrayList<>();
        files.each { file ->
            Path pathAbsolute = Paths.get(file.toString())
            Path pathBase = Paths.get(project.projectDir.toString())
            Path pathRelative = pathBase.relativize(pathAbsolute)
            paths.add(pathRelative.toString())
        }
        return paths
    }

    /**
     * @return Javac arguments to compile the project.
     */
    private String getJavacArguments() {
        StringBuilder argumentsBuilder = new StringBuilder();

        try {
            def bootClasspath = bootClasspath()
            if (bootClasspath != null) {
                argumentsBuilder.append(JavacUtils.generateJavacArgument(bootClasspath, '-bootclasspath'))
            }
        } catch (UnknownConfigurationException ignored) {
        }

        def processorDependenciesList = project.files();

        try {
            processorDependenciesList += processorDependencies()
        } catch (UnknownConfigurationException ignored) {
        }

        try {
            processorDependenciesList += providedDependencies()
        } catch (UnknownConfigurationException ignored) {
        }

        if (!processorDependenciesList.isEmpty()) {
            argumentsBuilder.append(JavacUtils.generateJavacArgument(processorDependenciesList, '-processorpath'))
        }

        argumentsBuilder.append(JavacUtils.generateJavacArgument(additionalClasses + compileDependencies() + providedDependencies(),
                "-classpath"))

        String sourceFileText = sourceFiles().join('\n')

        TmpDirTemporaryFileProvider provider = new TmpDirTemporaryFileProvider()
        File inferSourceFiles = provider.createTemporaryFile("infer", "sourceFiles", "/tmp")
        inferSourceFiles.deleteOnExit()
        inferSourceFiles.text = sourceFileText

        argumentsBuilder.append("@${inferSourceFiles.absolutePath}")

        return argumentsBuilder.toString()
    }
}
