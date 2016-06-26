package com.uber.infer.util

import org.gradle.api.Project
import org.gradle.api.artifacts.UnknownConfigurationException
import org.gradle.api.file.FileCollection

class ConfigurationUtils {

  private ConfigurationUtils() {}

  static FileCollection getAvailable(Project project, String... configurations) {
    def available = [] as Set
    configurations.each { String configuration ->
      try {
        available += project.configurations.getByName(configuration).files
      } catch (UnknownConfigurationException ignored) { }
    }
    return project.files(available)
  }
}
