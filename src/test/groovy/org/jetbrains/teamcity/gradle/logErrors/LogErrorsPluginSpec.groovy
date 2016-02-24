package org.jetbrains.teamcity.gradle.logErrors

import spock.lang.Specification
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.BuildResult
import static org.gradle.testkit.runner.TaskOutcome.*

class LogErrorsPluginSpec extends Specification {
    List<File> pluginClasspath
    @Rule final TemporaryFolder testProjectDir = new TemporaryFolder()
    File buildFile
    BuildResult result

    def setup() {
        def pluginClasspathResource = getClass().classLoader.findResource("plugin-classpath.txt")
        if (pluginClasspathResource == null) {
            throw new IllegalStateException("Did not find plugin classpath resource, run `testClasses` build task.")
        }
        pluginClasspath = pluginClasspathResource.readLines().collect { new File(it) }

        buildFile = testProjectDir.newFile('build.gradle')
        buildFile << "plugins { id 'org.jetbrains.teamcity.log-errors' }\n"
    }

    def "Run without configuration"() {
        when:
        build('tasks')

        then:
        with(result) {
            task(':tasks').outcome == SUCCESS
        }
    }

    def "Show error on missing pattern"() {
        when:
        buildAndFail()

        then:
        with(result) {
            output.contains("'pattern' must be specified.")
        }
    }

    def "Show error if pattern lacks level"() {
        given:
        buildFile << $/
            reportLogErrors {
                pattern = /(?<message>.*)/
            }
        /$

        when:
        buildAndFail()

        then:
        with(result) {
            output.contains("Pattern must contain 'level' group.")
        }
    }

    def "Show error if pattern lacks message"() {
        given:
        buildFile << $/
            reportLogErrors {
                pattern = /(?<level>.*)/
            }
        /$

        when:
        buildAndFail()

        then:
        with(result) {
            output.contains("Pattern must contain 'message' group.")
        }
    }

    def "Show error on missing file"() {
        given:
        buildFile << $/
            reportLogErrors {
                pattern = /\[.{23}\] \s*(?<level>\S+) - \s*\S+ - (?<message>.*)\s?/
                file 'src/test/resources/missing.log'
            }
        /$

        when:
        build()

        then:
        with(result) {
            output.contains("File 'missing.log' does not exist")
        }
    }

    def "Report a service message"() {
        given:
        buildFile << $/
            reportLogErrors {
                pattern = /\[.{23}\] \s*(?<level>\S+) - \s*\S+ - (?<message>.*)\s?/
                file 'src/test/resources/error.log'
            }
        /$

        when:
        build()

        then:
        with(result) {
            output.contains("##teamcity[buildProblem description='Error message in error.log (line 1): error message' identity='-2066360288']")
        }
    }

    def "Different stacktraces produce different service message hashsums"() {
        given:
        buildFile << $/
            reportLogErrors {
                pattern = /\[.{23}\] \s*(?<level>\S+) - \s*\S+ - (?<message>.*)\s?/
                file 'src/test/resources/different checksums 1.log'
            }
        /$

        when:
        build()

        then:
        with(result) {
            output.contains "##teamcity[buildProblem description='Error message in different checksums 1.log (line 1): the same message|nJspException: java|n\t123' identity='545184986']"
            output.contains "##teamcity[buildProblem description='Error message in different checksums 1.log (line 4): the same message|nJspException: java|n\t456' identity='545187965']"
        }
    }

    def "Different error texts produce different service message hashsums"() {
        given:
        buildFile << $/
            reportLogErrors {
                pattern = /\[.{23}\] \s*(?<level>\S+) - \s*\S+ - (?<message>.*)\s?/
                file 'src/test/resources/different checksums 2.log'
            }
        /$

        when:
        build()

        then:
        with(result) {
            output.contains "##teamcity[buildProblem description='Error message in different checksums 2.log (line 1): different messages|n123|nJspException: java|n\tthe same stacktrace' identity='350940111']"
            output.contains "##teamcity[buildProblem description='Error message in different checksums 2.log (line 5): different messages|n456|nJspException: java|n\tthe same stacktrace' identity='-2134943630']"
        }
    }

    void build(String task = 'reportLogErrors') {
        result = GradleRunner.create()
                    .withProjectDir(testProjectDir.root)
                    .withArguments(task)
                    .withPluginClasspath(pluginClasspath)
                    .build()
    }

    void buildAndFail(String task = 'reportLogErrors') {
        result = GradleRunner.create()
                    .withProjectDir(testProjectDir.root)
                    .withArguments(task)
                    .withPluginClasspath(pluginClasspath)
                    .buildAndFail()
    }
}
