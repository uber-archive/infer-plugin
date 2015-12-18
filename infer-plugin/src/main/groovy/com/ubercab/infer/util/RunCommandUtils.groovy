package com.ubercab.infer.util

import static com.ubercab.infer.InferPlugin.TAG

/**
 * Utility class to run command line commands.
 */
final class RunCommandUtils {

    private final static int SUCCESS = 0

    private RunCommandUtils() { }

    /**
     * Method to run a command line command.
     *
     * @param command command string to be run.
     * @param errorIfFail should throw exception or not upon failure.
     * @param dir directory to run the command from.
     * @return A list containing stdout and stderr strings.
     *
     * @throws CommandException if the command fails while running and if errorIfFail is true.
     */
    static List<String> run(String command, boolean errorIfFail = true, File dir = new File("."))
            throws CommandException {
        String[] env = null
        Process process = command.execute(env, dir)
        StringBuffer stdoutBuffer = new StringBuffer(), stderrBuffer = new StringBuffer()
        process.waitForProcessOutput(stdoutBuffer, stderrBuffer)

        String stdout = stdoutBuffer.toString()
        String stderr = stderrBuffer.toString()

        int exitCode = process.exitValue()
        if (errorIfFail && exitCode != SUCCESS) {
            throw new CommandException("Command exited with ${exitCode} - ${stderr}")
        }
        return [stdout, stderr]
    }

    /**
     * A wrapper class for exceptions caused while running command line commands
     */
    static class CommandException extends RuntimeException {

        /**
         * CommandLine exception constructor.
         *
         * @param message the message to be thrown.
         */
        public CommandException(String message) {
            super("${TAG} :${message}");
        }
    }
}
