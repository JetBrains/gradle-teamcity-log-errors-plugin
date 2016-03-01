package org.jetbrains.teamcity.gradle.logErrors

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.*
import java.util.regex.Pattern
import java.util.regex.PatternSyntaxException

class ReportLogErrorsTask extends DefaultTask {
    Pattern pattern
    List<File> files = []

    def pattern(String pattern) {
        if(pattern.count('(') != 2) {
            throw new TaskValidationException("Pattern must contain two saving groups", [])
        }
        try {
            this.pattern = Pattern.compile(pattern)
        } catch (PatternSyntaxException e) {
            throw new TaskValidationException("Pattern '$pattern' is incorrect", [])
        }
    }

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

        files.each { file->
            new LogFile(file, pattern).parse().each { message ->
                LogFile.printError message
            }
        }
    }
}
