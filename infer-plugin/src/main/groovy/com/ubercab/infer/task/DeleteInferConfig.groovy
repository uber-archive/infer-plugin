package com.ubercab.infer.task

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

public class DeleteInferConfig extends DefaultTask {

    @TaskAction
    def deleteInferConfig() {
        new File(project.projectDir.absolutePath + "/.inferConfig").delete()
    }
}
