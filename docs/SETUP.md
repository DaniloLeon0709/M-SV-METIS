# VitaAlert Setup

## JDK configuration

The project is pinned to **JDK 17** via Gradle toolchains. If your build fails with:

```
Value 'C:\Users\<user>\jdk-23' given for org.gradle.java.home Gradle property is invalid
```

remove or fix the `org.gradle.java.home` entry in your **user-level** `~/.gradle/gradle.properties`
(or set it to a valid JDK 17 installation). Gradle uses this property to pick the JVM before
it evaluates toolchains.

Recommended:

- Android Studio **Settings > Build, Execution, Deployment > Gradle > Gradle JDK** = JDK 17.
- Or set `JAVA_HOME` to a valid JDK 17 installation and remove `org.gradle.java.home` overrides.
