---
layout: default
title:
---

[github actions](#github-actions)

[components](#components)

[workflow](#workflow-1)


## github actions

github actions는 빌드, 테스트, 배포 파이프라인을 자동화할 수 있는 ci/cd 플랫폼으로 리포지토리에 대한 특정 이벤트들(pr, new issue, merged pull request 등)에 대한 워크플로우를 만들고 실행할 수 있는 환경을 제공한다

ci/cd 뿐만 아니라 이벤트(코드 변경 등)를 감지하고 다양한 자동화 작업을 실행할 수 있다

자동화 예시
- pr이 열릴 때 코드 리뷰어 자동 할당
- 특정 label이 달리면 이슈 자동 응답
- 릴리즈 태그가 생성되면 자동 배포
- 정적 분석, 코드 스타일 체크, 보안 검사 등


## components

![github actions components](./images/github%20actions%20components.png)

#### workflow

github actions의 최상위 개념으로 전체 실행 단위를 의미하는 것으로 `.github/workflows/*.yml` 파일로 정의된다

특정 이벤트가(push, pull_request) 감지되거나 주기적인 설정 또는 직접적으로 트리거하여 실행시킬 수 있다  

또한 워크플로우가 또 다른 워크플로우를 사용할 수도 있다 (workflow_call)

```yaml
name: test-workflow  # 워크플로우 이름

on: push  # 트리거할 github event

jobs:  # 하나 이상의 job을 실행한다
  build:
    runs-on: ubuntu-latest
    steps:
      - name: checkout code
        uses: actions/checkout@v3  # github 제공 action
```

#### event

워크플로우 실행을 트리거하는 리포지토리에서 발생하는 특정 이벤트를 의미한다

풀 리퀘스트를 만들거나, 이슈를 열거나 리포지토리에 커밋하는 등의 행동으로 이벤트가 발생할 수 있다

[이벤트 목록](https://docs.github.com/en/actions/writing-workflows/choosing-when-your-workflow-runs/events-that-trigger-workflows)

#### job, steps

워크플로우에 속한 한 개 이상의 steps을 가진 독립적인 실행 단위로 가상 머신 runner(linux, windows, macos) 또는 컨테이너에서 순차 또는 병렬적으로 실행된다

step은 쉘 스크립트 또는 action을 실행하는 개별적인 작업 단위로 한 개 이상의 step이 하나의 job을 구성한다

job에 속한 step은 모두 같은 runner(실행 환경)에서 실행되며 inputs, outputs를 사용하여 하나의 step은 다른 step과 데이터를 공유할 수 있다

각 job은 서로 다른 프로세스에서 실행되므로 기본적으로 데이터를 공유할 수 없지만 [artifact](./artifact,%20caching.md#artifact) 메커니즘을 이용하면 하나의 workflow에서 여러 job 간 데이터를 공유할 수 있다

e.g) 애플리케이션을 빌드하는 단계와 빌드된 애플리케이션을 테스트하는 단계

```yaml
jobs:
  build:                     # build job
    runs-on: ubuntu-latest   # runner
    steps:
      - name: checkout code
        uses: actions/checkout@v3  # step 1: github 제공 action 실행
      - name: build project
        run: echo "Building..."    # step 2: 쉘 스크립트 실행

  test:                       # test job
    runs-on: ubuntu-latest
    needs: build              # build -> test 실행 순서 지정
    steps:
      - name: run tests
        run: echo "Running tests..."
```

#### actions

자주 반복되는 작업을 수행하는 github actions 전용 커스텀 애플리케이션이다

애플리케이션 코드에서 메서드를 통해 로직을 재사용하듯이 복잡하고 반복되는 코드를 github actions에서 action을 통해 간단하게 처리할 수 있다 

커스텀 action을 만들 수도 있으며 github market place에서 미리 정의된 action들을 찾을 수도 있다 

#### runners

workflow가 실행되는 컴퓨팅 환경으로 기본적으로 github이 제공한다

지원하는 환경: ubuntu linux, windwos, macos

각 workflow는 새로 프로비저닝된 새로운 환경에서 실행되며, 각 runner는 단일 job을 실행할 수 있다

직접 서버를 설정하는 [self-hosted runner](./github-hosted%20runner%20vs%20self-hosted%20runner.md)를 구축할 수도 있다


## workflow

#### 이벤트 발생 (trigger)

push, pull_request, schedule, issue_commit 등의 이벤트가 발생한다

#### workflow 실행

이벤트 감지, 설정된 스케줄에 따라 `.github/workflows/` 디렉토리에서 해당하는 workflow를 실행한다

#### job 실행

runs-on에 정의된 환경에서 job이 실행된다

#### step 실행

job 내부의 step들이 순차적으로 실행된다

action 또는 shell script를 실행한다







