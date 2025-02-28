## Static Web Page

웹 서버에서 정적 웹 페이지의 렌더링을 하고 반환하는 기술

## CGI(Common Gateway Interface)

웹 서버의 동적 컨텐츠 처리를 위한 기술

동작 구조
1. 요청
2. 웹 서버 수신 및 대응되는 CGI 프로그램 확인
3. 운영체제에 의해 CGI 프로그램 실행(동적 처리)
4. 렌더링된 HTML 반환

단점
- 요청마다 CGI 프로세스가 실행됨
- 스크립트 언어 CGI 프로그램의 경우 매번 스크립트를 해석해야 됨
- C, Perl, Shell Script로 개발했는데, 이 언어들은 대규모 웹 서버 설계에 적합하지 않음

웹 서버에서 임의의 프로그램을 실행할 수 있기 때문에 간혹 쓰이는 경우도 있음 

## Server Side Scripting

웹 서버 자체에서 동적 페이지를 처리하는 기술

요청마다 프로세스를 생성하여 운영체제를 통해 실행하지 않고 웹 서버 내의 스레드를 통해 직접 처리

자바같은 객체지향 언어으로 대규모 웹 서버를 개발

Servlet
- Java EE 사양의 일부로 HTTP를 처리하고 응답을 생성하는 서버 컴포넌트
    - HTML 페이지 생성, 세션 관리, DB 처리 등
    - 자바로 작성되며 JVM 위에서 실행
- ServletContainer가 Servlet의 생명주기와 실행을 관리함
- 멀티 스레드 모델 : 서블릿 인스턴스 하나(스레드)와 여러 클라이언트 요청마다 별도의 스레드를 할당하여 요청 처리 
- DispatchType : 서블릿 요청이 처리되는 방식
    - REQUEST : 기본 디스패치 타입
    - FORWARD : 다른 Servlet이나 JSP로 요청을 전달할 때 사용(RequestDispatcher.forward())
    - INCLUDE : 다른 Servlet이나 JSP를 응답에 포함시킬 때 사용(RequestDispather.include())
    - ERROR   : 에러가 발생했을 때(/error)
    - ASYNC   : 비동기 처리(Servlet 3.0 이상)

JSP
- JSP는 HTML 페이지에 자바 코드를 삽입하는 방식, Servlet은 자바 클래스에 HTML을 삽입하는 방식
- 자바에서 HTML 관련 코드를 Servlet보다 편리하게 작성할 수 있는 Server Side Scripting 기술
- Servlet을 기반으로 하며 JSP 페이지는 Servlet으로 변환되어 실행됨
- MVC 아키텍처를 지원함
- JSP - View, Servlet - Controller

## MVC 

두 가지의 관심사(비즈니스 로직, 화면)를 분리하는 디자인패턴

구성 요소
- Model : 데이터와 비즈니스 로직 처리
- View  : 화면 처리
- Controller : 요청 수신/검증 및 Model과 View에게 명령 전달

동작 구조
1. 컨트롤러 요청 수신
2. 컨트롤러 -> 모델 데이터 변경
3. 컨트롤러 -> 뷰 화면 처리

SSR(서버 사이드 렌더링) : 서버에서 화면 처리 담당

CSR(클라이언트 사이드 렌더링) : 클라이언트에서 화면 처리 담당

## Spring MVC, FrontController

MVC 패턴의 Controller(Servlet)는 path(요청 url)에 마다 개별 Controller(Servlet)를 만듦

-> Controller가 수행하는 검증, 세션 관리, 필터 등의 코드가 중복됨

