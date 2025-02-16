plugins {
    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform("io.micrometer:micrometer-bom:1.14.4"))
    implementation("io.micrometer:micrometer-core")
//    implementation("io.micrometer:micrometer-registry-prometheus")
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
    mainClass = "hansanhha.MicrometerApplication"
}
