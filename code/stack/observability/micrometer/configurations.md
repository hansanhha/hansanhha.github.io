#### index
- [gradle configuration](#gradle-configuration)

## gradle configuration

[micrometer module dependencies](https://docs.micrometer.io/micrometer/reference/concepts.html#concepts-dependencies)

마이크로미터는 계측 api가 포함된 코어 라이브러리, 인메모리 구현체(데이터를 외부로 내보내지 않는), 다양한 모니터링 시스템에 대한 구현 모듈 및 테스트 모듈로 구성된다

마이크로미터 공식문서에서는 모니터링 시스템에서 의존성을 추가할 때 마이크로미터에서 제공되는 bom을 사용하는 것을 권장한다

다음과 같이 bom을 설정한 뒤 필요한 추가 모듈을 선언한다

참고로 프레임워크를 사용하는 경우에는 bom의 버전을 직접 명시하기 보다 프레임워크의 dependency management에게 맡기는 것이 안전한 동작을 보장한다

```kotlin
dependencies {
    // micrometer bom 설정
    implementation(platform("io.micrometer:micrometer-bom:1.14.4"))

    // micrometer prometheus 모듈 추가
    implementation("io.micrometer:micrometer-registry-prometheus")
}
```



 






