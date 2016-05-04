package com.uber.infer.extension

import org.gradle.api.Project
import org.gradle.api.file.FileCollection

class EradicateExtension {

    /**
     * The {@link FileCollection} to exclude from an eradicate run.
     */
    FileCollection exclude

    /**
     * The {@link FileCollection} to include from an eradicate run.
     */
    FileCollection include

    EradicateExtension(Project project) {
        exclude = project.files()
        include = project.files()
    }
}