[FrontController](https://martinfowler.com/eaaCatalog/frontController.html)는 모든 요청을 처리하는 Controller를 두는 디자인패턴으로 코드 중복없이 재사용할 수 있는 유연한 구조를 가짐

스프링 MVC 구성요소
- DispatcherServlet
    - FrontController 역할
    - 요청에 맞는 Component 검색 및 실행
    - View 해결
    - 에러 처리 등
- Handler Mapping
    - 요청(URL)을 어느 Controller의 메서드가 처리할 지 결정
    - RequestMappingHandlerMapping : @RequestMapping 기반 어노테이션이 적용된 컨트롤러 메서드 매핑
- Controller(Handler)
    - 요청을 처리하는 컴포넌트
    - URL 별로 로직을 수행할 메서드를 가짐
- View Resolver
    - Controller가 반환하는 View 이름을 기반으로 실제 View를 찾는 역할
    - View 파일을 렌더링하여 클라이언트에게 응답
    - InteralResourceViewResolver : View 이름을 기반으로 내부 리소스를 찾는 View Resolver
- View
    - View 파일

스프링 MVC 계층 구조
- Presentation Layer
    - 요청을 처리하는 계층
    - Controller, View
- Business Logic Layer
    - 애플리케이션 핵심 로직을 수행하는 계층
    - Service : 비즈니스 로직 집합
    - Domain Model : 비즈니스 로직/연산, 데이터
- Data Access Layer
    - DB 접근, 영속성 기술 수행 계층
    - DAO(Data Access Object) : 
    - Repository : 

DispatcherServlet과 WebApplicationContext 관계
1. 각 DispatcherServlet은 자신만의 Servlet WebApplicationContext가 존재
2. 또한 애플리케이션 로딩 시점에 생성되는 Root WebApplicationContext를 부모로 설정

Root WebApplicationContext 
- 애플리케이션의 공통 컴포넌트(서비스 계층, 데이터 액세스 계층)
- ContextLoaderListnener에 의해 초기화
    - 스프링 - web.xml, application-context.xml
    - 스프링부트 - @SpringBootApplication

Servlet WebApplicationContext
- 프레젠테이션 계층(Controller, View Resolver, Handler Mapping)
- DispatcherServlet 초기화 시 자동 생성
    - 스프링 - dispatcher-servlet.xml
    - 스프링부트 - @EnableAutoConfiguration을 통해 내부적으로 DispatcherServlet 초기화 및 Servlet WebApplicationContext 자동 처리
- Servlet WebApplicationContext 빈은 특정 Servlet에서만 접근할 수 있음

## Filter

Servlet과 마찬가지로 Java/Jakarata EE 스펙에 포함됨

특징
- HTTP Request 사전 처리, 사후 처리를 담당하는 요소(Servlet 실행 전/후 동작)
- url(path)에 매핑되는 Filter 동작
- 다음 Filter의 실행을 중단하거나 실행 결과를 무시하고 직접 응답을 반환할 수 있음
- ServletContainer 내에서 동작

FilterChain
- FilterChain은 여러 Filter가 순서대로 연결된 체인 형태로 작동
- Servlet은 모든 FilterChain의 Filter가 실행된 이후 마지막에 호출됨

## Interceptor

Spring MVC 스펙에 포함됨

특징
- DispatcherServlet에서 Controller(Handler)로 요청 위임 전/후/완료 후에 동작하는 요소
- url(path)에 매핑되는 Interceptor 동작
- 스프링 웹 애플리케이션 컨텍스트 내에서 동작함

시점
- 요청 전 : Controller가 요청을 처리하기 전
- 요청 후 : Controller가 요청을 정상적으로 처리한 후(View 렌더링 전) 
- 요청 완료 후 : 예외 발생 여부와 무관하게 Controller가 요청을 처리한 후

차이점
- Filter : 스프링 컨텍스트 도달 전/후로 필요한 공통 로직 처리 용도
- Interceptor : 스프링 컨텍스트 내에서 필요한 공통 로직 처리 용도

## Error, Exception Handling

예외는 크게 두 가지로 구분
1. 클라이언트 요청에 의한 예외 발생
2. 서버의 로직 처리 과정 중 예외 발생

[자세한 에러 발생 부분](./error point)

### Server Error, Exception Handling

웹 서버의 에러
- 서버 자체(네트워크 레벨 등) 문제
- 서버가 요청을 제대로 처리할 수 없는 상황
- web.xml
    - HTTP Status Code 별로 지정된 에러 페이지 반환

웹 애플리케이션에서 예외를 처리할 수 있는 부분
- Filter
    - FilterChain.doFilter() 생략 -> 스프링 컨텍스트 도달 전 예외 처리
    - 직접 HttpServletResponse을 통해 Status Code, Header, Message Body 반환
    - 웹 서버는 Filter에서 처리한 응답을 그대로 클라이언트에게 반환
- Interceptor
    - 시점에 따른 에러 처리
    - 요청 전 에러 처리 : Controller 호출 X, HttpServletResponse로 직접 응답 반환
    - 요청 완료 후 에러 처리 : 클라이언트에게 응답 반환 후 호출되므로 직접 응답 반환 불가, 리소스 정리, 로깅 등 후처리 작업 수행
- Controller, 비즈니스 로직
    - web.xml, Filter, Interceptor, BasicController, @ControllerAdvice 중 선택하여 에러 처리 가능
    - Controller와 그 이후 예외 발생 시 DispatcherServlet에서 처리되지 않은 모든 예외를 Catch함
    - 적절한 @ControllerAdvice, @ExceptionHandler를 검색하고 예외 처리 수행
    - 등록된 Interceptor의 afterCompletion() 호출
- Web Request
    - 클라이언트의 요청이 올바르지 못한 경우(Binding, Validation, HTTP Method, Authentication, Authroization 등)
    - Filter, Interceptor, @ControllerAdvice 등에서 에러 처리

BasicController
- 스프링부트 애플리케이션 내부에서 예외 발생 시 임베디드 웹 서버(Tomcat)까지 예외 전달, 임베디드 웹 서버는 에러 처리를 위해 /error 경로로 요청을 재전송(DispatcherType.ERROR)
- 재전송된 에러 처리 요청은 애플리케이션에서 ErrorController 인터페이스를 통해 에러 처리 가능
- BasicController는 스프링부트에서 제공하는 ErrorController 구현체로 두 가지 동작방식이 있음
    - 클라이언트가 받는 미디어 타입(Accept, produces)이 text/html이라면 View를 찾음
        - 에러 페이지 매핑 : src/main/resources/templates/error 디렉토리 아래에 상태 코드별 에러 페이지 배치
            - 404.html : 404 에러에 대한 HTML
    - 아니라면 ResponseEntity 반환

@ControllerAdvice, @ExceptionHandler
- 에러 처리를 중앙화하여 관리할 수 있는 스프링 어노테이션
- @ControllerAdvice
    - 애플리케이션 전역에서 발생할 수 있는 예외를 잡아 처리하는 클래스에 붙이는 어노테이션
    - 해당 클래스 내에서 @ExceptionHandler 메서드를 통해 특정 예외를 잡아 처리함
- @ExceptionHandler
    - @Controller 클래스 또는 Controller 내부에서 특정 예외를 잡아 처리할 메서드에 붙이는 어노테이션
    - 예외를 처리하고 클라이언트에 응답함
        - Controller와 응답 포맷 동일
        - HTML 응답 : ModelAndView
        - Json 응답 : ResponseEntity
    - 웹 서버까지 예외를 전파하지 않고 스프링 컨텍스트 내에서 예외 처리
- Interceptor
    - Controller와 그 이후에서 발생한 예외의 경우 Interceptor.postHandle()은 호출되지 않고 @ExceptionHandler 처리 과정이 진행된 후 afterCompletion() 호출
    - 다만 Interceptor.preHandle() 내에서 예외가 발생한 경우 @ControllerAdvice로 예외를 잡히지 않는 경우가 있음

HandlerExceptionResolver
- 예외 처리를 위한 스프링 인터페이스
- ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, @Nullable Object handler, Exception ex)
- 반환 값에 따른 동작 방식
    - response.sendError()와 new ModelAndView() : 정상 흐름으로 Servlet 리턴되지만 WAS에게 Error를 알려 예외 처리가 작동됨
    - 특정 ModelAndView : View 렌더링
    - null : 다음 ExceptionResolver 찾아서 실행, 처리할 수 없는 경우 기존 발생한 예외를 Servlet 밖으로 던짐(WAS 예외 처리 동작)
