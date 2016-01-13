package com.ubercab.infer.task
import com.ubercab.infer.util.RunCommandUtils
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
/**
 * Responsible for capturing Infer metadata for future analysis. Must be subclassed for specific platforms to provide
 * appropriate configuration.
 */
public abstract class Capture extends DefaultTask {

    @TaskAction
    def captureInferData() {
        def outputDir = new File(project.getBuildDir(), "infer-out")
        outputDir.mkdirs()

        def javacOutputDirPath = temporaryDir.absolutePath

        def result = RunCommandUtils.run("infer -i -a capture --out ${outputDir.absolutePath}"
                + " -- javac -d ${javacOutputDirPath} -s ${javacOutputDirPath} ${getJavacArguments()}",
                project.projectDir)

        if (!result.success) {
            throw new RuntimeException("Error capturing Infer data: " + result.stderr)
        }
    }

    /**
     * @return Javac arguments to compile the project.
     */
    abstract protected String getJavacArguments();
}
