plugins {
    application
    id("org.springframework.boot") version("3.4.2")
    id("io.spring.dependency-management") version("1.1.7")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")

    // annotation-based metrics measurement
    implementation("org.springframework.boot:spring-boot-starter-aop")

    // spring boot observability
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    // metrics
    implementation("io.micrometer:micrometer-registry-prometheus")
}


java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}