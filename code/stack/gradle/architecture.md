#### index
- [gradle basic project structure and components](#gradle-basic-project-structure-and-components)
- [gradle wrapper and local gradle](#gradle-wrapper-and-local-gradle)
- [GRADLE_USER_HOME](#gradle_user_home)
- [gradle daemon](#gradle-daemon)
- [gradle properties](#gradle-properties)


## gradle basic project structure and components

[example basic project](https://github.com/hansanhha/hansanhha.github.io/tree/default/code/stack/gradle/examples/gradle%20basic%20project)

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

#### `.gradle`

gradle 빌드 캐시 및 설정 파일을 저장하는 디렉토리

`./gradlew clean` 명령으로 제거되지 않는다

#### build

`./gradlew build` 명령을 통해 나온 빌드 산출물들이 저장되는 디렉토리

컴파일된 클래스 파일(classes), jar 파일(libs), 테스트 결과(test-results), 리포트(reports), 리소스(resources) 등이 포함된다

`./gradlew clean` 시 자동으로 삭제된다

#### gradle

그레이들을 실행하기 위한 관련 파일들을 저장하는 디렉토리

wrapper: 그레이들 래퍼와 관련된 파일을 저장하는 디렉토리

gradle-wrapper.jar: 로컬에 그레이들을 설치하지 않아도 그레이들을 통해 프로젝트 빌드를 할 수 있게 도와주는 그레이들 래퍼 실행 파일

gradle-wrapper.properties: 그레이들 래퍼 설정 파일 - 그레이들 버전, 배포 url 등을 정의한다

그레이들 래퍼를 이용하여 ci/cd 파이프라인에서 그레이들을 자동 설치하여 빌드 자동화를 진행할 수 있다

#### src

소스 코드 및 리소스 파일을 저장하는 디렉토리

src/main/java: 애플리케이션의 메인 소스 코드들이 저장되는 디렉토리

src/main/resources: 애플리케이션의 리소스 파일들이 저장되는 디렉토리

src/test/java: 테스트 코드들이 저장되는 디렉토리

src/test/resources: 테스트 시 필요한 리소스 파일들이 저장되는 디렉토리

#### build.gradle.kts

프로젝트 빌드 설정 파일 (kotlin dsl)

프로젝트의 의존성, 플러그인, 태스크 등을 정의한다

groovy dsl 사용 시 build.gradle로 작성할 수 있다

#### settings.gradle.kts

최상위 프로젝트 설정 파일 (kotlin dsl)

프로젝트 이름, 모듈(서브 프로젝트) 포함 설정, 빌드 스크립트 설정 등을 정의한다

빌드 스크립트와 마찬가지로 groovy dsl 사용 시 settings.gradle로 작성할 수 있다

#### gradlew

unix 기반 시스템에서 사용하는 그레이들 래퍼 실행 스크립트

로컬에 그레이들을 설치하지 않아도 빌드를 실행할 수 있다 (`./gradlew build`)

#### gradlew.bat

windows 시스템에서 사용하는 그레이들 그레이들 래퍼 실행 스크립트

gradlew의 윈도우 버전이다 `./gradlew.bat build`


## gradle wrapper and local gradle

gradle wrapper는 로컬에 그레이들이 설치되지 않은 경우 그레이들을 인터넷을 통해 다운받아서 프로젝트 빌드를 가능하게 해준다

이 특징은 ci/cd 파이프라인 환경에서 그레이들과 관련된 환경 설정을 하지 않아도 프로젝트를 빌드할 수 있도록 자동적으로 그레이들을 다운받는 유용한 기능을 할 뿐만 아니라 프로젝트에 참여하는 모든 팀원이 동일한 버전의 그레이들을 사용하도록 강제할 수 있다

덕분에 새로운 팀원이 프로젝트에 참여해도 해당 팀원의 로컬에 설치된 그레이들의 버전과 별개로 설정된 그레이들 버전을 사용할 수 있다

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


## gradle daemon

그레이들 래퍼를 통해서 동적으로 그레이들을 인터넷으로 다운받거나 로컬에 설치된 그레이들 프로그램을 통해 그레이들을 실행하면 어떻게 될까?

그레이들을 실행 시키는 파일은 `gradle` 또는 `gradle.bat` 파일이다

```shell
# 그레이들 래퍼를 통해서 다운받은 그레이들 실행 파일
../gradle-8.10-bin/deqhafrv1ntovfmgh0nh3npr9/gradle-8.10/bin # ls -al
total 24
drwxr-xr-x 2 hansanhha hansanhha 4096 Feb  4 17:27 .
drwxr-xr-x 5 hansanhha hansanhha 4096 Feb  4 17:27 ..
-rwxr-xr-x 1 hansanhha hansanhha 8836 Feb  4 17:27 gradle
-rw-r--r-- 1 hansanhha hansanhha 3037 Feb  4 17:27 gradle.bat
```

이 파일은 그레이들을 동작시키는 쉘 스크립트 파일로 gradle 런처를 실행시킨다

이후 





## gradle properties











