---
layout: default
title:
---

#### index
- [plugins](#plugins)
- [dependencies](#dependencies)
- [configurations](#configurations)
- [repositories](#repositories)
- [tasks](#tasks)


## plugins

그레이들은 플러그인 기반으로 빌드 스크립트를 동작하며, 플러그인은 빌드 스크립트에 적용되어 빌드 프로세스를 제어하거나 빌드 스크립트에 추가적인 기능을 제공한다

플러그인은 제공자와 위치에 따라 3가지 유형으로 나뉜다

**gradle core plugin**: 그레이들에서 제공하는 플러그인으로 java, java-library, kotlin-dsl 등이 있다

**gradle community plugin**: gradle plugin portal에 등록된 커스텀 플러그인으로 대표적으로 스프링에서 제공하는 org.springframework.boot 플러그인이 있다

**convention plugin**: 사용자가 빌드에 적용하기 위해 로컬에서 만든 플러그인으로 빌드 로직을 중앙화할 때 유용하다

플러그인을 프로젝트에 적용하려면 아래와 같이 빌드 파일의 plugins 블록을 통해 적용할 수 있다

코어 플러그인은 string 값만 설정해도 적용되고 컨벤션 플러그인은 id값만, 커뮤니티 플러그인은 id와 version(optional)을 설정해야 한다 

```kotlin
plugins {
    java // gradle core plugin
    id("org.springframework.boot") version("3.4.3") // gradle community plugin
    id("my-java-plugin") // convention plugin
}
```

그레이들에서 특정 언어(java, kotlin, c++ 등)로 개발 및 빌드를 진행하려면 해당 언어에 대한 플러그인을 필수로 적용해야 한다

만약 추가하지 않으면 해당 언어의 소스 코드를 그레이들이 인식하지 못하고 컴파일과 패키징이 불가능해진다

일례로 자바 프로젝트를 만들려면 java 또는 application 플러그인을, 코틀린 프로젝트는 org.jetbrains.kotlin.jvm 플러그인을 추가해야 그레이들이 빌드를 수행할 수 있으며 ide 또한 해당 언어와 관련된 기능을 지원할 수 있다

### plugin extension

plugin extension은 그레이들 플러그인의 동작을 커스텀할 수 있도록 제공하는 설정 api로, 사용자가 빌드 스크립트에서 플러그인의 동작을 설정할 수 있도록 만들어준다

예를 들어 java 플러그인을 적용하면 빌드 파일에 java {} 블록을 통해 플러그인과 관련된 추가 설정을 할 수 있다

java 블록이 java 플러그인의 extension이며, 이런 extension은 dsl 형태로 제공되고 이후에 그레이들의 ExtensionContainer를 통해 등록된다

```kotlin
// java plugin extension을 통해 그레이들이 컴파일/런타임 시점에 사용할 자바 버전을 명시한다 
java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}
```

### plugin hierarchy

플러그인은 다른 플러그인을 의존성으로 포함할 수 있다

즉, 특정 플러그인을 적용하면 내부적으로 포함된 모든 플러그인이 해당 프로젝트에 적용된다

플러그인의 상속 관계를 파악해놓으면 불필요한 중복 적용을 피하고 효율적으로 플러그인을 구성할 수 있다

### convention plugin

컨벤션 플러그인은 각 서브 프로젝트에서 중복되는 빌드 로직을 유지보수하기 쉽도록 중앙화하고, 캡슐화하기 위해 로컬에 만드는 플러그인을 말한다

프로젝트와 별도의 디렉토리에 만들거나 루트 프로젝트의 특정 위치 또는 buildSrc에 컨벤션 플러그인을 정의할 수 있으며 buildSrc가 아닌 특정 위치에 정의하는 경우 그 위치를 명시해야 한다

[컨벤션 플러그인 정의 예제](./examples/java-convention-plugin), [컨벤션 플러그인 사용 예제](./examples/convention%20plugin%20using%20project)

### common plugins

[gradle core plugins](https://docs.gradle.org/current/userguide/plugin_reference.html)

[gradle plugin portal](https://plugins.gradle.org)

#### java-related core plugins

java: java 애플리케이션 또는 라이브러리를 위한 기본 플러그인

java-library: api와 implementation 의존성을 구분할 수 있는 java 라이브러리 플러그인 (java 플러그인 포함)

application: 실행 가능한 java 애플리케이션을 만들기 위한 플러그인 (java 플러그인 포함)

java-gradle-plugin: 그레이들 플러그인을 개발하기 위한 플러그인 (java 플러그인 포함)

maven-publish: maven 리포지토리에 빌드 결과(아티팩트)를 배포하는 플러그인

checkstyle: 자바 코드 스타일을 검사하는 정적 분석 플러그인

pmd: PMD 정적 코드 분석을 수행하는 플러그인

jacoco: 자바 코드의 테스트 커버리지를 측정하는 플러그인

#### kotlin-related community plugins

org.jetbrains.kotlin.jvm: jvm용 코틀린 프로젝트를 위한 필수 플러그인

org.jetbrains.kotlin.kapt: 코틀린 어노테이션 프로세서(dagger, room) 지원 플러그인

io.gitlab.arturbosch.detekt: 코틀린 정적 분석 도구 플러그인

org.jilleitschuh.gradle.ktlint: 코틀린 코드 스타일 검사 플러그인 (구글 코틀린 스타일 가이드 기반)
 
org.jetbrains.kotlinx.kover: 코틀린 프로젝트용 코드 커버리지 측정 플러그인

#### spring-related community plugins

org.springframework.boot: 스프링 부트 플러그인

io.spring.dependency-management: 스프링 프로젝트 bom(bill of materials) 적용 플러그인

#### docker-related community plugins

com.palantir.docker: 도커 컨테이너 빌드 자동화 플러그인

com.bmuschko.docker-remote-api: 그레이들에서 도커 api를 직접 호출할 수 있게 해주는 플러그인

com.google.cloud.tools.jib: 자바/코틀린 프로젝트를 컨테이너 이미지로 빠르게 빌드해주는 플러그인

#### useful community plugins

com.github.ben-manes.versions: 사용 중인 라이브러리의 최신 버전을 체크해주는 플러그인


## dependencies

```kotlin
dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter")
}
```

dependency는 프로젝트 소스 코드에서 의존하는 라이브러리를 말하며 빌드 파일의 dependencies 블록에서 프로젝트 코드에서 의존하는 jar, plugin, 라이브러리 또는 빌드 스크립트 자체에서 필요로 하는 의존성을 선언한다

이후 그레이들의 [의존성 해결 메커니즘](#dependency-resolution-mechanism)을 거치면서 프로젝트에서 의존하는 라이브러리들을 사용하게 된다

의존성의 종류는 크게 direct dependency와 transitive dependency로 나뉜다

direct dependency: 프로젝트에서 직접 선언한 의존성 `implementation("org.junit.jupiter:junit-jupiter")`

transitive dependency: 직접 의존한 라이브러리에서 의존하는 다른 라이브러리 (그레이들과 메이븐 같은 빌드 도구는 의존성을 해결할 때 전이 의존성도 포함함)

```text
--- org.junit.jupiter:junit-jupiter
    +--- org.junit.jupiter:junit-jupiter-params
    \--- org.junit.jupiter:junit-jupiter-engine
```

또한 의존성은 용도에 따라 클래스 패스에 포함될 범위를 지정될 수 있다 [configurations](#configurations)   

### platform(): pom, bom


### dependency version management

#### version catalog


### dependency locking 


### dependency resolution mechanism

그레이들의 dependency management 메커니즘에 의해 해결되어 해당 의존성을 프로젝트의 클래스패스에 포함할 수 있게 된다

### dependency cache


### dependencies task

java 플러그인을 통해 추가되는 dependencies task는 프로젝트의 클래스패스에 포함되는 라이브러리들을 표시한다

아래는 예시 프로젝트의 `dependencies` 태스크를 실행한 결과이다 

```kotlin
dependencies {
    testImplementation(platform("org.junit:junit-bom:5.11.4"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}
```

```text
------------------------------------------------------------
Root project 'gradle-basic-project-example'
------------------------------------------------------------

testCompileClasspath - Compile classpath for source set 'test'.
+--- org.junit:junit-bom:5.11.4
|    +--- org.junit.jupiter:junit-jupiter:5.11.4 (c)
|    +--- org.junit.jupiter:junit-jupiter-api:5.11.4 (c)
|    +--- org.junit.jupiter:junit-jupiter-params:5.11.4 (c)
|    \--- org.junit.platform:junit-platform-commons:1.11.4 (c)
\--- org.junit.jupiter:junit-jupiter -> 5.11.4
     +--- org.junit:junit-bom:5.11.4 (*)
     +--- org.junit.jupiter:junit-jupiter-api:5.11.4
     |    +--- org.junit:junit-bom:5.11.4 (*)
     |    +--- org.opentest4j:opentest4j:1.3.0
     |    +--- org.junit.platform:junit-platform-commons:1.11.4
     |    |    +--- org.junit:junit-bom:5.11.4 (*)
     |    |    \--- org.apiguardian:apiguardian-api:1.1.2
     |    \--- org.apiguardian:apiguardian-api:1.1.2
     \--- org.junit.jupiter:junit-jupiter-params:5.11.4
          +--- org.junit:junit-bom:5.11.4 (*)
          +--- org.junit.jupiter:junit-jupiter-api:5.11.4 (*)
          \--- org.apiguardian:apiguardian-api:1.1.2

testCompileOnly - Compile only dependencies for source set 'test'. (n)
No dependencies

testImplementation - Implementation only dependencies for source set 'test'. (n)
+--- org.junit:junit-bom:5.11.4 (n)
\--- org.junit.jupiter:junit-jupiter (n)

testRuntimeClasspath - Runtime classpath of source set 'test'.
+--- org.junit:junit-bom:5.11.4
|    +--- org.junit.jupiter:junit-jupiter:5.11.4 (c)
|    +--- org.junit.jupiter:junit-jupiter-api:5.11.4 (c)
|    +--- org.junit.jupiter:junit-jupiter-params:5.11.4 (c)
|    +--- org.junit.jupiter:junit-jupiter-engine:5.11.4 (c)
|    +--- org.junit.platform:junit-platform-commons:1.11.4 (c)
|    \--- org.junit.platform:junit-platform-engine:1.11.4 (c)
\--- org.junit.jupiter:junit-jupiter -> 5.11.4
     +--- org.junit:junit-bom:5.11.4 (*)
     +--- org.junit.jupiter:junit-jupiter-api:5.11.4
     |    +--- org.junit:junit-bom:5.11.4 (*)
     |    +--- org.opentest4j:opentest4j:1.3.0
     |    \--- org.junit.platform:junit-platform-commons:1.11.4
     |         \--- org.junit:junit-bom:5.11.4 (*)
     +--- org.junit.jupiter:junit-jupiter-params:5.11.4
     |    +--- org.junit:junit-bom:5.11.4 (*)
     |    \--- org.junit.jupiter:junit-jupiter-api:5.11.4 (*)
     \--- org.junit.jupiter:junit-jupiter-engine:5.11.4
          +--- org.junit:junit-bom:5.11.4 (*)
          +--- org.junit.platform:junit-platform-engine:1.11.4
          |    +--- org.junit:junit-bom:5.11.4 (*)
          |    +--- org.opentest4j:opentest4j:1.3.0
          |    \--- org.junit.platform:junit-platform-commons:1.11.4 (*)
          \--- org.junit.jupiter:junit-jupiter-api:5.11.4 (*)

testRuntimeOnly - Runtime only dependencies for source set 'test'. (n)
No dependencies

(c) - A dependency constraint, not a dependency. The dependency affected by the constraint occurs elsewhere in the tree.
(*) - Indicates repeated occurrences of a transitive dependency subtree. Gradle expands transitive dependency subtrees only once per project; repeat occurrences only display the root of the subtree, followed by this annotation.

(n) - A dependency or dependency configuration that cannot be resolved.
```



## configurations

## repositories





## tasks