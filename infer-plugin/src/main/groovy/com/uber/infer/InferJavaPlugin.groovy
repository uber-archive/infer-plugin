package com.uber.infer

import com.uber.infer.extension.InferPluginExtension
import com.uber.infer.task.DeleteInferConfig
import com.uber.infer.task.PrepareForInfer
import com.uber.infer.task.CheckForInfer

import com.uber.infer.task.Eradicate
import com.uber.infer.task.Infer
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Infer plug-in for standard Java projects.
 */
class InferJavaPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        def checkForInferTask = project.tasks.create(Constants.TASK_CHECK_FOR_INFER, CheckForInfer)
        def inferCaptureTask = project.tasks.create(Constants.TASK_PREPARE_FOR_INFER, PrepareForInfer) {
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
            eradicateExclude = {
                project.eradicate.exclude
            }
            eradicateInclude = {
                project.eradicate.include
            }
            inferExclude = {
                project.infer.exclude
            }
            inferInclude = {
                project.infer.include
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

        project.extensions.create(Constants.EXTENSION_INFER_PLUGIN_NAME, InferPluginExtension, project)

        def deleteInferConfigTask = project.tasks.create("deleteInferConfig", DeleteInferConfig)
        inferTask.finalizedBy(deleteInferConfigTask)
        eradicateTask.finalizedBy(deleteInferConfigTask)
    }
}
