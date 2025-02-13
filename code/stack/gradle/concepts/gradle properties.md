## Gradle Properties

gradle properties - they also called "gradle project properties" or "gradle.properties file" are a properties that gets passed in from an external source when the build executed,

and can be accessed in the build script

So that, the same build script can be executed differently in different scenarios, Especially useful to avoid storing credentials or other sensitive information in version control

## Passing Properties

**for example:**

To refer to gradle property in the build script, it should be used property method

The property method returns a value of `Any?`, so we need to call `toString()` to convert it to a String before assigning it to a variable

```kotlin
publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }

    repositories {
        mavenLocal {
            group = property("group").toString()
            version = property("version").toString()
        }
    }
}
```

And then, we run the task `publishToMavenLocal` and pass properties using the `-P` option

```shell
./gradlew :theme-park-api:publishToMavenLocal -Pgroup=com.gradle.theme-park -Pversion=1.0.0
```

If you remote repository than local repository, you can use `PasswordCredentials`

You just give your repository a name and tell Gradle that is uses PasswordCredentials

Gradle then automatically looks for a username and password property prefixed with the repository name you specified

```kotlin
 publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
    repositories {
        maven {
            name = "maven"
            credentials(PasswordCredentials::class)
            url = uri("<your-repository-url>")
        }
    }
}
```

```shell
./gradlew publish -PmavenUsername=<username> -PmavenPassword=<password>
```

### Passing Properties Ways

Gradle has several ways to pass project properties into build, some of which have higher priority that others

Or in other words, the same property can be passed into build in mutiple ways, but the one with highest priority wins

Different options, from highest to lowest priority:

**On the command line when calling Gradle using `-P`**

```shell
./gradlew <task-name> -P<property-name>=<property-value>
```

**As Java system properties using `-D`**

```shell
./gradlew <task-name> -D<property-name>=<property-value>
```

**As environment variables**

```shell
<ENN_VARIABLE>=<property-value> ./gradlew <task-name>
```

**In a gradle.properties file located in the Gradle user home directory(`~/.gradle/gradle.properties`)**
```text
// ~/.gradle/gradle.properties

<property-key>=<property-value>
```

**In a gradle.properties file located in the project root directory**

You can use this option when the gradle.properties file should be committed to version control and shared with other team-members 
```text
// project-root-directory/gradle.properties

<property-key>=<property-value>
```

### General Gradle Properties

Regardless of the specific project, you can pass gradle properties generally to the build script

you can set the property in the `gradle.properties` file in the "command line interface", "gradle user home directory," "gradle project root directory" or "gradle home directory"

[gradle properties](https://docs.gradle.org/current/userguide/build_environment.html#sec:gradle_configuration_properties)

## Accessing properties

**First accessing option**

using `property` method to access a property

```text
 Object property(String var1) throws MissingPropertyException
```

```kotlin
tasks.register("accessProperty") {
      doLast {
          
          // we must call the property method directly on the project object
          // otherwise, Gradle tries to look for the property in the task
          println(project.property("myProperty"))
      }
}
```

**Second accessing option**

First option throws exception if the property isn't found

When we need to access a property but don't want an exception to be thrown if it's not provided, use `findProperty` method

If the property is not found "null" is returned 

```text
 Object findProperty(String var1)
```

```kotlin
tasks.register("accessProperty") {
      doLast {
          println(project.findProperty("myProperty"))
      }
}
```

Or specify a default value using Kotlin's elvis operator if the property is not found 

```kotlin
tasks.register("accessProperty") {
    doLast {
        println(project.findProperty("myProperty") ?: "defaultValue")
    }
}
```

**useful option**

We can check for the existence of a property using the `hasProperty` method

```text
 boolean hasProperty(String var1)
```

```kotlin
tasks.register("accessProperty") {
    doLast {
        if (project.hasProperty("myProperty")) {
            println(project.property("myProperty"))
        }
    }
}
```