### 그레이들 주요 개념

[Project](#project) : 전체 빌드 컨테이너

[Build Script](#build-script) : 빌드 시 수행되는 스크립트

[Tasks](#tasks) : 빌드 스크립트에 추가할 수 있는 개별적인 작업 유닛

[Plugins](#plugins) : 유용한 빌드 기능을 제공하는 패키지된 컴포넌트

[Gradle Wrapper](gradle init,gradle-wrapper) : gradle project에 gradle을 설치하고, 빌드 및 task를 수행하는 스크립트

[Build](#전체-gradle-프로젝트-구조) : 빌드 후 생성된 파일, 아티팩트를 보관하는 디렉토리

[Gradle Daemon](#gradle-daemon) : Gradle이 내부적으로 빌드 실행 간 성능 향상을 위해 사용하는 자바 프로세스

## Project

그레이들에서 Project는 빌드할 애플리케이션 전체를 나타내는 고수준 개념임(gradle build와 gradle project는 동의어)

스프링 부트 프로젝트가 있으면 Gradle Project에 포함되어 gradle 빌드 시 스프링 부트 애플리케이션을 빌드함

settings.gradle : 그레이들 프로젝트를 생성하는 파일

rootProject.name 속성에다가 프로젝트 이름을 지정할 수 있는데, 해당 프로젝트의 디렉토리와 다른 이름을 가져도 됨

필수 지정 값은 아니지만 일관된 프로젝트 이름을 유지할 수 있음

[step 1](../step-by-step/1.gradle-project)

## Build Script

프로젝트에 무언가를 선언하거나 실행하고자 할 때 (애플리케이션 빌드) build script에 추가하면 됨

build script가 실질적으로 프로젝트를 구성하는 데 사용됨

보통 task, plugin, dependency 등을 추가함

[step 2](../step-by-step/2.build-script)

## Tasks

빌드 스크립트에 추가할 수 있는 개별적인 작업 유닛

여러 가지의 task를 추가해서 빌드 스크립트를 구성할 수 있음

task 사용 방법
- 미리 정의된 task 사용 :  task 인스턴스를 정의하고 어떻게 사용할지 구성하면 됨
- 커스텀 task 생성 및 사용

Copy task(Gradle에서 제공) 예시
```java
tasks.register<Copy>("copyHello") {
    from("source 파일 위치")
    into("복사할 위치")
}
```

[step 3](../step-by-step/3.tasks)

## Plugins

plugin은 미리 정의된 tasks, 도메인 객체, 컨벤션 등으로 구성된 컴포넌트로 빌드 스크립트에 추가할 수 있음

plugin을 gradle project에 추가하면 자동적으로 task들이 등록됨

크게 세 가지로 나뉨
- [gradle core plugins](https://docs.gradle.org/current/userguide/plugin_reference.html#plugin_reference) - java, application plugin 등
- [community plugins](https://plugins.gradle.org/) - org.springframework.boot plugin 등
- custom plugins - 직접 제작([Plugin API](https://docs.gradle.org/current/javadoc/org/gradle/api/Plugin.html))

예시
```java
plugins {
    java
}
```

**Legacy apply plugins syntax**

core 플러그인을 적용할 때 버전을 명시하지 않음

3rd party 플러그인을 적용할 땐 버전을 명시해야 됨

레거시 플러그인 적용 문법을 사용하면 최적화가 되지 않음

```groovy
// apply core plugin using legacy syntax
apply(plugin = "base")

// apply 3rd party plugin using legacy syntax
buildscript {
    repositories {
        maven {
            url = uri("https://plugins.gradle.org/m2/")
        } }
    dependencies {
        classpath("group.artifact:version")
    } 
}
```

**Configuration plugin**

몇몇 plugin들은 configuration 기능을 지원함

지원하는 plugin은 별도 확인 필요

```kotlin
base {
      archivesName.set("stuff")
}
```

[step 4](../step-by-step/4.plugins)

## [Gradle Wrapper](gradle init,gradle-wrapper)

## Build directory

gradle이 프로젝트 아티팩트를 생성하는 기본 디렉토리임

프로젝트 아티팩트
- 컴파일된 클래스 파일
- jar 파일
- gradle build 중 생성된 것들

gradle build directory == maven target directory

## 전체 Gradle 프로젝트 구조

```
project
- .gradle/
- build/
- build.gradle.kts
- buildSrc
- gradle/
    - wrapper/
        - gradle-wrapper.jar
        - gradle-wrapper.properties
- gradle.properties
- gradlew
- gradlew.bat
- settings.gradle.kts
```

.gradle : gradle이 해당 프로젝트에서 내부적으로 사용하는 디렉토리

buildSrc(optional) : build.gradle 대신 빌드 로직를 중앙화/캡슐화할 때 사용하는 디렉토리

gradle.properties(optional) : 해당 프로젝트에서 사용하는 프로퍼티(gradle property, 커스텀 property)

**참고사항**
- 필수 : .gradle과 build 디렉토리는 git 커밋 대상에서 제외

## Gradle Daemon

Gradle이 내부적으로 빌드 실행 간 성능 향상을 위해 사용하는 자바 프로세스

gradle command를 실행하면 실제로는 daemon을 사용해서 build가 수행됨

daemon 장점
- 최초 한 번 daemon 실행 이후 JVM 초기화 대기 시간이 필요하지 않음
- project, files, tasks 인메모리 캐시 사용
- JVM이 런타임 코드 최적화를 함

`gradle --status` : 로컬 환경에 띄워진 daemon 출력

`gradle --stop` : 동작 중인 daemon stop 명령


