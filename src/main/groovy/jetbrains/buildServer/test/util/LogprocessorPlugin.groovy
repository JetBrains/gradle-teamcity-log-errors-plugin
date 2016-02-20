package jetbrains.buildServer.test.util

import org.gradle.api.Plugin
import org.gradle.api.Project
import jetbrains.buildServer.test.util.processLogfileTask

class LogprocessorPlugin implements Plugin<Project> {
    void apply(Project project) {
        project.task('processLogfile', type: processLogfileTask)
    }
}
