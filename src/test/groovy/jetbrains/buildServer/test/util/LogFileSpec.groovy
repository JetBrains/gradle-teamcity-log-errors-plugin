package jetbrains.buildServer.test.util

import spock.lang.Specification
import spock.lang.Unroll

import jetbrains.buildServer.test.util.LogFile

class LogFileSpec extends Specification {
    List<Message> errors

    def "No errors"() {
        when:
        parse 'no errors.log'

        then:
        errors == []
    }

    @Unroll
    def "Error messages in #filename"(String filename, int lineNumber, String status, String text, String stacktrace) {
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
        filename                 | lineNumber | status  | text  | stacktrace
        'error.log'              | 1          | 'ERROR' | '111' | ''
        'error in last line.log' | 2          | 'ERROR' | '222' | ''
    }

    void parse(String filename) {
        errors = new LogFile(new File("src/test/resources/$filename")).parse()
    }
}
