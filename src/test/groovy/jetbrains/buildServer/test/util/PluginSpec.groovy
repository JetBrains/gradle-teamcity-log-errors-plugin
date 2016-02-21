package jetbrains.buildServer.test.util

import spock.lang.Specification
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.BuildResult
import static org.gradle.testkit.runner.TaskOutcome.*

class PluginSpec extends Specification {
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
        buildFile << """
            plugins {
                id 'jetbrains.buildServer.test.util.log-processor'
            }
        """
    }

    def "Report a service message"() {
        given:
        buildFile << """
            processLogfile {
                file '1.log'
            }
        """

        when:
        build()

        then:
        result.output.contains("##teamcity[buildProblem description='Error message in 1.log (line 1): 111' identity='1508414']")
    }

    def "Show error message on missing file"() {
        given:
        buildFile << """
            processLogfile {
                file 'error.log'
            }
        """

        when:
        build()

        then:
        result.output.contains("File 'error.log' does not exist")
    }

    void build() {
        result = GradleRunner.create()
                    .withProjectDir(testProjectDir.root)
                    .withArguments('processLogfile')
                    .withPluginClasspath(pluginClasspath)
                    .build()
    }
}
