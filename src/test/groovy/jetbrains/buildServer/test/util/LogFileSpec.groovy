package jetbrains.buildServer.test.util

import spock.lang.Specification

import jetbrains.buildServer.test.util.LogFile

class LogFileSpec extends Specification {
    List<Message> errors

    def "No errors"() {
        when:
        parse '0.log'

        then:
        errors == []
    }

    def "Error message"() {
        when:
        parse '1.log'

        then:
        errors.size() == 1
        errors[0].filename == '1.log'
        errors[0].lineNumber == 1
        errors[0].status == 'ERROR'
        errors[0].text == '111'
        errors[0].stacktrace == ''
    }

    def "Error message in last line"() {
        when:
        parse '2.log'

        then:
        errors.size() == 1
        errors[0].filename == '2.log'
        errors[0].lineNumber == 2
        errors[0].status == 'ERROR'
        errors[0].text == '222'
        errors[0].stacktrace == ''
    }

    void parse(String filename) {
        errors = new LogFile(new File(filename)).parse()
    }
}
