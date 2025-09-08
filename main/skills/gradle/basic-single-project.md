---
layout: default
title:
---

#### index
- [gradle project basic structure](#gradle-project-basic-structure)


## gradle project basic structure

[example basic project](https://github.com/hansanhha/hansanhha.github.io/tree/default/code/stack/gradle/examples/gradle%20basic%20project)

일반적인 그레이들 프로젝트의 디렉토리 구조는 아래와 같다 (gradle init 명령 사용 X)

```text
project directory (root project)
├─ .gradle
├─ build
├─ gradle
│   └─ wrapper
│       ├─ gradle-wrapper.jar
│       └─ gradle-wrapper.properties
├─ src
│   ├─ main
│   │   ├─ java
│   │   └─ resources
│   └─ test
│       ├─ java
│       └─ resources
├─ build.gradle.kts
├─ settings.gradle.kts
├─ gradlew
└─ gradlew.bat
```

### .gradle

gradle 빌드 캐시 및 설정 파일을 저장하는 디렉토리

`./gradlew clean` 명령으로 제거되지 않는다

### build

`./gradlew build` 명령을 통해 나온 빌드 산출물들이 저장되는 디렉토리

컴파일된 클래스 파일(classes), jar 파일(libs), 테스트 결과(test-results), 리포트(reports), 리소스(resources) 등이 포함된다

`./gradlew clean` 시 자동으로 삭제된다

### gradle

그레이들을 실행하기 위한 관련 파일들을 저장하는 디렉토리

wrapper: 그레이들 래퍼와 관련된 파일을 저장하는 디렉토리

gradle-wrapper.jar: 로컬에 그레이들을 설치하지 않아도 그레이들을 통해 프로젝트 빌드를 할 수 있게 도와주는 그레이들 래퍼 실행 파일

gradle-wrapper.properties: 그레이들 래퍼 설정 파일 - 그레이들 버전, 배포 url 등을 정의한다

그레이들 래퍼를 이용하여 ci/cd 파이프라인에서 그레이들을 자동 설치하여 빌드 자동화를 진행할 수 있다

### src

소스 코드 및 리소스 파일을 저장하는 디렉토리

src/main/java: 애플리케이션의 메인 소스 코드들이 저장되는 디렉토리

src/main/resources: 애플리케이션의 리소스 파일들이 저장되는 디렉토리

src/test/java: 테스트 코드들이 저장되는 디렉토리

src/test/resources: 테스트 시 필요한 리소스 파일들이 저장되는 디렉토리

### build.gradle.kts

프로젝트 빌드 설정 파일 (kotlin dsl)

프로젝트의 의존성, 플러그인, 태스크 등을 정의한다

groovy dsl 사용 시 build.gradle로 작성할 수 있다

### settings.gradle.kts

최상위 프로젝트 설정 파일 (kotlin dsl)

프로젝트 이름, 모듈(서브 프로젝트) 포함 설정, 빌드 스크립트 설정 등을 정의한다

빌드 스크립트와 마찬가지로 groovy dsl 사용 시 settings.gradle로 작성할 수 있다

### gradlew

unix 기반 시스템에서 사용하는 그레이들 래퍼 실행 스크립트

로컬에 그레이들을 설치하지 않아도 빌드를 실행할 수 있다 (`./gradlew build`)

### gradlew.bat

windows 시스템에서 사용하는 그레이들 그레이들 래퍼 실행 스크립트

gradlew의 윈도우 버전이다 `./gradlew.bat build`
