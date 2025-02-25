#### index
- [gradle build workflow and components](#gradle-build-workflow-and-components)
- [gradle basic files](#gradle-basic-files)
- [gradle wrapper and local gradle](#gradle-wrapper-and-local-gradle)
- [GRADLE_USER_HOME](#gradle_user_home)
- [gradle daemon](#gradle-daemon)
- [gradle daemon vs no-daemon](#gradle-daemon-vs-no-daemon)
- [build environment: customizing build process](#build-environment-customizing-build-process)
- [gradle script file analysis](#gradle-script-file-analysis)


## gradle build workflow and components

그레이들은 빌드 스크립트의 내용을 기반으로 빌드, 테스트, 소프트웨어 배포 등을 자동화하는 메커니즘을 구현한다

그레이들 빌드 과정의 구성 요소는 크게 세 가지로 이뤄진다

프로젝트: 애플리케이션이나 라이브러리 같은 소프트웨어로 빌드될 수 있는 요소

그레이들: 빌드를 수행하는 주체로서 빌드 로직을 구성 및 실행하거나 의존성 관리 등의 기능을 제공한다 

결과물: 테스트 결과, 빌드 결과물(아티팩트, .jar/.apk/.zip/.war 등)

#### 1. gradle or gradle wrapper

사용자가 그레이들 빌드를 시작하는 단계로 그레이들 래퍼를 사용하면 정의된 버전에 따라 그레이들을 자동으로 다운받는다 (그레이들 버전 관리)

#### 2. initialize gradle

그레이들이 실행되어 초기화 작업을 수행한다

그레이들의 환경 설정 및 설정 파일(gradle.properties 등)을 로드하고 필요한 그레이들 버전을 확인한다

#### 3. daemon

--no-daemon 옵션이 주어지지 않은 이상 데몬을 사용한다

사용할 데몬이 없는 경우 새 데몬을 생성한다

#### 4. initialize project

Settings 인스턴스를 만들고 settings.gradle(.kts) 스크립트 파일을 파싱한 뒤 프로젝트의 설정을 평가한다

빌드에 참여하는 각 프로젝트에 대한 Project 인스턴스를 생성한다

#### 5. configure tasks

빌드에 참여하는 프로젝트의 build.gradle(.kts) 스크립트 파일을 파싱한 뒤 태스크 의존관계에 따른 태스크 그래프를 생성한다

#### 6. execute the tasks

태스크 실행 계획을 세우고 의존 관계 순서에 따라 각 태스크를 실행한다 (병렬적으로 실행될 수도 있다)

#### 7.



읽기 좋은 글
- [gradle basics](https://docs.gradle.org/current/userguide/gradle_basics.html)
- [how gradle works part 1 - startup](https://blog.gradle.org/how-gradle-works-1)
- [how gradle works part 2 - inside the daemon](https://blog.gradle.org/how-gradle-works-2)
- [how gradle works part 3 - build script](https://blog.gradle.org/how-gradle-works-3)


## gradle basic files

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

#### .gradle

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


## gradle daemon

그레이들 래퍼를 통해서 인터넷으로 그레이들을 다운받거나 로컬에 설치된 그레이들 프로그램을 통해 그레이들을 실행하면 어떻게 될까?

그레이들을 실행 시키는 파일은 `gradle` 또는 `gradle.bat` 파일이다

```shell
# 그레이들 래퍼를 통해서 다운받은 그레이들 실행 파일 (GRADLE_USER_HOME/wrapper/dists/gradle-8.10-bin/)
../gradle-8.10-bin/deqhafrv1ntovfmgh0nh3npr9/gradle-8.10/bin # ls -al
total 24
drwxr-xr-x 2 hansanhha hansanhha 4096 Feb  4 17:27 .
drwxr-xr-x 5 hansanhha hansanhha 4096 Feb  4 17:27 ..
-rwxr-xr-x 1 hansanhha hansanhha 8836 Feb  4 17:27 gradle
-rw-r--r-- 1 hansanhha hansanhha 3037 Feb  4 17:27 gradle.bat
```

이 gradle 스크립트 파일을 실행하면 [일련의 과정](#gradle-script-file-analysis)을 거쳐 결론적으로 그레이들 api인 GradleMain이라고 하는 그레이들 런처를 실행한다

즉, 그레이들 빌드 시스템을 동작시키기 위해 그레이들 자체를 jvm 상에서 실행하게 되고 프로젝트를 빌드할 때마다 매번 새로운 그레이들 jvm을 시작하게 된다

이 오버헤드를 줄이고자 그레이들은 3.0 버전부터 데몬(long-lived background process)을 기본적으로 사용하여 빌드마다 새로 jvm을 시작하지 않고 이미 실행 중인 데몬 프로세스를 재사용하여 빌드 시간을 단축시킨다

또한 빌드 간 프로젝트 정보를 캐시하고 종속성 및 그레이들 환경을 유지함으로써 반복적인 빌드 속도를 향상시킨다

데몬은 512MB 힙 메모리를 차지하며 백그라운드 프로세스로 동작하여 그레이들을 실행한 터미널을 닫아도 계속 실행되며 새로운 빌드 요청을 기다린다 (요청이 다시 올 때까지 idle 상태에 놓인다)

GradleMain으로 시작한 그레이들 빌드 시스템은 데몬이 실행 중인지 확인한 뒤, 만약 데몬이 실행 중이면 기존 데몬에 빌드 정보(프로젝트 디렉토리, 환경 변수 등)를 포함하여 로컬 소켓 통신을 활용하여 빌드 요청을 보내고 그렇지 않다면 새 데몬을 시작한다

이후 GradleLauncher와 협력하여 빌드 수행을 진행하고 빌드 결과를 반환한다

### multiple daemons

그레이들 데몬이 여러 개 띄워질 수 있는 시나리오는 다음과 같다 [gradle daemon compatibility docs](https://docs.gradle.org/8.10/userguide/gradle_daemon.html#deamon_compatibility)

#### 1. 서로 다른 그레이들 버전을 사용할 때

프로젝트마다 그레이들 버전이 다르면 그레이들은 각 버전에 맞는 데몬을 실행한다

#### 2. 다른 jvm 옵션을 사용할 때

org.gradle.jvmargs가 서로 다른 경우에 새로운 데몬이 실행된다

#### 3. 병렬 빌드를 사용할 때

단일 프로젝트 내에서 병렬 task 실행을 위해 여러 데몬이 실행될 수 있다

#### 4. 기존 데몬이 사용 중일 때

현재 실행 중인 데몬이 idle이 아닌 busy 상태인 상황에서 새로운 빌드 요청이 추가되면 추가 데몬이 시작된다

#### 5. 데몬이 비정상적인 상태일 때

메모리 부족, 프로세스 충돌 등으로 인해 기존 데몬을 사용할 수 없는 경우 새로운 데몬이 시작될 수 있다

### stop daemon

기본적으로 데몬은 아래의 조건 중 하나라도 만족하면 자동으로 종료된다
- 잔여 시스템 메모리가 적은 경우
- 데몬의 idle 상태가 3시간이 지난 경우

또는 아래의 명령어를 사용하여 직접 데몬 프로세스를 종료할 수 있다 (해당 명령어를 실행하는 그레이들 버전과 동일한 버전의 데몬 프로세스만 종료된다)

```shell
gradle --stop
```

### disable daemon

데몬은 single-use daemon과 no-daemon 방식으로 비활성화할 수 있다

#### single-use daemon

클라이언트 jvm 설정(JAVA_OPTS, GRADLE_OPTS)이 빌드에 필요한 설정(org.gradle.jvmargs)과 일치하지 않는 경우 새로운 데몬을 일회성으로만 사용하여 빌드가 끝나면 데몬을 종료한다

e.g) -Xmx2g 메모리만 가진 jvvm에서 -Xmx4g 설정을 요구하는 빌드를 진행하면 일회용 데몬을 띄워서 빌드를 진행한 뒤 빌드가 끝나면 데몬은 자동으로 종료한다

#### no-daemon

JAVA_OPTS와 GRADLE_OPTS, org.gradle.jvmargs의 jvm 설정이 모두 일치한다면 그레이들은 데몬을 사용하지 않고 클라이언트 jvm 내에서 직접 빌드를 진행할 수 있다

별도의 데몬 프로세를 띄우지 않기 때문에 메모리와 리소스를 절약할 수 있지만 데몬을 완전히 비활성화하려면 jvm 설정이 완전히 일치해야 한다 (불일치 시 single-use daemon 사용)

특정 빌드에만 비활성화하는 방법은 다음과 같다

```shell
./gradlew <task> --no-deamon
```

프로젝트 전체에 대해 데몬을 비활성화하려면 프로젝트 루트 디렉토리의 gradle.properties 파일에 아래와 같이 설정한다

```properties
org.gradle.daemon=false
```

### find daemons

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


## gradle daemon vs no-daemon

daemon 사용 시 다음과 같은 이점을 누릴 수 있다
- 반복 빌드 성능 향상
- 리소스 재사용 (tooling api, 메모리 풀)
- 캐시 및 jvm warm-up 효과

리소스 재사용 및 캐시 기능은 반복적인 빌드를 하는 환경에서 성능을 향상시키기엔 좋지만 ci 서버나 도커같이 깨끗한 환경을 요구하는 경우 이전 빌드의 상태를 잔존하여 **환경 불일치 문제**를 일으킬 수 있다

또한 ci 파이프라인에서 병렬 빌드가 일어나면 데몬이 여러 개 띄워지면서 메모리와 cpu 리소스의 사용량을 꽤나 차지하거나 도커 컨테이너 내에 불필요한 데몬 프로세스가 남아 리소스를 낭비할 수도 있다

데몬을 사용하지 않으면 다음과 같은 이점을 누릴 수 있다
- 깨끗한 빌드 환경 보장
- 메모리 안정성 
- 병렬 빌드 안정성

다만 느린 빌드 초기화로 인해 의존성 캐시 초기화와 빌드 스크립트 컴파일에 시간이 오래 걸리고, 그레이들의 툴링 api나 캐시를 활용할 수 없다

장기 실행되는 ci 서버에서 --no-daemon 옵션을 사용하면 빌드 캐시 활용을 할 수 없게 되어 빌드 속도가 느려질 수 있다

각각 장단점이 있기에 요구사항에 맞춰 데몬 사용 여부를 결정하는 게 올바르다고 볼 수 있다

데몬을 사용하기 적합한 상황
- 개발 환경에서 로컬 빌드 속도를 빠르게 하고 싶을 때
- 장기 실행되는 CI 서버에서 동일한 환경에서 반복적인 빌드를 수행할 때

데몬을 사용하지 않아도 되는 상황
- ci/cd 파이프라인에서 깨끗한 빌드 환경을 유지하고 싶을 때
- 도커 빌드 시 컨테이너가 항상 동일한 초기 상태에서 시작해야 할 때
- 단발성 빌드 작업이나 스케줄러를 통한 빌드처럼 jvm을 재사용할 필요가 없는 경우


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


## gradle script file analysis

그레이들을 동작시키는 gradle 파일(gradlew 아님)의 내용을 살펴보자 (추후)

아래의 파일은 그레이들 8.10 버전을 기준으로 한다

```shell
#!/bin/sh

##############################################################################
#
#   Gradle start up script for POSIX generated by Gradle.
#
#   Important for running:
#
#   (1) You need a POSIX-compliant shell to run this script. If your /bin/sh is
#       noncompliant, but you have some other compliant shell such as ksh or
#       bash, then to run this script, type that shell name before the whole
#       command line, like:
#
#           ksh Gradle
#
#       Busybox and similar reduced shells will NOT work, because this script
#       requires all of these POSIX shell features:
#         * functions;
#         * expansions «$var», «${var}», «${var:-default}», «${var+SET}»,
#           «${var#prefix}», «${var%suffix}», and «$( cmd )»;
#         * compound commands having a testable exit status, especially «case»;
#         * various built-in commands including «command», «set», and «ulimit».
#
#   Important for patching:
#
#   (2) This script targets any POSIX shell, so it avoids extensions provided
#       by Bash, Ksh, etc; in particular arrays are avoided.
#
#       The "traditional" practice of packing multiple parameters into a
#       space-separated string is a well documented source of bugs and security
#       problems, so this is (mostly) avoided, by progressively accumulating
#       options in "$@", and eventually passing that to Java.
#
#       Where the inherited environment variables (DEFAULT_JVM_OPTS, JAVA_OPTS,
#       and GRADLE_OPTS) rely on word-splitting, this is performed explicitly;
#       see the in-line comments for details.
#
#       There are tweaks for specific operating systems such as AIX, CygWin,
#       Darwin, MinGW, and NonStop.
#
#   (3) This script is generated from the Groovy template
#       https://github.com/gradle/gradle/blob/HEAD/platforms/jvm/plugins-application/src/main/resources/org/gradle/api/internal/plugins/unixStartScript.txt
#       within the Gradle project.
#
#       You can find Gradle at https://github.com/gradle/gradle/.
#
##############################################################################

# Attempt to set APP_HOME

# Resolve links: $0 may be a link
app_path=$0

# Need this for daisy-chained symlinks.
while
    APP_HOME=${app_path%"${app_path##*/}"}  # leaves a trailing /; empty if no leading path
    [ -h "$app_path" ]
do
    ls=$( ls -ld "$app_path" )
    link=${ls#*' -> '}
    case $link in             #(
      /*)   app_path=$link ;; #(
      *)    app_path=$APP_HOME$link ;;
    esac
done

# This is normally unused
# shellcheck disable=SC2034
APP_BASE_NAME=${0##*/}
# Discard cd standard output in case $CDPATH is set (https://github.com/gradle/gradle/issues/25036)
APP_HOME=$( cd -P "${APP_HOME:-./}.." > /dev/null && printf '%s
' "$PWD" ) || exit

# Use the maximum available, or set MAX_FD != -1 to use that value.
MAX_FD=maximum

warn () {
    echo "$*"
} >&2

die () {
    echo
    echo "$*"
    echo
    exit 1
} >&2

# OS specific support (must be 'true' or 'false').
cygwin=false
msys=false
darwin=false
nonstop=false
case "$( uname )" in                #(
  CYGWIN* )         cygwin=true  ;; #(
  Darwin* )         darwin=true  ;; #(
  MSYS* | MINGW* )  msys=true    ;; #(
  NONSTOP* )        nonstop=true ;;
esac

CLASSPATH=$APP_HOME/lib/gradle-gradle-cli-main-8.10.jar


# Determine the Java command to use to start the JVM.
if [ -n "$JAVA_HOME" ] ; then
    if [ -x "$JAVA_HOME/jre/sh/java" ] ; then
        # IBM's JDK on AIX uses strange locations for the executables
        JAVACMD=$JAVA_HOME/jre/sh/java
    else
        JAVACMD=$JAVA_HOME/bin/java
    fi
    if [ ! -x "$JAVACMD" ] ; then
        die "ERROR: JAVA_HOME is set to an invalid directory: $JAVA_HOME

Please set the JAVA_HOME variable in your environment to match the
location of your Java installation."
    fi
else
    JAVACMD=java
    if ! command -v java >/dev/null 2>&1
    then
        die "ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.

Please set the JAVA_HOME variable in your environment to match the
location of your Java installation."
    fi
fi

# Increase the maximum file descriptors if we can.
if ! "$cygwin" && ! "$darwin" && ! "$nonstop" ; then
    case $MAX_FD in #(
      max*)
        # In POSIX sh, ulimit -H is undefined. That's why the result is checked to see if it worked.
        # shellcheck disable=SC2039,SC3045
        MAX_FD=$( ulimit -H -n ) ||
            warn "Could not query maximum file descriptor limit"
    esac
    case $MAX_FD in  #(
      '' | soft) :;; #(
      *)
        # In POSIX sh, ulimit -n is undefined. That's why the result is checked to see if it worked.
        # shellcheck disable=SC2039,SC3045
        ulimit -n "$MAX_FD" ||
            warn "Could not set maximum file descriptor limit to $MAX_FD"
    esac
fi

# Collect all arguments for the java command, stacking in reverse order:
#   * args from the command line
#   * the main class name
#   * -classpath
#   * -D...appname settings
#   * --module-path (only if needed)
#   * DEFAULT_JVM_OPTS, JAVA_OPTS, and GRADLE_OPTS environment variables.

# For Cygwin or MSYS, switch paths to Windows format before running java
if "$cygwin" || "$msys" ; then
    APP_HOME=$( cygpath --path --mixed "$APP_HOME" )
    CLASSPATH=$( cygpath --path --mixed "$CLASSPATH" )

    JAVACMD=$( cygpath --unix "$JAVACMD" )

    # Now convert the arguments - kludge to limit ourselves to /bin/sh
    for arg do
        if
            case $arg in                                #(
              -*)   false ;;                            # don't mess with options #(
              /?*)  t=${arg#/} t=/${t%%/*}              # looks like a POSIX filepath
                    [ -e "$t" ] ;;                      #(
              *)    false ;;
            esac
        then
            arg=$( cygpath --path --ignore --mixed "$arg" )
        fi
        # Roll the args list around exactly as many times as the number of
        # args, so each arg winds up back in the position where it started, but
        # possibly modified.
        #
        # NB: a `for` loop captures its iteration list before it begins, so
        # changing the positional parameters here affects neither the number of
        # iterations, nor the values presented in `arg`.
        shift                   # remove old arg
        set -- "$@" "$arg"      # push replacement arg
    done
fi


# Add default JVM options here. You can also use JAVA_OPTS and GRADLE_OPTS to pass JVM options to this script.
DEFAULT_JVM_OPTS='"-Xmx64m" "-Xms64m"'" \"-javaagent:$APP_HOME/lib/agents/gradle-instrumentation-agent-8.10.jar\""

# Collect all arguments for the java command:
#   * DEFAULT_JVM_OPTS, JAVA_OPTS, JAVA_OPTS, and optsEnvironmentVar are not allowed to contain shell fragments,
#     and any embedded shellness will be escaped.
#   * For example: A user cannot expect ${Hostname} to be expanded, as it is an environment variable and will be
#     treated as '${Hostname}' itself on the command line.

set -- \
        "-Dorg.gradle.appname=$APP_BASE_NAME" \
        -classpath "$CLASSPATH" \
        org.gradle.launcher.GradleMain \
        "$@"

# Stop when "xargs" is not available.
if ! command -v xargs >/dev/null 2>&1
then
    die "xargs is not available"
fi

# Use "xargs" to parse quoted args.
#
# With -n1 it outputs one arg per line, with the quotes and backslashes removed.
#
# In Bash we could simply go:
#
#   readarray ARGS < <( xargs -n1 <<<"$var" ) &&
#   set -- "${ARGS[@]}" "$@"
#
# but POSIX shell has neither arrays nor command substitution, so instead we
# post-process each arg (as a line of input to sed) to backslash-escape any
# character that might be a shell metacharacter, then use eval to reverse
# that process (while maintaining the separation between arguments), and wrap
# the whole thing up as a single "set" statement.
#
# This will of course break if any of these variables contains a newline or
# an unmatched quote.
#

eval "set -- $(
        printf '%s\n' "$DEFAULT_JVM_OPTS $JAVA_OPTS $GRADLE_OPTS" |
        xargs -n1 |
        sed ' s~[^-[:alnum:]+,./:=@_]~\\&~g; ' |
        tr '\n' ' '
    )" '"$@"'

exec "$JAVACMD" "$@"
```







