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
        buildFile << "plugins { id 'jetbrains.buildServer.test.util.log-processor' }\n"
    }

    def "Run without configuration"() {
        when:
        build()

        then:
        result.task(":processLogfile").outcome == SUCCESS
    }

    def "Report a service message"() {
        given:
        buildFile << "processLogfile { file 'src/test/resources/error.log'}"

        when:
        build()

        then:
        result.output.contains("##teamcity[buildProblem description='Error message in error.log (line 1): error message' identity='-2066360288']")
    }

    def "Show error on missing file"() {
        given:
        buildFile << "processLogfile { file 'src/test/resources/missing.log' }"

        when:
        build()

        then:
        result.output.contains("File 'missing.log' does not exist")
    }

    def "Different stacktraces produce different service message hashsums"() {
        given:
        buildFile << "processLogfile { file 'src/test/resources/different checksums 1.log' }"

        when:
        build()

        then:
        result.output.contains "##teamcity[buildProblem description='Error message in different checksums 1.log (line 1): the same message|nJspException: java|n\t123' identity='545184986']"
        result.output.contains "##teamcity[buildProblem description='Error message in different checksums 1.log (line 4): the same message|nJspException: java|n\t456' identity='545187965']"
    }

    def "Different error texts produce different service message hashsums"() {
        given:
        buildFile << "processLogfile { file 'src/test/resources/different checksums 2.log' }"

        when:
        build()

        then:
        result.output.contains "##teamcity[buildProblem description='Error message in different checksums 2.log (line 1): different messages|n123|nJspException: java|n\tthe same stacktrace' identity='350940111']"
        result.output.contains "##teamcity[buildProblem description='Error message in different checksums 2.log (line 5): different messages|n456|nJspException: java|n\tthe same stacktrace' identity='-2134943630']"
    }

    void build() {
        result = GradleRunner.create()
                    .withProjectDir(testProjectDir.root)
                    .withArguments('processLogfile')
                    .withPluginClasspath(pluginClasspath)
                    .build()
    }
}
