---
layout: default
title:
---

## Profile

```
@Configuration
@Profile("development")
public class DataConfig {
    
    @Bean
    public DataSource dataSource() {
        ...
    }
}

@Component
@Profile("development")
public class ...
```

```
@Configuration
public class DataConfig {

    @Bean("dataSource")
    @Profile("development")
    public DataSource EmbeddedDataSource() {
        ...
    }

    @Bean("dataSource")
    @Profile("production")
    public DataSource MySqlDataSource() throws Exception{
        ...
    }
}
```
@Bean 메서드를 오버로드 하는 경우 빈 이름을 통일시켜 @Profile을 적용해야 올바른 동작을 함

### Profile Expression, Annotation

표현식을 통해 다양하게 Profile을 활성화할 수 있음

! : NOT, 해당 Profile이 아닐 때
& : AND
| : OR

&와 |를 같이 사용하려면 괄호 사용 필요

```
@Profile("production & us-east")
@Profile("production & (us-east | eu-central)")
@Profile({"{p1, p2}"}) - p1 | p2와 동일
```

Profile 별 메타 어노테이션
```
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Profile("production")
public @interface Production {
}
```

### Profile 활성화

활성화 방법
1. spring.profiles.active 프로퍼티 지정
    - -Dspring.profiles.active="p1,p2"
2. ApplicationContext를 통해 Environment API 사용
    - ApplicationContext.getEnvironment.setActiveProfiles("p1", "p2")

### Default Profile

Default Profile : Default

Default Profile 이름 변경 방법
1. spring.profiles.default 프로퍼티 지정
2. ApplicationContext를 통해 Environment API 사용
    - ApplicationContext.getEnvironment.setDefaultProfiles("development")