- 구현체(스프링 부트는 HandlerExceptionResolverComposite에 순서대로 자동으로 등록해줌)
    - ExceptionHandlerExceptionResolver : @ExceptionHandler 처리
    - ResponseStatusExceptionResolver : 예외에 따른 response 상태 코드 지정(WAS 예외 처리 동작)
    - DefaultHandlerExceptionResolver : 스프링 MVC 내부 예외 처리(WAS 예외 처리 동작)

비즈니스 로직 예외 처리 workflow
- 해결되지 않은 예외 Controller 밖으로 전파
- DispatcherServlet 예외 확인, 등록된 HandlerExceptionResolver 실행
- ExceptionHandlerExceptionResolver -> Controller 또는 @ControllverAdvice 클래스 내에서 해당 예외 처리 @ExceptoionHandler 메서드 검색 -> 예외 처리
    - 바인딩이나 필드 검증 예외 시(BindingResult가 없는 경우) MessageSource를 통해 메시지 사용 가능(메시지 소스 파일 정의 필요 - MessageCodesResolver의 메시지 코드 변환 규칙 참고)
- @ExceptionHandler로 예외 처리를 하지 못한 경우, HandlerExcpetionResolverComposite에 등록된 다른 Resolver 호출 -> 예외 처리
    - 대부분 WAS에 response 상태 코드를 전달하여 WAS 예외 처리 동작으로 인해 내부 요청 발생
    - 스프링부트의 BasicController 동작 -> 클라이언트 Accept 값에 따라 HTML 페이지 또는 ResponseEntity 응답

