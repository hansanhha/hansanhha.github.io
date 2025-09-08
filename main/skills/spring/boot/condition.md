---
layout: default
title:
---

[Spring Boot @Conditional](#spring-boot-conditional)

[ConditionOutcome, ConditionMessage](#conditionoutcome-conditionmessage)

[SpringBootCondition](#springbootcondition)

[AbstractNestedCondition](#abstractnestedcondition)

[AnyNestedCondition](#anynestedcondition)

## Spring Boot @Conditional

`@Conditional`은 스프링 프레임워크에서 제공하는 로우 레벨 어노테이션이고

스프링 부트는 개발자가 다양한 조건문을 작성할 수 있도록 추가적인 `@Conditional` 어노테이션을 제공함

@Conditional이 메타 어노테이션으로 적용되어 있어서, 속성 값으로 지정된 Condition 타입의 메서드 결과에 따라 빈 등록과 기타 어노테이션(@Import 등) 적용 여부를 결정함

#### Class Conditions

`@ConditionalOnClass`: 클래스 패스에 지정한 클래스가 있는 경우 true 반환

`@ConditionalOnMissingClass`: 클래스 패스에 지정한 클래스가 없는 경우 true 반환

#### Bean Conditions

`@ConditionalOnBean`: ApplicationContext에 지정한 타입의 빈이 이미 있는 경우 true 반환

`@ConditionalOnMissingBean`ApplicationContext에 지정한 타입의 빈이 아직 없는 경우 true 반환

#### Property Conditions

`@ConditionalOnProperty`: ApplicationContext에 특정 속성이 설정된 경우 true 반환

#### Resource Conditions

`@ConditionalOnResource`: ApplicationContext에 특정 리소스가 존재하는 경우 true 반환

#### Other Conditions

`@ConditionalOnSingleCandidate`: ApplicationContext에 특정 타입의 빈이 존재하거나, 여러 타입이 있더라도 해당 타입의 primary 빈이 설정된 경우 true 반환

## ConditionOutcome, ConditionMessage

ConditionOutcome은 Condition 결과 값과 로그 메시지(ConditionMessage)를 포함한 객체임

스프링 부트에서 Condition 평가 결과를 나타낼 때 ConditionOutcome을 사용함

```java
public class ConditionOutcome {
    
    private final boolean match;
    
    private final ConditionMessage message;
    
    public static ConditionOutcome match(String message) {
        return new ConditionOutcome(true, message);
    }

    public static ConditionOutcome noMatch(String message) {
        return new ConditionOutcome(false, message);
    }

    public boolean isMatch() {
        return this.match;
    }
}
```

ConditionMessage는 Condition 결과에 따른 로그를 상세하게 남길 수 있는 헬퍼 객체로, fluent 빌더 API를 제공함

```java
public final class ConditionMessage {
    private final String message;
    
    // builder API
}
```

## SpringBootCondition

[스프링 코어의 Condition](../../core/txt/condition.md)을 구현하는 추상 클래스로, 스프링 부트에서 사용하는 모든 Condition 구현체들은 이 클래스를 기반으로 함

```java
// SpringBootCondition 코드 중 일부분 (로깅 로직 생략)
public abstract class SpringBootCondition implements Condition {

    @Override
    public final boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        try {
            /*
                getMatchOutCome 추상 메서드를 통해 SpringBootCondition을 상속하는 구현체에게 실제 조건 평가 로직을 위임함
                템플릿 메서드 패턴
             */
            ConditionOutCome outcome = getMatchOutCome(context, metadata);
            
            // 자식 구현체의 조건 평가 결과 반환
            return outcome.isMatch();
        } catch (NoClassDefFoundError ex) {
            // ...
        }
    }

    public abstract ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata);
}
```

## AbstractNestedCondition

복합적인 조건을 처리하기 위한 특수 Condition 추상 클래스로, 구현체의 멤버 클래스 중 조건(@Conditional)이 적용된 클래스를 감지하여 조건을 생성하고 평가함

멤버 클래스의 여러 조건을 그룹화하고, 특정 논리에 따라 조건 평가를 실시한 뒤 결과를 보관하고, 이에 대한 최종 판단 결과는 구현체에게 위임함

AbstractNestedCondition은 [SpringBootCondition](#springbootcondition)과 [ConfigurationCondition](../../core/txt/condition.md/#spring-configurationcondition)을 확장함

```java
// AbstractNestedCondition 일부분
public abstract class AbstractNestedCondition extends SpringBootCondition implements ConfigurationCondition {
    
    private final ConfigurationPhase configurationPhase;
    
    // SpringBootCondition의 추상 메서드인 getMatchOutcome 메서드 구현
    @Override
    public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
        // getClass(): this가 생략된 상태로, AbstractNestedCondition을 상속한 구현체 클래스 이름을 가져옴
        String className = getClass().getName();
        
        // MemberConditions: ConditionContext, ConfigurationPhase, 구현체 클래스 이름을 기반으로 한 하위 조건들 그룹화
        // AbstractNestedCondition 구현체의 멤버를 기반으로 조건(Condition)들을 구성함
        MemberConditions memberConditions = new MemberConditions(context, this.configurationPhase, className);
        
        // MemberMatchOutcomes: 각 하위 조건의 평가 결과(ConditionOutcome) 저장
        MemberMatchOutcomes memberOutcomes = new MemberMatchOutcomes(memberConditions);
        
        // 추상 메서드(템플릿 메서드)를 통해 memberOutcomes을 기반으로 최종 평가 결과를 반환함
        return getFinalMatchOutcome(memberOutcomes);
    }
    
    // 멤버 클래스 조건 평가 결과에 대한 최종 판단은 템플릿 메서드 패턴을 사용하여 구현체에게 위임함
    protected abstract ConditionOutcome getFinalMatchOutcome(MemberMatchOutcomes memberOutcomes);
    
}
```

**조건 추출**

AbstractNestedCondition은 MemberConditions를 통해 구현체의 멤버 클래스로부터 조건들을 확보함  

MemberConditions 클래스는 생성될 때 @Conditional 어노테이션이 적용된 멤버 클래스를 감지하여 조건을 추출함
```java
public abstract class AbstractNestedCondition extends SpringBootCondition implements ConfigurationCondition {
    
    // private 멤버 클래스, MemberConditions 코드 일부분
    private static class MemberConditions {
        
        private final ConditionContext context;

        private final MetadataReaderFactory readerFactory;

        // 어노테이션 타입에 따라 매핑된 조건 리스트 (AbstractNestedCondition 구현체 자식 멤버 기반)
        private final Map<AnnotationMetadata, List<Condition>> memberConditions;

        MemberConditions(ConditionContext context, ConfigurationPhase phase, String className) {
            this.context = context;
            this.readerFactory = new SimpleMetadataReaderFactory(context.getResourceLoader());
            // getMetadata: AbstractNestedCondition 구현체 클래스 이름을 통해 AnnotationMetadata(클래스 구조적 정보 및 어노테이션 메타데이터 정보 제공) 획득,
            // getMemberClassNames: 구현체의 멤버 클래스 이름 획득
            String[] members = getMetadata(className).getMemberClassNames();
            // 멤버 클래스 이름, 구성 단계, 구현체 클래스 이름을 통해 구현체 멤버 클래스의 조건(Condition) 추출
            // @Conditional 어노테이션 타입(@ConditionalOnProperty, @ConditionalOnClass 등)에 따라 매핑된 조건 리스트를 반환함
            this.memberConditions = getMemberConditions(members, phase, className);
        }

        // 멤버 클래스 이름, 구성 단계, 구현체 클래스 이름을 통해 구현체 멤버 클래스의 조건(Condition) 추출
        private Map<AnnotationMetadata, List<Condition>> getMemberConditions(String[] members, ConfigurationPhase phase,
                                                                             String className) {
            MultiValueMap<AnnotationMetadata, Condition> memberConditions = new LinkedMultiValueMap<>();
            // 멤버 클래스마다 루프문을 돌아서 조건 생성
            for (String member : members) {
                // 멤버 클래스에 대한 AnnotationMetadata 획득
                AnnotationMetadata metadata = getMetadata(member);
                // 멤버 클래스 중 @Conditional 어노테이션이 적용된 클래스들을 반환
                for (String[] conditionClasses : getConditionClasses(metadata)) {
                    for (String conditionClass : conditionClasses) {
                        // 멤버 클래스에 적용된 조건 추출
                        Condition condition = getCondition(conditionClass);
                        // 조건에 대한 ConfigurationPhase 검증
                        validateMemberCondition(condition, phase, className);
                        memberConditions.add(metadata, condition);
                    }
                }
            }
            return Collections.unmodifiableMap(memberConditions);
        }

        // 조건 평가 결과 반환
        List<ConditionOutcome> getMatchOutcomes() {
            List<ConditionOutcome> outcomes = new ArrayList<>();
            // 루프문을 돌면서 MemberOutcomes.getUltimateOutcome()를 통해 조건 평가 실시
            this.memberConditions.forEach((metadata, conditions) -> outcomes
                    .add(new MemberOutcomes(this.context, metadata, conditions).getUltimateOutcome()));
            // 모든 조건 평가 결과 반환
            return Collections.unmodifiableList(outcomes);
        }
    }
}
```

**조건 평가 위임 및 결과 보관**

AbstractNestedCondition은 MemberConditions로부터 조건을 획득한 후, MemberMatchOutcomes를 통해 각 조건에 대한 평가 결과를 추출함

MemberMatchOutcomes는 생성될 때 주어진 MemberConditions를 모두 평가한 뒤, 결과를 보관함 ([ConditionOutcome](#conditionoutcome-conditionmessage))

```java
public abstract class AbstractNestedCondition extends SpringBootCondition implements ConfigurationCondition {
    
    // protected 클래스
    protected static class MemberMatchOutcomes {
        
        // 모든 조건 결과들
        private final List<ConditionOutcome> all;

        // 매치되는 조건 결과들
        private final List<ConditionOutcome> matches;

        // 매치되지 않는 조건 결과들
        private final List<ConditionOutcome> nonMatches;

        // MemberMatchOutcomes 생성 시, 주어진 모든 조건 평가 실시
        public MemberMatchOutcomes(MemberConditions memberConditions) {
            this.all = Collections.unmodifiableList(memberConditions.getMatchOutcomes());
            List<ConditionOutcome> matches = new ArrayList<>();
            List<ConditionOutcome> nonMatches = new ArrayList<>();
            for (ConditionOutcome outcome : this.all) {
                (outcome.isMatch() ? matches : nonMatches).add(outcome);
            }
            this.matches = Collections.unmodifiableList(matches);
            this.nonMatches = Collections.unmodifiableList(nonMatches);
        }
    }
}
```

**실질적 조건 평가**

MemberMatchOutcomes 생성 시 MemberConditions.getMatchOutcomes()를 호출하여 조건 평가 값을 가져오는데,

실제로 조건 평가를 수행하는 클래스는 MemberOutcomes 멤버 클래스임

```java
public abstract class AbstractNestedCondition extends SpringBootCondition implements ConfigurationCondition {

    // private 멤버 클래스
    private static class MemberOutcomes {

        private final ConditionContext context;

        private final AnnotationMetadata metadata;

        private final List<ConditionOutcome> outcomes;

        // MemberOutcomes 생성 시, getConditionOutcome를 호출하여 조건 평가 실시
        // 생성자로 주어진 List<Condition>은 MemberConditions가 가지고 있는 @Conditional 어노테이션 타입(@ConditionalOnProperty, @ConditionalOnClass 등)에 따라 매핑된 조건 리스트임
        MemberOutcomes(ConditionContext context, AnnotationMetadata metadata, List<Condition> conditions) {
            this.context = context;
            this.metadata = metadata;
            this.outcomes = new ArrayList<>(conditions.size());
            for (Condition condition : conditions) {
                this.outcomes.add(getConditionOutcome(metadata, condition));
            }
        }

        // 조건 평가 수행 메서드, SpringBootCondition.getMatchOutcome() 또는 Condition.matches() 호출
        private ConditionOutcome getConditionOutcome(AnnotationMetadata metadata, Condition condition) {
            if (condition instanceof SpringBootCondition springBootCondition) {
                return springBootCondition.getMatchOutcome(this.context, metadata);
            }
            return new ConditionOutcome(condition.matches(this.context, metadata), ConditionMessage.empty());
        }

        /*
                MemberMatchOutcomes 생성 시 MemberConditions.getMatchOutcomes() 호출
                MemberConditions.getMatchOutcomes()에서 루프문을 돌면서 MemberOutcomes를 생성하고 MemberOutcomes.getUltimateOutcome()를 호출함

                getUltimateOutcome은 MemberOutcomes 생성될 때 각 어노테이션 타입에 따른 List<Condition> 조건들이 평가되고 나서 호출되는 메서드로
                조건 평가 결과가 모두 부합되면 매치되고, 하나라도 부합되지 않으면 매치되지 않는 것으로 결과를 반환함
         */
        ConditionOutcome getUltimateOutcome() {
            ConditionMessage.Builder message = ConditionMessage
                    .forCondition("NestedCondition on " + ClassUtils.getShortName(this.metadata.getClassName()));
            // 조건 평가 결과가 하나인 경우
            if (this.outcomes.size() == 1) {
                ConditionOutcome outcome = this.outcomes.get(0);
                return new ConditionOutcome(outcome.isMatch(), message.because(outcome.getMessage()));
            }
            // 조건 평가 결과가 여러 개 인 경우(AbstractNestedCondition은 구현체의 멤버 클래스에 적용된 @Conditional 타입에 따라 조건들을 그룹화하여 평가 진행함)
            List<ConditionOutcome> match = new ArrayList<>();
            List<ConditionOutcome> nonMatch = new ArrayList<>();
            for (ConditionOutcome outcome : this.outcomes) {
                (outcome.isMatch() ? match : nonMatch).add(outcome);
            }
            // 조건이 모두 부합된 경우 match true 반환
            if (nonMatch.isEmpty()) {
                return ConditionOutcome.match(message.found("matching nested conditions").items(match));
            }
            // 하나라도 실패한 경우 match false 반환
            return ConditionOutcome.noMatch(message.found("non-matching nested conditions").items(nonMatch));
        }
    }
    
}
```

## AnyNestedCondition

AbstractNestedCondition을 상속하는 추상 클래스로, 멤버 클래스 중 하나라도 매치되는 경우 조건에 매치된다고 판단함

```java
@Order(Ordered.LOWEST_PRECEDENCE - 20)
public abstract class AnyNestedCondition extends AbstractNestedCondition {
    
    // 구성 단계 설정
    public AnyNestedCondition(ConfigurationPhase configurationPhase) {
        super(configurationPhase);
    }

    // getFinalMatchOutcome 메서드는 AbstractNestedCondition의 추상 메서드로, 구현체의 멤버 클래스 조건 평가 결과에 대한 최종 판단을 위임함
    // AnyNestedCondition의 getFinalMatchOutcome은 매치되는 평가 결과가 하나라도 있으면 조건에 매치된다고 판단함
    @Override
    protected ConditionOutcome getFinalMatchOutcome(MemberMatchOutcomes memberOutcomes) {
        // 조건이 매치되는 게 하나라도 있는지 확인
        boolean match = !memberOutcomes.getMatches().isEmpty();
        
        // ConditionMessage 로직 생략
        
        // 결과 반환
        return new ConditionOutcome(match, ConditionMessage.of(messages));
    }
}
```