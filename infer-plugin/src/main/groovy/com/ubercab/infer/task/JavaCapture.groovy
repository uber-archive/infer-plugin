package com.ubercab.infer.task
import com.ubercab.infer.util.JavacUtils
import org.gradle.api.artifacts.UnknownConfigurationException
/**
 * Capture task subclass for Java projects.
 */
public class JavaCapture extends Capture {

    @Override
    protected String getJavacArguments() {
        StringBuilder argumentsBuilder = new StringBuilder();

        try {
            argumentsBuilder.append(
                    JavacUtils.generateJavacArgument(project.configurations.getByName("apt"), '-processorpath')
            )
        } catch (UnknownConfigurationException ignored) { }

        try {
            argumentsBuilder.append(
                    JavacUtils.generateJavacArgument(project.configurations.getByName("provided"), '-processorpath')
            )
        } catch (UnknownConfigurationException ignored) { }

        argumentsBuilder.append(JavacUtils.generateJavacArgument(project.configurations.getByName("compile"),
                "-classpath"))

        argumentsBuilder.append(JavacUtils.generateJavacArgument(project.sourceSets.main.java, " ", " "))

        return argumentsBuilder.toString()
    }
}
