package com.ubercab.infer.task

import com.ubercab.infer.util.JavacUtils
import com.ubercab.infer.util.RunCommandUtils
import org.gradle.api.DefaultTask
import org.gradle.api.artifacts.UnknownConfigurationException
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

/**
 * Responsible for capturing Infer metadata for future analysis.
 */
public class Capture extends DefaultTask {

    @Input Closure<FileCollection> bootClasspath
    @Input Closure<FileCollection> compileDependencies
    @Input Closure<FileCollection> processorDependencies
    @Input Closure<FileCollection> providedDependencies
    @Input Closure<FileCollection> sourceFiles
    @Input Closure<String> sourceJavaVersion
    @Input Closure<String> targetJavaVersion

    @TaskAction
    def captureInferData() {
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
        } catch (UnknownConfigurationException ignored) {}

        def processorDependenciesList = project.files();

        try {
            processorDependenciesList += processorDependencies()
        } catch (UnknownConfigurationException ignored) {}

        try {
            processorDependenciesList += providedDependencies()
        } catch (UnknownConfigurationException ignored) { }

        if (!processorDependenciesList.isEmpty()) {
            argumentsBuilder.append(JavacUtils.generateJavacArgument(processorDependenciesList, '-processorpath'))
        }

        argumentsBuilder.append(JavacUtils.generateJavacArgument(compileDependencies() + providedDependencies(),
                "-classpath"))

        argumentsBuilder.append(JavacUtils.generateJavacArgument(sourceFiles(), " ", " "))

        return argumentsBuilder.toString()
    }
}
