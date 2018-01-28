package com.uber.infer.task

import com.uber.infer.util.RunCommandUtils
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.internal.impldep.org.apache.commons.lang.StringUtils

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

        if (result.stderr != null && !StringUtils.contains(result.stderr, "No issues found")) {
            println result.stderr
            throw new RuntimeException("please check the report to fix the issues. [build/infer-out/bugs.txt]");
        }
    }

    /**
     * @return the analyzer the task should run.
     */
    protected abstract String getAnalyzerParameter();
}
