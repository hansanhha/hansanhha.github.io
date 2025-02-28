plugins {
    java
    id("org.springframework.boot") version "3.4.1"
    id("io.spring.dependency-management") version "1.1.7"
}

java {
    toolchain {
        languageVersion =
            JavaLanguageVersion.of(21)
    }
}

configurations {
    compileOnly {
        extendsFrom(
            configurations.annotationProcessor.get()
        )
    }

    testCompileOnly {
        extendsFrom(
            configurations.testAnnotationProcessor.get()
        )
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.redisson:redisson-spring-boot-starter:3.43.0")

    annotationProcessor("org.projectlombok:lombok")
    testAnnotationProcessor("org.projectlombok:lombok")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
