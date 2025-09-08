---
layout: default
title:
---

#### index
- [build](#build)
- [build tool](#build-tool)
- [gradle](#gradle)
- [gradle and maven](#gradle-and-maven)
- [build workflow overview](#build-workflow-overview)
- [gradle wrapper and local gradle](#gradle-wrapper-and-local-gradle)
- [GRADLE_USER_HOME](#gradle_user_home)
- [gradle startup with daemon jvm](#gradle-startup-with-daemon-jvm)
- [gradle daemon jvm](#gradle-daemon-jvm)
- [build environment: customizing build process](#build-environment-customizing-build-process)
- [gradle shell script files](#gradle-shell-script-files)

#### reference
- [gradle basics](https://docs.gradle.org/current/userguide/gradle_basics.html)
- [how gradle works part 1 - startup](https://blog.gradle.org/how-gradle-works-1)
- [how gradle works part 2 - inside the daemon](https://blog.gradle.org/how-gradle-works-2)

## build

소프트웨어 빌드란 무엇일까?

개발자가 작성한 소스 코드가 IDE 상에서 일시적으로 동작하는 게 아니라 프로그램으로써 동작하도록 변환하는 과정 또는 그 결과물을 말한다

즉, 소스 코드를 실행할 수 있는 형태의 배포 가능한 소프트웨어 아티팩트(jar, exe 등)로 변환하는 것을 빌드라고 한다

일반적으로 빌드 과정은 다음을 포함한다

#### build process

**컴파일(compile)**: 소스 코드를 기계어, 바이트 코드 등으로 변환하는 과정 (.java -> .class)

**의존성 관리(dependency management)**: 필요한 외부 라이브러리 다운로드 및 설정

**리소스 관리(resource management)**: 코드 이외에 애플리케이션에서 필요로 하는 추가적인 파일 관리 (코드와 함께 패키징된다)

**테스트(test)**: 자동화된 단위 및 통합 테스트 실행

**패키징(packaging)**: 컴파일된 파일과 리소스를 하나의 아티팩트로 묶는 과정


## build tool

만약 빌드 도구 없이 자바 애플리케이션을 빌드한다면 얼마나 번거로울까?

[예시 자바 프로젝트](https://github.com/hansanhha/hansanhha.github.io/tree/default/code/stack/gradle/examples/no-build-tool%20java%20app)를 보면 .java 파일과 자바 컴파일러를 통해 컴파일부터 라이브러리 관리, jar 파일 생성 과정을 모두 수동으로 진행한 결과를 볼 수 있다

정말 간단한 자바 코드임에도 불구하고 여간 한 두 가지가 불편한 게 아니다

intellij에서 빌드 도구없이 순수 .java 파일로만 코드를 작성하면 코드 자동 완성, 자동 import문 작성 등의 기능을 사용하지 못할 뿐더러 컴파일 및 jar 파일 생성/실행 명령어를 실행하려면 모든 파일 경로를 명시해야 하기 때문에 오류 발생 가능성이 커지게 된다

또한 컴파일 후 자동으로 테스트를 실행하는 기능이 없기 때문에 개발자가 잊지 않고 항상 의무적으로 수행해줘야 하며, 마찬가지로 이 때 필요한 모든 외부 라이브러리 경로를 명시해야 한다

코드를 수정할 때마다 반복적으로 컴파일 후 테스트, 패키징 명령어를 각각 실행해야 하고 빌드 최적화 따위의 기능을 사용할 수 없다

어차피 빌드 도구 없이 개발할 일은 극히 드무니 여기까지만 지옥을 맛보고 빌드 도구에 대해서 알아보자

빌드 도구는 [빌드 과정](#build-tool)에 포함된 모든 기능을 제공할 뿐만 아니라 다음과 같은 부가적인 기능도 지원한다

- 애플리케이션 실행
- 빌드 도구 차원에서 자바 버전 관리(로컬에 설치된 자바 버전과 실행되는 애플리케이션의 자바 버전을 분리)
- 빌드 최적화: 증분 빌드, 캐싱

#### jvm 생태계의 빌드 도구들

gradle: 스크립트 언어 dsl, task 기반 빌드 도구

maven: xml, 목표 기반 선언적 빌드 도구

ant: xml, 절차적 기반 빌드 도구

sbt: scala, apache spark, play framework 등에서 사용하는 빌드 도구

leiningen: clojure에서 사용하는 빌드 도구

buildr: jruby에서 사용하는 빌드 도구

pants: java, kotlin 및 go, python 등의 언어를 지원하며 대규모 모노 레포 프로젝트에서 사용하는 빌드 도구


## gradle

그레이들은 ant와 maven의 단점을 개선하고 성능과 유연성을 강조한 오픈 소스 빌드 도구로 프로젝트 빌드(컴파일 - 테스트 - 실행), 의존성 관리, 배포까지 전반적인 빌드 라이프사이클을 관리하는 빌드 자동화 도구이다

주로 jvm 언어 기반 프로젝트 빌드에서 사용되지만 c++과 swift 기반의 프로젝트 빌드도 지원하고 있다

[gradle github 리포지토리](https://github.com/gradle/gradle?tab=readme-ov-file#-gradle-build-tool)에서 소개하는 그레이들에 대한 전반적인 설명은 다음과 같다

```text
그레이들은 대규모, 멀티 프로젝트 엔터프라이즈 빌드부터 다양한 언어를 통한 빠른 개발 작업에 이르기까지 모든 것을 처리하도록 설계된 확장성이 뛰어난 빌드 자동화 도구다
그레이들의 모듈식 성능 중심(modular performance-oriented) 아키텍처는 개발 환경과 원활하게 통합되므로 java, kotlin, scala, android, groovy, c++ 및 swift에서 애플리케이션을 구축, 테스트 및 배포하기 위한 솔루션으로 활용된다
```

### gradle main features

- flexible dsl: 프로젝트의 빌드는 그레이들에서 지원되는 빌드 스크립트를 통해 동작하는데, 개발자는 groovy 또는 kotlin dsl을 사용하여 빌드 스크립트를 작성할 수 있다
- dependency management: maven, ivy 저장소와 호환되어 외부 라이브러리를 쉽게 관리할 수 있고 private 저장소를 사용할 수도 있다
- build optimization: 증분 빌드(incremental build), 빌드 캐시, 데몬을 통해 빠른 빌드를 지원한다
- multi-project build: 단일 프로젝트 뿐만 아니라 다중 프로젝트 빌드도 지원한다
- plugin system: 플러그인을 통해 빌드의 기능을 확장할 수 있다 (그레이들은 자체적으로 제공하는 플러그인과 누구나 자유롭게 만들 수 있는 커뮤니티 플러그인, 커스텀 플러그인을 지원한다)
- task-based build process: 빌드 프로세스를 작업(task) 단위로 관리한다 (작업의 설정 값을 통해 동작을 변경하거나 작업 간의 의존성을 설정할 수 있으며 플러그인을 통해 커스텀 작업을 추가할 수도 있다)
- build automation on continuous integration: ci/cd 파이프라인에서 자동화된 테스트, 패키징, 배포 기능을 활용할 수 있다


## gradle and maven

그레이들과 메이븐은 프로젝트 빌드, 테스트, 배포 과정을 자동화하는 빌드 자동화 도구다

메이븐은 2004년 아파치 프로젝트로 시작했으며, 그레이들은 그로부터 4년 뒤인 2008년에 메이븐의 단점과 한계를 보완하는 유연한 빌드 시스템을 목표로 시작했다  

이제 자바의 대표적인 두 빌드 도구 간의 차이점과 그레이들의 비밀에 대해서 알아보자

### xml vs kotlin/groovy

메이븐과 그레이들의 가장 큰 차이점은 dsl 방식에서 비롯된다

메이븐은 xml 기반(pom.xml)으로 선언적인 구성을 하고 그레이들은 코틀린, 그루비 dsl을 활용하여 유연하고 강력한 확장성을 가진 빌드 로직을 작성한다

메이븐의 xml 설정은 그레이들에 비해 확실히 장황하며 유연성이 부족하다 (선언적인 방식을 선호하는 개발자는 메이븐의 xml이 더 익숙할 것이다. 재미있는 점은 그레이들도 메이븐처럼 선언적인 방식으로 빌드 로직을 구성할 수 있는 기능(declarative gradle)을 제공한다)

반면 그레이들은 프로그래밍 방식으로 빌드 스크립트를 제공하여 빌드 로직의 유연성을 제공한다

maven pom.xml 

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
        <version>3.2.2</version>
    </dependency>
</dependencies>
```

gradle build.gradle.kts

```kotlin
dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web:3.2.2")
}
```

### build optimization

그레이들은 이전 빌드와 비교하여 변경된 파일만 빌드하는 incremental build 기능, 태스트 병렬 실행, 빌드 캐시 등 강력한 빌드 최적화 기능을 선보인다

반면 메이븐은 빌드를 할 때마다 매번 전체 태스크를 실행하고, 병렬 실행을 할 수 없으며 빌드 캐시 따위가 존재하지 않는다

이 부분에 관해선 그레이들이 메이븐보다 우수하다고 볼 수 있다

### plugin system

메이븐과 그레이들은 모두 플러그인 시스템을 기반으로 동작한다

다만 빌드 도구 차원에서 제공하는 기본 플러그인은 그레이들이 메이븐에 비해 많으며, 이 밖에도 그레이들은 buildSrc 또는 컨벤션 플러그인을 활용하여 커스텀 플러그인을 만들기 쉽지만 메이븐은 커스텀 플러그인을 상대적으로 작성하기 어렵다

### maven repository, artifact

그레이들은 maven central과 같은 메이븐 리포지토리와 메이븐 아티팩트 구조 등 기존 메이븐 생태계의 일부분을 그대로 활용한다

메이븐이 먼저 운영된 빌드 도구인 만큼, maven central 리포지토리는 이미 상당한 수의 오픈소스 라이브러리를 저장하고 있다

새로운 중앙 리포지토리를 운영하려면 저장 공간, 트래픽 등과 같은 비용 문제가 발생한다

그리고 그레이들이 자체 리포지토리를 운영하려면 기존 메이븐 사용자들에게 자신의 리포지토리를 추가하도록 강제해야 한다

이는 비용 문제와 더불어 개발자 경험 개선을 해치거나 자바 생태계의 아티팩트 관리 문제를 불러 일으킬 수 있다

그레이들은 메이븐 리포지토리를 그대로 사용하면서도 빌드 시스템만 개선하는 전략을 채택했고, 덕분에 기존 생태계를 깨지 않으면서도 그레이들의 기능을 제공함으로써 개발자들은 어느 빌드 도구나 자유롭게 선택하고 같은 리포지토리를 공유하면서 작업할 수 있게 된다

또한 메이븐의 아티팩트 구조(groupId:artifactId:version) 형식을 그대로 사용하여 일관된 아티팩트 관리를 가능하게 했다


## build workflow overview

그레이들은 빌드 스크립트의 내용을 기반으로 빌드, 테스트, 소프트웨어 배포 등을 자동화하는 메커니즘을 구현한다

빌드 과정에 참여하는 구성 요소는 크게 세 가지로 이뤄진다

**프로젝트**: 애플리케이션이나 라이브러리 같은 소프트웨어로 빌드될 수 있는 코드, 리소스 파일 등을 포함한 프로젝트

**그레이들**: 빌드를 수행하는 주체로서 빌드를 구성, 실행, 캐시하거나 의존성 다운로드/캐싱 등의 기능을 제공한다

**결과물**: 테스트 결과(리포트 포함), 빌드 결과물(아티팩트, .jar/.apk/.zip/.war 등)

#### 1. gradle or gradle wrapper

그레이들이 시작하는 단계로 그레이들 래퍼를 통해 버전 고정 및 일관된 빌드 환경을 제공한다

```text
gradle <task>
./gradlew <task>
```

사용자가 그레이들 명령어를 통해 빌드를 시작하는 단계로 그레이들 래퍼를 사용하면 정의된 버전에 따라 로컬에 그레이들을 자동으로 다운받는다 (그레이들 버전 관리 가능)

#### 2. initialize gradle

빌드를 수행할 기본 환경을 구성하는 단계로 그레이들을 실행할 자원을 준비한다

gradle.properties, init.gradle 등 전역(로컬) 및 프로젝트 설정 파일을 로드한 뒤 필요한 gradle 버전과 jvm, os, 사용자 환경 설정을 확인한다

#### 3. start daemon process

빌드 속도를 향상시키기 위해 그레이들 데몬을 연결하는 단계

--no-daemon 옵션이 주어지지 않은 이상 그레이들은 기존에 실행 중인 idle 상태의 daemon과 연결을 시도한다

사용 가능한 daemon이 없는 경우 새로운 데몬을 생성한다

만약 후보 daemon과 jvm 옵션 등이 일치하지 않으면 일회용 dameon을 생성한다 (일회용 데몬은 해당 빌드가 끝난 후 자동으로 종료된다)

#### 4. initialize project

빌드에 참여하는 모든 프로젝트의 구조와 기본 속성을 결정하는 단계

Settings 인스턴스를 만들고 settings.gradle(.kts) 스크립트 파일을 파싱하여 싱글/멀티 프로젝트의 구조와 포함된 서브 프로젝트들을 평가한다

빌드에 참여하는 각 프로젝트에 대한 Project 인스턴스를 생성하고 기본 설정을 적용한다

#### 5. configure tasks & resolve dependency

모든 필요한 리소스(의존성, 플러그인 등)와 태스크 구성을 완료하는 단계로 실제 빌드를 수행할 준비를 마친다

각 프로젝트의 build.gradle(.kts) 스크립트 파일을 파싱하여 태스크들을 구성하고 태스크 의존관계에 따른 태스크 그래프를 생성한다

그리고 dependency resolution 메커니즘이 작동하여 프로젝트에서 사용하는 의존성(라이브러리, 플러그인)을 maven central, 기타 원격/로컬 저장소에서 다운로드하거나, 로컬에 캐시되어 있는 의존성을 클래스패스에 포함시킨다

#### 6. execute the tasks

필요한 빌드 작업을 효율적으로 수행하고 최종 빌드 결과물을 생성하는 단계

태스크 그래프에 따라 태스크 실행 계획을 수립하고 의존 관계 순서에 따라 각 태스크를 실행한다

태스크는 병렬적으로 실행될 수 있으며, 실행 전에 해당 태스크의 입력(파일, 설정 등)과 캐시된 입력(이전 빌드에서 캐시된 입력)을 비교하여 값이 같으면 태스크를 재실행하지 않고 이전 결과를 재사용(UP-TO-DATE)한다 ([자세한 내용](./incremental build))

소스 코드를 컴파일하거나 테스트 실행, 코드 분석, 리포지토리 패키징 등의 작업이 여기서 수행된다

#### 7. post execution: cache, publish artifact, report, cleanup

빌드 결과를 저장하고 최종 산출물을 배포 준비 상태로 만드는 단계

실행된 태스크의 결과는 다음 빌드에서 동일한 입력이 있을 경우 재사용하기 위해 캐시에 저장한다

아티팩트(jar, war, exe 등)가 생성되면 publishing 태스크(`publish` `uploadArchives`)를 통해 지정된 위치(로컬 디렉토리, 로컬/원격 리포지토리 - nexus/artifactory, ci 서버)에 저장한다

빌드가 완료되면 빌드 결과, 테스트 결과, 코드 분석 리포트 등을 생성하여 로그 파일 및 보고서 형태로 저장한다

daemon은 idle 상태로 전환되어 후속 빌드를 위해 일정 시간(기본값: 3시간) 동안 빌드 요청을 대기한다

그리고 임시 파일, 로그, 캐시 데이터는 다음 빌드를 위해 유지되거나 옵션에 따라 정리한다


## gradle wrapper and local gradle

gradle wrapper는 로컬에 설정된 버전의 그레이들이 설치되지 않은 경우 그레이들을 인터넷을 통해 다운받아서 프로젝트 빌드를 가능하게 해준다

이 특징은 ci/cd 파이프라인 환경에서 그레이들과 관련된 환경 설정을 하지 않아도 프로젝트를 빌드할 수 있도록 자동적으로 그레이들을 다운받는 유용한 기능을 할 뿐만 아니라 프로젝트에 참여하는 모든 팀원이 동일한 버전의 그레이들을 사용하도록 강제할 수 있다

덕분에 새로운 팀원이 프로젝트에 참여해도 해당 팀원의 로컬에 설치된 그레이들의 버전과 별개로 래퍼에 설정된 그레이들 버전을 사용할 수 있다

#### gradle wrapper version properties

`gradle/wrapper/gradle-wrapper.properties` 파일에서 그레이들 래퍼가 사용할 버전을 설정할 수 있다

```properties
# gradle-8.4-bin.zip: 그레이들 8.4 버전
distributionUrl=https\://services.gradle.org/distributions/gradle-8.4-bin.zip
```

그렇다면 그레이들 래퍼를 통해 다운로드된 그레이들이 저장되는 위치는 어디일까?

기본적으로 [GRADLE_USER_HOME](#gradle_user_home)이라고 하는 그레이들 루트 디렉토리를 가리키는 환경 변수 값 아래의 wrapper/dists 에 저장된다 (`GRADLE_USER_HOME/wrapper/dists`)

이 디렉토리 아래에 다운로드된 각 그레이들에 대해 `gradle-<version>-bin` 형식으로 버전에 따라 디렉토리가 생성되고 실행 파일을 포함한 그레이들과 관련된 파일들이 저장된다 (`GRADLE_USER_HOME/wrapper/dists/gradle-8.4-bin/`)

이미 다운로드된 그레이들 버전으로 설정된 래퍼를 이용하면 추가 다운로드 없이 저장된 그레이들 실행 파일을 사용하므로 네트워크 연결 없이도 그레이들을 이용할 수 있다

그레이들 래퍼를 사용하지 않고 사용자가 직접 로컬에 그레이들을 설치하면 `gradle init` 명령으로 초기 프로젝트를 생성/설정하거나 특정 프로젝트와 무관하게 그레이들을 사용할 수 있다


## GRADLE_USER_HOME

GRADLE_USER_HOME 환경 변수는 로컬의 그레이들 루트 디렉토리를 가리키는 환경 변수이다

기본적으로 그레이들 루트 디렉토리는 사용자 홈 디렉토리의 .gradle 폴더를 말한다

windows: `C:\Users\{username}\.gradle`

macos/linux: `/Users/{username}/.gradle` `/home/{username}/.gradle`

그레이들 루트 디렉토리에는 그레이들의 전역 설정 파일, 캐시, 래퍼 다운로드 파일 등이 저장되며 상세한 구조는 아래와 같다

```text
.gradle/
├── build-cache/            # gradle 빌드 캐시 (빌드 속도 향상)
├── daemon/                 # gradle daemon의 실행 파일 및 로그
├── native/                 # 네이티브 라이브러리 캐시 (jni 사용 시)
├── notifications/          # gradle 알림 캐시
├── wrapper/                # gradle wrapper 다운로드 및 압축 해제 디렉터리
│   └── dists/
│       └── gradle-8.4-bin/
│           └── <해시값>/
│               ├── gradle-8.4-bin.zip   # gradle 패키지 (다운로드된 zip 파일)
│               └── gradle-8.4/          # 압축 해제된 gradle 실행 파일
|
├── caches/                 # 의존성 캐시 (maven, ivy 저장소의 라이브러리)
│   ├── modules-2/
│   │   └── files-2.1/
│   │       └── org.springframework/spring-core/5.3.29/
│   │           ├── spring-core-5.3.29.jar       # jar 파일
│   │           └── spring-core-5.3.29.pom       # pom 파일
│   └── jars-*             # 로컬 저장소의 jar 파일 캐시
└── gradle.properties       # 전역 gradle 설정 파일
```

#### build cache

그레이들 빌드 캐시를 저장하는 디렉토리

동일한 입력 값에 대해 이전 빌드 결과를 재사용하여 빌드 속도를 높인다

#### daemon

그레이들 데몬의 실행 상태와 로그를 관리하는 디렉토리

#### native

그레이들에서 사용하는 네이티브 라이브러리(jni, c/c++)를 캐시하는 디렉토리

#### wrapper

그레이들 래퍼를 통해 다운로드된 그레이들을 저장하는 디렉토리

#### caches

의존성 라이브러리(maven, jcenter 등)에서 받은 jar/pom을 저장하는 라이브러리

groovy dsl, kotlin dsl로 작성된 빌드 스크립트에 대한 class 파일, gradle api jar 파일 등도 포함된다

캐시를 사용해 네트워크 요청을 줄이고 빌드 시간을 단축한다

#### gradle.properties

그레이들의 전역 설정 파일

프록시 설정, jvm 옵션, 캐시 정책 등을 정의할 수 있다


## gradle startup with daemon jvm

참고: [(gradle official blog post) how gradle works 1- startup](https://blog.gradle.org/how-gradle-works-1)

일반적으로 그레이들은 세 가지 방법으로 실행될 수 있다

1. cli에서 로컬에 직접 설치한 gradle 사용
2. cli에서 gradle wrapper 사용
3. ide를 통한 실행(그레이들 프로젝트 import 또는 특정 테스트나 태스크 실행)

### local gradle distribution in cli

```shell
gradle <task name>
```

![local gradle shell script file](./assets/local%20gradle%20shell%20script%20file.png)

위와 같이 gradle 명령어를 cli를 통해 입력하면 로컬에 직접적으로 설치한 해당 버전의 gradle 쉘 스크립트 파일(gradle 또는 gradle.bat)이 실행된다

쉘 스크립트 파일은 주로 java 명령어를 찾고, 파라미터를 결정하는 로직으로 이뤄져 있고 맨 마지막에 `exec "$JAVACMD" "$@"` 명령어를 통해 jvm을 실행시킨다

동일한 프로세스 내(해당 쉘 스크립트를 실행하는 프로세스)에서 exec 명령어를 실행하기 때문에 현재 프로세스는 jvm 프로세스로 대체된다

이 프로세스를 gradle client jvm이라고 부르고 [org.gradle.launcher.GradleMain 클래스](https://github.com/gradle/gradle/blob/acc6044325b11874e9626d98dec976a0e495cb62/subprojects/bootstrap/src/main/java/org/gradle/launcher/GradleMain.java)를 호출한다

![local gradle daemon stratup](./assets/local%20gradle%20daemon%20startup.png)

exec 명령으로부터 시작된 gradle client jvm은 실제 빌드 로직을 실행하지 않고 호환되는 gradle daemon을 탐색한 뒤 찾았다면 로컬 소켓을 통해 연결한다

그 후 gradle 명령어를 실행하면서 받은 입력값들(cli 인자, 환경 변수, stdin 등)을 gradle daemon jvm에게 전달한다

gradle daemon jvm이 실질적으로 빌드 로직을 수행하고 그 결과(stdout/stderr)를 gradle client jvm에게 응답한다

빌드가 종료되면 grade client jvm 프로세스는 종료되고 gradle daemon jvm 프로세스는 idle 상태로 전환되어 후속 빌드 요청을 기다린다

#### --no-daemon

[--no-daemon](#no-daemon)

### gradle wrapper in cli

```shell
./gradlew <task name>
```

![gradlew wrapper shell script file](./assets/gradle%20wrapper%20shell%20script%20file.png)

그레이들 래퍼(gradlew, gradlew.bat)도 gradle 파일처럼 쉘 스크립트 파일이며 인터넷을 통해 그레이들 원격 서버로부터 설정된 버전의 그레이들을 다운받은 뒤 [local gradle](#local-gradle-distribution-in-cli)과 동일한 메커니즘으로 동작한다

다만 GradleMain 클래스 대신 [GradleWrapperMain 클래스](https://github.com/gradle/gradle/blob/acc6044325b11874e9626d98dec976a0e495cb62/subprojects/wrapper/src/main/java/org/gradle/wrapper/GradleWrapperMain.java)를 시작으로 한다는 점의 차이가 있다 

### gradle in ide

cli의 경우 gradle 또는 gradlew 쉘 스크립트 파일을 실행하는 프로세스가 exec 명령에 의해 gradle client jvm로 대체되는 메커니즘으로 그레이들이 실행된다

ide에서는 이와 달리 동적으로 gradle jar 파일을 로드하는 jvm을 gradle client로 취급하여 데몬을 검색하고 연결하거나 새 데몬을 시작하는 등 데몬과 통신하게 할 수 있다

그레이들에서는 이 프로그래밍 방식의 api를 tooling api라고 한다

![ide gradle startup](./assets/ide%20gradle%20startup.png)

intellij idea에서 gradle sync 버튼을 클릭하면 idea는 tooling api를 통해 빌드에 필요한 정보(프로젝트 구조, 의존성, 태스크 등)들을 가져오기 위해 gradle 빌드를 시작한다

이 때 tooling api는 빌드 결과를 읽고 호출자(idea)에게 반환하는 역할을 할 뿐, 실제 모든 빌드 로직은 gradle daemon jvm에서 실행된다


## gradle daemon jvm

[--no-daemon](#no-daemon) 인자와 함께 그레이들이 실행되지 않는 이상 기본적으로(3.0 버전 이상부터) 그레이들은 gradle client jvm(shell script file, tooling api)이 gradle daemon jvm에게 빌드 로직을 위임한다 

즉, 그레이들을 실행하면 최소 2개의 그레이들 jvm 프로세스가 로컬에 띄워지는다는 말인데 (idle 상태의 데몬이 많아질수록 그레이들 jvm 프로세스는 증가한다) 그레이들은 데몬을 굳이 사용하는 이유가 무엇일까?

[예시](./what is gradle#build-tool)에서 볼 수 있다시피 빌드 도구는 프로젝트 개발에 필수적인 요소로 기능(api 등)을 구현하는 과정에서 컴파일, 테스트 등을 수행하기 위해 반복적으로 빌드 도구를 사용할 수 밖에 없다

이러한 반복적인 빌드 수행 시간을 줄이고자 그레이들은 데몬을 사용하는 것이다

데몬은 다음과 같은 기능을 제공하여 빌드 시간을 줄인다

1. 빌드 간 프로젝트 정보를 캐시한다
2. 백그라운드 프로세스로 동작하여 그레이들이 매번 jvm 실행을 위해 기다릴 필요가 없어진다
3. jvm에서 지속적인 런타임 최적화를 제공한다
4. 빌드를 실행하기 전, 파일 시스템을 평가하여 어떤 부분이 리빌드해야 되는지 파악한다 (해당 부분만 실행하고 나머지는 이전에 캐시된 결과값을 그대로 사용하여 불필요한 빌드 작업을 줄인다)

### how daemon works

![client connects to daemon](./assets/client%20connects%20to%20daemon.png)

데몬의 전반적인 동작 방식은 아래와 같다

1. 로컬 소켓을 통해 클라이언트(gradle client jvm)으로부터 프로젝트 디렉토리, cli 인자, 환경 변수 등을 전달받는다
2. 데몬은 빌드를 실행한 뒤 빌드의 출력 값(logging, stdout/stderr)을 클라이언트에게 응답한다
3. idle 상태로 전환하여 클라이언트의 후속 빌드 요청을 기다린다

#### 1. initialization phase: creation of build objects

데몬은 클라이언트를 통해 빌드에 필요한 모든 정보를 전달받은 뒤, 내부적으로 빌드를 자바 객체로 생성하여 표현한다 (그레이들은 jvm에서 실행되기 때문에 자바 객체로 표현한다)

[Gradle 객체](https://github.com/gradle/gradle/blob/ba32027bf0656be5c8a71e6281939ff410a9cf1a/subprojects/core-api/src/main/java/org/gradle/api/invocation/Gradle.java): 전체 그레이들 빌드

[Settings 객체](https://github.com/gradle/gradle/blob/fd341b1e7016ff0ba82995b4e3211fb6e6805dd4/subprojects/core-api/src/main/java/org/gradle/api/initialization/Settings.java): 프로젝트 계층 구조를 구성하는 데 필요한 설정

[Project 객체](https://github.com/gradle/gradle/blob/6121fa83ce4ac07a27ee043d8e69b0f5f99d1c49/subprojects/core-api/src/main/java/org/gradle/api/Project.java): 빌드에 참여하는 각 프로젝트

![creating build instances](./assets/creating%20build%20instances.png)

위 객체들은 init, settings, build script에서 호출하는 메서드 등의 기본 위임 대상이 된다 (init - Gradle, settings - Settings, build script - Project)

빌드 스크립트에서 println(name) 메서드를 호출하면 실제로는 Project 인스턴스의 Project.getName() 메서드를 호출하게 된다

#### 2. configuration phase: build script execution

첫 번째 페이즈에서 빌드 실행에 필요한 자바 객체 생성을 마치면 두 번째 페이즈에서 그레이들은 데몬에 있는 빌드 스크립트(클라언트로 전달받은)를 로드하고 실행한다

일반적으로 빌드 스크립트는 프로젝트 디렉토리에 위치하고 `*.gradle`(groovy dsl) 또는 `*.gradle.kts` (kotlin dsl) 형식의 이름을 가진다

groovy dsl 빌드 스크립트의 경우 작성된 내용에 따라 [Groovy 인스턴스](https://groovy-lang.org/closures.html)를 만들고 이전 페이즈에서 미리 생성된 Project 인스턴스의 메서드를 호출하여 클로저를 전달함으로써 빌드 스크립트를 실행하게 한다

아래 예시의 repositories의 코드 블록인 `mavenCentral()` 을 호출하는 부분이 클로저 인스턴스가 되며 `Project.repositories(Closure)` 메서드로 전달된다

```groovy
repositories {
    mavenCentral()
}
```

이렇게 그레이들은 위에서 아래로 라인별로 빌드 스크립트를 실행하는 인터프리터로써 동작한다

![configuration build script execution](./assets/configuration%20build%20script%20execution.png)

또한 빌드 스크립트의 내용을 해석하면서 데몬 jvm 내부에 빌드에 대한 datastructure를 구성하는데, 다음과 같이 태스크를 등록하는 로직은 그레이들의 TaskContainer라고 하는 태스크 컨테이너 자료구조에 등록된다

등록된 태스크는 이후 필요한 시점에 Task 인스턴스로 생성되어지며, 이 밖에도 빌드 스크립트를 실행하면서 평가된 내용을 바탕으로 태스크 실행에 필요한 다양한 자료 구조를 구성하는 과정이 이어진다 

```kotlin
tasks.register("greeting") {
    doLast {
        println("hello world")
    }
}
```

빌드 스크립트 실행이 종료되면 빌드에 필요한 필수 데이터를 기반으로 빌드 자료 구조가 구성되며 이것들을 기반으로 다음 페이즈에서 태스크를 실행한다

#### 3. execution phase: execution of selected tasks

빌드 스크립트를 실행하는 이전 페이즈에서 모든 빌드 자료구조 구성을 마쳤다면 실제 태스크를 실행할 단계에 도달한다

그레이들은 모든 태스크를 실행하는 것이 아니라 효율적인 빌드 실행을 위해 태스크 집합을 선별하기 위해 gradle/gradlew 명령어와 함께 주어진 인자를 기반으로, 실행할 태스크 집합을 구성하고 선택된 각 작업들을 실행한다

![daemon execution phase](./assets/daemon%20execution%20phase.png)

각 태스크는 실행할 메서드(코드 청크)로 구성된 action 목록(@TaskAction)을 가진다

java 플러그인을 통해 추가되는 `Test` 태스크가 실행하는 action은 다음과 같다

```java
public abstract class Test extends AbstractTask implements JavaForkOptions, PatternFilterable {
    
    
    @Override
    @TaskAction
    public void executeTests {
        // ...
    }
}
```

태스크가 실행됐다는 의미는 데몬 jvm에서 그 태스크를 꾸리는 action의 코드가 실행됐다는 것을 뜻한다

태스크 액션은 일반적으로 데몬 jvm에서 실행되지만, 새로운 jvm으로 fork하여 해당 jvm에서 코드를 실행시킬 수 있다

아래의 그림과 같이 Test 태스크는 데몬 jvm으로부터 몇 개의 jvm으로 fork되어 테스트 코드를 실행할 수 있다

![daemon forks test jvms](./assets/daemon%20forks%20test%20jvms.png)

빌드가 종료되면 데몬은 콜백 실행, 에러 리포팅, 빌드 스캔 발행 등의 후처리 작업을 진행한다

이후 gradle client jvm은 데몬과 로컬 소켓 연결을 해제하고 종료되며, 데몬은 후속 빌드 요청을 대기하는 idle 상태로 전환된다

### no-daemon

gradle, gradlew에 모두 해당되는 내용이다

만약 cli 인자에 --no-daemon을 추가하거나 gradle.properties 파일에 아래와 같이 설정한 뒤 명령어를 실행하면 그레이들은 곧장 데몬을 실행하지 않는 게 아니라 빌드 요구사항의 호환성을 확인한다

```properties
org.gradle.daemon=false
```

즉, gradle client jvm이 받은 빌드 관련 설정 값과 빌드에 필요한 설정 값이 일치하는지 확인하는데 이 때 일치한다면 daemon 프로세스를 생성하지 않고 gradle client jvm에서 빌드를 실행한다

![local gradle no-daemon startup](./assets/local%20gradle%20no-daemon%20startup.png)

만약 그렇지 않다면 일회용 daemon 프로세스를 생성한 뒤 사용한다

일회용 daemon jvm은 해당 gradle client jvm에서만 사용되고 빌드가 끝나면 자동적으로 종료된다

![local gradle single-use daemon startup](./assets/local%20gradle%20single-use%20daemon%20startup.png)

그렇다면 데몬을 사용하는 것과 사용하지 않는 것에 어떤 차이가 있을까?

데몬 사용 시 다음과 같은 이점을 누릴 수 있다
- 반복 빌드 성능 향상
- 리소스 재사용 (tooling api, 메모리 풀)
- 캐시 및 jvm warm-up 효과

리소스 재사용 및 캐시 기능은 반복적인 빌드를 하는 환경에서 성능을 향상시키기엔 좋지만 ci 서버나 도커같이 깨끗한 환경을 요구하는 경우 이전 빌드의 상태를 잔존하여 **환경 불일치 문제**를 일으킬 수 있다

또한 ci 파이프라인에서 병렬 빌드가 일어나면 데몬이 여러 개 띄워지면서 메모리와 cpu 리소스의 사용량을 꽤나 차지하거나 도커 컨테이너 내에 불필요한 데몬 프로세스가 남아 리소스를 낭비할 수도 있다

데몬을 사용하지 않으면 다음과 같은 이점을 누릴 수 있다
- 깨끗한 빌드 환경 보장
- 메모리 안정성
- 병렬 빌드 안정성

다만 느린 빌드 초기화로 인해 의존성 캐시 초기화와 빌드 스크립트 컴파일에 상대적으로 시간이 오래 걸리고, 그레이들의 툴링 api나 캐시를 활용할 수 없다

장기 실행되는 ci 서버에서 --no-daemon 옵션을 사용하면 빌드 캐시 활용을 할 수 없게 되어 빌드 속도가 느려질 수 있다

각 방식에 장단점이 있기에 요구사항에 맞춰 데몬 사용 여부를 결정하는 게 올바르다고 볼 수 있다

데몬을 사용하기 적합한 상황
- 개발 환경에서 로컬 빌드 속도를 빠르게 하고 싶을 때
- 장기 실행되는 CI 서버에서 동일한 환경에서 반복적인 빌드를 수행할 때

데몬을 사용하지 않아도 되는 상황
- ci/cd 파이프라인에서 깨끗한 빌드 환경을 유지하고 싶을 때
- 도커 빌드 시 컨테이너가 항상 동일한 초기 상태에서 시작해야 할 때
- 단발성 빌드 작업이나 스케줄러를 통한 빌드처럼 jvm을 재사용할 필요가 없는 경우

### multiple daemon process

그레이들 데몬이 여러 개 띄워질 수 있는 시나리오는 다음과 같다 [gradle daemon compatibility](https://docs.gradle.org/8.10/userguide/gradle_daemon.html#deamon_compatibility)

- 서로 다른 그레이들 버전을 사용할 때: 프로젝트마다 그레이들 버전이 다르면 그레이들은 각 버전에 맞는 데몬을 실행한다
- 다른 jvm 옵션을 사용할 때: org.gradle.jvmargs가 서로 다른 경우에 새로운 데몬이 실행된다
- 병렬 빌드를 사용할 때: 단일 프로젝트 내에서 병렬 task 실행을 위해 여러 데몬이 실행될 수 있다
- 기존 데몬이 사용 중일 때: 현재 실행 중인 데몬이 idle이 아닌 busy 상태인 상황에서 새로운 빌드 요청이 추가되면 추가 데몬이 시작된다
- 데몬이 비정상적인 상태일 때: 메모리 부족, 프로세스 충돌 등으로 인해 기존 데몬을 사용할 수 없는 경우 새로운 데몬이 시작될 수 있다

### daemon management

#### stop daemon

기본적으로 데몬은 아래의 조건 중 하나라도 만족하면 자동으로 종료된다
- 잔여 시스템 메모리가 적은 경우
- 데몬의 idle 상태가 3시간이 지난 경우

또는 아래의 명령어를 사용하여 직접 데몬 프로세스를 종료할 수 있다 (해당 명령어를 실행하는 그레이들 버전과 동일한 버전의 데몬 프로세스만 종료된다)

```shell
gradle --stop
```

#### find daemons

아래의 명령어는 명령어를 실행한 그레이들 버전과 동일한 버전으로 실행 중인 데몬 프로세스의 상태를 출력한다

```shell
gradle --status
```

```text
$ ./gradlew --status 
   PID STATUS   INFO
 83366 IDLE     8.10
```

특정 버전과 상관없이 실행 중인 모든 그레이들 데몬 프로세스를 보고 싶다면 jdk에서 제공하는 jps 명령어로 확인할 수 있다

```shell
$ jps
7509 
99652 Jps
83366 GradleDaemon
65438 Main
```


## build environment: customizing build process

그레이들은 빌드 환경을 구성할 수 있는 다양한 메커니즘을 통해 빌드 프로세스 커스터마이징을 지원한다

메커니즘은 설정 사항의 적용 대상과 범위에 따라 구분되며 그 목록은 아래와 같다

| 메커니즘                  | 설명                                                          | 예시                                                                                                          |
|-----------------------|-------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------|
| cli                   | 빌드 또는 그레이들 기능을 지정하는 커맨드라인 플래그                               | --rerun                                                                                                     |
| project properties    | 특정 그레이들 프로젝트의 프로퍼티 (system, gradle properties를 모두 설정할 수 있음) | ./gradlew build -PmyProperty='hello' (system), myProperty='hello' (gradle.properties)                       |
| system properties     | 그레이들 런타임(jvm)에 전달할 프로퍼티                                     | ./gradlew build -Dgradle.wrapperUser=myuser (cli), systemProp.gradle.wrapperUser=myuser (gradle.properties) |
| gradle properties     | 그레이들 설정과 빌드를 실행하는 자바 프로세스의 프로퍼티                             | ./gradlew build -Dorg.gradle.caching.debug=false (cli), org.gradle.caching.debug=false (gradle.properties)  |
| environment variables | 빌드 기능을 지정하는 환경 변수                                           | GRADLE_HOME, JAVA_OPTS, GRADLE_OPTS, JAVA_HOME                                                              |

메커니즘 별 우선순위 (위에 있을수록 높은 우선순위를 가지며 낮은 우선순위와 중복되는 값을 덮어쓴다)
- cli
- system properties (프로젝트 루트 디렉토리의 gradle.properties)
- gradle properties (GRADLE_USER_HOME의 gradle.properties, 프로젝트 루트 디렉토리의 gradle.properties, GRADLE_HOME의 gradle.properties )
- environment properties

[cli flags](https://docs.gradle.org/current/userguide/build_environment.html#sec:gradle_command_line_flags)

[environment variables](https://docs.gradle.org/current/userguide/build_environment.html#sec:gradle_environment_variables)

[project properties](https://docs.gradle.org/current/userguide/build_environment.html#sec:project_properties)

[system properties](https://docs.gradle.org/current/userguide/build_environment.html#sec:gradle_system_properties)

[gradle properties](https://docs.gradle.org/current/userguide/build_environment.html#sec:gradle_system_properties)

### gradle.properties file

cli를 통해 지정할 수 있는 설정 값들을 제외한 나머지(gradle/system/project) 프로퍼티들을 설정할 수 있는 파일이다

프로젝트의 루트 디렉토리의 파일은 해당 프로젝트에만 적용되고, GRADLE_USER_HOME의 파일은 전체 그레이들 프로젝트에 적용된다


## gradle shell script files

추후 gradle 및 gradlew 쉘 스크립트 파일 분석


