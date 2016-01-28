package com.ubercab.infer

import com.android.build.gradle.api.BaseVariant
import com.ubercab.infer.extension.InferPluginExtension
import com.ubercab.infer.task.Capture
import com.ubercab.infer.task.CheckForInfer
import com.ubercab.infer.task.Eradicate
import com.ubercab.infer.task.Infer
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Infer plug-in for Android projects. This generates tasks for each Android build variant.
 */
class InferAndroidPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        if (project.plugins.hasPlugin("com.android.application")) {
            createInferTasks(project, project.android.applicationVariants)
        } else if (project.plugins.hasPlugin("com.android.library")) {
            createInferTasks(project, project.android.libraryVariants)
        }

        project.extensions.create(Constants.EXTENSION_INFER_PLUGIN_NAME, InferPluginExtension, project)
    }

    private createInferTasks(Project project, Set<BaseVariant> variants) {
        def checkForInferTask = project.tasks.create(Constants.TASK_CHECK_FOR_INFER, CheckForInfer)

        variants.all { BaseVariant variant ->
            def taskVariantName = variant.name.capitalize()

            def inferCaptureTask = createCaptureTask(project, taskVariantName, variant)
            inferCaptureTask.dependsOn(checkForInferTask)

            // Required to get exploded-aar directory.
            inferCaptureTask.dependsOn("compile${taskVariantName}Sources")

            def eradicateTask = project.tasks.create(Constants.TASK_ERADICATE + taskVariantName, Eradicate)
            eradicateTask.dependsOn(inferCaptureTask)
            if (taskVariantName != null) {
                eradicateTask.setDescription("Runs Infer's eradicate static analysis on the ${taskVariantName} " +
                        "variant.")
            } else {
                eradicateTask.setDescription("Runs Infer's eradicate static analysis.")
            }

            def inferTask = project.tasks.create(Constants.TASK_INFER + taskVariantName, Infer)
            inferTask.dependsOn(inferCaptureTask)
            inferTask.setGroup(Constants.GROUP)

            if (taskVariantName != null) {
                inferTask.setDescription("Runs Infer static analysis on the ${taskVariantName} variant.")
            } else {
                inferTask.setDescription("Runs Infer static analysis.")
            }
        }
    }

    private createCaptureTask(Project project, String taskVariantName, BaseVariant variant) {
        return project.tasks.create(Constants.TASK_CAPTURE + taskVariantName, Capture) {
            bootClasspath = {
                project.files(project.android.bootClasspath)
            }
            compileDependencies = {
                project.configurations.getByName("compile") +
                        project.configurations.getByName("${variant.name}Compile") +
                        project.files(project.fileTree(dir: "${project.buildDir.path}/intermediates/exploded-aar", include: "**/*.jar").files)
            }
            processorDependencies = {
                project.configurations.getByName("apt")
            }
            providedDependencies = {
                project.configurations.getByName("provided") +
                project.configurations.getByName ("${variant.name}Provided")
            }
            sourceFiles = {
                variant.javaCompiler.source
            }
            sourceJavaVersion = {
                variant.javaCompiler.sourceCompatibility
            }
            targetJavaVersion = {
                variant.javaCompiler.targetCompatibility
            }
        }
    }
}
