---
layout: default
title:
---

## Spring web exception hierarchy

spring 6.0 이후 기준

스프링 web 예외는 크게 2가지의 최상위 예외 클래스를 구현함

**core 모듈 예외**
- 최상위 예외 클래스 : NestedRuntimeException

**web 모듈 예외**
- 최상위 예외 클래스 : ErrorResponseException
- 예외 스펙 : ErrorResponse

## ErrorResponse

spring web에서 HTTP error 응답을 나타내는 인터페이스

status, headers, [ProblemDetail](https://datatracker.ietf.org/doc/html/rfc7807) 객체를 필드로 가짐

ProblemDetail 필드
- type, title, status, detail, instance, properties(추가 정보)

### RFC 7807의 Problem Detail

RFC 7807은 Problem Detail을 정의하는 문서임(권장 사항 문서)

Problem Detail은 API 클라이언트에게 HTTP API에서 발생하는 문제 발생 상황을 표현하는 방법임(common error format)

JSON으로 직렬화할 때 `application/problem+json` 미디어 타입을 사용함
 
```text
HTTP/1.1 403 Forbidden
Content-Type: application/problem+json
Content-Language: en

{
"type": "https://example.com/probs/out-of-credit",
"title": "You do not have enough credit.",
"detail": "Your current balance is 30, but that costs 50.",
"instance": "/account/12345/msgs/abc",
"balance": 30,
"accounts": ["/account/12345",
             "/account/67890"]
}
```

**type**
- 문제 유형(problem type)을 식별하는 URI
- 문제에 대한 추가 정보를 제공하는 웹 페이지(자세한 설명, 해결 방법, 관련 문서 링크 제공)를 가리킬 수 있음

**title**
- 문제 유형에 대한 간략한 설명(human-readable)

**detail**
- 문제 발생에 대한 상세 설명(human-readable)

**status**
- 문제 발생에 대해 origin 서버가 발행한 HTTP 상태 코드

**instance**
- 문제의 특정 발생을 식별하는 URI
- 문제가 발생한 특정 리소스를 가리킴
- 위의 instance는 특정 계정의 특정 데이터 항목에 대한 문제를 식별함

balance, accounts : 추가 정보

### ErrorResponseException

최상위 예외 클래스로 ErrorResponse를 구현함

spring web의 모든 예외는 ErrorResponseException을 상속받음

## Exception resolution process

```java
public class DispatcherServlet extends FrameworkServlet {

    @Nullable
    private List<HandlerExceptionResolver> handlerExceptionResolvers;
}
```

1. handler 예외 발생
2. DispatcherServlet -> handlerExceptionResolvers 순회하며 예외 처리 시도
3. 해당 resolver가 null을 반환하면 다음 resolver에게 예외 처리 위임
    1. resolver가 예외 처리를 하면 해당 resolver가 반환한 ModelAndView를 반환 -> 다음 resolver는 실행되지 않음
    2. 모든 resolver가 예외 처리 실패하면 servlet container에게 예외를 던짐

## Spring web exception resolver

### HandlerExceptionResolver

handler 예외를 처리하는 최상위 인터페이스

예외 발생 시점
- handler mapping
- handler execution

```java
public interface HandlerExceptionResolver {
    ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex);
}
```

handler : 실행된 handler 또는 null(핸들러 매핑이 실패한 경우)

ModelAndView
- ModelAndView 반환 : 예외 처리 후 view를 렌더링
- null 반환 : 다음 resolver에게 예외 처리 위임
- 모든 resolver가 예외 처리를 실패하면 servlet container에게 예외를 던짐 -> 500 에러 발생

### DefaultHandlerExceptionResolver

HandlerExceptionResolver의 기본 구현체

표준 spring mvc 예외들을 해결하고 그에 맞는 http status code로 변환함

지원하는 표준 spring mvc 예외들
- HttpRequestMethodNotSupportedException
- HttpMediaTypeNotSupportedException
- HttpMediaTypeNotAcceptableException
- MissingPathVariableException
- MissingServletRequestParameterException
- MissingServletRequestPartException
- ServletRequestBindingException
- ConversionNotSupportedException
- TypeMismatchException
- HttpMessageNotReadableException
- HttpMessageNotWritableException
- MethodArgumentNotValidException
- MethodValidationException
- HandlerMethodValidationException
- NoHandlerFoundException
- NoResourceFoundException
- AsyncRequestTimeoutException

### SimpleMappingExceptionResolver

예외 클래스와 뷰 이름을 매핑하는 구현체

### ExceptionHandlerExceptionResolver

**@ExceptionHandler**
- 특정 유형의 예외 처리를 하는 메서드를 정의하는 어노테이션
- @ControllerAdvice, @RestControllerAdvice, 일반 빈에 정의
- 일반 빈에 정의한 경우 해당 빈 내부에서 발생하는 예외만 처리 가능
- ModelAndView 또는 ResponseEntity 반환

**@ControllerAdvice, @RestControllerAdvice**
- 전역 예외 핸들러(애플리케이션의 모든 컨트롤러에서 발생하는 해당 유형의 예외 처리)
- @ExceptionHandler 메서드를 포함하는 클래스를 정의하는 어노테이션
- ExceptionHandlerExceptionResolver를 통해 DispatcherServlet에 등록되어 해당 빈 내부의 @ExceptionHandler 메서드가 예외 처리를 할 수 있음
- @ControllerAdvice : ModelAndView 반환
- @RestControllerAdvice : ResponseEntity 반환

**ExceptionHandlerExceptionResolver**
- @ExceptionHandler 메서드를 찾아 예외 처리를 수행함

### ResponseEntityExceptionHandler

HandlerExceptionResolver를 구현하지 않고 ResponseEntity를 반환하는 예외 처리 추상 클래스

기본적으로 표준 spring mvc 예외 처리를 지원함

ResponseEntityExceptionHandler의 구현체는 오버라이딩을 통해 표준 spring mvc 예외 처리 및 커스텀 예외 처리를 확장할 수 있음

핵심 메서드
- createProblemDetail() : ProblemDetail 생성
- handleExceptionInternal() : ProblemDetail을 body로 담은 ResponseEntity 생성

### BasicErrorController

spring boot에서 기본 제공하는 에러 처리 컨트롤러(/error 경로의 요청을 처리)

애플리케이션에서 처리되지 않은 예외가 발생했을 때 동작함

