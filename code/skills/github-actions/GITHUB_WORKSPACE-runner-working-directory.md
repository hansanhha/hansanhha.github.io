---
layout: default
title:
---

[GITHUB_WORKSPACE: runner working directory](#github_workspace-runner-working-directory)

[sparse-checkout: 일부 디렉토리만 GITHUB_WORKSPACE에 로드하기](#sparse-checkout-일부-디렉토리만-github_workspace에-로드하기)

[actions/checkout 주의점](#actionscheckout-주의점)


## GITHUB_WORKSPACE: runner working directory

github-hosted runner는 기본적으로 프로비저닝된 새로운 가상 환경에서 실행되지만 리포지토리의 파일들이 자동으로 복사되지 않는다

리포지토리의 파일을 사용하려면 직접 가져와야 하는데, 보통 actions/checkout 액션을 사용해서 가져온다

```yaml
jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - name: Checkout Repository
        uses: actions/checkout@v4
```

위처럼 actions/checkout 액션을 사용하면 `GITHUB_WORKSPACE` 디렉토리(워크플로우가 실행되는 작업 디렉토리)에 리포지토리의 모든 파일을 가져오며 runner 내부에서 모든 파일들에 접근할 수 있다


## sparse-checkout: 일부 디렉토리만 GITHUB_WORKSPACE에 로드하기

actions/checkout 액션은 기본적으로 전체 리포지토리를 가져오지만 특정 디렉토리만 가져오는 옵션을 제공하지 않는다

대신 git sparse-checkout 기능을 활용하면 특정 디렉토리만 워킹 디렉토리에 로드할 수 있다

```yaml
jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - name: sparse-checkout repository
        uses: actions/checkout@v4
        with:
          sparse-checkout: |
            src/
            config/
            
          # sparse-checkout은 기본적으로 디렉토리와 그 하위 모든 파일을 가져온다
          # 디렉토리의 특정 파일만 워킹 디렉토리에 로드하는 경우 cone-mode 비활성화
#          sparse-checkout-cone-mode: false
```


## actions/checkout 주의점

각 job은 서로 다른 runner에서 실행되므로 job 간에는 리포지토리 파일이 공유되지 않는다

따라서 리포지토리 파일 접근이 필요한 경우 actions/checkout을 다시 실행해야 한다

같은 job에서 실행되는 step의 경우 GITHUB_WORKSPACE가 유지되므로 그대로 파일을 사용할 수 있다

서브 모듈까지 가져오려면 다음과 같이 submodules: true 옵션을 추가해야 한다

```yaml
- name: Checkout Repository with Submodules
  uses: actions/checkout@v4
  with:
    submodules: true
```



