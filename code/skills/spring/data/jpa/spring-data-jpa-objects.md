---
layout: default
title:
---

## JpaRepositoryFactoryBean

## JpaRepositoryFactory

## JpaRepositoryConfigurationExtension

스프링 데이터는 리포지토리 스캔 및 등록 과정을 추상화하여 공통 로직으로 처리하면서, 각 데이터 모듈 별 확장 설정을 할 수 있는 [RepositoryConfigurationExtension](../../common/spring%20data%20repository%20config%20objects.md#repositoryconfigurationextension) 인터페이스를 제공한다

스프링 데이터 JPA는 JpaRepositoryConfigurationExtension을 통해 리포지토리 자동 생성 과정에서 스프링 데이터 JPA만의 확장 설정을 진행한다

#### JpaRepositoryConfigurationExtension에서 등록하는 빈 목록
- SharedEntityManagerCreator
- EntityManagerBeanDefinitionRegistrarPostProcessor
- JpaMetamodelMappingContextFactoryBean
- PersistenceAnnotationBeanPostProcessor
- DefaultJpaContext
- JpaMetamodelCacheCleanup
- JpaEvaluationContextExtension

### 필드

```java
/* -------------- 문자열 전역 상수 필드 --------------*/
private static final Class<?> PAB_POST_PROCESSOR = PersistenceAnnotationBeanPostProcessor.class;
private static final String DEFAULT_TRANSACTION_MANAGER_BEAN_NAME = "transactionManager";
private static final String ENABLE_DEFAULT_TRANSACTIONS_ATTRIBUTE = "enableDefaultTransactions";
private static final String JPA_METAMODEL_CACHE_CLEANUP_CLASSNAME = "org.springframework.data.jpa.util.JpaMetamodelCacheCleanup";
private static final String ESCAPE_CHARACTER_PROPERTY = "escapeCharacter";

/* -------------- 필드 --------------*/
private final Map<Object, String> entityManagerRefs = new LinkedHashMap<>();
```

### 정보 제공 구현 메서드

RepositoryConfigurationExtension 구현 메서드

```java
// 스프링 데이터 JPA 모듈 이름 반환(jpa)
@Override
public String getModuleName() {
    return "JPA";
}

// RepositoryFactoryBean 구현체 이름 반환
@Override
public String getRepositoryFactoryBeanClassName() {
    return JpaRepositoryFactoryBean.class.getName();
}

@Override
protected String getModulePrefix() {
    return getModuleName().toLowerCase(Locale.US);
}

// 리포지토리 인터페이스를 평가할 때 스캔해야 될 도메인 타입 어노테이션 반환
// @Entity @MappedSuperClass
@Override
protected Collection<Class<? extends Annotation>> getIdentifyingAnnotations() {
    return Arrays.asList(Entity.class, MappedSuperclass.class);
}

// 엄격하게 리포지토리 매치를 할 때 매치돼야 할 리포지토리 인터페이스 타입 반환
// JpaRepository
@Override
protected Collection<Class<?>> getIdentifyingTypes() {
    return Collections.<Class<?>> singleton(JpaRepository.class);
}

// aot 컴파일러 관련
@Override
public Class<? extends BeanRegistrationAotProcessor> getRepositoryAotProcessor() {
    return JpaRepositoryRegistrationAotProcessor.class;
}
```

### 빈 등록 구현 메서드

RepositoryConfigurationExtension.registerBeansForRoot와 postProcess 구현

SharedEntityManagerCreator 및 스프링 데이터 JPA 관련 빈들을 등록함

[registerIfNotAlreadyRegistered](../../common/spring%20data%20repository%20config%20objects.md#repositoryconfigurationextensionsupport의-빈-등록-static-메서드)

```java
@Override
public void registerBeansForRoot(BeanDefinitionRegistry registry, RepositoryConfigurationSource config) {

    super.registerBeansForRoot(registry, config);

    // SharedEntityManagerCreator
    registerSharedEntityMangerIfNotAlreadyRegistered(registry, config);

    Object source = config.getSource();

    /* ------------------ spring data jpa 관련 빈 등록 ------------------ */
    
    registerLazyIfNotAlreadyRegistered(
            () -> new RootBeanDefinition(EntityManagerBeanDefinitionRegistrarPostProcessor.class), registry,
            EM_BEAN_DEFINITION_REGISTRAR_POST_PROCESSOR_BEAN_NAME, source);

    registerLazyIfNotAlreadyRegistered(() -> new RootBeanDefinition(JpaMetamodelMappingContextFactoryBean.class),
            registry, JPA_MAPPING_CONTEXT_BEAN_NAME, source);

    registerLazyIfNotAlreadyRegistered(() -> new RootBeanDefinition(PAB_POST_PROCESSOR), registry,
            AnnotationConfigUtils.PERSISTENCE_ANNOTATION_PROCESSOR_BEAN_NAME, source);

    registerLazyIfNotAlreadyRegistered(() -> {

        RootBeanDefinition contextDefinition = new RootBeanDefinition(DefaultJpaContext.class);
        contextDefinition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_CONSTRUCTOR);

        return contextDefinition;

    }, registry, JPA_CONTEXT_BEAN_NAME, source);

    registerIfNotAlreadyRegistered(() -> new RootBeanDefinition(JPA_METAMODEL_CACHE_CLEANUP_CLASSNAME), registry,
            JPA_METAMODEL_CACHE_CLEANUP_CLASSNAME, source);

    registerIfNotAlreadyRegistered(() -> {

        Object value = config instanceof AnnotationRepositoryConfigurationSource //
                ? config.getRequiredAttribute(ESCAPE_CHARACTER_PROPERTY, Character.class) //
                : config.getAttribute(ESCAPE_CHARACTER_PROPERTY).orElse("\\");

        BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(JpaEvaluationContextExtension.class);
        builder.addConstructorArgValue(value);

        return builder.getBeanDefinition();

    }, registry, JpaEvaluationContextExtension.class.getName(), source);
}
```
