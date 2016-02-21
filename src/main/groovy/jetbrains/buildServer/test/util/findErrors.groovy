package jetbrains.buildServer.test.util

import jetbrains.buildServer.messages.serviceMessages.ServiceMessage

class findErrors {
    static def processFile(File file) {
        if (!file.exists()) {
            System.err.println "File '$file.name' does not exist"
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

    private static def printMessage(file, message) {
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
