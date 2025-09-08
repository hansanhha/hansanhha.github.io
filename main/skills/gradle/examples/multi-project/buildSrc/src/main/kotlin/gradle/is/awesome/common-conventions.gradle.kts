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