### Client Request Exception Handling

클라이언트 요청 자체의 에러 종류
- Request Data Error(Binding, Validation Error)
    - 요청 데이터 타입과 요청 처리 메서드 파라미터 타입 불일치
    - 필드 유효성 검증 불만족
    - 비즈니스 유효성 검증 불만족
    - @Valid, @Validated, BindingResult을 통해 처리
- Request format Error
    - HTTP Method 불일치
    - Media Type 불일치(consumes, produces - Content-Type, Accept)
    - HttpMediaTypeNotSupportedException, HttpMediaTypeNotAcceptableException 발생
- Authentication, Authorization Error
    - 스프링 시큐리티 - AuthenticiationError, AccessDeniedException 발생

타입 변환에 실패한 경우 발생할 수 있는 스프링 예외
- MethodArgumentTypeMismatchException
    - Controller 메서드의 파라미터 타입과 클라이언트 데이터 타입 불일치 시 발생
- TypeMismatchException 
    - MethodArgumentTypeMismatchException 상위 클래스
    - 프로퍼티, 필드 등의 타입 변환 실패 시 발생
- HttpMessageNotReadableException
    - @RequestBody를 통해 HTTP Request Message Body를 객체로 바인딩할 수 없는 경우 발생
- ConversionFailedException
    - 스프링 ConversionService를 사용하여 데이터 타입 변환을 하다가 실패했을 때 발생
    - MethodArgumentTypeMismatchException보다 더 일반적인 상황에 발생
- 타입 변환 실패는 바인딩 과정 중 예외가 발생하므로 Controller 메서드는 실행되지 않음
    - 이미 Interceptor.preHandle()이 수행된 시점이고, @ExceptionHandler로 발생된 예외를 잡아 처리 가능

@Valid, @Validated
- 바인딩할 클라이언트 데이터를 검증하기 위한 어노테이션
- @Valid : Java Bean Validation - 객체 그래프에 대한 유효성 검사 수행 어노테이션(메서드, 파라미터, 클래스 레벨 적용 가능)
- @Validated : Spring Bean Validation - @Valid와 동일한 기능 제공, 추가적으로 그룹화 유효성 검사 지원

