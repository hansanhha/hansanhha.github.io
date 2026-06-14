---
layout: default
title: 자바에서 제공하는 시간 API에 대해 알아보자
---

#### index

시간 소스 API
- [System.currentMillis(), System.nanoTime()](#systemcurrentmillis-systemnanotime)
- [Clock](#clock)
- [Year, YearMonth, Month, MonthDay, DayOfWeek](#year-yearmonth-month-monthday-dayofweek)
- [LocalDate, LocalTime, LocalDateTime](#localdate-localtime-localdatetime)
- [Instant](#instant)
- [ZonedDateTime, ZonedId, ZoneOffset](#zoneddatetime-zonedid-zoneoffset)
- [OffsetDateTime, OffsetTime](#offsetdatetime-offsettime)

시간 양/간격 API: [Duration, Period](#duration-period)

시간 데이터 API
- [Temporal](#temporal)
- [TimeUnit](#timeunit)

달력 시스템 API: [Chrono](#chrono)


## System.currentMillis(), System.nanoTime()

System.currentMillis()
- 현재 시스템의 UTC 기준 시각을 밀리초 단위로 반환한다 ([Epoch Time](./term.md#epoch-time))
- 절대 시간 (long 타입)
- 운영체제의 시스템 시계(wall-clock time)를 읽어온다 (하드웨어 타이머나 RTC 기반)
- NTP 동기화나 수동 변경으로 인해 시스템 시간이 바뀌면 값이 급격하게 바뀔 수 있다 -> 정확한 시간 간격 측정에는 부적합함

System.nanoTime()
- JVM 또는 OS 시작 이후 경과 시간을 나노초 단위(long)로 반환한다
- 절대 시간이 아니며 시작 기준이 정해져 있지 않다
- CPU의 고해상도 타이머에 기반하여 OS나 CPU에 따라 정확도가 다르다
- 정밀한 시간 간격을 측정/성능을 분석할 때 사용한다


## Clock

Clock 추상 클래스는 시스템의 현재 시각이나 시간대 기반 시각 정보를 제공하는 역할을 한다

java.time 패키지에서 시간에 접근하기 위한 추상화된 시간 소스로 Instant, LocalDateTime, ZonedDateTime 등의 API들과 함께 사용된다

### 구현체

**SystemClock**
- OS 시스템 시계를 기준으로 실시간으로 동작하는 구현체
- Clock.systemUTC(), Clock.systemDefaultZone() 메서드를 통해 생성할 수 있다
- JVM 실행 시 System.currentTimeMillis() 또는 System.nanoTime()을 기반으로 시간을 측정한다
- 실시간 값을 제공할 수 있다
- 값이 계속 변해서 테스트나 시뮬레이션에는 적합하지 않음

**FixedClock**
- 항상 동일한 시간 값을 반환하는 구현체
- Clock.fixed(Instant, ZoneId) 메서드를 통해 생성할 수 있다
- 특정 시점을 기준으로 코드의 동작을 검증할 수 있다
- 실시간 애플리케이션에는 부적합

**OffsetClock**
- 기준 시계(Clock)에 Duration 만큼 시간 차를 더하는 구현체
- Clock.offset(Clock, Duration) 메서드를 통해 생성할 수 있다
- 상대적인 시간 흐름을 만들 수 있다
- DST(서머타임)이나 타임존은 고려하지 않음

**TickClock**
- 지정된 단위(분, 초, 밀리초)로만 시간이 변경되는 구현체 (Duration.ofSeconds(1)이면 1초 단위로만 값이 바뀜)
- Clock.tick() 메서드 등으로 생성할 수 있다
- 고정된 해상도로 시간을 측정하여 샘플링, 메트릭 측정, 테스트 등에 적합하다

실시간 시간: SystemClock

테스트 고정 시각: FixedClock

상대적 시간 조작: OffsetClock

단위 해상도 제한: TickClock


## Year, YearMonth, Month, MonthDay, DayOfWeek

다섯 개의 클래스는 모두 특정 단위에 특화된 클래스이며 내부적으로 [LocalDate](#localdate-localtime-localdatetime)를 사용한다

Year: 연도만을 나타내는 클래스

YearMonth: 연도와 월을 나타내는 클래스

Month: 월만 나타내는 Enum 클래스

MonthDay: 월과 일만 표현하는 클래스

DayOfWeek: 요일을 나타내는 Enum 클래스

각 클래스는 특정 날짜를 빼거나 더하는 계산이나 요일을 구하는 메서드를 지원한다


## LocalDate, LocalTime, LocalDateTime

이들은 모두 타임존 없이 날짜와 시간을 다루는 불변 클래스로 로컬 기준 시간을 표현한다

필요한 경우 ZoneId를 지정하여 [ZonedDateTime](#zoneddatetime-zonedid-zoneoffset)으로 변환할 수 있다

LocalDate: 날짜만 다루는 클래스 (YYYY-MM-DD)

LocalTime: 시간만 다루는 클래스 (HH:mm:ss.nnnnnnnnn)

LocalDateTime: 날짜와 시간을 다루는 클래스 (YYYY-MM-DDTHH:mm:ss.nnnnnnnnn)

타임존이 필요한 경우 [ZonedDateTime](#zoneddatetime-zonedid-zoneoffset) [OffsetDateTime](#offsetdatetime-offsettime)


## Instant

Instant는 [Epoch Time](./term.md#epoch-time) 기반의 UTC 타임스탬프를 표현하는 클래스이다

LocalDateTime처럼 기본적으로 타임존을 포함하지 않는 불변 정보이지만 필요시 ZoneId와 함께 [ZonedDateTime](#zoneddatetime-zonedid-zoneoffset)으로 변환할 수 있다

나노초(10⁻⁹)까지 표현할 수 있다

용도
- UTC 기준으로 절대 시간이 필요할 때 (타임존 독립적 시간)
- 시스템 간 동기화 로그, 트랜잭션 타임스탬프

```java
// 2025-05-22T09:28:03.265941Z
Instant.now();
```


## ZonedDateTime, ZonedId, ZoneOffset

**ZonedDateTime**은 날짜와 시간, 타임존 정보(ZonedId, ZoneOffset)까지 포함하고 있는 완전한 시각 표현 클래스이다 (UTC 기반 시각 표현)

특징: 서머 타임 자동 반영, 불변 객체(연산 시 항상 새 객체 반환), Instant로 변환 가능(절대 시각 표현)

주로 사용자의 지역 시간을 다룰 때 사용한다 (지역 시간 기반 처리가 필요한 경우) 

용도
- 글로벌 서비스 운영 시: 시간 동기화, 사용자 기준 시간 로직
- 이벤트 예약, 항공권 등 지역 시간 기반 처리
- DB 또는 로그에서 UTC <-> 지역 시간 변환 시

```java
// 2025-05-23T14:00+09:00[Asia/Seoul]
ZonedDateTime.now();
```

**ZoneId**는 타임존을 식별하는 객체로 도시(Asia/Seoul 등)의 시간대 규칙(서머타임 등)을 포함하여 시간대를 표현한다 (IANA 타임존 데이터베이스 기반)

```java
// 시스템 타임존 확인
ZoneId.systemDefault();

// 문자열로부터 타임존 생성
ZoneId.of("Asia/Seoul")

// 지원하는 타임존 목록 확인
ZoneId.getAvailableZoneIds();
```

**ZoneOffset**은 고정된 시간 오프셋을 지정할 때 사용한다 (서머 타임 미포함)

```java
// 고정된 오프셋 생성하기 (문자열 또는 int)
ZoneOffset.of("+09:00");
ZoneOffset.ofHours(1);
ZoneOffset.ofHours(-4);
```


## OffsetDateTime, OffsetTime

**OffsetDateTime**
- LocalDateTime + ZoneOffset을 합친 클래스로, 날짜와 시간 및 UTC 기준 오프셋 정보를 표현한다
- 타임존 대신 UTC 기준 오프셋 정보를 포함하여 시간을 기록해야 될 때 사용한다

**OffsetTime**
- LocalTime + ZoneOffset 조합으로 시간과 UTC 기준 오프셋 정보를 표현한다
- 날짜는 중요하지 않지만 오프셋 기준의 시간이 필요한 경우 사용 (항상 같은 시간대에 작동하는 이벤트 등)

```java
// ZonedDateTime과 달리 타임존(Asia/Seoul 등), 서머 타임을 포함하지 않음
// 2025-05-23T14:00+09:00
OffsetDateTime.now();
```


## Duration, Period

Duration: 시간 양 또는 간격 계산 (불변, DST 고려 X)

Period: 날짜 양 또는 간격 계산 (불변)

```java
// 시간 간격 계산하기
// PT2H30M
Duration between = Duration.between(
    LocalTime.of(10, 0),
    LocalTime.of(12, 30)
);
```

```java
// 날짜 간격 계산하기
// P2Y4M21D
Period between = Period.between(
    LocalDate.of(2023, 1, 1),
    LocalDate.of(2025, 5, 22)
);
```


## Temporal

java.time.temporal 패키지는 LocalDateTime, ZonedDateTime 등의 고수준 API를 구현하고 확장 가능하게 해주는 기반 모델을 정의한다

주요 컴포넌트
- Temporal: 날짜/시간을 나타내는 추상 모델(읽기/쓰기)
- TemporalAccessor: 날짜/시간을 읽기 전용으로 추상화한 모델
- TemporalAdjuster: Temporal 객체를 커스터마이징하는 전략 객체
- TemporalAmount: 시간의 양을 나타내는 모델 (Duration, Period가 구현체)
- TemporalField: 연도, 월, 일 등의 시간 범위 (IsoField, WeekFields 등)
- TemporalUnit: 초, 분, 일 등의 시간 단위 (ChronoField 등)

### ChronoUnit

ChronoUnit은 TemporalUnit의 구현체로 시간 단위별로 나초초 또는 초의 양을 정의한 Enum 클래스다

아래와 같이 단위 별 이름과 기간을 필드로 가지며 해당 단위가 몇 초를 가지는지 매핑한다 (초 이하의 단위는 나노초로 표현)

표현 범위
- 하루: 1년 이하: 나노초, 마이크로초, 밀리초, 초, 분, 시, 반나절, 일,
- 1년: 주, 월, 년
- 기타: 10년, 1세기, 천 년, 시대, 영원

```java
public enum ChronoUnit implements TemporalUnit {

    private final String name;
    private final Duration duration;
}
```

### ChronoField

ChronoField는 시간 단위를 기반으로 특정 시간의 범위를 나타내는 Enum 클래스다

일주일의 일수: 1 ~7일

1일 총 분수: 0 ~ (24 * 60) - 1)분 

TemporalUnit(ChronoUnit)을 기반으로 다양하게 시간 범위를 정의해놓았다 


## TimeUnit

TimeUnit은 시간 단위를 명확하게 표현하고 변환할 수 있도록 정의된 Enum 클래스다

java.time이 아닌 java.util.concurrent 패키지에 위치해있으며 다음과 같은 용도로 사용한다

1. 시간 간격을 나노초, 밀리초, 초, 분, 시간, 일 등의 단위로 변환 (convert())
2. 쓰레드 지연(sleep), 대기 시간 설정 등


## Chrono

자바의 날짜 기능(LocalDateTime, ZonedDateTime 등)은 기본적으로 ISO-8601(그레고리력)을 기반으로 한다

크로노 시스템은 그레고리력 뿐만 아니라 다양한 달력 시스템을 지원하여 비서양식 달력(일본, 히잡력, 타이력 등)도 동일한 API로 처리할 수 있다

주요 컴포넌트
- Chronology: 달력 시스템을 정의하는 인터페이스
- ChronoLocalDateTime, ChronoZonedDateTime: 각 달력 시스템에 맞는 LocalDateTime, ZonedDateTime 버전 (Chronology에 따라 실제 달력 구현체로 분기됨)
- Era: 달력 시스템에서 사용하는 연호 (ISO - IsoEra, 일본 - JapaneseEra)

Chronology 구현체
- IsoChronology: 기본 그레고리력 (ISO-8601)
- JapaneseChronology: 일본 연호 시스템 (헤이세이, 레이와)
- MinguoChronology: 대만 민국력
- ThaiBuddhistChronology: 태국 불기력 (불기)
- HijrahChronology: 이슬람 히잡력 (음력 기반)

