---
layout: default
title:
---

#### index
- [artifact](#artifact)
- [artifact file structure](#jar-artifact-file-structure)
- [artifact properties](#artifact-properties)
- [identifying artifact (maven coordinates)](#identifying-artifact-maven-coordinates)
- [pom](#pom)
- [bom](#bom)


## artifact

아티팩트는 실행 가능한 파일이나 라이브러리같이 빌드 과정에서 생성되는 최종 파일로 [coordinate](#identifying-artifact-maven-coordinates)로 식별되며 다운로드, 설치, 배포될 수 있다

아티팩트 예시
- java/스프링 부트 프로젝트 빌드 -> .jar (java archive)
- 네이티브 애플리케이션 빌드 -> .exe, .dmg, .apk 등 
- docker 빌드 -> docker image

그레이들은 메이븐의 리포지토리와 아티팩트 시스템을 차용하므로 그레이들의 아티팩트는 곧 메이븐의 아티팩트를 의미한다고 볼 수 있다


## artifact properties

groupId: 프로젝트 그룹

artifactId: 아티팩트 식별자(이름)

version: 아티팩트의 버전 (baseVersion과 연결됨)

baseVersion: 아티팩트의 기반 버전 (version에서 파생되거나 연결됨)

classifier: 아티팩트의 종류를 구분하는 정보 (선택) - groupId, artifactId, version이 동일하지만 서로 다른 아티팩트임을 구분할 필요가 있을 때 사용한다

extension: 아티팩트 확장자 (기본값: jar)

아티팩트가 릴리즈되는 경우 버전과 베이스 버전은 동일한 값을 가지지만, 스냅샷 아티팩트의 경우 서로 다른 값을 가진다

스냅샷 아티팩트 버전: 1.0-20220119.164608-1

스냅샷 아티팩트 베이스 버전: 1.0-SNAPSHOT


## identifying artifact (maven coordinates)

고유한 아티팩트임을 나타내는 아티팩트 식별자(artifact coordinate)의 대부분은 `groupId:artifactId:version`의 형식으로 표현된다 (줄여서 GAV 형식이라고 함)

groupId와 artifactId를 합쳐 module이라고도 한다

```text
org.springframework.boot:spring-boot-starter:3.4.3
--------groupId--------- ----artifactId---- -version-
```

#### 주의점

GAV 식별자는 하나의 리포지토리 내에서만 고유하고, 다른 리포지토리에는 같은 GAV 형식을 가진 다른 아티팩트가 포함될 수 있다

이런 경우는 심각한 문제가 될 수 있으므로 이미 존재하는 GAV를 따라하지 않도록 해야 한다  


## pom

pom은 Project Object Model의 약자로 메이븐 프로젝트 pom.xml 파일의 XML 포맷으로 표현된다 

메이븐 아티팩트는 pom 파일을 기반으로 프로젝트에 대한 정보(설정 파일, 개발자 정보, 조직 및 라이선스 정보, 프로젝트 의존성 등)를 관리한다

그레이들은 메이븐 리포지토리를 사용하기 때문에 pom 파일과 밀접한 관계를 가진다

그레이들에서 메이븐 리포지토리로 아티팩트를 업로드하려면 플러그인(maven-publish)을 통해 pom 파일을 생성해야 한다 

[pom.xml 파일에 대한 자세한 내용](https://maven.apache.org/pom.html)


## bom

bom은 bill of materials의 약자로 의존성 버전을 일괄적으로 관리하는 pom 파일이다

보통 여러 개의 라이브러리를 사용할 때 버전 충돌을 방지하고 일관성을 유지하기 위해 사용된다

bom 자체는 실제 코드나 바이너리를 포함하지 않고 단순히 호환되는 의존성 버전을 정의하는 역할을 한다 (직접적인 의존성이 아닌 버전 추천을 제공함)

프로젝트에서 bom을 가져오면 해당 bom에서 관리하는 라이브러리들은 버전 없이 선언해도 자동으로 맞춰진다

동일한 라이브러리라도 여러 모듈에서 사용하는 경우 bom을 활용하면 모든 모듈에서 같은 버전의 라이브러리를 사용하도록 보장할 수 있다

그레이들에서 bom을 사용하려면 platform 또는 enforcedPlatform 키워드를 사용한다

platform 키워드는 버전 명시 없이 bom에서 관리하는 라이브러리들의 버전을 맞춰주는데, bom에서 지정한 버전보다 높은 버전을 수동으로 설정할 수 있게 한다

이와 달리 enforcedPlatform 키워드는 bom이 지정한 버전을 절대적으로 강제 적용하여 bom이 지정한 버전보다 높은 버전을 수동으로 설정한 경우 강제로 다운그레이드된다

```kotlin
dependencies {
    implementation(platform("org.springframework.boot:spring-boot-dependencies:3.4.3"))

    // bom에서 버전을 제공하므로 버전을 생략할 수 있다
    implementation("org.springframework.boot:spring-boot-starter-web") 
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
}
```

```kotlin
dependencies {
    // 수동으로 bom의 버전보다 높은 버전을 지정한 경우 강제 다운그레이드
    implementation(enforcedPlatform("org.springframework.boot:spring-boot-dependencies:3.4.3"))

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
}
```

대표 bom 목록
- org.springframework.boot:spring-boot-dependencies: 스프링 부트에서 제공하는 공식 bom
- io.micrometer:micrometer:bom
- com.fasterxml.jackson:jackson-bom
- org.apache.logging.log4j:log4j-bom
- io.grpc:grpc-bom