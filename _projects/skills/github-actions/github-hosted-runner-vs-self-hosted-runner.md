---
layout: default
title:
---

[github-hosted runner](#github-hosted-runner)

[self-hosted runner](#self-hosted-runner)


## github-hosted runner

github에서 무료로 제공하는 실행 환경으로 github이 관리하는 가상 머신에서 job이 실행되고, 실행이 끝나면 자동으로 정리된다

자동 프로비저닝, 최신 환경 지원, 보안 관리, 무료 사용(일정 사용량) 등의 기능을 제공한다

runs-on에 os(macos, linux, windows)를 지정하면 github-hosted runner에서 job을 실행할 수 있다

다만 실행 환경이 매번 새롭게 생성되므로 캐시가 초기화되며 사용량이 많으면 비용이 발생할 수 있다


## self-hosted runner

본인이 운영하는 서버(물리/가상 서버, 클라우드 등)에 github actions runner를 설치해서 운영하는 방식이다

직접 서버를 구현하기 때문에 고성능/커스텀 환경 구성, 네트워크 보안 제어, 특정 os/하드웨어 사용, 비용 절약 등의 이점을 누릴 수 있다

runs-os에 self-hosted 태그를 지정한다