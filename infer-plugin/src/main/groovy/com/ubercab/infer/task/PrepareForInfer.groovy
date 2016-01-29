package com.ubercab.infer.task

import com.amazonaws.util.json.JSONArray
import com.amazonaws.util.json.JSONObject
import com.ubercab.infer.util.JavacUtils
import com.ubercab.infer.util.RunCommandUtils
import org.gradle.api.DefaultTask
import org.gradle.api.artifacts.UnknownConfigurationException
import org.gradle.api.file.FileCollection
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

    @TaskAction
    def prepareForInfer() {
        createInferConfig()
        captureInferData()
    }

    private def captureInferData() {
        def outputDir = new File(project.getBuildDir(), "infer-out")
        outputDir.mkdirs()

        def javacOutputDirPath = temporaryDir.absolutePath

        def result = RunCommandUtils.run("infer -i -a capture --out ${outputDir.absolutePath}"
                + " -- javac -source ${sourceJavaVersion()} -target ${targetJavaVersion()} -d ${javacOutputDirPath} "
                + "-s ${javacOutputDirPath} ${getJavacArguments()}", project.projectDir)

        if (!result.success) {
            throw new RuntimeException("Error capturing Infer data: " + result.stderr)
        }
    }

    private def createInferConfig() {
        FileWriter inferConfigFile = new FileWriter(project.projectDir.absolutePath + "/.inferConfig")

        def eradicateExcludeWithGenerated = eradicateExclude()
        def inferExcludeWithGenerated = inferExclude()

        JSONObject root = new JSONObject()
        root.put("eradicate_blacklist", getJSONArrayInInferFormat(eradicateExcludeWithGenerated))
        root.put("eradicate_whitelist", getJSONArrayInInferFormat(eradicateInclude()))
        root.put("infer_blacklist", getJSONArrayInInferFormat(inferExcludeWithGenerated))
        root.put("infer_whitelist", getJSONArrayInInferFormat(inferInclude()))

        try {
            inferConfigFile.write(root.toString() + "\n")

        } catch (IOException e) {
            e.printStackTrace()

        } finally {
            inferConfigFile.flush()
            inferConfigFile.close()
        }
    }

    private JSONArray getJSONArrayInInferFormat(FileCollection fileCollection) {
        JSONArray returnArray = new JSONArray()
        fileCollection.each { file ->
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

        argumentsBuilder.append(JavacUtils.generateJavacArgument(sourceFiles(), " ", " "))

        return argumentsBuilder.toString()
    }
}
