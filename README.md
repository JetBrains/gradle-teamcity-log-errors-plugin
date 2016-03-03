# Gradle TeamCity Log Errors Plugin

If your web application includes a suite of acceptance tests, they start an instance of the app,
and perform external checks using Selenium WebDriver or REST API.

But sometimes backend components of the tested application raise unhandled exceptions,
which produce no errors in UI, and cannot be caught this way.
Even if all tests are *green*, the app still has *hidden issues*, which should be addressed.

This plugin helps detecting such problems. It provides `reportLogErrors` task, which starts after the tests,
scans log files for error messages or stacktraces, and report them to TeamCity as build problems.

If any errors are found, the build is failed. **Build Results** page and e-mail notifications show a clear explanation what went wrong.

# Features

- **Stacktraces**: all Java stacktraces are reported, even from INFO or WARN messages.
- **Error history**: if the same error is reported in multiple builds, even with different timestamps,
  all occurrences are linked together: *assigned investigations* and *muted problems* are preserved.

# Usage

```
buildscript {
    repositories {
        mavenCentral()
        maven { url 'http://repository.jetbrains.com/teamcity' }
    }
    dependencies {
        classpath 'org.jetbrains.teamcity.gradle:log-errors:1.+'
    }
}

apply plugin: 'org.jetbrains.teamcity.log-errors'

reportLogErrors {
    pattern ( /\[.+?\] \s*(\S+) - \s*\S+ - (.*)/ )
    file 'service1.log'
    file 'service2.log'
}
```

- `pattern`: [regex string](https://docs.oracle.com/javase/8/docs/api/java/util/regex/Pattern.html), must contain two saving groups:
  - message level (INFO/WARN/ERROR),
  - a text.
- `file`: a log filename.
