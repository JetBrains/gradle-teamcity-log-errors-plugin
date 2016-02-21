package jetbrains.buildServer.test.util

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

import static jetbrains.buildServer.test.util.findErrors.processFile

class ProcessLogfileTask extends DefaultTask {
    List<File> files = []

    def file(String filename) {
        files << new File(filename)
    }

    def file(File file) {
        files << file
    }

    @TaskAction
    def processLogfileAction() {
        files.each { file->
            processFile(file)
        }
    }
}
