[5 core requirements](#5-core-requirements-of-build-tool)
- [Compile Classes](#compile-classes)
- [Resources](#resources)
- [Dependencies](#dependencies)
- [Package](#package)
- [Run Tests](#run-tests)

[Additional requirements (developer workflow features)](#additional-requirements-developer-workflow-features)
- [Run Application](#run-application)
- [Java Version](#java-version)
- [Separating Unit & Integration Test](#separating-unit--integration-tests)
- [Publishing to Maven Repository](#publishing-to-maven-repository)

## 5 core requirements of build tool

### Compile Classes

**.java file**
1. Writing .java source file
2. Java compiler compiles source files into .class class file

**class file**
- Bytecode that is ready to run on the jvm

**classpath**
- A list of files passed to jvm(source file, properties ...)
- If application use external libraries such spring boot then compiler needs to know where they're located during compilation
- This is where classpath helps

### Resources

It's additional files that an application relies on(not java source file)

Source code may refer this, so need to get packaged up with the code

Images, HTML, text files or anything else accessed by the application

### Dependencies

Any libraries referenced from your application code becomes a dependency of the application

It's needed to compile and run the application

But java doesn't resolve dependencies itself, and it relies on the classpath being correctly generated,

referencing any libraries used in the code

The build tool should let us specify dependencies required for compilation, which it should use to generate the compile classpath

### Package

The build tool should package the compiled classes and resources into a single artifact

**jar file**
- single artifact in the java
- which is a zip file containing all the class files and resources, extra metadata

### Run Tests

The tests themselves need to be compiled, and may need a different classpath to that of the main application

Test classes shouldn't end up in the published artifact

The build tool should also be able to run those and show us the results

## Additional requirements (developer workflow features)

### Run Application

If you run through build tool, and it can generate the runtime classpath automatically

### Java Version

The version of java used to run gradle should be decoupled from the version of java used to compile or run in application

To change java version just change a single line of code

By default, Gradle uses the same java version to run java processes as the one its using to run itself, because both are use specified java version of ENVIRONMENT PATH

So that you can see java version that gradle uses through `./gradlew --version` command

gradle and java version in my local computer is as follows:
```text
$ java -version
openjdk version "21" 2023-09-19 LTS
OpenJDK Runtime Environment Temurin-21+35 (build 21+35-LTS)
OpenJDK 64-Bit Server VM Temurin-21+35 (build 21+35-LTS, mixed mode, sharing)

$ ./gradlew --version
------------------------------------------------------------
Gradle 8.6
------------------------------------------------------------

Build time:   2024-02-02 16:47:16 UTC
Revision:     ...

Kotlin:       1.9.20
Groovy:       3.0.17
Ant:          Apache Ant(TM) version 1.10.13 compiled on January 4 2023
JVM:          21 (Eclipse Adoptium 21+35-LTS)
OS:           Mac OS X 14.5 x86_64
```

if you want to configure a specific version to run your java application, you can set it through `java toolchain` configuration:

Then you can have different version of Java that gradle runs and the version that your java application runs on

```kotlin
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}
```

Moreover, tasks of class type "JavaExec", "JavaCompile", and "Test" are can use java toolchain version, so can that override the java version at the task level  

```kotlin
tasks.withType<JavaExec>().configureEach {
    javaLauncher.set(javaToolchains.launcherFor {
        languageVersion.set(JavaLanguageVersion.of(8))
    })
}
```

### Separating Unit & Integration Tests

[jvm test suite plugin configuring](tasks/Test%20task.md/#separate-test-task-and-directory-that-unit-test-and-integration-test)

### Publishing to Maven Repository

If an artifact(library) is stored where maven repository, whether public or private, then it can be referenced by some other project

[maven publish plugin](./plugins/maven-public%20plugin.md)