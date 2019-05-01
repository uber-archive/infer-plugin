package com.uber.infer.task

import com.uber.infer.util.RunCommandUtils
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction

/**
 * Checks if Infer is installed and stops the build with an error if not.
 */
public class CheckForInfer extends DefaultTask {

    @TaskAction
    def checkForInfer() {
        try {
            RunCommandUtils.run("infer --version")
        } catch (IOException e) {
            throw new GradleException(
                "Infer is not installed on this machine.\n" +
                "If you have Homebrew, you can easily install Infer with: brew install infer\n" +
                "For other installation options, see: http://fbinfer.com/docs/getting-started.html"
            )
        }
    }
}
