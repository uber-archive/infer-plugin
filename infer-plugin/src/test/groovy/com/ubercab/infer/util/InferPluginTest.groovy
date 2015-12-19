package com.ubercab.infer.util

import com.ubercab.infer.InferJavaPlugin
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Test

class InferPluginTest {

    private Project project;

    @Before
    public void setup() {
        project = ProjectBuilder.builder().build()
    }

    @Test
    public void apply_withPlugin_shouldWork() {
        project.plugins.apply(InferJavaPlugin.class)
    }
}
