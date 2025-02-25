plugins {
    java
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform("io.micrometer:micrometer-tracing-bom:latest.release"))
    implementation("io.micrometer:micrometer-tracing")

    // 필요에 따라 구현체 추가
//    implementation("io.micrometer:micrometer-tracing-bridge-brave")
//    implementation("io.micrometer:micrometer-tracing-bridge-otel")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}