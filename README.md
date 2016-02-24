# Gradle TeamCity Log Errors Plugin

Scan log files for error messages, and report them to TeamCity as build problems.

# Usage

```
buildscript {
    repositories {
        mavenCentral()
        maven { url 'http://repository.jetbrains.com/teamcity' }
    }
    dependencies {
        classpath 'org.jetbrains.teamcity.gradle:log-errors:+'
    }
}

apply plugin: 'org.jetbrains.teamcity.log-errors'

processLogfile {
    pattern = /\[.{23}\] \s*(?<level>\S+) - \s*\S+ - (?<message>.*)/
    file 'service1.log'
    file 'service2.log'
}
```
`pattern` [regex string](https://docs.oracle.com/javase/8/docs/api/java/util/regex/Pattern.html) is mandatory.
