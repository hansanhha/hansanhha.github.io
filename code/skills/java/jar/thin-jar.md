---
layout: default
title:
---

#### index
- [jar file structure](#jar-file-structure)

https://docs.oracle.com/javase/tutorial/deployment/jar/manifestindex.html

## jar file structure

jar 파일의 내부 구조는 다음과 같다

```text
my-app.jar
|-- META-INF/
    |-- MANIFEST.MF        메타데이터 파일 (메인 클래스, 버전, 클래스 경로 등)   
|-- com/example            패키지 구조 (프로젝트 클래스 파일 패키지)
    |-- Application.class  엔트리 포인트 클래스 (메인 클래스)
    |-- Service.class
    |-- ...
|-- application.yml        설정 파일
|-- lib/                   jar 아티팩트 안에 의존성 jar 포함 (fat jar 또는 spring boot jar의 경우) 
    |-- dependency-1.jar
    |-- dependency-2.jar
```