#### index
- [configuration](#configuration)
- [example project](https://www.github.com/hansanhha/hansanhha.github.io/default/tree/code/stack/observability/examples/micrometer-prometheus)


## configuration

마이크로미터가 지원하는 프로메테우스 자바 클라이언트는 두 가지 버전이 존재한다

일반 클라이언트(`1.x`): `micrometer-registry-prometheus`

레거시 클라이언트(`0.x`): `micrometer-registry-prometheus-simpleclient`

```kotlin
dependencies {
    implementation(platform("io.micrometer:micrometer-bom:1.14.4"))
    implementation("io.micrometer:micrometer-core")
    implementation("io.micrometer:micrometer-registry-prometheus")
}
```