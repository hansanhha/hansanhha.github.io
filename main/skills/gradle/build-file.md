---
layout: default
title:
---

#### index
- [gradle build process](#gradle-build-process)
- [dsl rules](#dsl-rules)
- [build.gradle(.kts)](#buildgradlekts)
- [multiple build files](#multiple-build-files)


#### reference
- [how gradle works part 3 - build script](https://blog.gradle.org/how-gradle-works-3)


## gradle build process

빌드 스크립트는 프로젝트의 빌드 프로세스 및 속성과 설정(메타 데이터 등)을 정의하는 파일로 그루비 dsl 또는 코틀린 dsl로 작성할 수 있다 (빌드 스크립트는 프로젝트 단위로 관리된다)

그레이들은 초기화 단계(initialization phase)에 빌드에 참여하는 각 서브 프로젝트마다 Project 인스턴스를 만드는데, 이후 구성 단계(configuration phase)에서 빌드 스크립트에 작성된 dsl 코드가 실행되며 이전 단계에서 생성된 Project 인스턴스의 메서드 등의 gradle api를 호출하여 빌드 로직이 수행된다

즉, 그레이들이 빌드 스크립트를 위에서 아래로 순차적으로 빌드 스크립트 라인을 실행하여 dsl 코드를 기반으로 gradle api를 호출하며 이 과정에서 일반 자바 파일처럼 jvm에서 실행되기 전에 바이트코드로 컴파일된다 

결과적으로 빌드 과정에서 빌드 스크립트를 실행하면 태스크 그래프 및 의존성 등이 설정되며 빌드 실행 계획이 수립된다


## dsl rules

그레이들의 빌드 스크립트를 작성할 수 있는 dsl(`.gradle` 또는 `.gradle.kts`) 에는 암묵적인 규칙이 존재한다

### 1. lambda/closure

빌드 스크립트에서 사용되는 `{ ... }` 문법은 코틀린과 그루비에서 자바의 람다나 자바스크립트의 함수 객체와 유사한 람다(코틀린)와 클로저(그루비)라는 특별한 객체를 나타낸다

코틀린과 그루비는 함수의 마지막 파라미터로 람다나 클로저를 받으면 괄호 밖에 코드를 넣을 수 있는 특징을 가진다

`plugins { ... }`는 코틀린 람다 또는 그루비 클로저 객체를 매개변수로 전달받는 메서드의 호출문으로, 아래와 같이 함수 정의를 생략하고 괄호 밖에 코드를 작성할 수 있다 

매개변수로 전달된 람다나 클로저는 해당 메서드의 구현에 따라 즉시 또는 이후에 실행될 수 있다

```text
plugins {
    ...
}
    |
    |
    ▼
plugins(function() {
    ...
})
```

```text
tasks.register("some task") {
    doLast {
        ...
    }
}
    |
    |
    ▼
tasks.registry("some task", function() {
    doLast(function() {
        ...
    })
})
```

### 2. chained method invocation

아래 plugins 코드 블럭에 있는 코드는 `id("some.plugin).version("0.0.1")` 체인이 적용된 메서드 호출과 동일하다

이 기능은 코틀린에서는 [infix function](https://kotlinlang.org/docs/functions.html#infix-notation), 그루비에서는 [command chains](https://docs.groovy-lang.org/docs/latest/html/documentation/core-domain-specific-languages.html#_command_chains)라고 한다

```text
plugins {
    id("some.plugin") version("0.0.1")
}
    |
    |
    ▼
plugins {
    id("some.plugin").version("0.0.1")
}
```

또한 함수 안의 코드는 this 객체를 통해 실행되는데 그레이들 dsl에서 this는 **코드 블록이 실행되는 컨텍스트 객체**를 의미하며 코틀린 람다에서는 receiver로, 그루비 클로저에서는 delegate라고 일컫는다

그레이들은 특정 블록 안에서 this가 어떤 타입인지 자동으로 결정하므로 위의 `id("some.plugin")` 메서드에 대한 this의 타입은 PluginDependenciesSpec이 된다

즉, 각 dsl 블록 내부 코드의 this 객체(receiver, delegate)에 따라 적절한 gradle api가 실행되어 빌드 작업을 구성하고 실행한다

```text
plugins {
    id("some.plugin").version("0.0.1")
}
    |
    |
    ▼
plugins {
    // val this: PluginDependenciesSpec
    this.id("some.plugin").version("0.0.1")
}
```

각 dsl 코드에 대한 this 객체
- `plugins { ... }`: PluginDependenciesSpec
- `dependencies { ... }` : DependencyHandler
- `tasks { ... }`: TaskContainer


## build.gradle(.kts)

build.gradle(.kts)은 플러그인 적용, 의존성 설정, 태스크 추가 등 프로젝트의 빌드 로직을 담는 빌드 스크립트 파일이다

모든 그레이들 빌드는 한 개 이상의 빌드 스크립트 파일로 구성되며, 일반적으로 프로젝트에 참여하는 각 빌드 스크립트 파일은 해당 프로젝트의 바로 아래에 위치한다

또한 빌드 파일에는 두 종류의 의존성을 추가할 수 있다 
- 그레이들과 빌드 스크립트에서 의존하는 라이브러리 또는 플러그인
- 프로젝트 소스 코드에서 의존하는 라이브러리

```text
/my-root-project
    ├── build.gradle.kts  (루트 프로젝트 빌드 스크립트)
    ├── settings.gradle.kts
    ├── subproject1/
        ├── build.gradle.kts  (서브 프로젝트 1의 빌드 스크립트)
    ├── subproject2/
        ├── build.gradle.kts  (서브 프로젝트 2의 빌드 스크립트)
```

빌드 스크립트에 포함될 수 있는 주요 요소와 호출되는 gradle api는 다음과 같다

### plugins block

plugins 코드 블럭을 통해 gradle 공식 또는 커뮤니티, 커스텀 플러그인을 적용할 수 있다

plugins 블록이 실행되면 PluginDependenciesSpec api를 사용하여 플러그인을 추가하는 로직이 실행된다

`version "3.4.3"`은 코틀린 dsl의 infix function이며 내부적으로 version("3.4.3")을 호출하고 버전이 적용된 플러그인은 PluginContainer.apply 메서드를 통해 실행된다

```kotlin
plugins {
    id("org.springframework.boot") version "3.4.3"
}
```

```kotlin
PluginDependenciesSpec.id("org.springframework.boot")
PluginContainer.apply()
```

### dependencies block

dependencies 코드 블럭을 통해 프로젝트의 의존성을 정의할 수 있다

각 의존성은 클래스패스 범위를 지정하고 그룹화하기 위해 configurations(implementation, testImplementation 등)를 반드시 지정해야 한다

```kotlin
dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
}
```

dependencies 블록이 실행되면 DependencyHandlerScope(코틀린) 또는 DependencyHandler가 dsl 스크립트의 this 객체가 된다

gradle은 설정된 configurations에 따라 해당 라이브러리를 클래스패스에 추가한다

### repositories block

repositories 블록으로 프로젝트 의존성을 가져올 아티팩트 저장소를 설정할 수 있다

```kotlin
repositories {
    mavenCentral()
}
```

repositories 블록이 실행되면 RepositoryHandler 객체가 this로 설정된다

위의 코드는 RepositoryHandler.mavenCentral() 메서드를 호출하여 그레이들이 maven central 저장소를 기본 리포지토리로 등록하게 한다

### tasks block

tasks 블록으로 프로젝트의 태스크를 정의하거나 플러그인이 제공하는 태스크를 설정할 수 있다

```kotlin
tasks.register("greeting") {
    doLast {
        println("hello build script")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
```

`tasks.register` 블록은 TaskContainer.register 메서드를 호출하여 내부적으로 DefaultTask 객체를 생성하고 `doLast {}` 블록을 등록하여 실행 로직을 추가한다

등록된 태스크는 `./gradlew greeting` 또는 `tasks.getByName("greeting").execute()`로 실행할 수 있다

### build metadata configuration

```kotlin
group = "hansanhha"
version = "1.0.0"
```

group과 version은 configuration phase에 생성된 Project 인스턴스의 메서드를 호출하여 메타데이터를 설정한다

group: Project.setProperty("group", "hansanhha") 메서드 호출

version: Project.setVersion("1.0.0") 메서드 호출


## multiple build files