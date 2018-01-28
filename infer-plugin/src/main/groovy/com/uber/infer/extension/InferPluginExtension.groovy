package com.uber.infer.extension

import com.uber.infer.Constants
import org.gradle.api.Project

class InferPluginExtension {
    InferExtension infer

    InferPluginExtension(Project project) {
        infer = project.extensions.create(Constants.EXTENSION_INFER_NAME, InferExtension, project)
    }
}
