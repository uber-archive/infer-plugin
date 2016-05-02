package com.ubercab.infer.task

import com.amazonaws.util.json.JSONArray
import com.amazonaws.util.json.JSONObject
import com.ubercab.infer.util.JavacUtils
import com.ubercab.infer.util.RunCommandUtils
import org.gradle.api.DefaultTask
import org.gradle.api.artifacts.UnknownConfigurationException
import org.gradle.api.file.FileCollection
import org.gradle.api.internal.file.TmpDirTemporaryFileProvider
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

import java.nio.file.Path
import java.nio.file.Paths

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

    @TaskAction
    def prepareForInfer() {
        createInferConfig()
        captureInferData()
    }

    private def captureInferData() {
        classOutputDirectory.mkdirs()
        generatedSourceOutputDirectory.mkdirs()

        def outputDir = new File(project.getBuildDir(), "infer-out")
        outputDir.mkdirs()

        def result = RunCommandUtils.run("infer -i -a capture --out ${outputDir.absolutePath}"
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

        JSONObject root = new JSONObject()
        root.put("eradicate_blacklist", getJSONArrayInInferFormat(eradicateExcludeWithGenerated))
        root.put("eradicate_whitelist", getJSONArrayInInferFormat(eradicateInclude().files))
        root.put("infer_blacklist", getJSONArrayInInferFormat(inferExcludeWithGenerated))
        root.put("infer_whitelist", getJSONArrayInInferFormat(inferInclude().files))

        project.file('.inferConfig').text = root.toString() + "\n"
        project.file('.inferConfig').deleteOnExit()
    }

    private JSONArray getJSONArrayInInferFormat(Collection<File> files) {
        JSONArray returnArray = new JSONArray()
        files.each { file ->
            Path pathAbsolute = Paths.get(file.toString())
            Path pathBase = Paths.get(project.projectDir.toString())
            Path pathRelative = pathBase.relativize(pathAbsolute)
            returnArray.put(pathRelative.toString())
        }
        return returnArray
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

        argumentsBuilder.append(JavacUtils.generateJavacArgument(compileDependencies() + providedDependencies(),
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
