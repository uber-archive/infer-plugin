package com.uber.infer.util

import org.gradle.api.file.FileCollection

/**
 * Utilities for creating Javac commands.
 */
public final class JavacUtils {

    private JavacUtils() { }

    static String generateJavacArgument(FileCollection files, String argument = "", String fileDelimiter = ":") {
        StringBuilder argumentBuilder = new StringBuilder();

        if (files.size() > 0) {
            argumentBuilder.append(argument)
            argumentBuilder.append(" ")
            files.each {
                argumentBuilder.append(it.absolutePath)
                argumentBuilder.append(fileDelimiter)
            }
            argumentBuilder.append(" ")
        }

        return argumentBuilder.toString()
    }
}
