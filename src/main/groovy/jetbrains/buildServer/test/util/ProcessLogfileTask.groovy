package jetbrains.buildServer.test.util

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

import jetbrains.buildServer.test.util.LogFile

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
            new LogFile(file).parse().each { LogFile.printError it }
        }
    }
}
