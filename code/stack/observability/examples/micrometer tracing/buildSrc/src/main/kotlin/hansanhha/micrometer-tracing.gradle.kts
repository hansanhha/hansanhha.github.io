package hansanhha

import gradle.kotlin.dsl.accessors._4f122da6961e7d041703e1e3f07926e2.implementation
import org.gradle.kotlin.dsl.application
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.repositories

plugins {
    application
    id("org.springframework.boot")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform("org.springframework.boot:spring-boot-dependencies:3.4.3"))
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-aop")

    implementation(platform("io.micrometer:micrometer-tracing-bom:1.4.3"))
    implementation("io.micrometer:micrometer-tracing")
    implementation("io.micrometer:micrometer-registry-prometheus")
    implementation("io.micrometer:micrometer-tracing-bridge-otel")

    implementation("io.opentelemetry:opentelemetry-exporter-prometheus:1.47.0-alpha")
    implementation("io.opentelemetry:opentelemetry-api")
    implementation("io.opentelemetry:opentelemetry-sdk")

}

