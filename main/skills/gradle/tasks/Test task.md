## Test Task

if we run the test with gradle, it always used `test` task which is based on the `Test` task class

## Configure the Test Task

by the default, configured test task in build script by `gradle init` command is as follows: 

```kotlin
tasks.named<Test>("test") {
    useJUnitPlatform()
}
```

if you use other test dependency besides JUnit, then you can configure:
```kotlin
tasks.withType<Test>().configureEach {
//    useTestNG()
//    useJUnitPlatform()
}
```

In addition to, it can also exclude or include specific package that runs the test

```kotlin
tasks.withType<Test>().configureEach {
//    useTestNG()
    useJUnitPlatform()
    include("com/example/application/unit")
    exclude("com/example/application/integration")
}
```

## CleanTest task

Gradle has incremental build feature, so if you don't change production code or test code, 

then gradle use cached test result  when you run test after first run test

`cleanTest` task clean only about test result, so you can use it to rerun test task without change the code

`./gradlew :playground:theme-park:cleanTest :playground:theme-park:test --console=verbose`

## Separate Test Task and Directory that Unit Test and Integration Test

The [JVM Test suite plugin(`jvm-test-suite`)](https://docs.gradle.org/current/userguide/jvm_test_suite_plugin.html) provides a DSL and API to model multiple groups of automated tests into test suite in JVM based proejcts

Tests suites are intended to grouped by their purpose and can have separate dependencies and use different testing frameworks

For instance, this plugin can be used to define a group of Integration Tests, which might run much longer than unit tests and have different environmental requirements

Note that, if you apply java plugin, then this plugin automatically applied

**Default Test Suite**
- A default test suite called `test` is automatically added for running unit tests
- As you know unit test's package location is `src/test/java` and you run them with `test` task

**New Test Suite for Integration Test**
- We can configure new test suite with testing and suites lambdas
- Register integrationTest and then say we depend on the production code in build script
- JVM Test Suite plugin automatically adds JUnit5 dependencies for any declared new test suites
- To automatically add JUnit5 dependencies for unit tests too, configure the test suite named `test`
- Besides we can remove dependencies and configuration for JUnit Jupiter

```kotlin
testing {
    suites {
        val test by getting(JvmTestSuite::class) {
            useJUnitJupiter()
        }

        register<JvmTestSuite>("integrationTest") {
            dependencies {
                implementation(project())
            }
        }
    }
}
```

Then, we move integration test classes into `src/integrationTest/java` to run separately from unit tests in `src/test/java`