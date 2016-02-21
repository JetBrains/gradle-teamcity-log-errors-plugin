package jetbrains.buildServer.test.util

import spock.lang.Specification
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder

class TaskSpec extends Specification {
    Project project

    def setup() {
        project = ProjectBuilder.builder().build()
    }

    def "Task type is available"() {
        when:
        def task = project.task('checkLogs', type: ProcessLogfileTask)

        then:
        task instanceof ProcessLogfileTask
    }

    def "Task is configurable"() {
        given:
        def task = project.task('checkLogs', type: ProcessLogfileTask)

        when:
        task.configure {
            file '1.log'
        }

        then:
        task.files == [new File('1.log')]
    }

    def "Task accepts multiple parameters"() {
        given:
        def task = project.task('checkLogs', type: ProcessLogfileTask)

        when:
        task.configure {
            file '1.log'
            file '2.log'
        }

        then:
        task.files == [new File('1.log'), new File('2.log')]
    }

    def "Task accepts File objects"() {
        given:
        def task = project.task('checkLogs', type: ProcessLogfileTask)

        when:
        task.configure {
            file new File('1.log')
        }

        then:
        task.files == [new File('1.log')]
    }
}
