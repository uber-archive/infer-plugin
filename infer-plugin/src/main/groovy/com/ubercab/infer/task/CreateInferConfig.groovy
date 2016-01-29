package com.ubercab.infer.task

import com.amazonaws.util.json.JSONArray
import com.amazonaws.util.json.JSONObject
import org.gradle.api.DefaultTask
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

import java.nio.file.Path
import java.nio.file.Paths

public class CreateInferConfig extends DefaultTask {

    @Input Closure<FileCollection> eradicateExclude
    @Input Closure<FileCollection> eradicateInclude
    @Input Closure<FileCollection> inferExclude
    @Input Closure<FileCollection> inferInclude

    @TaskAction
    def createInferConfig() {
        FileWriter inferConfigFile = new FileWriter(project.projectDir.absolutePath + "/.inferConfig")
        
        JSONObject root = new JSONObject()
        root.put("eradicate_blacklist", getJSONArrayInInferFormat(eradicateExclude))
        root.put("eradicate_whitelist", getJSONArrayInInferFormat(eradicateInclude))
        root.put("infer_blacklist", getJSONArrayInInferFormat(inferExclude))
        root.put("infer_whitelist", getJSONArrayInInferFormat(inferInclude))

        try {
            inferConfigFile.write(root.toString() + "\n")

        } catch (IOException e) {
            e.printStackTrace()

        } finally {
            inferConfigFile.flush()
            inferConfigFile.close()
        }
    }

    private JSONArray getJSONArrayInInferFormat(Closure<FileCollection> fileCollectionClosure) {
        JSONArray returnArray = new JSONArray()
        fileCollectionClosure().each { file ->
            Path pathAbsolute = Paths.get(file.toString())
            Path pathBase = Paths.get(project.projectDir.toString())
            Path pathRelative = pathBase.relativize(pathAbsolute)
            returnArray.put(pathRelative.toString())
        }
        return returnArray
    }
}
