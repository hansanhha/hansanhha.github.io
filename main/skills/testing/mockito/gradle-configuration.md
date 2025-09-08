---
layout: default
title:
---

## gradle configuration

#### 의존성 설정

mockito를 사용하려면 핵심 기능을 제공하는 mockito-core 모듈과 junit 5 엔진과의 호환을 위한 mockito-junit-jupiter를 의존성으로 추가하면 된다

```kotlin
dependencies {
    // junit 5 의존성
    testImplementation("org.junit.jupiter:junit-jupiter:${junit version}")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // mockito 의존성
    testImplementation("org.mockito:mockito-core:${mockito version}")
    testImplementation("org.mockito:mockito-junit-jupiter:${mockito version}")
}
```