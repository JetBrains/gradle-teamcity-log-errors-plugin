package jetbrains.buildServer.test.util

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.*

import jetbrains.buildServer.test.util.LogFile

class ProcessLogfileTask extends DefaultTask {
    String pattern
    List<File> files = []

    def file(String filename) {
        files << new File(filename)
    }

    def file(File file) {
        files << file
    }

    @TaskAction
    def processLogfileAction() {
        if(pattern == null){
            throw new TaskExecutionException(this, new Exception("'pattern' must be specified."))
        }
        files.each { file->
            new LogFile(file, pattern).parse().each { LogFile.printError it }
        }
    }
}
