package com.ubercab.infer

import com.ubercab.infer.task.CheckForInfer
import com.ubercab.infer.task.Eradicate
import com.ubercab.infer.task.Infer
import com.ubercab.infer.task.JavaCapture
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Infer plug-in for standard Java projects.
 */
class InferJavaPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        def checkForInferTask = project.tasks.create(Constants.TASK_CHECK_FOR_INFER, CheckForInfer)
        def inferCaptureTask = project.tasks.create(Constants.TASK_CAPTURE, JavaCapture)
        inferCaptureTask.dependsOn(checkForInferTask)

        def eradicateTask = project.tasks.create(Constants.TASK_ERADICATE, Eradicate)
        eradicateTask.dependsOn(inferCaptureTask)
        eradicateTask.setGroup(Constants.GROUP)
        eradicateTask.setDescription("Runs Infer's eradicate static analysis.")

        def inferTask = project.tasks.create(Constants.TASK_INFER, Infer)
        inferTask.dependsOn(inferCaptureTask)
        inferTask.setGroup(Constants.GROUP)
        inferTask.setDescription("Runs Infer static analysis.")
    }
}
