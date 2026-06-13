---
layout: default
title:
---

[Spring Data Repository Auto Configuration Base Class](#spring-data-repository-auto-configuration-base-class)
- 추상클래스: [AbstractRepositoryConfigurationSourceSupport](#abstractrepositoryconfigurationsourcesupport)
- 설정 옵션 추상화 구현체: [AutoConfiguredAnnotationRepositoryConfigurationSource](#autoconfiguredannotationrepositoryconfigurationsource)

## Spring Data Repository Auto Configuration Base Class

스프링 부트를 사용하는 경우 스프링 데이터에서 제공하는 추상화 객체([AnnotationRepositoryConfigurationSource](../common/spring%20data%20repository%20config%20objects.md#annotationrepositoryconfigurationsource))와 스프링 부트에서 제공하는 객체를 통합하여 리포지토리 인터페이스 스캔/등록 작업을 진행함

### AbstractRepositoryConfigurationSourceSupport

스프링 부트에서 제공하는 리포지토리 자동 구성 작업을 수행하는 기반 클래스로 데이터 스토어 별 공통적으로 진행하는 작업을 추상화함

스프링 데이터에서 제공하는 추상화 객체와 통합하여 리포지토리 자동 구성을 진행하는데, ImportBeanDefinitionRegistrar를 구현함으로써 자동 구성 작업을 트리거함 (사실상 리포지토리 구성의 시작점)

스프링 데이터의 경우 `RepositoryBeanDefinitionRegistrarSupport`을 통해 리포지토리 구성 작업을 진행함

각 데이터 스토어는 이 기반 클래스를 상속하고 스프링 부트의 `@AutoConfiguration` 클래스에 의해 스프링 컨텍스트에 등록됨
- 스프링 부트의 데이터 JPA: [JpaRepositoriesRegistrar](../jpa/txt/spring%20data%20jpa%20autoconfiguration.md#jparepositoriesregistrar)

#### 역할

리포지토리 스캔

리포지토리 설정/등록

```java
/*
    ImportBeanDefinitionRegistrar 인터페이스
    - 동적으로 스프링 컨텍스트에 빈 정의를 등록할 때 사용
    - @Configuration 클래스를 처리할 때 호출됨
    - AbstractRepositoryConfigurationSourceSupport가 구현해서 리포지토리 인터페이스 스캔 및 등록 작업을 수행함
      (RepositoryConfigurationDelegate에게 위임)
*/
public abstract class AbstractRepositoryConfigurationSourceSupport
		implements ImportBeanDefinitionRegistrar, BeanFactoryAware, ResourceLoaderAware, EnvironmentAware {


    /* =============== 필드 =================== */

	private ResourceLoader resourceLoader; // 리소스 파일 로딩
	private BeanFactory beanFactory; // 스프링 컨텍스트 빈 조회 및 조작
	private Environment environment; // 프로퍼티 소스 접근


    /* ============================================================
       ImportBeanDefinitionRegistrar 구현 메서드
       
       리포지토리 스캔 및 등록 수행
       - 구현체(JpaRepositoriesRegistrar 등)의 설정 정보를 바탕으로 진행
       ============================================================ */
	@Override
	public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry,
			BeanNameGenerator importBeanNameGenerator) {

        // getConfigurationSource: 리포지토리 설정 정보 객체 생성
        // new RepositoryConfigurationDelegate: 리포지토리 등록 작업 객체 생성
		RepositoryConfigurationDelegate delegate = new RepositoryConfigurationDelegate(
				getConfigurationSource(registry, importBeanNameGenerator), this.resourceLoader, this.environment);
        
        // 리포지토리 인터페이스 스캔 및 빈 등록 위임
        // getRepositoryConfigurationExtension: 자식 구현체 리포지토리 구성 확장 클래스 조회 (JpaRepositoryConfigExtension 등)
		delegate.registerRepositoriesIn(registry, getRepositoryConfigurationExtension());
	}

    
    /* =============== 스프링 데이터 리포지토리 설정 객체 생성 =================== */
    
    // getConfiguration() 템플릿 메서드를 통해 자식 구현체의 @Enable*Repositories 어노테이션을 조회하고
    // 리포지토리 설정 객체(AnnotationRepositoryConfigurationSource) 생성
	private AnnotationRepositoryConfigurationSource getConfigurationSource(BeanDefinitionRegistry registry,
			BeanNameGenerator importBeanNameGenerator) {
		AnnotationMetadata metadata = AnnotationMetadata.introspect(getConfiguration());
		return new AutoConfiguredAnnotationRepositoryConfigurationSource(metadata, getAnnotation(), this.resourceLoader,
				this.environment, registry, importBeanNameGenerator) {
		};
	}

    
    /* =============== 스프링 데이터 리포지토리 설정 오버라이딩 =================== */
    
    // 리포지토리 스캔 대상 패키지 반환
    // @SpringBootApplication 어노테이션이 위치한 패키지 반환
	protected Streamable<String> getBasePackages() {
		return Streamable.of(AutoConfigurationPackages.get(this.beanFactory));
	}

    // 리포지토리 인터페이스 프록시 생성 시점 결정
    // BootstrapMode.DEFAULT: 애플리케이션 로드 시점에 프록시 생성(@Lazy 적용 제외) 
    protected BootstrapMode getBootstrapMode() {
        return BootstrapMode.DEFAULT;
    }

    
    /* =============== 추상 메서드 =================== */
    
    // 리포지토리 활성화 어노테이션 조회 템플릿 메서드
    // 스프링 데이터 JPA: @EnableJpaRepositories
	protected abstract Class<? extends Annotation> getAnnotation();

    // 리포지토리 구성 클래스 조회 템플릿 메서드
    // 스프링 데이터 JPA: JpaRepositoriesRegistrar.EnableJpaRepositoriesConfiguration
	protected abstract Class<?> getConfiguration();
    
    // 리포지토리 구성 확장 클래스 조회 템플릿 메서드
    // 스프링 데이터 JPA: JpaRepositoryConfigExtension
	protected abstract RepositoryConfigurationExtension getRepositoryConfigurationExtension();
    
}
```

### AutoConfiguredAnnotationRepositoryConfigurationSource

스프링 데이터에서 제공하는 리포지토리 설정 객체를 사용하기 위한 AbstractRepositoryConfigurationSourceSupport 내부 클래스

스프링 데이터의 AnnotationRepositoryConfigurationSource 리포지토리 설정 객체를 상속하는데

그 중 두 가지 메서드를 상위 클래스인 AbstractRepositoryConfigurationSourceSupport의 메서드를 호출하도록 오버라이딩함

`getBasePackages()`
- 리포지토리 스캔 대상 패키지 반환 메서드
- 스프링 부트에서 제공하는 방식을 사용해서 스캔 대상 패키지 목록을 결정함

`getBootstrapMode()`
- 리포지토리 프록시 생성 시점 결정 메서드
- 기본값: `BootstrapMode.DEFAULT` (애플리케이션 로드 시점에 모두 생성)

```java
private class AutoConfiguredAnnotationRepositoryConfigurationSource
			extends AnnotationRepositoryConfigurationSource {

    AutoConfiguredAnnotationRepositoryConfigurationSource(AnnotationMetadata metadata,
            Class<? extends Annotation> annotation, ResourceLoader resourceLoader, Environment environment,
            BeanDefinitionRegistry registry, BeanNameGenerator generator) {
        super(metadata, annotation, resourceLoader, environment, registry, generator);
    }

    // 상위 클래스 getBasePackages 호출
    @Override
    public Streamable<String> getBasePackages() {
        return AbstractRepositoryConfigurationSourceSupport.this.getBasePackages();
    }

    // 상위 클래스 getBootstrapMode 호출
    @Override
    public BootstrapMode getBootstrapMode() {
        return AbstractRepositoryConfigurationSourceSupport.this.getBootstrapMode();
    }

}
```