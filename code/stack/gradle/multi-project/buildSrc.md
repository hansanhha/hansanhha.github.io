[buildSrc](#buildsrc)

[Convention Plugins](#convention-plugins)

## buildSrc

One of problems in gradle multi-project is that as project grows, each subproject build script duplicates and things may become difficult to maintain

So that, Gradle offers several mechanism to combat this problem, with an aim to keep your individual build scripts as concise as possible

**buildSrc** is one such mechanism, which lets you pull logic out of your build script into a directory called "buildSrc" at the top level of your project

This extracted build code can then be reused in any project of your build

## Convention Plugins

you want to call from build scripts, and you can put any code in buildSrc, your own custom task classes or plugins

Convention plugins are a way of applying the same build logic to multiple subprojects

create convention plugin and apply it subproject in sample [multi-project](../../examples/multi-project)

1. create buildSrc directory at the root of the project and create build.gradle.kts file

```text
multi-project
- settings.gradle.kts
- build.gradle.kts
- api (subproject)
- message (subproject)
- buildSrc
    - build.gradle.kts
```

build.gradle.kts file in buildSrc directory is used to buildSrc own dependencies
- Applying the kotlin-dsl plugin means we'll write the convention plugin with Kotlin DSL
- The repositories block is needed so that the build can fetch kotlin dependencies
- Inside the dependencies block, I add the dependency to use version catalog in convention plugin

```kotlin
plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
}

repositories {
    mavenCentral()
}
```

2. create `src/main/kotlin` directory and create `common-conventions.gradle.kts` in `gradle.is.awesome` package

```text
multi-project
- buildSrc
    - src/main/kotlin
        - gradle.is.awesome
            - common-conventions.gradle.kts
    - build.gradle.kts
    - settings.gradle.kts
```

3. write build code in `common-conventions.gradle.kts`

```kotlin
plugins {
    java
}

group = "gradle.is.awesome"

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

val Project.libs
    get() = the<org.gradle.accessors.dm.LibrariesForLibs>()

dependencies {
    testImplementation(libs.junit.jupiter)
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}
```

4. apply the convention plugin in subproject

```kotlin
// api subproject

plugins {
    id("common-conventions")
    alias(libs.plugins.springBoot)
    alias(libs.plugins.springDependencyManagement)
}

dependencies {
    implementation(project(":message"))

    implementation(libs.springboot.web)
    implementation(libs.guava)
}
```

```kotlin
// message subproject

plugins {
    id("common-conventions")
}
```
