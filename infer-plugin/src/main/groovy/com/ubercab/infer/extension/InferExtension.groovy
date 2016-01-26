package com.ubercab.infer.extension

import org.gradle.api.Project
import org.gradle.api.file.FileCollection

class InferExtension {

    /**
     * The {@link FileCollection} to exclude from an infer run.
     */
    FileCollection exclude

    /**
     * The {@link FileCollection} to include for an infer run.
     */
    FileCollection include

    InferExtension(Project project) {
        exclude = project.files()
        include = project.files()
    }
}
