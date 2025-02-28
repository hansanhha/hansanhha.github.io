plugins {
    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform("io.micrometer:micrometer-bom:1.14.4"))
    implementation("io.micrometer:micrometer-core")
    implementation("io.micrometer:micrometer-observation")
    implementation("io.micrometer:micrometer-tracing:1.4.3")

    implementation("org.springframework:spring-aop:6.2.3")
    testImplementation("io.micrometer:micrometer-test:1.14.4")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.11.4")
    testImplementation("org.wiremock:wiremock:3.12.0")

    runtimeOnly("org.aspectj:aspectjweaver:1.9.22.1")
    runtimeOnly("org.aspectj:aspectjrt:1.9.22.1")
}


java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

tasks.withType<Test>() {
    useJUnitPlatform()
}

application {
    mainClass = "hansanhha.MicrometerObservationApplication"
}
