---
layout: default
title:
---

[gradle configurations](#gradle-configurations)

[gradle test task commands](#gradle-test-task-commands)


## gradle configurations

### gradle 기본 설정

#### junit 5 의존성 설정

```kotlin
dependencies {
    
    // junit 5 api 및 실행 엔진 추가
    testImplementation("org.junit.jupiter:junit-jupiter:${version}")
    
    // junit 4 테스트를 실행하는 경우
    testImplmentation("org.junit.vintage:junit-vintage-engine:${version}")
    
}
```

#### gradle test task의 junit 5 사용 설정

gradle의 test task를 수행할 때 junit 5 기반 테스트를 실행하기 위해 useJUnitPlatform()을 호출한다 (build.gradle.kts)

```kotlin
tasks.withType<Test> {
    useJUnitPlatform()
}
```

### gradle test task 추가 옵션 설정

```kotlin
tasks.withType<Test> {
    useJUnitPlatform()
    
    // 특정 테스트 클래스 제외
    exclude("**/SomeExcludedTest.class")
    
    // 특정 태그를 실행하거나 제외
    useJUnitPlatform {

        // account 태그를 가진 테스트 클래스만 실행
        includeTags("account")
        
        // orders 태그를 가진 테스트 클래스 제외
        excludeTags("orders")
    }
    
    // 테스트 실패 시 빌드 실패 여부 결정
    // 기본적으로 테스트가 실패하면 gradle 빌드도 실패한다
    ingnoreFailures = true
    
    // 테스트 실행 중간 결과 보기
    // 테스트 실행 중 각 테스트의 결과를 보고 싶을 때 추가하는 옵션
    testLogging {
        events("PASSED", "FAILED", "SKIPPED")
        // PASSED: 테스트 성공, FAILED: 테스트 실패, SKIPPED: 테스트 스킵
    }
    
    // 테스트 실행 중 콘솔 출력(logs, System.out.println)을 볼 수 있도록 설정
    // gradle은 기본적으로 테스트 실행 중에 콘솔 출력을 표시하지 않고, 테스트가 완료된 후에만 결과를 출력한다
    // 따라서 테스트 메서드에서 System.out.println을 사용해도 실행 중에는 보이지 않고, 테스트가 끝난 후 리포트에서만 확인할 수 있다
    testLogging {
        // STANDARD_OUT: System.out 출력, STANDARD_ERROR: System.err 출력
        events("PASSED", "FAILED", "SKIPPED", "STANDARD_OUT", "STANDARD_ERROR")
        
        // showStandardStreams 옵션을 활성화시켜서 볼 수도 있다
        showStandardStreams = true         
    }

    // cpu 코어 개수의 절반 만큼 병렬 실행
    maxParallelForks = Runtime.getRuntime().availableProcessors() / 2

    // 테스트가 성공적으로 실행된 경우 테스트 결과 캐싱
    outputs.cacheIf { true }
}
```

### gradle junit 5 + jacoco 코드 커버리지 측정

#### jacoco gradle 플러그인 추가

```kotlin
plugins {
    jacoco
}
```

#### jacocoTestReport task 설정

```kotlin
tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<JacocoReport> {
    dependsOn(tasks.withType<Test>())
    reports {
        html.required.set(true)
        xml.required.set(true)
    }
}
```

#### 커버리지 리포트 생성

`./gradlew jacocoTestReport`: `build/report/jacoco/test/html/index.html` 경로에 커버리지 리포트 생성

### gradle spring boot + junit 5

#### 스프링 부트 테스트 스타터 의존성 설정

```kotlin
dependencies {
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}
```

#### junit vintage engine 제외

스프링 부트 테스트 스타터는 기본적으로 junit 5를 지원하지만 junit vintage가 함께 포함되어 있기 때문에 junit 4 기반 테스트 실행을 방지하려면 별도로 제외해야 한다

```kotlin
configurations {
    testImplementation {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
}
```


## gradle test task commands

`./gradlew test`: test source set(`src/test/java` 디렉토리)에 있는 모든 테스트 실행

`./gradlew test --tests junit.hansanhha.ExampleTest`: 특정 테스트 클래스만 테스트 실행

`./gradlew test --tests junit.hansanhha.*`: 특정 패키지만 테스트 실행

`./gradlew test --rerun-tasks`: 이전 테스트 결과를 무시하고 모든 테스트 다시 실행

`./gradlew test --info`: 테스트 실행 로그를 자세히 출력하고 `build/reports/tests/test/index.html` 경로에 html 리포트 생성

`./gradlew test --console=plain`: 터미널 로그가 사라지지 않도록 유지