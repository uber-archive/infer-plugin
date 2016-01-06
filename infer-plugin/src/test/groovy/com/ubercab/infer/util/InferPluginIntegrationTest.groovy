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
        setupTestWithFixture("failing_java_app")

        def result = GradleRunner.create()
                .withProjectDir(mProjectFile)
                .withArguments('infer')
                .buildAndFail()

        assert result.task(":infer").outcome == TaskOutcome.FAILED
    }

    @Test
    void infer_withGoodSource_shouldPassWhenInferFindsNoWarnings() {
        setupTestWithFixture("passing_java_app")

        def result = GradleRunner.create()
                .withProjectDir(mProjectFile)
                .withArguments('infer')
                .build()

        assert result.task(":infer").outcome == TaskOutcome.SUCCESS
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
            """
    }
}
