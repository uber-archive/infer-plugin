package com.ubercab.infer.util

import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Before
import org.junit.Rule
import org.junit.rules.TemporaryFolder

class IntegrationTest {

    static String VERSION_GRADLE = "2.10"

    @Rule
    public TemporaryFolder mFolder = new TemporaryFolder()

    File mProjectFile;

    @Before
    void setupBase() {
        mProjectFile = mFolder.newFolder("test")
    }

    protected def runCommand(String command, String fixtureName, String buildFileContents, boolean shouldSucceed) {
        setupTestWithFixture(fixtureName, buildFileContents)

        def runner = GradleRunner.create()
                .withGradleVersion(VERSION_GRADLE)
                .withProjectDir(mProjectFile)
                .withArguments(command)

        def result
        if (shouldSucceed) {
            result = runner.build()
            assert result.task(":${command}").outcome == TaskOutcome.SUCCESS
        } else {
            result = runner.buildAndFail()
            assert result.task(":${command}").outcome == TaskOutcome.FAILED
        }
        assert !(new File(runner.projectDir.absolutePath + "/.inferConfig").exists())
    }

    private def setupTestWithFixture(String fixtureName, String buildFileContents) {
        def buildFile = new File(mProjectFile, 'build.gradle')
        buildFile.createNewFile()

        TestUtils.setupFixture(mProjectFile, fixtureName)

        buildFile << buildFileContents
    }
}
