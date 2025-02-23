#### index
- [build](#build)
- [build tool](#build-tool)
- [gradle](#gradle)
- [terminology](#terminology)


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

#### flexible dsl

프로젝트의 빌드는 그레이들에서 지원되는 빌드 스크립트를 통해 동작하는데, 개발자는 groovy 또는 kotlin dsl을 사용하여 빌드 스크립트를 작성할 수 있다

#### dependency management

maven, ivy 저장소와 호환되어 외부 라이브러리를 쉽게 관리할 수 있고 private 저장소를 사용할 수도 있다

#### build optimization

증분 빌드(incremental build), 빌드 캐시, 데몬을 통해 빠른 빌드를 지원한다

#### multi-project build

단일 프로젝트 뿐만 아니라 다중 프로젝트 빌드도 지원한다

#### plugin system

플러그인을 통해 빌드의 기능을 확장할 수 있다

그레이들은 자체적으로 제공하는 플러그인과 누구나 자유롭게 만들 수 있는 커뮤니티 플러그인, 커스텀 플러그인을 지원한다

#### task-based build process

빌드 프로세스를 작업(task) 단위로 관리한다

작업의 설정 값을 통해 동작을 변경하거나 작업 간의 의존성을 설정할 수 있으며 플러그인을 통해 커스텀 작업을 추가할 수도 있다

#### build automation on continuous integration

ci/cd 파이프라인에서 자동화된 테스트, 패키징, 배포 기능을 활용할 수 있다

## terminology

dependency: 프로젝트를 빌드 또는 실행하기 위해 빌드 파일에 명시된 외부 리소스

library: 코드의 관점에서 해당 리소스를 참조할 때 지칭하는 용어

artifact: 리포지토리 내의 jar 파일 같은 물리적인 리소스