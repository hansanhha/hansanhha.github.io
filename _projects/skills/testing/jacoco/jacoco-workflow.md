---
layout: default
title:
---

[jaococ 주요 개념](#jacoco-주요-개념)

[workflow](#workflow)

[runtime vs offline instrumentation](#runtime-vs-offline-instrumentation)

## jacoco 주요 개념

jacoco는 기본적으로 런타임에 jvm 바이트코드를 변환하고 실행 정보를 수집하여 코드 커버리지를 측정하는 방식으로 동작한다

계측(instrumentation): 바이트코드를 수정하여 커버리지 측정 코드를 삽입한다

실행 데이터(execution data): 테스트 실행 시 어떤 코드가 실행되었는지 기록하는 데이터

커버리지 보고서(coverage report): html, xml, csv 등의 형식으로 실행 결과를 분석하여 보고서를 생성한다


## workflow

바이트코드 계측 -> 테스트 실행 및 실행 데이터 저장 -> 코드 커버리지 분석 -> 커버리지 보고서 생성

### 바이트코드 계측

jacoco는 빌드된 클래스 파일(바이트코드)에 추가적인 계측 코드를 삽입한다

이 과정에서 각 코드의 실행 여부를 추적하는 코드가 자동으로 추가된다 (gradle jacocoTestReport)

### 테스트 실행 및 실행 데이터 저장

junit, testng 등의 테스트 프레임워크를 통해 테스트 코드를 실행한다

jacoco는 계측된 바이트코드를 통해 각 메서드 및 라인이 실행되었는지 추적한다

실행 결과는 저장 방식에 따른 실행 데이터에 저장하며 이 데이터를 기반으로 분석하여 보고서를 생성한다

실행 데이터 저장 방식
- 파일 시스템(default): jacoco.exec 파일
- tcp/socket(remove server 모드): 원격 서버에서 실행된 결과를 수집할 때 사용한다
- in-memory: ci/cd 환경에서 빠른 분석을 위해 메모리에서 직접 처리하는 방식

### 코드 커버리지 분석

실행 데이터를 기반으로 어떤 코드가 실행되었는지 분석하고, 분석 결과를 명령어/메서드 등의 단위로 분류하여 커비리지 비율을 계산한다

| 기준                   | 설명                      |
|----------------------|-------------------------|
| instruction coverage | jVM 명령어(바이트코드) 기준 커버리지  |
| line coverage        | 소스 코드의 라인별 실행 여부        |
| branch coverage      | if, switch 등의 분기문 실행 여부 |
| method coverage      | 클래스 내 메서드별 실행 여부        |
| class coverage       | 클래스별 실행 여부              |

### 커버리지 보고서 생성

jacoco는 최종적으로 html, xml, csv 등의 보고서를 생성하여 결과를 시각화해서 보여준다

테스트되지 않은 코드는 빨간색, 테스트된 코드는 초록색으로 처리한다


## runtime vs offline instrumentation

jacoco는 기본적으로 실행 시점에 바이트코드를 수정하여 계측하는 방식으로 동작한다 (runtime instrumentation)

offline instrumentation 방식은 빌드 시점에 바이트코드를 수정하여 계측하는 방식으로 실행 전에 미리 변환된 클래스를 사용한다