plugins {
//    application
    `java-library`
    `maven-publish`
}

//application {
//    mainClass.set("com.gradle.theme_park.status.RideStatusService")
//}

repositories {
    mavenCentral()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }

    repositories {
        maven {
//            url = uri()
//            credentials {
//                username = "aws"
//                password = System.getenv("")
//            }
        }
    }
}

//tasks.withType<JavaExec>().configureEach {
//    javaLauncher.set(javaToolchains.launcherFor {
//        languageVersion.set(JavaLanguageVersion.of(8))
//    })
//}

tasks.register<JavaExec>("runJar") {
    group = "application"
    description = "Run the jar file itself by gradle"

    classpath(tasks.named("jar").map { it.outputs })
    classpath(configurations.runtimeClasspath)
    args("    teacups    ")

    mainClass.set("com.gradle.theme_park.status.RideStatusService")
}

//tasks.named<Jar>("jar") {
//    manifest {
//        attributes["Main-Class"] = "com.gradle.playground.RideStatusService"
//    }
//}

// compileJava, compileTestJava task configuring
tasks.withType<JavaCompile> {
//    options.isVerbose = true
}

// processResources task configuring
tasks.named<Copy>("processResources") {
    include("**/*.txt")
}

group = "com.gradle.theme-park"
version = "1.0.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.guava)
    implementation(libs.apache.commons.lang3)
    api(libs.jackson.databind)

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

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

tasks.check { dependsOn("integrationTest") }

