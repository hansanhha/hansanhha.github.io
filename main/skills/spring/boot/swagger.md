---
layout: default
title:
---

[openapi specification](#openapi-specification)

[swagger](#swagger)

[springdoc-openapi](#springdoc-openapi)

[사용](#사용)

[예시](#예시)


## openapi specification

openapi initiative에서 정의한 http api 문서에 관한 공식 표준 포맷으로 openapi 또는 openapi specification(oas)이라고 한다 (open api로 띄워쓰지 않고 붙여 씀)

swagger에서 openapi 명세 초안을 작성한 것을 시작으로 현재 openapi initiative(oai)에서 관리한다 (2024.10.24 - open open api 3.1.1)

json 또는 yaml 포맷으로 api 사양을 작성한다


## swagger

openapi 명세를 사용하여 api 설계, 빌드, 문서화, 테스트하는 도구/프레임워크

스웨거를 통해 api의 ui 문서를 자동 생성하고 테스트할 수 있다

#### 구성 요소
- swagger editor: open api 명세 작성 편집기
- swagger ui: html, js 기반 문서 시각화 및 api 테스트 도구
- swagger codegen: open api 문서 기반 클라이언트/서버 코드 생성 도구
- [기타 등등](https://swagger.io/docs/specification/v3_0/about/)


## springdoc-openapi

스프링 부트 프로젝트에서 springdoc-openapi 라이브러리를 통해 api 문서를 자동으로 작성할 수 있다

springdoc-openapi는 런타임에 애플리케이션을 검사하여 스프링 설정, 클래스 구조, 어노테이션(swagger-api 어노테이션 등) 등을 기반으로 api 의미를 추론하고 자동으로 json/yaml 또는 html page를 생성하는 방식으로 동작한다

springdoc-openapi가 지원하는 라이브러리/프레임워크
- openapi 3
- spring boot 3 (java 17 & jakarta ee 9)
- jsr 303 (@NotNull, @Min, @Max, @Size 등)
- swagger-ui
- oauth 2
- graalvm native images


## 사용

### 의존성 설정

```kotlin
implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.3")
```

### 프로퍼티 파일 설정

```yaml
# prefix: springdoc
springdoc:

  # api 문서 경로 
  api-docs:
    path: /api-docs

  # 스웨거 ui 경로
  swagger-ui:
    path: /api-docs/swagger-ui.html
    
    # 스웨거 ui 설정
    disable-swagger-default-url: true
    display-request-duration: true
    operations-sorter: alpha

  # actuator api 표시 설정
  show-actuator: true

  # restful api media 타입 설정
  default-consumes-media-type: application/json
  default-produces-media-type: application/json

  writer-with-default-pretty-printer: true
```

### 주요 어노테이션

#### @OpenApiDefinition

openapi 문서의 전역적인 정보를 설정하는 어노테이션

주요 속성
- info(@Info): api 문서 메타정보(제목, 설명, 버전) 설정
- servers(@Server): api 서버 url과 정보 설정
- tags(@Tag): api 전역적 그룹화
- security(@SecurityRequirement): api 전역 보안 스키마 설정

```java
@OpenAPIDefinition(
    info = @Info(
            title = "springboot webmvc openapi example docs",
            description = "using springdoc-openapi library",
            version = "v1"
    )
)
```

#### @Server

openapi 문서에서 사용할 api 서버의 정보를 설정한다

서버 환경 별로 서버의 정보를 구분시킬 수 있다(개발, 테스트, 운영)

주요 속성
- url: 서버 기본 url
- description: 서버 설명

```java
@Server(url = "localhost:8080", 
        description = "localhost")
```

#### @Tag

api 그룹을 정의하여 엔드포인트를 카테고리별로 묶는다

주요 속성
- name: 태그 이름
- description: 태그 설명

```java
@Tag(name = "users", description = "사용자 관련 api")
```

#### @Operation

특정 api 엔드포인트(메서드)에 대한 정보 설정

주요 속성
- summary: api에 대한 간단 설명
- description: api에 대한 상세 설명
- tags(@Tag): api가 속하는 카테고리
- parameters(@Parameter): api가 사용하는 파라미터
- responses(@ApiResponse): api 응답 정의

```java
@Operation(
    summary = "사용자 조회",
    description = "사용자 ID로 사용자를 조회한다",
    tags = {"users"}
)
```

#### @ApiResponse

특정 api 엔드포인트에 대한 응답 상태 코드와 설명 정의

주요 속성
- responseCode: http 상태 코드
- description: 응답에 대한 설명
- content(@Content): 응답의 content-type 및 데이터 스키마 정의

```java
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "user creation successful",
            content = {@Content(schema = @Schema(implementation = CommonResponseFormat.class))}),

        @ApiResponse(responseCode = "400", description = "bad request")
})
```

#### @Schema

데이터 모델(객체) 스키마 정의

요청-응답의 데이터 구조, 필드 이름, 타입 등을 설정한다

주요 속성
- type: 데이터 타입 (string, array, object, interger) 정의
- description: 필드에 대한 설명
- example: 필드 값 예제

```java
public record CommonResponseFormat<T>(

        @Schema(description = "비즈니스 로직 처리 성공 여부", example = "true")
        boolean success,

        @Schema(description = "비즈니스 로직 결과에 따른 human-readable 응답 메시지", example = "created user")
        String message,

        @Schema(description = "필요 응답 데이터")
        T data) {
}
```

#### @Content

요청 또는 응답의 content-type 및 데이터 스키마 정의

@ApiResponse와 함께 사용되어 응답 데이터를 설명할 수 있다

주요 속성
- mediaType: content-type (application/json 등)
- scheme(@Scheme): 데이터 스키마 정의
- examples(@ExampleObject): 

```java
@Content(
    mediaType = "application/json",
    schema = @Schema(implementation = UserDto.class)
)
```

#### @RequestBody

요청 본문(json 형식) 데이터 스키마 정의

주요 속성
- content(@Content): 요청 본문의 content-type과 데이터 스키마

```java
@RequestBody(
    description = "사용자 정보",
    required = true,
    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class))
)
```

#### @Parameter

api 파라미터 정의

path(경로 변수), querystring(쿼리 파라미터), header, cookie 등을 정의할 수 있다

주요 속성
- name: 파라미터 이름
- in: 파라미터 위치(path 등)
- description: 파라미터 설명
- required: 필수 여부

```java
@Parameter(name = "userId", in = ParameterIn.PATH, description = "사용자 ID", required = true)
```

#### @Hidden

api 문서에서 특정 엔드포인트나 필드를 숨길 때 사용

```java
@Hidden
public void hiddenApi() {
    // 스웨거 문서에 노출되지 않는다
}
```

#### @ExampleObject

api 요청 또는 응답에 대한 예제 정의

주요 속성
- name: 예제 데이터 이름
- summary: 데이터 간단 설명
- value: 데이터 값

```java
@ApiResponse(
    responseCode = "200",
    description = "정상 응답",
    content = @Content(
        examples = @ExampleObject(
            name = "정상 예제",
            summary = "예제 응답",
            value = "{\"id\": 1, \"name\": \"John Doe\"}"
        )
    )
)
```

## 예시

[예시 코드](../src/main/java/hansanhha/documentation/swagger)





