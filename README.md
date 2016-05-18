# Infer Gradle Plugin

This Gradle plug-in creates tasks to run [Infer](http://fbinfer.com) on Android and Java projects.

## Integration

To use this plug-in, [you must have Infer installed](http://fbinfer.com/docs/getting-started.html).

Add the plug-in dependency and apply it in your project's `build.gradle`:
```groovy
buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        ...
        classpath "com.uber:infer-plugin:0.7.0"
    }
}
```
## Applying the Plugin

### Android

```groovy
apply plugin: 'com.android.application'
apply plugin: 'com.uber.infer.android'
```

### Java

```groovy
apply plugin: 'java'
apply plugin: 'com.uber.infer.java'
```

## Including and Excluding

In the build.gradle of the project that applies the plugin:
```groovy
inferPlugin {
    infer {
        include = project.files("<PATH_TO_INCLUDE>")
        exclude = project.files("<PATH_TO_EXCLUDE>")
    }
    eradicate {
        include = project.files("<PATH_TO_INCLUDE>")
        exclude = project.files("<PATH_TO_EXCLUDE>")
    }
}
```

## Tasks

* `infer` - runs Infer's standard analyzer.
* `eradicate` - runs Infer's [Eradicate](http://fbinfer.com/docs/eradicate.html) analyzer.

For Android projects, the plug-in will create a task for each build variant in your application or library (for example, debug and release).

* `inferVaraint` - runs Infer's standard analyzer.
* `eradicateVariant` - runs Infer's [Eradicate](http://fbinfer.com/docs/eradicate.html) analyzer.

For a specific list of tasks available on your project, run Gradle's `tasks` command.
