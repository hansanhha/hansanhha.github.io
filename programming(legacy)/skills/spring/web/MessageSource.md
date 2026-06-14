---
layout: default
title:
---

## MessageSource

```java
public interface MessageSource {
    @Nullable
    String getMessage(String code, @Nullable Object[] args, @Nullable String defaultMessage, Locale locale);

    String getMessage(String code, @Nullable Object[] args, Locale locale) throws NoSuchMessageException;

    String getMessage(MessageSourceResolvable resolvable, Locale locale) throws NoSuchMessageException;
}
```

국제화(i18n) 및 지역화(l10n)를 위한 메시지 관리 인터페이스(spring core)

**동작 과정**
1. 미리 메시지 소스 파일에 메시지 코드와 그에 대응하는 메시지를 키-값으로 정의
   1. 메시지 소스 파일은 Locale별로 구성 가능
   2. 각 메시지 소스 파일에 로케일 정보를 포함시키면 됨(messages_en.properties, messages_ko.properties)
2. MessageSource를 스프링 컨텍스트에 등록
   1. 스프링 부트의 auto-configuration으로 인해 자동 등록되고 메시지 소스 파일의 이름이 "messages"로 설정됨
   2. 추가적으로 메시지 소스 파일 이름을 정의하려면 `spring.messages.basename` 속성에 지정
3. MessageSource.getMessage()를 통해 메시지 소스 파일에 정의된 메시지를 읽어옴
4. 이 때 code, args, defaultMessage, locale 정보를 파라미터로 받음

**MessageSource 구현체**
- ResourceBundleMessageSource : 메시지 소스 파일(.properties)에서 메시지를 로드
- ReloadResourceBundleMessageSource : 애플리케이션 실행 중 메시지 소스 파일의 변경 사항 반영, 캐싱 기간 설정 가능
- StaticMessageSource : 테스트용 MessageSource

## MessageCodesResolver

```java
public interface MessageCodesResolver {
    String[] resolveMessageCodes(String errorCode, String objectName);

    String[] resolveMessageCodes(String errorCode, String objectName, String field, @Nullable Class<?> fieldType);
}
```

데이터 바인딩 및 검증 과정에서 발생하는 에러 코드(ObjectError, FieldError)를 메시지 코드로 변환하는 인터페이스

변환된 메시지 코드를 MessageSource를 통해 메시지를 읽어옴

**MessageCodesResolver 구현체**

DefaultMessageCodesResolver : 특정 필드에 대한 오류 코드를 바탕으로 메시지 코드 생성

DefaultMessageCodesResolver 동작 방식(아래의 순서로 메시지 코드 변환)
- 객체 이름과 필드 이름을 포함한 코드(simpleUser.email.required)
- 필드 이름만 포함한 코드(email.required)
- 객체 이름만 포함한 코드(simpleUser.required)
- 글로벌 코드(required)

BindingResult 또는 Errors 인터페이스와 함께 동작

## LocaleResolver

현재 요청의 Locale을 결정하는 객체 

MessageSource와 함께 동작하며 결정된 Locale에 맞는 메시지 제공

**LocaleResolver구현체**
- FixedLocaleResolver
- AcceptHeaderLocaleResolver
- SessionLocaleResolver
- CookieLocaleResolver

**LocalChangeInterceptor**
- HTTP Request의 특정 파라미터를 기반으로 Locale을 변경할 수 있는 Interceptor

## ErrorResponse + MessageSource

MessageSource는 bean validation, spring mvc 표준 예외에 대한 메시지를 처리하는 데 사용되는 경우가 많았음

spring 6.0 부터 추가된 [ErrorResponse](Error(exception)%20handling.md#errorresponse)를 통해 예외 메시지를 처리할 수 있음

```java
// ErrorResponse
default ProblemDetail updateAndGetBody(@Nullable MessageSource messageSource, Locale locale) {
  if (messageSource != null) {
      String type = messageSource.getMessage(getTypeMessageCode(), null, null, locale);
      if (type != null) {
          getBody().setType(URI.create(type));
      }
      Object[] arguments = getDetailMessageArguments(messageSource, locale);
      String detail = messageSource.getMessage(getDetailMessageCode(), arguments, null, locale);
      if (detail != null) {
          getBody().setDetail(detail);
      }
      String title = messageSource.getMessage(getTitleMessageCode(), null, null, locale);
      if (title != null) {
          getBody().setTitle(title);
      }
  }
  return getBody();
}
```

updateAndGetBody()는 예외 타입, argument, detail message code를 기반으로 MessageSource를 통해 메시지로 변환한 뒤 

각각 ProblemDetail의 type, detail, title로 설정하는 메서드임

근데 사실 ProblemDetail은 MessageSource를 사용하지 않고도 직접 예외 메시지를 처리할 수 있기 때문에 편한 방법으로 사용하면 되지 않을까 싶기도 하고