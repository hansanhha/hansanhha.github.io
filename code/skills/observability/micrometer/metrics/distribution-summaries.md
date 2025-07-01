---
layout: default
title:
---

#### index
- [Distribution Summary](#distribution-summary)
- [scaling and histograms](#scaling-and-histograms)


## Distribution Summary

Distribution Summary는 여러 개의 이벤트에서 발생한 값들의 분포(distribution)를 측정하고 집계하는 데 사용되는 메트릭 타입이다

주로 작업의 크기, 금액, 바이트 수 등과 같이 단순한 횟수나 시간과는 다른 "양"을 기록할 때 유용하다

#### 실시간 누적 측정

이벤트가 발생할 때마다 실시간으로 해당 이벤트의 "값"을 누적한다

e.g) http 요청의 응답 바이트 수, 거래 금액, 파일 전송 크기 등

#### 카운트 및 총합

이벤트의 총 개수(count)와 모든 이벤트의 합계(total amount)를 집계한다

#### 평균값과 분포 통계

count와 total amount값을 통해 평균값을 계산할 수 있으며 백분위(percentile)나 히스토그램같은 추가 통계를 구성할 수 있다


### DistributionSummary.builder

DistributionSummary 인스턴스는 아래와 같이 빌더를 이용하여 생성할 수 있다

```java
public class ResponseSizeTracker {

    private final DistributionSummary responseSizeSummary;

    public ResponseSizeTracker(MeterRegistry registry) {
        responseSizeSummary = DistributionSummary.builder("distribution.summary")
                .description("distribution of http response sizes")
                .baseUnit("bytes")
                .publishPercentiles(0.5, 0.95, 0.99)
                .register(registry);
    }

    public void recordResponseSize(long byteSize) {
        responseSizeSummary.record(byteSize);
    }
}
```


## scaling and histograms

Distribution Summary는 측정한 값들의 분포를 기록하기 위해 여러 히스토그램 버킷을 사용한다

기본적으로 마이크로미터는 미리 선택된 백분위 히스토그램 버킷을 1부터 `Long.MAX_VALUE` 까지의 정수 값들로 구성한다

만약 측정 대상의 값 범위가 작다면 기본 버킷 범위는 너무 넓어서 의미 있는 분포를 얻기 어렵다

scale 메서드를 통해 미리 버킷의 범위를 조정하여 이러한 문제를 해결할 수 있다

```java
public class ScalingBucketCardinality {

    private final DistributionSummary weightSummary;

    /*
        기본 버킷 범위: 1 ~ Long.MAX_VALUE 
        scale 메서드를 통해 기본 버킷 범위를 조정할 수 있다
        serviceLevelObjectives 메서드로 slo 경계를 설정하여 그 경계에 도달하면 비율을 모니터링할 수 있다
     */
    public ScalingBucketCardinality(MeterRegistry registry) {
        weightSummary = DistributionSummary.builder("distribution.summary.scale")
                .scale(100)
                .baseUnit("kg")
                .serviceLevelObjectives(70, 80, 90)
                .register(registry);
    }

    public void recordWeight(int weight) {
        weightSummary.record(weight);
    }

}
```

