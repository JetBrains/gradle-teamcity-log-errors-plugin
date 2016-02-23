package jetbrains.buildServer.test.util

import spock.lang.Specification
import spock.lang.Unroll

import jetbrains.buildServer.test.util.LogFile

class LogFileSpec extends Specification {
    String pattern = /\[.{23}\] \s*(?<level>\S+) - \s*\S+ - (?<message>.*)\s?/
    List<Message> errors

    @Unroll
    def "No errors in #filename"(String filename) {
        when:
        parse(filename)

        then:
        errors.size() == 0

        where:
        filename                                | _
        'no errors.log'                         | _
        'multiline message.log'                 | _
        'unformatted message in first line.log' | _
        'empty line.log'                        | _
    }

    @Unroll
    def "Error message in #filename"(String filename, Integer lineNumber, String status, String text, String stacktrace) {
        when:
        parse(filename)

        then:
        errors.size() == 1
        with(errors[0]) { message->
            message.filename == filename
            message.lineNumber == lineNumber
            message.status == status
            message.text == text
            message.stacktrace == stacktrace
        }

        where:
        filename                      | lineNumber | status  | text                      | stacktrace
        'error.log'                   | 1          | 'error' | 'error message'           | ''
        'error in last line.log'      | 2          | 'error' | 'last line'               | ''
        'multiline error.log'         | 2          | 'error' | 'first line\nsecond line' | ''
        'exception.log'               | 1          | 'error' | 'error'                   | 'JspException: java\n\tat org'
        'exception without error.log' | 1          | 'info'  | 'message'                 | 'JspException: java\n\tat org'
        'exception in first line.log' | null       | null    | '\nUsing CATALINA_BASE'   | 'JspException: java\n\tat org'
    }

    def "Error message with alternate pattern"() {
        given:
        pattern = /.{19} \[(?<level>.+?)\] \S+ \S+ (?<message>.*)/

        when:
        parse('nginx.log')

        then:
        errors.size() == 1
    }

    void parse(String filename) {
        errors = new LogFile(new File("src/test/resources/$filename"), pattern).parse()
    }
}
