package com.ubercab.infer.task
import com.ubercab.infer.util.RunCommandUtils
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class Infer extends DefaultTask {

    @TaskAction
    def runAnalysis() {
        def outputDir = new File(project.getBuildDir(), "infer-out")

        def result = RunCommandUtils.run("infer -i -a infer --fail-on-bug --out ${outputDir.absolutePath}"
                + " -- analyze", project.getProjectDir())

        println result.stdout

        if (!result.success) {
            throw new RuntimeException("Infer analysis found issues.")
        }
    }
}