BindingResult
- 클라이언트 데이터를 @ModelAttribute, @ResponseBody를 통해 바인딩하는 과정에서 발생하는 에러를 보관하는 객체
- 요청을 처리하는 Controller 메서드의 @Validated가 적용된 파라미터 뒤에 추가하면 스프링이 바인딩 과정 중 발생한 바인딩, 필드 예외를 캡처함
- 비즈니스 검증 예외의 경우 수동으로 BindingResult에 담아줘야 함
- 글로벌 에러와 필드 에러로 구분해서 처리
    - 글로벌 오류 : 특정 필드에 국한되지 않는 오류
    - 필드 오류   : 특정 필드와 관련된 오류

Validator
- 객체 Validaion 로직을 수행하는 스프링의 인터페이스
- supports, validate 메서드
- @InitBinder, WebDataBinder를 통해 Validator 실행

스프링부트 Validator
- LocalValidatorFactoryBean
- 스프링 부트는 스프링 Bean Validation API를 기반으로 필드 검증을 자동으로 수행하는 글로벌 Validator 제공
- spring-boot-starter-validation 의존성 필요

Method Validation
- @Validated를 메서드 레벨에 적용해서 파라미터와 반환 값에 검증 로직을 추가할 수 있음
- MethodValidationPostProcessor를 통해 메서드 실행 시점에 파라미터나 반환 값에 대한 유효성 검사를 진행
- Bean Validation API 확장 기능
- spring 5.2 ~ 

ResponseEntityExceptionHandler
- 다양한 예외 유형에 대한 처리 로직을 제공하는 스프링 추상 클래스 
- 클래스명 그대로 ResponseEntity 형태로 클라이언트에게 응답
- 기본적으로 스프링 내부 예외를 처리해줌
- 이 클래스를 상속하고 @ControllerAdvice를 적용시켜 전역 예외 처리 클래스로 사용 가능

## Message

MessageSource
- 국제화 및 지역화를 위한 메시지 관리 인터페이스(spring core)
- 애플리케이션에서 사용하는 메시지를 프로퍼티 파일에 정의하면, MessageSource를 통해 메시지를 읽어올 수 있음
- getMessage(String code, Object[] args, String defaultMessage, Locale locale)
    - 메시지 코드, 메시지 포맷에 사용될 인자, 기본 메시지, Locale 정보를 받아 적절한 메시지를 반환함
- 구현체
    - ResourceBundleMessageSource : 메시지 소스 파일(.properties)에서 메시지를 로드
    - ReloadResourceBundleMessageSource : 애플리케이션 실행 중 메시지 소스 파일의 변경 사항 반영, 캐싱 기간 설정 가능
    - StaticMessageSource : 테스트용 MessageSource
- MessageSource 타입 스프링 빈 등록 필요(메시지 소스 파일 basename, 기본 인코딩 지정)
- 스프링부트 autoconfiguration : basename을 messages으로 하는 자동 MessageSource 빈 등록
    - `messages.properties, messages_en.properties, messages_ko.properties` 
    - spring.messages.basename 속성으로 추가 메시지 소스 파일 이름 설정 가능

LocaleResolver
- 현재 요청의 Locale을 결정하는 객체, MessageSource와 함께 동작하며 결정된 Locale에 맞는 메시지 제공
- 구현체
    - FixedLocaleResolver
    - AcceptHeaderLocaleResolver
    - SessionLocaleResolver
    - CookieLocaleResolver
- LocalChangeInterceptor : HTTP Request의 특정 파라미터를 기반으로 Locale을 변경할 수 있는 Interceptor

MessageCodesResolver
- 데이터 바인딩 및 검증 과정에서 발생하는 에러 코드를 메시지 코드로 변환하는 인터페이스(spring validation)
- BindingResult 또는 Errors 인터페이스와 함께 동작
- DefaultMessageCodesResolver : 특정 필드에 대한 오류 코드를 바탕으로 메시지 코드 생성
    - 동작 방식(다음과 같은 순서로 메시지 코드 생성)
        - 객체 이름과 필드 이름을 포함한 코드(simpleUser.email.required)
        - 필드 이름만 포함한 코드(email.required)
        - 객체 이름만 포함한 코드(simpleUser.required)
        - 글로벌 코드(required)
