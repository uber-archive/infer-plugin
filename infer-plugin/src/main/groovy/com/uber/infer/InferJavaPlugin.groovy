package com.uber.infer

import com.uber.infer.extension.InferPluginExtension
import com.uber.infer.task.*
import com.uber.infer.util.ConfigurationUtils
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
        def inferCaptureTask = project.tasks.create(Constants.TASK_PREPARE_FOR_INFER, PrepareForInfer) {
            bootClasspath = {
                null
            }
            compileDependencies = {
                project.configurations.getByName("compile")
            }
            processorDependencies = {
                ConfigurationUtils.getAvailable(project, 'apt')
            }
            providedDependencies = {
                ConfigurationUtils.getAvailable(project, 'provided', 'compileOnly')
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

        makeTaskRunWithCheck(project, inferTask)
        makeTaskRunWithCheck(project, eradicateTask)

        inferTask.finalizedBy(deleteInferConfigTask)
        eradicateTask.finalizedBy(deleteInferConfigTask)
    }

    private makeTaskRunWithCheck(Project project, Task task) {
        project.getTasksByName(Constants.TASK_CHECK, false).each { it.dependsOn task }
    }
}
