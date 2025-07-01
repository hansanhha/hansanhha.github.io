plugins {
    java
}

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion =
            JavaLanguageVersion.of(21)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

dependencies {
    implementation("org.hibernate.orm:hibernate-core:6.6.4.Final")

    runtimeOnly("com.h2database:h2:2.3.232")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
