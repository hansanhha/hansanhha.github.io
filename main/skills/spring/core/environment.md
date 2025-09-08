---
layout: default
title:
---

[Environment](#environment)

[ConfigurableEnvironment](#configurableenvironment)

Property
- [PropertySource](#propertysource)
- [@PropertySource](#propertysource-1)
- [@Value](#value)
- [Converter for Property Injection](#converter-for-property-injection)

[스프링부트 @ConfigurationProperties](../../boot/txt/properties.md)

## Environment

Environment는 스프링에서 사용하는 Profile과 Property를 추상화한 모델(인터페이스)임

스프링 애플리케이션의 환경설정을 담당하는 역할

Profile
- 애플리케이션 컨텍스트 프로파일 이름
- 별다른 지정을 하지 않으면 "default" 프로파일이 활성화됨
- 현재 활성화된 프로파일과 @Profile로 지정한 표현식이 일치할 때 빈으로 등록됨 
- 테스트, 개발, 프로덕션 환경 구분 등으로 사용

Property
- 애플리케이션 컨텍스트에서 사용되는 키 값 매핑 엔트리
- 스프링은 다양한 PropertySource로부터 프로퍼티에 접근함
- 프로퍼티 파일, JVM 시스템 변수, 환경변수, JNDI, Map 객체 등

```java
// Enviroment 인터페이스는 PropertyResolver를 확장하여 프로퍼티 접근을 지원함
public interface Environment extends PropertyResolver {
    
    String[] getActiveProfiles;
    
    String[] getDefaultProfiles;

    default boolean matchesProfiles(String... profileExpressions) {
        return acceptsProfiles(Profiles.of(profileExpressions));
    }

    boolean acceptsProfiles(Profiles profiles);
}
```

```java
// 프로퍼티 접근 인터페이스
public interface PropertyResolver {
    
    boolean containsProperty(String key);
    
    @Nullable
    String getProperty(String key);

    String getProperty(String key, String defaultValue);

    @Nullable
    <T> T getProperty(String key, Class<T> targetType);

    <T> T getProperty(String key, Class<T> targetType, T defaultValue);

    String getRequiredProperty(String key) throws IllegalStateException;

    <T> T getRequiredProperty(String key, Class<T> targetType) throws IllegalStateException;

    String resolvePlaceholders(String text);

    String resolveRequiredPlaceholders(String text) throws IllegalArgumentException;
}
```

### ConfigurableEnvironment

Environment 인터페이스는 프로퍼티에 접근할 수만 있는 메서드를 지원함

ConfigurableEnvironment 인터페이스는 Environment를 확장하여 프로퍼티를 추가/수정하는 등의 애플리케이션 환경설정을 더 세부적으로 할 수 있는 메서드를 지원함

프로파일과 프로퍼티 소스를 동적으로 조작할 때 사용함

## Property

### PropertySource

PropertySource는 프로퍼티(키-값 쌍)에 접근하는 로직을 추상화한 추상 클래스로, 스프링은 구현체들을 통해 여러 프로퍼티 소스에 접근함

```java
public abstract class PropertySource<T> {
    protected final Log logger;
    protected final String name;
    protected final T source;

    public PropertySource(String name, T source) {
        this.logger = LogFactory.getLog(this.getClass());
        Assert.hasText(name, "Property source name must contain at least one character");
        Assert.notNull(source, "Property source must not be null");
        this.name = name;
        this.source = source;
    }

    public boolean containsProperty(String name) {
        return this.getProperty(name) != null;
    }

    @Nullable
    public abstract Object getProperty(String name);
```

애플리케이션 종류에 따라 접근하는 프로퍼티 소스가 정해짐
- Standalone Application(StandardEnvironment)의 경우 : JVM 시스템 변수, 환경변수 
- Web Application(StandardServletEnvironment - StandardEnvironment 상속)의 경우 : StandardEnvironment + 서블릿 컨텍스트 프로퍼티, 서블릿 설정 프로퍼티, JNDI

### @PropertySource

@PropertySource: 애플리케이션 컨텍스트에 프로퍼티 소스를 추가하는 어노테이션
@PropertySources: 여러 개의 프로퍼티 소스를 추가할 때 사용하는 어노테이션 

classpath: file: http: 등의 프로토콜을 통해 접근할 프로퍼티 소스에 대한 위치를 지정할 수 있음

```java
@Configuration
// 클래스패스 하위에 있는 프로퍼티 파일 추가
@PropertySource("classpath:/com/my/app.properties")
public class AppConfig {
    
    // Environment 타입 객체를 애플리케이션 컨텍스트로부터 주입받음
    private final Environment env;
    
    public AppConfig(Environment env) {
        this.env = env;
    }
    
    @Bean
    public TestBean testBean() {
        TestBean testBean = new TestBean();
        // Environment API 사용
        testBean.setName(env.getProperty("testbean.name"));
        return testBean;
    }
}
```

다른 프로퍼티를 사용하여 @PropertySource 프로퍼티 소스 파일 경로를 지정할 수 있음

```java
@PropertySource("classpath:/com/${my.properties:default/path}/app.properties")
/*
    ${my.properties:default/path}
    - placeholder를 사용하여 애플리케이션 컨텍스트에 등록된 프로퍼티 값 사용
    - 만약 프로퍼티가 없는 경우 :default/path로 지정된 기본 값 사용
    - 프로퍼티와 기본 값 모두 없는 경우 IllegalArgumentException 발생
*/
```

### @Value

프로퍼티를 필드에 직접 주입받는 방법

@PropertySource와 마찬가지로 classpath: file: http: 등의 프로토콜 지원

SpEL 사용 가능

```java
@Component
public class MovieRecommender {

    private final String catalog;

    public MovieRecommender(@Value("${catalog.name}") String catalog) {
        this.catalog = catalog;
    }
}
```

**프로퍼티 값이 없을 때 애플리케이션 초기화 에러가 필요한 경우**

PropertySourcesPlaceholderConfigurer 타입의 빈을 등록하면 됨

```java
@Configuration
public class AppConfig {

    // PropertySourcesPlaceholderConfigurer를 Java Config로 설정할 때 static으로 선언
	@Bean
	public static PropertySourcesPlaceholderConfigurer propertyPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}
}
```

### Converter for Property Injection

primitive(String 포함) 타입의 경우 spring에서 변환하여 프로퍼티 값을 주입해주지만

사용자가 만든 객체 타입의 프로퍼티를 주입받으려면 별도의 ConversionService 타입의 빈을 등록해주면 됨 

```java
@Configuration
public class AppConfig {

    @Bean
    public ConversionService conversionService() {
        DefaultFormattingConversionService conversionService = new DefaultFormattingConversionService();
        conversionService.addConverter(new MyCustomConverter());
        return conversionService;
    }
}
```

[스프링부트 @ConfigurationProperties](../../boot/txt/properties.md)