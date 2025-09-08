---
layout: default
title:
---

[plugin 설정](#plugin-설정)

[gradle 명령어](#gradle-명령어)

[커버리지 보고서 설정](#커버리지-보고서-설정)

[최소 코드 커버리지 기준 설정](#최소-코드-커버리지-기준-설정)

[build.gradle.kts](../build.gradle.kts)


## plugin 설정

```kotlin
plugins {
    java
    jacoco
}

jacoco {
    // gradle jacoco 플러그인의 기본 버전 대신 특정 버전이 필요한 경우 명시
    toolVersion = "0.8.12"
}
```


## gradle 명령어

jacoco는 테스트 실행 후 자동으로 커버리지 데이터를 생성한다

jacoco 플러그인의 jacocoTestReport gradle task를 통해 커버리지 결과를 볼 수 있다

기본 보고서 생성 위치: `build/reports/jacoco/test/html/index.html`

```shell
./gradlew test jacocoTestReport
```


## 커버리지 보고서 설정

jacoco 플러그인의 JacocoReport task를 통해 커버리지 보고서를 설정할 수 있다

커버리지 보고서 및 보고서 생성 제외 대상 설정

```kotlin
tasks.withType<JacocoReport> {

    // 커버리지 보고서 설정
    reports {
        // 기본 생성 위치: build/reports/jacoco/test/html/index.html
        html.required.set(true)
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
```


## 최소 코드 커버리지 기준 설정

최소 커버리지 기준을 설정하여 조건을 충족하지 못하면 빌드를 실패하게 만들 수 있다

jacoco 플러그인의 JacocoCoverageVerification task를 통해 커버리지 검증 기준을 설정할 수 있다
- 커버리지 체크 단위 설정 (element)
- 최소 커버리지 제한 설정 (limit)
- 커버리지 검증 제외 대상 설정 (excludes)

```kotlin
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
```




