package jetbrains.buildServer.test.util

import spock.lang.Specification
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder

class ProcessLogfileTaskSpec extends Specification {
    Project project

    def setup() {
        project = ProjectBuilder.builder().build()
    }

    def "Task type should be available in the project"() {
        when:
        def task = project.task('checkLogs', type: ProcessLogfileTask)

        then:
        task instanceof ProcessLogfileTask
    }

    def "Task should be configurable"() {
        given:
        def task = project.task('checkLogs', type: ProcessLogfileTask)

        when:
        task.configure {
            file '1.log'
        }

        then:
        task.files == [new File('1.log')]
    }
}
