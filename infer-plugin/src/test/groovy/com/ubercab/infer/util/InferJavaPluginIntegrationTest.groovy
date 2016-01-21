package com.ubercab.infer.util

import org.junit.Before
import org.junit.Test

class InferJavaPluginIntegrationTest extends IntegrationTest {

    private String javaTestBuildFile

    @Before
    void setup() {
        def pluginClasspath = TestUtils.getPluginClasspath(this)
        javaTestBuildFile= """
                buildscript {
                    repositories {
                        jcenter()
                        mavenLocal()
                    }
                    dependencies {
                        classpath files($pluginClasspath)
                    }
               }

                apply plugin: 'java'
                apply plugin: 'com.ubercab.infer.java'

                repositories {
                    jcenter()
                }

                dependencies {
                    compile 'com.intellij:annotations:5.1'
               }
            """
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

    private def runCommand(String command, String fixtureName , boolean shouldSucceed) {
        runCommand(command, fixtureName, javaTestBuildFile, shouldSucceed)
    }
}