[@ConfigurationProperties](#configurationproperties)

[@EnableConfigurationProperties](#enableconfigurationproperties)

스프링 부트는 외부 프로퍼티 소스의 프로퍼티를 빈 객체 필드에 쉽게 바인딩할 수 있는 어노테이션을 지원함

## @ConfigurationProperties

특정 prefix를 가진 프로퍼티들을 자바 객체에 자동으로 바인딩하는 어노테이션임

코드 내에서 설정 값을 직접 불러오기보다 필요한 프로퍼티를 필드로 선언하여 한 곳에 주입받는 방식

다음과 같은 설정이 필요함
- getter, setter 필수
- @SpringBootApplication 클래스에 ConfigurationPropertiesScan 적용 필요 (@ConfigurationProperties 스캔 목적)

```java
@Component
@ConfigurationProperties(prefix = "my.app")
public class AppProperties {
    
    private String appName;
    
    private String appVersion;
    
    // getter, setter
}
```

```text
# application.yml

my:
  app:
    name: MyApplication
    description: This is an example application.
```

```java
@SpringBootApplication
@ConfigurationPropertiesScan
public class MyApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyApplication.class, args);
    }
}
```

## @EnableConfigurationProperties

@ConfigurationProperties 빈들을 스프링 빈으로 등록할 때 사용하는 어노테이션

@ConfigurationProperties 클래스가 여러 개일 때 선언적으로 관리할 수 있음

@EnableConfigurationProperties로 @ConfigurationProperties를 빈으로 등록할 때는 @ConfigurationPropertiesScan을 필요로 하지 않음

```java
@Configuration
// @ConfigurationProperties 클래스인 AppProperties와 SecurityProperties가 자동으로 빈으로 등록됨
@EnableConfigurationProperties({AppProperties.class, SecurityProperties.class})
public class AppConfig {
    
}
```