plugins {
    java
    jacoco
    id("org.sonarqube") version("6.0.1.5171")
}

jacoco {
    // gradle jacoco 플러그인의 기본 버전 대신 특정 버전이 필요한 경우 명시
    toolVersion = "0.8.12"
}

tasks.withType<Test> {
    useJUnitPlatform()

    testLogging {
        events("PASSED", "FAILED", "SKIPPED", "STANDARD_OUT", "STANDARD_ERROR")
    }

    // jacoco task 순서 지정: gradle test -> jacoco report
    finalizedBy(tasks.jacocoTestReport)
}

tasks.withType<JacocoReport> {


    // 커버리지 보고서 설정
    reports {
        // 기본 생성 위치: build/reports/jacoco/test/html/index.html
        html.required.set(true)
        xml.required.set(true)
    }

    // 보고서 생성 제외 대상 (특정 클래스 또는 패키지)
    classDirectories.setFrom(
        files(
            classDirectories.files.map {
                fileTree(it) {
                    exclude(
                        "**/dto/**",
                        "**/vo/**",
                        "**/config/**",
                        "**/exception/**",
                        "**/entity/**",
                        "**/*Request.class",) }
            }
        )
    )

    // jacoco task 순서 지정: gradle test -> jacoco report -> jacoco coverage verification
    finalizedBy(tasks.jacocoTestCoverageVerification)
}


tasks.withType<JacocoCoverageVerification> {

    violationRules {
        rule {

            // 커버리지 체크 단위
            // BUNDLE(default), PACKAGE, CLASS, SOURCEFILE, METHOD
            element = "CLASS"

            // 라인 커버리지 최소 80%
            limit {

                // 커버리지 측정 최소 단위
                // INSTRUCTION(default, JVM 명령어), LINE (빈 줄을 제외한 라인 수), BRANCH(조건문 분기 수),
                // CLASS(클래스 수), METHOD(한 클래스의 메서드 수), COMPLEXITY(복잡도)
                counter = "LINE"

                // 측정한 커버리지 표시 방식
                // COVEREDRATIO(default, 비율 0~1), MISSEDRATIO(커버되지 않은 비율),
                // TOTALCOUNT(전체 개수), MISSEDCOUNT(커버되지 않은 수), COVEREDCOUNT(커버된 수)
                value = "COVEREDRATIO"

                minimum = BigDecimal.valueOf(0.8)
            }

            // 메서드 커버리지 최소 70%
            limit {
                counter = "METHOD"
                value = "COVEREDRATIO"
                minimum = BigDecimal.valueOf(0.75)
            }

            // 커버리지 검증 제외 대상
            excludes = listOf(
                "**/dto/**",
                "**/vo/**",
                "**/config/**",
                "**/exception/**",
                "**/entity/**",
                "**/*Request.class",
            )

        }
    }
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

    testImplementation("org.mockito:mockito-core:5.15.2")
    testImplementation("org.mockito:mockito-junit-jupiter:5.15.2")
}