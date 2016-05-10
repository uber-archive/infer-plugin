package com.uber.infer.util

import org.codehaus.groovy.tools.shell.CommandException

/**
 * Utility class to run command line commands.
 */
final class RunCommandUtils {

    private static final int SUCCESS = 0
    private static final String TAG = "InferPlugin"

    private RunCommandUtils() { }

    /**
     * Method to run a command line command.
     *
     * @param command command string to be run.
     * @param dir directory to run the command from.
     * @return A list containing stdout and stderr strings.
     *
     * @throws CommandException if the command fails while running and if errorIfFail is true.
     */
    static CommandResult run(String command, File dir = new File(".")) {
        String[] env = null
        Process process = command.execute(env, dir)
        StringBuffer stdoutBuffer = new StringBuffer(), stderrBuffer = new StringBuffer()
        process.waitForProcessOutput(stdoutBuffer, stderrBuffer)

        int exitCode = process.exitValue()

        CommandResult result = new CommandResult()
        result.stdout = stdoutBuffer.toString()
        result.stderr = stderrBuffer.toString()
        result.success = exitCode == SUCCESS;
        return result
    }

    static class CommandResult {

        private String stderr;
        private String stdout;
        private boolean success;

        String getStderr() {
            return stderr
        }

        String getStdout() {
            return stdout
        }

        boolean getSuccess() {
            return success
        }
    }
}
