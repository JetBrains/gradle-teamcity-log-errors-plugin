package jetbrains.buildServer.test.util

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import jetbrains.buildServer.messages.serviceMessages.ServiceMessage

class processLogfileTask extends DefaultTask {
    List<String> files = new ArrayList<String>()

    def file(String filename) {
        files << filename
    }

    @TaskAction
    def processLogfileAction() {
        files.each { file->
            processFile(file)
        }
    }

    def processFile (String filename) {
        def file = new File(filename)
        if (!file.exists()) {
            System.err.println "$filename doest not exist"
            return
        }

        def message = [:]

        file.eachLine { line, number ->
            def matcher = line =~ /^\[(\d{4}-\d{2}-\d{2}) (\d{2}:\d{2}:\d{2},\d{3})\]\s+(\S+?) -\s+?(\S+?) - (.*?)\s?$/

            if (matcher.count == 0) {
                if (message.stacktrace == '') {
                    if (line ==~ /\S+Exception: .+/) {
                        // the line is a beginning of a stacktrace
                        message.stacktrace = line
                    } else {
                        // the line is not a stacktrace, sot it's a continuation of a log message
                        message.text += "\n$line"
                    }
                } else {
                    // the line is a continuation of a stacktrace
                    message.stacktrace += "\n$line"
                }
                return
            }

            // a line is a new log message. print previously saved message
            printMessage(file.name, message)

            // save the new message. it may be continued by multi-line message or a stacktrace in following lines
            message = [
                lineNumber: number,
                status    : matcher[0][3],
                text      : matcher[0][5],
                stacktrace: '',
            ]
        }
        printMessage(file.name, message)
    }

    def printMessage(file, message) {
        def text
        if (message.status == 'ERROR') {
            text = 'Error message'
        } else if (message.stacktrace) {
            text = 'Stacktrace without ERROR message'
        } else {
            return
        }
        text += " in $file (line $message.lineNumber): $message.text"
        if (message.stacktrace) {
            text += "\n$message.stacktrace"
        }
        def attrs = [
            description: text.toString(),
            identity   : "$message.text\n$message.stacktrace".hashCode().toString()
        ]
        println new ServiceMessage('buildProblem', attrs).asString()
    }
}
