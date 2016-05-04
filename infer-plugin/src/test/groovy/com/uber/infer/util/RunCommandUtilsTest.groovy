package com.uber.infer.util

import org.junit.Test

class RunCommandUtilsTest {

    @Test
    public void run_whenCommandReturnsFailure_shouldHaveFailureResult() {
        assert RunCommandUtils.run("ls RANDOM_DIRECTORY").success == false
    }

    @Test
    public void run_whenCommandReturnsSuccess_shouldSucceed() {
        def result = RunCommandUtils.run("ls")

        assert result.stdout.length() > 0
        assert result.stderr == ""
    }
}
