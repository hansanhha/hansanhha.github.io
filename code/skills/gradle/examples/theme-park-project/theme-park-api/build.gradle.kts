plugins {
    java
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
    `maven-publish`
}

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

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
//    implementation(libs.springboot.web)
    implementation("com.gradle.theme-park:theme-park-status:1.0.1")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks.test {
    useJUnitPlatform()
}