---
layout: default
title:
---

[Repository Configuration Information](#repository-configuration-information)
- [RepositoryConfiguration](#repositoryconfiguration)
- [DefaultRepositoryConfiguration](#defaultrepositoryconfiguration)

[Repository Configurable Options Abstraction](#repository-configurable-options-abstraction)
- [RepositoryConfigurationSource](#repositoryconfigurationsource)
- [RepositoryConfigurationSourceSupport](#repositoryconfigurationsourcesupport)
- [AnnotationRepositoryConfigurationSource](#annotationrepositoryconfigurationsource)

[Store Specific Repository Extension](#store-specific-repository-extension)
- [RepositoryConfigurationExtension](#repositoryconfigurationextension)
- [RepositoryConfigurationExtensionSupport](#repositoryconfigurationextensionsupport)

[Repository Scanning/Registration](#repository-scanningregistration)
- [RepositoryConfigurationDelegate](#repositoryconfigurationdelegate)

## Repository Configuration Information

### RepositoryConfiguration

단일 리포지토리 인스턴스에 대한 설정 정보를 정의한 인터페이스

[RepositoryConfigurationSource](#repositoryconfigurationsource)에게 위임하여 설정 정보를 반환하기 위해 제네릭 타입 정의 

```java
public interface RepositoryConfiguration<T extends RepositoryConfigurationSource> {

    /* -----------------------------
         패키지, 파일 위치 관련 메서드
       ----------------------------- */
    
    // 리포지토리 인터페이스 스캔 기반 패키지 반환
    // 스프링 부트 data jpa: @SpringBootApplication 패키지
    Streamable<String> getBasePackages();

    // 리포지토리 구현체를 스캔해야 할 기반 패키지 반환
    // 스프링 부트 data jpa: @SpringBootApplication 패키지
    Streamable<String> getImplementationBasePackages();

    // named query 파일 위치 반환
    Optional<String> getNamedQueriesLocation();
    
    /* -----------------------------
                이름 관련 메서드
       ----------------------------- */
    
    // 리포지토리 인터페이스 이름 반환
    String getRepositoryInterface();

    // 리포지토리 기반 클래스 이름 반환
    // 특정 스토어 별 적용돼야 할 기본 이름이 있다면 null 반환
    Optional<String> getRepositoryBaseClassName();
    
    // RepositoryFactoryBean 구현체 이름 반환
    // 스프링 데이터 JPA: JpaRepositoryFactoryBean
    String getRepositoryFactoryBeanName();
    
    // 리포지토리 빈 이름 반환
    String getRepositoryBeanName();
    
    // 리포지토리 커스텀 구현체 빈 이름 반환
    String getImplementationBeanName();

    /* -----------------------------
             설정 객체 관련
       ----------------------------- */
    
    // RepositoryConfiguration의 source 반환
    Object getSource();
    
    // RepositoryConfiguration을 지원하는 RepositoryConfigurationSource 반환
    T getConfigurationSource();
    
    /* -----------------------------
             스프링 빈 설정 관련
       ----------------------------- */
    
    // 리포지토리 프록시 lazy 초기화 여부
    boolean isLazyInit();
    
    // primary 적용 여부
    boolean isPrimary();
    
    /* -----------------------------
               필터 관련
       ----------------------------- */

    // 리포지토리 스캔 대상에 제외하는 필터 반환
    Streamable<TypeFilter> getExcludeFilters();

    /* -----------------------------
           커스텀 구현체 설정 관련
       ----------------------------- */
    
    // 해당 리포지토리에 사용되는 ImplementationDetectionConfiguration 반환
    ImplementationDetectionConfiguration toImplementationDetectionConfiguration(MetadataReaderFactory factory);

    // 주어진 MetadataReaderFactory에 대한 ImplementationLookupConfiguration 반환
    ImplementationLookupConfiguration toLookupConfiguration(MetadataReaderFactory factory);
    
    /* -----------------------------
                 기타
       ----------------------------- */
    
    // QueryLookupStrategy resolve 키 반환
    Object getQueryLookupStrategyKey();

    // 리포지토리 인터페이스 정의 에러 텍스트 문구 반환
    String getResourceDescription();
    
}
```

### DefaultRepositoryConfiguration

RepositoryConfiguration 기본 구현체

주입받은 [RepositoryConfigurationSource](#repositoryconfigurationsource)를 기반으로 리포지토리 인스턴스에 대한 설정 정보를 반환함

```java
public class DefaultRepositoryConfiguration<T extends RepositoryConfigurationSource>
    implements RepositoryConfiguration<T> {

    /* ================== 전역 상수 필드 ================== */
    
    // 커스텀 구현체 접미사 기본값
    public static final String DEFAULT_REPOSITORY_IMPLEMENTATION_POSTFIX = "Impl";
    // QueryLookupStrategy 기본키
    public static final Key DEFAULT_QUERY_LOOKUP_STRATEGY = Key.CREATE_IF_NOT_FOUND;

    /* ================== 필드 ================== */
    
    private final T configurationSource;
    private final BeanDefinition definition;
    private final RepositoryConfigurationExtension extension;
    private final Lazy<String> beanName;

    /* ============================================================
        구현 메서드는 대부분 configurationSource에게 위임하는 로직으로 작성됨
       ============================================================*/
}
```

## Repository Configurable Options Abstraction

스프링 데이터는 하위 모듈에서 설정할 수 있는 리포지토리 옵션을 추상화하여 제공함

인터페이스: RepositoryConfigurationSource

추상 클래스: RepositoryConfigurationSourceSupport

구현체: 어노테이션(AnnotationRepositoryConfigurationSource) 또는 XML 설정

### RepositoryConfigurationSource

리포지토리 인터페이스와 관련된 메타데이터를 캡슐화하는 인터페이스

데이터 스토어 특성과 상관없이 동일한 방식으로 리포지토리를 설정할 수 있음

#### 역할

리포지토리 스캔, 정보 추출

리포지토리와 관련된 메타데이터 제공

#### 사용 위치

주로 스프링 데이터 리포지토리 설정 및 빈 등록 과정에서 사용됨

RepositoryConfigurationDelegate
- RepositoryConfigurationSource를 통해 리포지토리 스캔 및 빈 정의를 생성함
- 어떤 리포지토리 인터페이스를 등록할지, 어떤 속성 값이 필요한지 등의 정보를 추출함

RepositoryBeanDefinitionBuilder
- RepositoryConfigurationSource로부터 얻은 정보를 기반으로 리포지토리의 BeanDefinition을 생성함

### 소스 코드

```java
public interface RepositoryConfigurationSource {

    // 구성이 시작된 실제 source object 반환
    Object getSource();
    
    /* =============== 리포지토리 스캔 관련 =============== */
    
    /*
        리포지토리 인터페이스가 있는 패키지 목록 반환
        스프링 데이터 JPA: @EnableJpaRepositories의 basePackages, basePackageClasses 
    */
	Streamable<String> getBasePackages();

    // 스프링 빈으로 등록할 리포지토리 인터페이스의 BeanDefinition 반환
    Streamable<BeanDefinition> getCandidates(ResourceLoader loader);

    // 리포지토리 스캔 필터 사용 여부
    boolean usesExplicitFilters();

    // 명시적으로 지정한 필터 반환
    Streamable<TypeFilter> getExcludeFilters();
    
    /* =============== 쿼리 관련 =============== */

    /*
        쿼리 메서드 전략 정의
        
        QueryLookupStrategyKey 값
        - CREATE: 쿼리 생성
        - USE_DECLARED_QUERY: 선언된 쿼리만 사용
        - CREATE_IF_NOT_FOUND: 선언된 쿼리가 없으면 생성
     */
    Optional<Object> getQueryLookupStrategyKey();
    
    // Named Query 위치 반환
	Optional<String> getNamedQueryLocation();
    

    /* =============== 리포지토리 구성 클래스 관련 =============== */
    
    /*
        리포지토리 기본 클래스 이름 반환
        특정 스토어 별 적용돼야 할 이름이 있는 경우 Optional.empty() 반환
        스프링 데이터 JPA: SimpleJpaRepository
     */
	Optional<String> getRepositoryBaseClassName();

    /*
        리포지토리 팩토리 빈 클래스 이름 반환
        정의된 팩토리 빈 클래스가 없는 경우 Optional.empty() 반환
        스프링 데이터 JPA: JpaRepositoryFactoryBean
     */
	Optional<String> getRepositoryFactoryBeanClassName();

    /* =============== 커스텀 리포지토리 관련 =============== */

    /*
        커스텀 리포지토리 구현체 접미사 반환
        기본값 : Impl (UserRepository의 구현체 -> UserRepositoryImpl)
     */
    Optional<String> getRepositoryImplementationPostfix();

    // 커스텀 리포지토리 구현체를 생성하기 위한 설정 정보 반환
    ImplementationDetectionConfiguration toImplementationDetectionConfiguration(MetadataReaderFactory factory);
    

    /* =============== 기타 설정 =============== */
    
    // 리포지토리 설정 추가 속성 반환
	Optional<String> getAttribute(String name);
	<T> Optional<T> getAttribute(String name, Class<T> type);

    /*
        리포지토리 빈 부트스트랩 모드 반환
        - DEFAULT: 기본 부트스트랩
        - LAZY: 지연 초기화
        - DEFERRED: 컨텍스트가 완전히 초기화된 후 빈 생성
     */
    BootstrapMode getBootstrapMode();

    // 에러 문구 설명 텍스트 반환
    @Nullable
    String getResourceDescription();

    // 리포지토리 인터페이스로부터 생성된 스프링 빈 이름 반환
	String generateBeanName(BeanDefinition beanDefinition);
	
}
```

### RepositoryConfigurationSourceSupport

리포지토리 설정 방식 별(어노테이션, XML 기반 등) 공통 로직을 추상화한 RepositoryConfigurationSource 기반 클래스

#### 역할 (공통 로직 추상화)

리포지토리 스캔, BeanDefinition 반환

커스텀 리포지토리 설정 정보 반환

리포지토리 빈 이름 생성

```java
public abstract class RepositoryConfigurationSourceSupport implements RepositoryConfigurationSource {

    /* =============== 필드 =============== */
    
    // 커스텀 리포지토리 접미사 기본값
    protected static final String DEFAULT_REPOSITORY_IMPL_POSTFIX = "Impl";

    private final Environment environment;
    private final RepositoryBeanNameGenerator beanNameGenerator;
    private final BeanDefinitionRegistry registry;    

    /*=============== 리포지토리 인터페이스 스캔 =================*/
    
    /*
        스프링 컨텍스트에 등록할 리포지토리 인터페이스 BeanDefinition 목록 반환
        
        RepositoryComponentProvider
        - 리포지토리 인터페이스 스캔을 수행하는 실제 객체
        - 리포지토리(CrudRepository 등) 인터페이스를 확장한 인터페이스를 스캔함
        - @NoRepositoryBean 어노테이션이 선언된 인터페이스는 스캔에서 제외함
        - 스프링 데이터에서 제공하는 리포지토리 인터페이스는 모두 @NoRepositoryBean 선언되어 있어서 스캔에서 제외됨 
     */
    @Override
    public Streamable<BeanDefinition> getCandidates(ResourceLoader loader) {

        RepositoryComponentProvider scanner = new RepositoryComponentProvider(getIncludeFilters(), registry);
        
        // 스캐너 설정
        // 다른 클래스에 중첩된 리포지토리 인터페이스 스캔 여부 (shouldConsiderNestedRepositories 기본값: false)
        scanner.setConsiderNestedRepositoryInterfaces(shouldConsiderNestedRepositories());
        scanner.setEnvironment(environment);
        scanner.setResourceLoader(loader);

        getExcludeFilters().forEach(scanner::addExcludeFilter);

        // getBasePackages 메서드는 리포지토리 인터페이스 스캔 대상 패키지 목록을 반환하는 추상 메서드임
        // 스캐너를 통해 각 패키지에서 스프링 컨텍스트에 등록할 리포지토리 인터페이스를 스캔하여 BeanDefinition을 반환함  
        return Streamable.of(() -> getBasePackages().stream()//
                .flatMap(it -> scanner.findCandidateComponents(it).stream()));
    }

    /*================ 리포지토리 빈 이름 설정 =========== */
    
    @Override
    public String generateBeanName(BeanDefinition beanDefinition) {
        return beanNameGenerator.generateBeanName(beanDefinition);
    }

    /*================ 커스텀 리포지토리 구현체 설정 정보 반환 ============== */
    
    @Override
    public ImplementationDetectionConfiguration toImplementationDetectionConfiguration(MetadataReaderFactory factory) {
        return new SpringImplementationDetectionConfiguration(this, factory);
    }

    // 커스텀 리포지토리 구현체 감지 구현체
    private class SpringImplementationDetectionConfiguration implements ImplementationDetectionConfiguration {

        private final RepositoryConfigurationSource source;
        private final MetadataReaderFactory metadataReaderFactory;
        
        SpringImplementationDetectionConfiguration(RepositoryConfigurationSource source,
                                                   MetadataReaderFactory metadataReaderFactory) {
            this.source = source;
            this.metadataReaderFactory = metadataReaderFactory;
        }

        // 커스텀 리포지토리 구현체 기본 접미사: "Impl"
        // 사용자가 접미사를 설정한 경우 그 값을 사용함 
        @Override
        public String getImplementationPostfix() {
            return source.getRepositoryImplementationPostfix()
                    .orElse(DefaultRepositoryConfiguration.DEFAULT_REPOSITORY_IMPLEMENTATION_POSTFIX);
        }

        // 나머지 설정 정보는 구현체 설정과 동일함
    }
}
```

### AnnotationRepositoryConfigurationSource

@Enable*Repositories 어노테이션에 설정된 속성을 기반으로 리포지토리를 스캔/설정하는 [RepositoryConfigurationSource](#repositoryconfigurationsource) 구현체

```java
public class AnnotationRepositoryConfigurationSource extends RepositoryConfigurationSourceSupport {

    /* =========================================================
       문자열 상수 필드, 데이터 스토어와 상관없이 공통적으로 설정하는 정보들
      ========================================================= */
    
    private static final String REPOSITORY_IMPLEMENTATION_POSTFIX = "repositoryImplementationPostfix";
    private static final String BASE_PACKAGES = "basePackages";
    private static final String BASE_PACKAGE_CLASSES = "basePackageClasses";
    private static final String NAMED_QUERIES_LOCATION = "namedQueriesLocation";
    private static final String QUERY_LOOKUP_STRATEGY = "queryLookupStrategy";
    private static final String REPOSITORY_FACTORY_BEAN_CLASS = "repositoryFactoryBeanClass";
    private static final String REPOSITORY_BASE_CLASS = "repositoryBaseClass";
    private static final String CONSIDER_NESTED_REPOSITORIES = "considerNestedRepositories";
    private static final String BOOTSTRAP_MODE = "bootstrapMode";
    
    
    /*================== 필드===================*/
    
    /*
        @Enable*Repositories 어노테이션이 선언된 클래스의 메타데이터
        스프링 부트에 의한 데이터 JPA 자동 구성 시: JpaRepositoriesRegistrar.EnableJpaRepositoriesConfiguration  
    */
    private final AnnotationMetadata configMetadata; 
            
    /*
        @Enable*Repositories 어노테이션의 메타데이터
        특정 데이터 스토어별 설정된 속성에 접근할 수 있음
    */
    private final AnnotationMetadata enableAnnotationMetadata;

    // Enable*Repositories 어노테이션 속성값
    private final AnnotationAttributes attributes;

    // Enable*Repositories 어노테이션 속성을 기반으로 TypeFilter를 생성하는 함수형 인터페이스
    private final Function<AnnotationAttributes, Stream<TypeFilter>> typeFilterFunction;
    
    // 명시적인 필터 적용 여부, 기본 스캔 동작과 커스텀 스캔 동작 구분 목적
    private final boolean hasExplicitFilters;
    
    
    /* ==============================================================================
       리포지토리 인터페이스 스캔 대상 패키지 목록 조회 
       - @Enable*Repositories 어노테이션의 설정을 기반으로 리포지토리 스캔 대상 패키지 목록을 반환함
      
       리포지토리 스캔 과정에서 호출됨
       - 부모 클래스 RepositoryConfigurationSourceSupport.getCandidates()
       - DefaultRepositoryConfiguration.getBasePackages()
       
       (중요) 스프링 부트 리포지토리 자동 구성의 경우 이 메서드 대신 다른 방법으로 스캔 대상 패키지 목록을 조회함 
       AbstractRepositoryConfigurationSourceSupport.getBasePackages(), AutoConfigurationPackages
       ============================================================================== */
    @Override
    public Streamable<String> getBasePackages() {

        // @Enable*Repositories 어노테이션 속성에 지정된 스캔 대상 패키지 목록
        String[] value = attributes.getStringArray("value");
        String[] basePackages = attributes.getStringArray(BASE_PACKAGES);
        Class<?>[] basePackageClasses = attributes.getClassArray(BASE_PACKAGE_CLASSES);

        // 스캔 대상 패키지를 지정하지 않은 경우
        // @Enable*Repositories 어노테이션 선언 위치가 리포지토리 스캔 대상 패키지가 됨
        if (value.length == 0 && basePackages.length == 0 && basePackageClasses.length == 0) {
            
            String className = configMetadata.getClassName();
            return Streamable.of(ClassUtils.getPackageName(className));
        }

        // @Enable*Repositories 어노테이션에 속성에 한 개 이상의 패키지가 명시된 경우
        // 지정된 패키지들을 기반으로 베이스 패키지 목록을 구성함
        Set<String> packages = new HashSet<>(value.length + basePackages.length + basePackageClasses.length);
        packages.addAll(Arrays.asList(value));
        packages.addAll(Arrays.asList(basePackages));

        for (Class<?> c : basePackageClasses) {
            packages.add(ClassUtils.getPackageName(c));
        }

        return Streamable.of(packages);
    }

    /* ==============================================================================
        빈 이름 생성기 반환
        부모 클래스 RepositoryConfigurationSourceSupport.generatedBeanName()에서 사용
    ============================================================================== */
    private static BeanNameGenerator defaultBeanNameGenerator(@Nullable BeanNameGenerator generator) {

        return generator == null || ConfigurationClassPostProcessor.IMPORT_BEAN_NAME_GENERATOR.equals(generator) //
                ? new AnnotationBeanNameGenerator() //
                : generator;
    }
    
    /* ==============================================================================
        나머지 메서드는 어노테이션에 설정된 속성 값을 반환하는 동작을 주로 이룸
    ============================================================================== */
}
```

## Store Specific Repository Extension

### RepositoryConfigurationExtension

리포지토리 빈 정의에 대한 특정 데이터 스토어 별(JPA, MongoDB 등) 설정을 정의한 SPI

```java
public interface RepositoryConfigurationExtension {
    
    /* ----------------------------------
               default 메서드
       ---------------------------------- */

    // 모듈 식별자 반환
    // 스프링 데이터 JPA: jpa
    default String getModuleIdentifier() {
        return getModuleName().toLowerCase(Locale.ENGLISH).replace(' ', '-');
    }

    @NonNull
    default Class<? extends BeanRegistrationAotProcessor> getRepositoryAotProcessor() {
        return RepositoryRegistrationAotProcessor.class;
    }
    
    /* ----------------------------------
            데이터 스토어 별 정보 반환
       ---------------------------------- */
    
    // 스프링 데이터 모듈 이름(spring data jpa 등) 반환
    String genModuleName();

    // 스토어 별 RepositoryFactoryBean 이름 반환
    // 스프링 데이터 JPA: JpaRepositoryFactoryBean
    String getRepositoryFactoryBeanName();
    
    // named query 기본 위치 반환
    String getDefaultNameQueryLocation();

    // 주어진 T RepositoryConfigurationSource를 통해 모든 RepositoryConfiguration 반환
    <T extends RepositoryConfigurationSource> Collection<RepositoryConfiguration<T>> getRepositoryConfigurations(
            T configSource, ResourceLoader loader, boolean strictMatchesOnly
    );
    
    /* ----------------------------------
            데이터 스토어 별 빈 처리 작업
       ---------------------------------- */

    // 데이터 스토어 별(JPA, MongoDB 등) 리포지토리들이 공통적으로 필요한 빈을 스프링 컨테이너에 등록하는 메서드
    // 여러 리포지토리 빈에 공유되거나, 저장소 기술에 의존적인 기능을 제공하기 위해 사용됨 
    // 리포지토리들에 대한 빈 정의 작업을 시작하기 전에 호출됨
    // 스프링 데이터 JPA: SharedEntityManager 등
    void registerBeansForRoot(BeanDefinitionRegistry registry, RepositoryConfigurationSource configurationSource);
    
    // 빈 정의 후처리 또는 RepositoryConfigurationSource 추가 설정
    void postProcess(BeanDefinitionRegistry registry, RepositoryConfigurationSource config);
    void postProcess(BeanDefinitionBuilder builder, AnnotationRepositoryConfigurationSource config);
    void postProcess(BeanDefinitionBuilder builder, XmlRepositoryConfigurationSource config);
}
```

### RepositoryConfigurationExtensionSupport

구현체 별 공통 로직을 추상화한 RepositoryConfigurationExtension 기본 구현체

```java
public abstract class RepositoryConfigurationExtensionSupport implements RepositoryConfigurationExtension {
    
    /* ------------------ 전역 상수 필드 ------------------ */
    private static final String CLASS_LOADING_ERROR = "%s - Could not load type %s using class loader %s";
    private static final String MULTI_STORE_DROPPED = "Spring Data %s - Could not safely identify store assignment for repository candidate %s; If you want this repository to be a %s repository,";
    
    /* ------------------ 필드 ------------------ */
    private boolean noMultiStoreSupport = false;

    /* --------------------------------------------
            데이터 스토어 별 구성 정보 반환 메서드
       -------------------------------------------- */

    /*
        스프링 데이터가 리포지토리로 처리할 클래스를 식별하기 위해 사용됨
        주어진 리포지토리 설정(configSource)을 기반으로 리포지토리 인터페이스를 분석하고
        조건에 적합한 리포지토리 구성 정보(RepositoryConfiguration<T>)를 필터링하여 반환함 
        
        strictMatchesOnly: 엄격한 매칭 여부, true이면 엄격한 조건으로 리포지토리를 필터링함
     */
    @Override
    public <T extends RepositoryConfigurationSource> Collection<RepositoryConfiguration<T>> getRepositoryConfigurations(
            T configSource, ResourceLoader loader, boolean strictMatchesOnly) {

        Set<RepositoryConfiguration<T>> result = new HashSet<>();

        /*
           RepositoryConfigurationSource.getCandidates: 리포지토리 스캔 및 빈 정의 생성
           
           각 리포지토리 빈 정의와 리포지토리 설정 정보를 통해 RepositoryConfiguration를 생성하고
           loadRepositoryInterface를 호출하여 
         */
        for (BeanDefinition candidate : configSource.getCandidates(loader)) {

            // getRepositoryConfiguration: DefaultRepositoryConfiguration 반환
            RepositoryConfiguration<T> configuration = getRepositoryConfiguration(candidate, configSource);
            
            /*
               리포지토리 인터페이스를 실제로 로드하여 클래스 정보를 가져옴
               리포지토리 유효성 확인, 메타데이터 분석 기반 제공
               로드에 실패하면 null을 반환함     
             */
            Class<?> repositoryInterface = loadRepositoryInterface(configuration,
                    getConfigurationInspectionClassLoader(loader));

            // 리포지토리 인터페이스 로드 실패 시 결과 추가
            if (repositoryInterface == null) {
                result.add(configuration);
                continue;
            }

            // 리포지토리 인터페이스 로드 성공 시 메타데이터를 분석하여 조건에 따라 결과에 추가
            RepositoryMetadata metadata = AbstractRepositoryMetadata.getMetadata(repositoryInterface);

            boolean qualifiedForImplementation = !strictMatchesOnly || configSource.usesExplicitFilters()
                    || isStrictRepositoryCandidate(metadata);

            if (qualifiedForImplementation && useRepositoryConfiguration(metadata)) {
                result.add(configuration);
            }
        }

        return result;
    }

    // BeanDefinition과 RepositoryConfigurationSource를 기반으로 DefaultRepositoryConfiguration 생성
    protected <T extends RepositoryConfigurationSource> RepositoryConfiguration<T> getRepositoryConfiguration(
            BeanDefinition definition, T configSource) {
        return new DefaultRepositoryConfiguration<>(configSource, definition, this);
    }
    
}
```

#### RepositoryConfigurationExtensionSupport에서 정의한 메서드

RepositoryConfigurationExtensionSupport에서 정의한 메서드, 각 스토어 구현체 별로 호출하거나 오버라이딩하여 리포지토리 확장 설정

```java
/*
    리포지토리 인터페이스를 평가할 때 스캔해야 될 도메인 타입 어노테이션 반환
    -> 스토어(JPA, MongoDB 등) 별 명시적으로 선언해야 되는 어노테이션들을 반환해야 됨
    스프링 데이터 JPA: @Entity, @MappedSuperClass
*/
protected Collection<Class<? extends Annotation>> getIdentifyingAnnotations() {
    return Collections.emptySet();
}

// 엄격하게 리포지토리 매치를 할 때 매치돼야 할 리포지토리 인터페이스 타입 반환
// 스프링 데이터 JPA: JpaRepository
protected Collection<Class<?>> getIdentifyingTypes() {
    return Collections.emptySet();
}

// 리포지토리 인터페이스를 로드할 클래스 로더 반환
@Nullable
protected ClassLoader getConfigurationInspectionClassLoader(ResourceLoader loader) {
    return loader.getClassLoader();
}
```

#### RepositoryConfigurationExtensionSupport의 빈 등록 static 메서드

RepositoryConfigurationExtensionSupport는 상황 별로 빈을 등록할 수 있는 메서드를 제공함 

```java
/*
    지정된 BeanDefinition을 스프링 컨텍스트에 등록하면서 고유한 이름을 지정함
    source: BeanDefinition 생성 출처 또는 메타데이터, 디버깅/추적 목적으로 사용
    
    여러 번 bean 정의가 등록되는 것을 방지하려면 전용 빈 이름을 지정하여 registerIfNotAlreadyRegistered 호출 
*/
public static String registerWithSourceAndGeneratedBeanName(AbstractBeanDefinition bean,
                                                            BeanDefinitionRegistry registry, Object source) {

    bean.setSource(source);

    String beanName = generateBeanName(bean, registry);
    registry.registerBeanDefinition(beanName, bean);

    return beanName;
}

// BeanDefinitionRegistry에 주어진 beanName으로 등록된 BeanDefinition이 없는 경우 스프링 컨텍스트에 등록
public static void registerIfNotAlreadyRegistered(Supplier<AbstractBeanDefinition> supplier,
                                                  BeanDefinitionRegistry registry, String beanName, Object source) {

    if (registry.containsBeanDefinition(beanName)) {
        return;
    }

    AbstractBeanDefinition bean = supplier.get();

    bean.setSource(source);
    registry.registerBeanDefinition(beanName, bean);
}

// BeanDefinitionRegistry에 주어진 beanName으로 등록된 BeanDefinition이 없는 경우 lazy bean definition 스프링 컨텍스트에 등록
public static void registerLazyIfNotAlreadyRegistered(Supplier<AbstractBeanDefinition> supplier,
                                                      BeanDefinitionRegistry registry, String beanName, Object source) {

    if (registry.containsBeanDefinition(beanName)) {
        return;
    }

    AbstractBeanDefinition definition = supplier.get();
    definition.setSource(source);
    definition.setLazyInit(true);

    registry.registerBeanDefinition(beanName, definition);
}
```

## Repository Scanning/Registration

### RepositoryConfigurationDelegate

스프링 데이터의 리포지토리 인터페이스를 스캔하고 스프링 빈으로 등록하는 역할을 가진 객체

수행해야 할 동작을 적절한 객체에게 위임하기 때문에 클래스명에 Delegate 접미사가 붙음

#### 문자열 상수 필드

```java
// data 모듈을 사용하는 스프링 애플리케이션이 구동될 때 항상 표시되는 문구
private static final String REPOSITORY_REGISTRATION = "Spring Data %s - Registering repository: %s - Interface: %s - Factory: %s";

private static final String MULTIPLE_MODULES = "Multiple Spring Data modules found, entering strict repository configuration mode";
private static final String NON_DEFAULT_AUTOWIRE_CANDIDATE_RESOLVER = "Non-default AutowireCandidateResolver (%s) detected. Skipping the registration of LazyRepositoryInjectionPointResolver. Lazy repository injection will not be working";
```

#### 필드

```java
private final RepositoryConfigurationSource configurationSource;
private final ResourceLoader resourceLoader;
private final Environment environment;
private final boolean isXml;
private final boolean inMultiStoreMode;
```

#### registerRepositoriesIn: 리포지토리 빈 등록 메서드

리포지토리를 스캔하고 스프링 빈으로 등록하고, 등록된 빈들의 정보를 나타내는 `List<BeanComponentDefinition>`을 반환함

대부분 [RepositoryConfigurationExtension](#repositoryconfigurationextension)에게 위임하여 얻은 [RepositoryConfiguration](#repositoryconfiguration)을 기반으로 RepositoryBeanDefinitionBuilder를 통해 스프링 컨텍스트에 등록하는 로직임

[스프링 데이터 JPA Extension (SharedEntityManagerCreator 등록)](../jpa/txt/spring%20data%20jpa%20objects.md#jparepositoryconfigurationextension)

```java
public List<BeanComponentDefinition> registerRepositoriesIn(BeanDefinitionRegistry registry,
			RepositoryConfigurationExtension extension) {

    // 리포지토리 빈을 등록하기 전, 데이터 스토어에서 공통적으로 사용되는 빈을 먼저 등록함
    // 스프링 데이터 JPA: SharedEntityManagerCreator, DefaultJpaContext 등
    extension.registerBeansForRoot(registry, configurationSource);

    
    // 스프링 빈으로 등록할 리포지토리에 대한 BeanDefinitionBuilder를 생성하는 Builder
    RepositoryBeanDefinitionBuilder builder = new RepositoryBeanDefinitionBuilder(registry, extension,
            configurationSource, resourceLoader, environment);

    
    // spring metrics
    StopWatch watch = new StopWatch();
    ApplicationStartup startup = getStartup(registry);
    StartupStep repoScan = startup.start("spring.data.repository.scanning");

    repoScan.tag("dataModule", extension.getModuleName());
    repoScan.tag("basePackages",
            () -> configurationSource.getBasePackages().stream().collect(Collectors.joining(", ")));
    watch.start();

    
    // 리포지토리 스캔 및 스프링 빈으로 등록할 리포지토리에 대한 RepositoryConfiguration 생성 위임
    Collection<RepositoryConfiguration<RepositoryConfigurationSource>> configurations = extension
            .getRepositoryConfigurations(configurationSource, resourceLoader, inMultiStoreMode);

    
    List<BeanComponentDefinition> definitions = new ArrayList<>();

    Map<String, RepositoryConfiguration<?>> configurationsByRepositoryName = new HashMap<>(configurations.size());
    Map<String, RepositoryConfigurationAdapter<?>> metadataByRepositoryBeanName = new HashMap<>(configurations.size());

    // 스프링 빈으로 등록하기 전 데이터 스토어 별 후처리 메서드 호출
    // 스프링 컨텍스트 등록 및 BeanComponentDefinition 생성
    for (RepositoryConfiguration<? extends RepositoryConfigurationSource> configuration : configurations) {

        configurationsByRepositoryName.put(configuration.getRepositoryInterface(), configuration);

        BeanDefinitionBuilder definitionBuilder = builder.build(configuration);
        extension.postProcess(definitionBuilder, configurationSource);

        if (isXml) {
            extension.postProcess(definitionBuilder, (XmlRepositoryConfigurationSource) configurationSource);
        } else {
            extension.postProcess(definitionBuilder, (AnnotationRepositoryConfigurationSource) configurationSource);
        }

        RootBeanDefinition beanDefinition = (RootBeanDefinition) definitionBuilder.getBeanDefinition();
        
        /* ------------------------------------------
            리포지토리 TargetType 설정
            targetType은 스프링 빈이 인스턴스화될 때 객체의 타입 또는 팩토리 빈 타입을 나타냄
            스프링 데이터는 팩토리 빈을 통해 프록시 객체를 생성하기 때문에 스토어 별 팩토리 빈 클래스를 targetType으로 지정함
            
            여기서 지정된 팩토리 빈을 통해 스프링 데이터는 마법을 부리기 시작함
            
            스프링 데이터 JPA: JpaRepositoryFactoryBean        
           ------------------------------------------ */
        beanDefinition.setTargetType(getRepositoryFactoryBeanType(configuration));
        
        
        beanDefinition.setResourceDescription(configuration.getResourceDescription());

        /* ------- 리포지토리 빈 이름 설정 ------- */
        String beanName = configurationSource.generateBeanName(beanDefinition);

        if (logger.isTraceEnabled()) {
            logger.trace(LogMessage.format(REPOSITORY_REGISTRATION, extension.getModuleName(), beanName,
                    configuration.getRepositoryInterface(), configuration.getRepositoryFactoryBeanClassName()));
        }

        metadataByRepositoryBeanName.put(beanName, builder.buildMetadata(configuration));
        registry.registerBeanDefinition(beanName, beanDefinition);
        definitions.add(new BeanComponentDefinition(beanDefinition, beanName));
    }

    // lazy 리포지토리 처리
    potentiallyLazifyRepositories(configurationsByRepositoryName, registry, configurationSource.getBootstrapMode());

    // spring metrics
    watch.stop();
    repoScan.tag("repository.count", Integer.toString(configurations.size()));
    repoScan.end();

    // 스프링 aot 관련
    registerAotComponents(registry, extension, metadataByRepositoryBeanName);

    return definitions;
}
```
