package com.ubercab.infer

import com.ubercab.infer.extension.EradicateExtension
import com.ubercab.infer.extension.InferExtension
import com.ubercab.infer.task.Capture
import com.ubercab.infer.task.CheckForInfer
import com.ubercab.infer.task.Eradicate
import com.ubercab.infer.task.Infer
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task

/**
 * Infer plug-in for standard Java projects.
 */
class InferJavaPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        def checkForInferTask = project.tasks.create(Constants.TASK_CHECK_FOR_INFER, CheckForInfer)
        def inferCaptureTask = project.tasks.create(Constants.TASK_CAPTURE, Capture) {
            bootClasspath = {
                null
            }
            compileDependencies = {
                project.configurations.getByName("compile")
            }
            processorDependencies = {
                project.configurations.getByName("apt")
            }
            providedDependencies = {
                project.configurations.getByName("provided")
            }
            sourceFiles = {
                project.sourceSets.main.java
            }
            sourceJavaVersion = {
                project.sourceCompatibility
            }
            targetJavaVersion = {
                project.targetCompatibility
            }
        }
        inferCaptureTask.dependsOn(checkForInferTask)

        // Required to ensure dependency artifacts are available.
        inferCaptureTask.dependsOn("processResources")

        def eradicateTask = project.tasks.create(Constants.TASK_ERADICATE, Eradicate)
        eradicateTask.dependsOn(inferCaptureTask)
        eradicateTask.setGroup(Constants.GROUP)
        eradicateTask.setDescription("Runs Infer's eradicate static analysis.")

        def inferTask = project.tasks.create(Constants.TASK_INFER, Infer)
        inferTask.dependsOn(inferCaptureTask)
        inferTask.setGroup(Constants.GROUP)
        inferTask.setDescription("Runs Infer static analysis.")

        project.extensions.create(Constants.EXTENSION_ERADICATE_NAME, EradicateExtension, project)
        project.extensions.create(Constants.EXTENSION_INFER_NAME, InferExtension, project)
    }
}
