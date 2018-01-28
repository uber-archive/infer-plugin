package com.uber.infer.task

import com.uber.infer.util.RunCommandUtils
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * Base class for running different Infer analyzers.
 */
public abstract class InferAnalyzeCommand extends DefaultTask {

    @TaskAction
    def analyze() {
        def outputDir = new File(project.getBuildDir(), "infer-out")

        def result = RunCommandUtils.run("infer ${getAnalyzerParameter()} -o "
                + " ${outputDir.absolutePath}", project.getProjectDir())

        println result.stdout

        if (!result.success) {
            throw new RuntimeException("Infer analysis found issues.")
        }
    }

    /**
     * @return the analyzer the task should run.
     */
    protected abstract String getAnalyzerParameter();
}
