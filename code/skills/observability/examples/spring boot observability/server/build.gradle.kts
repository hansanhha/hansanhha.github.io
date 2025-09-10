plugins {
    application
    id("org.springframework.boot") version("3.4.3")
    id("io.spring.dependency-management") version("1.1.7")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")

    // spring boot observability, annotation-based observation
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-aop")

    // metrics
    implementation("io.micrometer:micrometer-registry-prometheus")

    // tracing (brave bridge, brave reporter)
    implementation("io.micrometer:micrometer-tracing-bridge-brave")
    implementation("io.zipkin.reporter2:zipkin-reporter-brave")

    // logs
    implementation("com.github.loki4j:loki-logback-appender:1.6.0")
}


java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}