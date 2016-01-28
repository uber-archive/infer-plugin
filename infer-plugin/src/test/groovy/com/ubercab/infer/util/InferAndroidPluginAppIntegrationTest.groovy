package com.ubercab.infer.util

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized.class)
class InferAndroidPluginAppIntegrationTest extends IntegrationTest {

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        def commands = [
                ["inferFlavorOneDebug", "passing_infer_android_project", true].toArray(),
                ["inferFlavorOneRelease", "passing_infer_android_project", true].toArray(),
                ["inferFlavorTwoDebug", "failing_infer_android_project", false].toArray(),
                ["inferFlavorTwoRelease", "failing_infer_android_project", false].toArray(),
                ["eradicateFlavorOneDebug", "passing_eradicate_android_project", true].toArray(),
                ["eradicateFlavorOneRelease", "passing_eradicate_android_project", true].toArray(),
                ["eradicateFlavorTwoDebug", "failing_eradicate_android_project", false].toArray(),
                ["eradicateFlavorTwoRelease", "failing_eradicate_android_project", false].toArray(),
        ]
        return commands
    }

    private String androidTestBuildFile
    private String command
    private String fixtureName
    private boolean expectedResult

    public InferAndroidPluginAppIntegrationTest(String command, String fixtureName, Boolean expectedResult) {
        this.command = command
        this.fixtureName = fixtureName
        this.expectedResult = expectedResult

    }

    @Before
    void setup() {
        def pluginClasspath = TestUtils.getPluginClasspath(this)
        androidTestBuildFile = """
                buildscript {
                    repositories {
                        jcenter()
                        mavenLocal()
                    }

                    dependencies {
                        classpath 'com.android.tools.build:gradle:1.5.0'
                        classpath 'com.neenbedankt.gradle.plugins:android-apt:1.8'
                        classpath files($pluginClasspath)
                    }
                }

                apply plugin: 'com.android.application'
                apply plugin: 'com.neenbedankt.android-apt'
                apply plugin: 'com.ubercab.infer.android'

                repositories {
                    jcenter()
                }

                android {
                    buildToolsVersion "23.0.2"
                    compileSdkVersion 23

                    defaultConfig {
                        minSdkVersion 23
                        targetSdkVersion 23
                    }

                    productFlavors {
                        flavorOne
                        flavorTwo
                    }

                    dependencies {
                        compile 'com.intellij:annotations:5.1'

                        // Annotations included to test provided support.
                        provided 'javax.annotation:jsr250-api:1.0'

                        // Leak Canary included to test AAR support.
                        debugCompile 'com.squareup.leakcanary:leakcanary-android:1.3.1'
                        releaseCompile 'com.squareup.leakcanary:leakcanary-android-no-op:1.3.1'
                        testCompile 'com.squareup.leakcanary:leakcanary-android-no-op:1.3.1'
                    }
                }
                inferPlugin {
                    eradicate {
                        exclude.plus("src")
                        include.plus("src")
                    }

                    infer {
                        exclude.plus("src")
                        include.plus("src")
                    }
                }
            """
    }

    @Test
    public void command_shouldPassOrFailAsExpected() {
        runCommand(command, fixtureName, expectedResult)
    }

    private def runCommand(String command, String fixtureName, boolean shouldSucceed) {
        runCommand(command, fixtureName, androidTestBuildFile, shouldSucceed)
    }
}
