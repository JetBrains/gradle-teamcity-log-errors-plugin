package jetbrains.buildServer.test.util

import spock.lang.Specification
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.gradle.api.Task

class TaskSpec extends Specification {
    Project project
    Task task

    def setup() {
        project = ProjectBuilder.builder().build()
        task = project.task('checkLogs', type: ProcessLogfileTask)
    }

    def "Task type is available"() {
        expect:
        task instanceof ProcessLogfileTask
    }

    def "Task is configurable"() {
        when:
        task.configure {
            file '1.log'
        }

        then:
        with(task) {
            files == [new File('1.log')]
        }
    }

    def "Task accepts multiple parameters"() {
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
