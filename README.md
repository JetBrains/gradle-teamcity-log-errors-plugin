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
        classpath 'org.jetbrains.teamcity.gradle:log-errors:1.+'
    }
}

apply plugin: 'org.jetbrains.teamcity.log-errors'

reportLogErrors {
    pattern (/\[.+?\] \s*(\S+) - \s*\S+ - (.*)/)
    file 'service1.log'
    file 'service2.log'
}
```

- `pattern`: [regex string](https://docs.oracle.com/javase/8/docs/api/java/util/regex/Pattern.html), must contain two saving groups for message level (INFO/WARN/ERROR) and text.
- 'file': a filename to scan.
