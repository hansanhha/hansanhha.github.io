plugins {
    java
    jacoco
}

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter:5.11.4")
}

tasks.withType<Test> {
    useJUnitPlatform()

    ignoreFailures = true

    testLogging {
        events("PASSED", "FAILED", "SKIPPED", "STANDARD_OUT", "STANDARD_ERROR")

        showStandardStreams = true
    }

    // cpu 코어 개수의 절반 만큼 병렬 실행
    maxParallelForks = Runtime.getRuntime().availableProcessors() / 2

    // 테스트가 성공적으로 실행된 경우 테스트 결과 캐싱
    outputs.cacheIf { true }
}

configurations {
    testImplementation {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
}

tasks.withType<JacocoReport> {
    dependsOn(tasks.withType<Test>())
    reports {
        html.required.set(true)
        xml.required.set(true)
    }
}
