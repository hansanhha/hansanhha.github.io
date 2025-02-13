plugins {
    id("java")
}

repositories {
    mavenCentral()
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
}

dependencies {
    implementation("org.springframework:spring-core")
    implementation("org.springframework:spring-context")
    implementation("org.springframework:spring-context-support")
    implementation("org.springframework:spring-context-indexer")
    implementation("org.springframework:spring-aop")
    implementation("org.springframework:spring-aspects")
    implementation("org.springframework:spring-beans")

    annotationProcessor("org.projectlombok:lombok")

    testImplementation("org.junit.jupiter:junit-jupiter-api")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
