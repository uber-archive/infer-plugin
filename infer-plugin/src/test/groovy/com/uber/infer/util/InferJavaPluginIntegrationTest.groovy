package com.uber.infer.util

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized)
class InferJavaPluginIntegrationTest extends IntegrationTest {

    private String javaTestBuildFile
    private String providedConfiguration

    @Parameterized.Parameters static Collection<Object[]> data() {
        def data = ['provided', 'compileOnly']
        return data.collect { [it] as Object[] }
    }

    InferJavaPluginIntegrationTest(String providedConfiguration) {
        this.providedConfiguration = providedConfiguration
    }

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
                        classpath 'com.netflix.nebula:gradle-extra-configurations-plugin:3.0.3'
                        classpath files($pluginClasspath)
                    }
                }

                apply plugin: 'java'
                apply plugin: 'nebula.provided-base'
                apply plugin: 'com.uber.infer.java'

                repositories {
                    jcenter()
                }

                dependencies {
                    // Annotations included to test provided support.
                    ${providedConfiguration} 'javax.annotation:jsr250-api:1.0'

                    compile 'com.intellij:annotations:5.1'
                }
            """
    }

    @Test
    void infer_withBadSource_shouldFailWhenInferFindsAWarning() {
        runCommand("infer", "failing_infer_java_app", false)
    }

    @Test
    void infer_withBadSourceAndSourceExcluded_shouldPassWhenInferFindsNoWarnings() {
        javaTestBuildFile += """
            inferPlugin {
                infer {
                    exclude = project.files("src")
                }
            }
        """
        runCommand("infer", "failing_infer_java_app", true)
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
    void eradicate_withBadSourceAndSourceExcluded_shouldPassWhenInferFindsNoWarnings() {
        javaTestBuildFile += """
            inferPlugin {
                eradicate {
                    exclude = project.files("src")
                }
            }
        """
        runCommand("eradicate", "failing_eradicate_java_app", true)
    }

    @Test
    void eradicate_withGoodSource_shouldPassWhenInferFindsNoWarnings() {
        runCommand("eradicate", "passing_eradicate_java_app", true)
    }


    @Test
    void check_runInferWithGoodSource_shouldPassWhenInferFindsNoWarnings() {
        runCommand("check", "passing_infer_java_app", true)
    }

    @Test
    void check_runEradicateWithGoodSource_shouldPassWhenInferFindsNoWarnings() {
        runCommand("check", "passing_eradicate_java_app", true)
    }

    private def runCommand(String command, String fixtureName , boolean shouldSucceed) {
        runCommand(command, fixtureName, javaTestBuildFile, shouldSucceed)
    }
}
