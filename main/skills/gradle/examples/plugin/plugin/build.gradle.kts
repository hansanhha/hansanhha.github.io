plugins {
    `kotlin-dsl`
    `maven-publish`
//    alias(libs.plugins.jvm)
}

group = "gradle.is.awesome"
version = "1.0.0"

repositories {
    mavenCentral()
}

//dependencies {
//    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
//    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
//}

gradlePlugin {
    plugins {
        create("fileDiff") {
            id = "gradle.is.awesome.file-diff"
            implementationClass = "gradle.is.awesome.FileDiffPlugin"
        }
    }
}

val functionalTestSourceSet = sourceSets.create("functionalTest") {
}

configurations["functionalTestImplementation"].extendsFrom(configurations["testImplementation"])
configurations["functionalTestRuntimeOnly"].extendsFrom(configurations["testRuntimeOnly"])

//val functionalTest by tasks.registering(Test::class) {
//    testClassesDirs = functionalTestSourceSet.output.classesDirs
//    classpath = functionalTestSourceSet.runtimeClasspath
//    useJUnitPlatform()
//}

gradlePlugin.testSourceSets.add(functionalTestSourceSet)

tasks.named<Task>("check") {
//    dependsOn(functionalTest)
}

testing {
    suites {
        val test by getting(JvmTestSuite::class) {
            useJUnitJupiter()
        }
    }
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}
