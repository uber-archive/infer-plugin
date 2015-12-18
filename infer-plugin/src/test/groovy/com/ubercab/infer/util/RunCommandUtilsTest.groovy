package com.ubercab.infer.util

import org.junit.Test

class RunCommandUtilsTest {

    @Test(expected = RunCommandUtils.CommandException)
    public void run_whenCommandReturnsFailure_shouldFail() {
        RunCommandUtils.run("ls RANDOM_DIRECTORY", true)
    }

    @Test
    public void run_whenCommandReturnsSuccess_shouldSucceed() {
        def (String stdout, String stderr) = RunCommandUtils.run("ls", true)

        assert stdout.length() > 0
        assert stderr == ""
    }
}
