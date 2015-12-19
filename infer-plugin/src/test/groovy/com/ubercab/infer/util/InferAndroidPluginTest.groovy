package com.ubercab.infer.util

import com.ubercab.infer.InferAndroidPlugin
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Test

class InferAndroidPluginTest {

    private Project project

    @Before
    public void setup() {
        project = ProjectBuilder.builder().build()
    }

    @Test
    public void apply_withAndroidPlugin_shouldWork() {
        project.plugins.apply(InferAndroidPlugin.class)
    }
}
