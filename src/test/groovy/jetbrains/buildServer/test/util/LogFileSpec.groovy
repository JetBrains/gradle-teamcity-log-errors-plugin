package jetbrains.buildServer.test.util

import spock.lang.Specification
import spock.lang.Unroll

import jetbrains.buildServer.test.util.LogFile

class LogFileSpec extends Specification {
    List<Message> errors

    @Unroll
    def "No errors in #filename"(String filename) {
        when:
        parse filename

        then:
        errors == []

        where:
        filename                | _
        'no errors.log'         | _
        'multiline message.log' | _
        'empty line.log'        | _
    }

    @Unroll
    def "Error message in #filename"(String filename, int lineNumber, String status, String text, String stacktrace) {
        when:
        parse(filename)

        then:
        errors.size() == 1
        errors[0].filename == filename
        errors[0].lineNumber == lineNumber
        errors[0].status == status
        errors[0].text == text
        errors[0].stacktrace == stacktrace

        where:
        filename                      | lineNumber | status  | text                      | stacktrace
        'error.log'                   | 1          | 'ERROR' | 'error message'           | ''
        'error in last line.log'      | 2          | 'ERROR' | 'last line'               | ''
        'multiline error.log'         | 2          | 'ERROR' | 'first line\nsecond line' | ''
        'exception.log'               | 1          | 'ERROR' | 'error'                   | 'JspException: java\n\tat org'
        'exception without error.log' | 1          | 'INFO'  | 'message'                 | 'JspException: java\n\tat org'
        'exception in first line.log' | 0          | null    | ''                        | 'JspException: java\n\tat org'
    }

    void parse(String filename) {
        errors = new LogFile(new File("src/test/resources/$filename")).parse()
    }
}
