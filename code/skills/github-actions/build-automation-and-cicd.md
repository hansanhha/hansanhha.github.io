---
layout: default
title:
---

[build automation](#build-automation)

[ci, cd](#ci-cd)

[ci, cd pipeline](#ci-cd-pipeline)

[ci, cd tools](#ci-cd-tools)


## build automation

build: 소스 코드를 실행 가능한 프로그램으로 변환하는 과정

실행 가능한 소프트웨어를 만들기 위한 컴파일, 의존성 다운로드, 테스트, 패키징 등의 과정을 build라고 한다

빌드 자동화란 개발자에 의한 수동 빌드 대신 자동(무인) 방식으로 소프트웨어 시스템을 구축하는 관행을 말한다

자동화된 빌드 시스템은 다음과 같은 이점을 가진다

#### 일관성 유지

개발자마다 다른 환경에서 수동 빌드 시 오류 발생 가능이 높은 반면, 자동 빌드는 일관된 환경에서 동작하므로 빌드 오류를 줄일 수 있다

#### 생산성 향상

반복적인 빌드 작업을 자동화하여 개발 시간을 절약할 수 있다

#### 배포 프로세스 단순화

코드 푸시만으로 자동화된 빌드 시스템을 거쳐 소프트웨어를 배포할 수 있다 (ci, cd 파이프라인)


## ci, cd

ci, cd는 소스 코드 통합과 배포를 자동화하여 빠르고 안정적인 소프트웨어 제공(빠른 릴리즈 사이클)을 목표로 하는 개념으로 새 버전을 배포하기 전 대규모 배치작으로 소프트웨어를 통합하는 전통적인 방식과 대조되는 프로세스를 가진다

continuous integration (지속적 통합)
- 코드 변경 사항을 자주 main 브랜치에 병합하고 자동 테스트를 실행하는 단계
- 자동화된 테스트는 빠르게 코드 품질을 검증하여 소프트웨어 기능을 보장하고 문제를 조기에 발견한다

continuous delivery/continuous deployment (지속적 전달/지속적 배포)
- continuous delivery: ci + 수동 배포 승인
- continuous deployment: ci + 배포 자동화

지속적 배포의 경우 테스트를 통과하면 수동 승인 없이 즉시 스테이징/프로덕션 환경에 아티팩트가 배포된다

이 때 배포 안정성을 확보하기 위해 canary 배포(일부 사용자에게 먼저 배포하는 방식), blue-green(새 버전과 기존 버전을 동시에 운영하는 방식), 롤백 전략(배포 실패 시 이전 버전으로 되돌리는 기능) 등의 방법을 사용한다


## ci, cd pipeline

1. 개발자 코드 변경 후 push/pr
2. 빌드, 테스트 자동 실행
3. 아티팩트(배포 파일) 생성
4. 스테이징 환경 배포 (선택적 배포 승인 - continuous delivery)
5. 프로덕션 환경 배포 (완전 자동화 배포 - continuous deployment)


## ci, cd tools

github actions: github 기반 소프트웨어 개발 workflow 자동화 도구

jenkins: 오픈소스 ci/cd 자동화 서버

gitlab ci/cd, circleci, travisci, argocd, tekton 등