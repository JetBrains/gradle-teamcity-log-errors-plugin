package org.jetbrains.teamcity.gradle.logErrors

import spock.lang.Specification
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.gradle.api.tasks.TaskValidationException
import spock.lang.Unroll

class ReportLogErrorsTaskSpec extends Specification {
    Project project
    ReportLogErrorsTask task

    def setup() {
        project = ProjectBuilder.builder().build()
        task = project.task('reportLogErrors', type: ReportLogErrorsTask)
    }

    def "Task type is available"() {
        expect:
        task instanceof ReportLogErrorsTask
    }

    def "Task accepts a pattern"() {
        when:
        task.configure {
            pattern (/\[.{23}\] \s*(?<level>\S+) - \s*\S+ - (?<message>.*)\s?/)
        }

        then:
        with(task) {
            pattern.pattern() == /\[.{23}\] \s*(?<level>\S+) - \s*\S+ - (?<message>.*)\s?/
        }
    }

    def "Task fails on incorrect pattern"() {
        when:
        task.configure {
            pattern (')()(')
        }

        then:
        def e = thrown TaskValidationException
        e.message == "Pattern ')()(' is incorrect"
    }

    @Unroll
    def "Task fails without saving groups in pattern '#value'"(String value) {
        when:
        task.configure {
            pattern (value)
        }

        then:
        def e = thrown TaskValidationException
        e.message == "Pattern must contain two saving groups"

        where:
        value << [
            /.+/,
            /(.+)/,
            /(.+).+(.+).+(.+).+/
        ]
    }

    def "Task accepts a file"() {
        when:
        task.configure {
            file '1.log'
        }

        then:
        with(task) {
            files == [new File('1.log')]
        }
    }

    def "Task accepts multiple files"() {
        when:
        task.configure {
            file '1.log'
            file '2.log'
        }

        then:
        with(task) {
            files == [new File('1.log'), new File('2.log')]
        }
    }

    def "Task accepts File objects"() {
        when:
        task.configure {
            file new File('1.log')
        }

        then:
        with(task) {
            files == [new File('1.log')]
        }
    }
}
