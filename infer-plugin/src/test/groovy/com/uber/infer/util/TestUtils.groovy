package com.uber.infer.util

class TestUtils {

    static def getPluginClasspath(Object object) {
        // Get the plugin classpath - https://docs.gradle.org/current/userguide/test_kit.html
        def pluginClasspathResource = object.getClass().classLoader.findResource("infer-plugin-classpath.txt")
        if (pluginClasspathResource == null) {
            throw new IllegalStateException("Did not find plugin classpath resource, run `testClasses` build task.")
        }

        return pluginClasspathResource.readLines()
                .collect { it.replace('\\', '\\\\') }
                .collect { "'$it'" }
                .join(", ")
    }

    static def setupFixture(File projectFile, String fixtureName) {
        AntBuilder antBuilder = new AntBuilder();
        antBuilder.copy(toDir: projectFile.getPath()) {
            fileset(dir: "src/test/fixtures/${fixtureName}")
        }
    }
}