- 생성된 메시지 코드는 MessageSource를 통해 실제 메시지 사용 가능 

## Web Util Object

@InitBinder
- Controller, @ControllerAdvice 클래스 내부에 선언된 메서드에 적용
- 적용된 메서드는 WebDataBinder를 초기화하는 데 사용되며, 요청 핸들러 메서드가 호출되기 전 실행됨

WebDataBinder
- 특정 Controller의 웹 요청 파라미터 -> 객체 바인딩, 프로퍼티 convert, 프로퍼티 format 지정 등을 수행하는 객체
- PropertyEditor, Converter, Formatter, Validator 등록 가능
- 외부 클라이언트로부터 수정되거나 접근하면 안되는 객체 그래프(object graph)의 일부를 노출시켜 보안 문제를 일으킬 수 있음

## Data Binding, Type Conversion, Formatting Mechanism

HandlerMethodArgumentResolver
- 클라이언트 요청을 처리할 Controller 메서드의 매개변수를 채워주는 인터페이스
- 매개변수의 타입에 따라 적절한 ArgumentResolver가 선택됨

Converter
- S 타입 <-> T 타입 변환 인터페이스

HttpMessageConverter
- HTTP Request 본문을 Controller 메서드 매개변수 타입에 맞게 변환하는 인터페이스
- @RequestBody, @ResponseBody가 붙은 메서드나 반환 타입에 대해 동작

ConversionService
- 타입 변환을 관리하는 중앙 인터페이스
- 요청받은 타입 변환을 수행할 수 있는 Converter를 찾고 변환 작업 실행

Formatter
- 객체 <-> 문자열 변환 인터페이스
- Converter는 범용, Formatter는 문자열 및 Locale 특화 Converter 

FormattingConversionService
- Formatter와 Converter를 모두 관리할 수 있는 구현체
- 스프링부트는 DefaultFormattingConversionService를 상속받은 WebConversionService를 autoconfiguration함
- WebConversionService는 DataBinder에 등록된 Formatter와 Converter를 통해 Request 파라미터의 타입을 변환함

## Request Parameters, Annotation 

클라이언트에서 데이터를 전송할 수 있는 방법과 스프링에서 받는 방법

Query String Parameters
- URL로 파라미터를 전송하는 방법
- http://localhost:8080?key=value&key2=value2
    - URL 끝에 ?를 붙이고 key=value 파라미터 추가
    - 여러 데이터를 전송할 때는 &로 구분
- 정렬, 필터링, 페이징, 검색 등에 사용
- @RequestParam
    - 메서드 파라미터가 primitive 타입일 경우 생략 가능

Path Parameters
- URL로 파라미터를 전송하는 방법
- http://localhost:8080/QBEUsers/2
    - 각 데이터가 URL 경로에 직접 포함됨
    - RESTful API에서 소스를 지정하기 위해 사용
- @PathVaraible

Form Data
- 사용자가 HTML Form 태그에 입력한 파라미터를 전송하는 방법
- 주요 데이터 형식
    - application/x-www-form-urlencoded : key=value&key2=value2 형태의 텍스트 데이터
    - multipart/form-data : 파일 업로드같은 바이너리 데이터 
- @ModelAttribute : Model 데이터를 View에 전달하거나, HTTP Request Parameter를 객체에 바인딩 
    - 메서드 파라미터가 객체 타입일 경우 생략 가능
    - 클래스 내의 모든 View에 공통으로 사용될 속성을 Model에 추가해야 될 경우 메서드 레벨에 선언
- @RequestParam

HTTP Message Body
- HTTP 요청 본문에 담는 데이터
- 클라이언트에서 Content-Type: application/json 명시 필요
- @RequestBody : HTTP 요청 본문 데이터(json, xml 등)를 Java 객체로 바인딩
    - HttpMessageConverter를 통해 바인딩

