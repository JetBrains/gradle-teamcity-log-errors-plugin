package org.jetbrains.teamcity.gradle.logErrors

import org.gradle.api.Plugin
import org.gradle.api.Project

class LogErrorsPlugin implements Plugin<Project> {
    void apply(Project project) {
        project.task('reportLogErrors', type: ReportLogErrorsTask)
    }
}
