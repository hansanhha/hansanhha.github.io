## gradle init : gradle 프로젝트 생성 CLI 명령어 

gradle 8.8 버전 기준

`gradle init` 명령어를 통해 gradle 프로젝트를 생성할 수 있음

생성할 수 있는 프로젝트 타입
```
- Basic
- Application
  - Single application project
  - Application and library project
- Library
- Gradle Plugin
```

일반적으로 application type을 사용함

Single application proejct 구조
```
- app (애플리케이션)
  - src/main/java, src/main/test
  - build.gradle.kts
- gradle (의존성 버전 명시, gradle wrapper)
  - wrapper
    - gradle-wrapper.jar
    - gradle-wrapper.properties
  - libs.version.toml
- gradlew 
- gradlew.bat 
- settings.gradle.kts
```

settings.gradle.kts가 위치한 곳엔 build script가 없고 소스 코드 파일이 포함된 디렉토리에만 있음

## Gradle Wrapper

위 프로젝트 구조 참고

Gradle Wrapper
- Gradle Project에 추가되어 task를 실행하는 데 사용되는 스크립트임
- gradle-wrapper.jar : 인터넷을 통해 gradle을 다운로드 받는 파일(다운받은 gradle은 project 대신 user home 디렉토리에 저장됨)
- gradle-wrapper.properties : 다운로드 받을 gradle 설정 파일(버전, URL 등)
- gradlew : Linux, Mac 환경 wrapper 실행 스크립트
- gradlew.bat : Windows 환경 wrapper 실행 스크립트

```
❯ ./gradlew tasks
Downloading https://services.gradle.org/distributions/gradle-8.8-bin.zip
.............10%.............20%.............30%.............40%.............50%..... 
........60%..............70%.............80%.............90%.............100%
```

wrapper 3가지 장점
- Gradle 설치 필요 X -> wrapper가 자동으로 인터넷을 통해 gradle을 설치하고 로컬에 캐시함
- local gradle 환경이나 협업 환경에서 버전 차이로 인한 문제 방지 -> project에서 지정한 grdale 버전을 사용하도록 보장함
- 버전 업데이트가 쉬움

**참고사항**
- gradle project 외부에서 gradle 명령이 필요한 경우가 아니라면(gradle init 등) 무조건 gradle wrapper 사용

### gradle wrapper 추가하는 방법

wrapper가 없는 gradle 프로젝트에 추가하는 방법

`gradle wrapper` 실행

**참고사항**
- `gradle init`과 `gradle wrapper`는 local에 설치되어 있는 gradle과 동일한 버전으로 gradle wrapper를 생성함

### gradle wrapper 버전 업데이트

`./gradlew wrapper --gradle-version=${new version}` 실행

### gradle wrapper가 gradle을 다운로드 받는 원리

처음 gradle wrapper를 통해 project build할 때 인터넷으로 특정 버전의 gradle을 다운받음

다운받은 Distribution은 user home 디렉토리의 ./gradle/wrapper/dists에 저장됨  

여기에 저장된 gradle-version-bin을 삭제한 뒤 gradle wrapper를 사용하면 다시 다운받게 됨

```shell
❯ ls ~/.gradle/wrapper/dists/
CACHEDIR.TAG  gradle-8.4-bin  gradle-8.5-bin  gradle-8.6-bin  gradle-8.8-bin
```