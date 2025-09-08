## maven-publish plugin

It's gradle's core plugin

This plugin lets you configure what artifact to publish and which maven repository to publish to

It handles generating everything required to Maven, including the Maven .pom XML file

## Publishing to a Remote Maven Repository

First of all, we need to add plugin `maven-publish` to publish artifact

```kotlin
plugin {
    application
    `maven-publish`
}
```

And then, we configure to what the artifact to publish and the repository to which to publish the artifact as follows:

```kotlin
publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }

    repositories {
        maven {
            url = uri(${remote repository uri})
            credentials {
                username = ${username}
                password = ${password}
            }
        }
    }
}
```

In Gradle components are defined by plugins to provide a simple way to reference an artifact to publish

The `java` plugin that is automatically applied when you appy `application` plugin adds the java component, representing the jar file created by the `jar` task  

Finally, we use `publish` task to publish artifact to remote maven repository

```shell
$ ./gradlew publish
```

## Publishing to a Local Maven Repository

In addition to publish to remote maven repository, we can publish artifact to in local maven repository

If you use `publishToMavenLocal` task, it published to your local maven repository that in "~/.m2/repository"  

```shell
$ ./gradlew publishToMavenLocal
```

