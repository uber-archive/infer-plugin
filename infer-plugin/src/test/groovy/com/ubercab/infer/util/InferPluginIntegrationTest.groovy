package com.ubercab.infer.util

import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class InferPluginIntegrationTest {

    @Rule
    public TemporaryFolder mFolder = new TemporaryFolder()

    File mProjectFile;

    @Before
    void setup() {
        mProjectFile = mFolder.newFolder("test")
    }

    @Test
    void infer_withBadSource_shouldFailWhenInferFindsAWarning() {
        runCommand("infer", "failing_infer_java_app", false)
    }

    @Test
    void infer_withGoodSource_shouldPassWhenInferFindsNoWarnings() {
        runCommand("infer", "passing_infer_java_app", true)
    }

    @Test
    void eradicate_withBadSource_shouldFailWhenInferFindsAWarning() {
        runCommand("eradicate", "failing_eradicate_java_app", false)
    }

    @Test
    void eradicate_withGoodSource_shouldPassWhenInferFindsNoWarnings() {
        runCommand("eradicate", "passing_eradicate_java_app", true)
    }

    private def runCommand(String command, String fixtureName, boolean shouldSucceed) {
        setupTestWithFixture(fixtureName)

        def runner = GradleRunner.create()
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
    }

    private def setupTestWithFixture(String fixtureName) {
        def buildFile = new File(mProjectFile, 'build.gradle')
        buildFile.createNewFile()
        def pluginClasspath = TestUtils.getPluginClasspath(this)

        TestUtils.setupFixture(mProjectFile, fixtureName)

        buildFile << """
                buildscript {
                    repositories {
                        jcenter()
                        mavenLocal()
                    }
                    dependencies {
                        classpath 'com.android.tools.build:gradle:1.5.0'
                        classpath files($pluginClasspath)
                    }
               }

                apply plugin: 'java'
                apply plugin: 'com.ubercab.infer'

                repositories {
                    jcenter()
                }

                dependencies {
                    compile 'com.intellij:annotations:5.1'
               }
            """
    }
}