Header Parameters
- HTTP Header에 포함되는 파라미터
- 인증 토큰, 컨텐츠 타입, 언어 설정 등에 사용
- `Authroziation: Bearer TOKEN_VALUE`
- @RequestHeader

Cookie Parameters
- 웹 서버가 사용자 브라우저에 저장하는 데이터
- 사용자 세션 관리, 개인화 등에 사용
- HTTP 요청 시 Cookie 헤더가 자동으로 전송됨
- @CookieValue

## DTO, VO, Command Object

자바 기반 애플리케이션에서 효율적인 데이터 전달 및 표현을 위해 사용되는 패턴이자 개념

DTO(Data Transfer Object)
- 계층 간 데이터 교환을 위한 객체
- DB의 데이터 구조와 클라이언트가 요구하는 데이터 구조 사이에서 적절한 데이터 조정, 전달을 위해 사용됨
- 로직을 갖지 않음
- 각 필드에 대해 getter/setter 포함 가능

VO(Value Object)
- 값을 표현하는 객체
- 값의 집합, 도메인 모델에서 비즈니스 개념을 표현하는 데 사용됨
- 불변성과 동등성에 초점을 맞춤 
    - 생성 후 상태 변경 불가능
    - 속성 값이 모두 같을 때 동일하다고 판단

Command Object
- 사용자의 입력(Form)이나 API 요청(Request Message Body)을 통해 전송된 데이터를 처리하는 데 사용되는 객체
- 요청 데이터를 담는 용도, 비즈니스 로직을 처리는 데 필요한 데이터를 Controller로 전달하는 역할
- 검증 로직 포함
- @RequestBody, @ModelAttribute 사용

## Spring MVC Workflow

Business Logic Process Workflow

- HTTP Request
- Web Server
- FilterChain
- DispatcherServlet -> HandlerMapping(HandlerExecutionChain)
- DispatcherServlet -> LocalResolver
- Interceptor.preHandle
    - Error
        - Global Exception Processor(@ExceptionHandler)
        - Interceptor.postHandle/afterCompletion call X
- DispatcherServlet -> HandlerAdpter
- HandlerMethodArgumentResolver(FormattingConvesionService, HttpMessageConverter)
- Controller -> Service -> DAO/Repository
    - Business Error or Request Error
    - HandlerExceptionResolverComposite
        - ExceptionHandlerExceptionResolver(@RestControllerAdvice, @ControllerAdvice, @ExceptionHandler) -> HTTP Response
        - ResponseStatusExceptionResolver -> response.sendError -> WAS(/error) -> BasicController -> HTTP Response
        - DefaultHandlerExceptionResolver -> response.sendError -> WAS(/error) -> BasicController -> HTTP Response
- DispatcherServlet -> HttpMessageConveter (@RestController)
    - MappingJackson2HttpMessageConverter
- DispatcherServlet -> ViewResolver (only SSR)
- DispatcherServlet -> View (only SSR)
- Interceptor.postHandle/afterCompletion
    - Error
        - postHandle : Global Exception Processor(@ExceptionHandler), afterCompletion call O
        - afterCompletion : processing X, logging
- FilterChain
- HTTP Response

## Web Application Server

### Path

ContextPath
- 웹 애플리케이션의 루트 경로
- 웹 애플리케이션이 서버에 배포될 때 여러 애플리케이션이 배포될 수 있음
- 각 애플리케이션을 구분하기 위해 고유한 ContextPath를 지정
- http://localhost:8080/{contextPath}/

ServletPath
- 요청이 도달한 서블릿을 식별하는 경로
- 특정 서블릿이나 컨트롤러가 처리할 요청의 범위를 결정
- http://localhost:8080/{contextPath}/{servletPath}

스프링 부트의 기본 ContextPath, ServletPath
- ContextPath : / (server.servlet.context-path 프로퍼티 설정)
- ServletPath : / (DispatcherServlet)