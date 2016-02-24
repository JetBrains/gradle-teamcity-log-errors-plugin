package org.jetbrains.teamcity.gradle.logErrors

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.*

class ReportLogErrorsTask extends DefaultTask {
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
        if(pattern == null)
            throw new TaskExecutionException(this, new Exception("'pattern' must be specified."))
        if(!pattern.contains('(?<level>'))
            throw new TaskExecutionException(this, new Exception("Pattern must contain 'level' group."))
        if(!pattern.contains('(?<message>'))
            throw new TaskExecutionException(this, new Exception("Pattern must contain 'message' group."))

        files.each { file->
            new LogFile(file, pattern).parse().each { message ->
                LogFile.printError message
            }
        }
    }
}
