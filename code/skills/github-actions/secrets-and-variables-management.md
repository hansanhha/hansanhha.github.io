---
layout: default
title:
---

[env](#env)

[secrets](#secrets)

[secrets 네이밍 규칙](#secrets-네이밍-규칙)

[secrets 개수, 사이즈 제한](#secrets-개수-사이즈-제한)

[repository secrets/variables 생성](#repository-secretsvariables-생성)

[secrets context, GITHUB_TOKEN](#secrets-context-github_token)

[secrets 사용](#secrets-사용)


## env

github actinos 내에서 일반적인 환경 변수를 저장하거나 사용할 때 사용하는 환경 변수를 말한다

job(env)또는 steps($GITHUB_ENV)에서 env 변수를 정의할 수 있다 

run에서 환경 변수를 사용할 때 각 실행 환경에 맞는 참조 형식(linux/macos: $변수명, windows: %변수명%)을 사용하여 변수에 접근할 수 있으며 job에 속한 step들은 모두 동일한 runner에서 실행되므로 정의된 변수는 모든 step에서 접근할 수 있다

민감한 정보를 저장하는 용도로는 secrets 사용을 권장한다

```yaml
name: example

on: push

jobs:
  example-job:
    runs-on: ubuntu-latest
    
    # job에서 env 변수 설정
    env:
      GREETING: "hello"
      NAME: "env"

    steps:
      - # steps 내부에서 env 변수 설정
      - name: set environment variable
        run: echo "STEP_ENV=test" >> $GITHUB_ENV
        
        # env 변수 접근
      - name: print environment variables
        run: echo "$GREETING, $STEP_ENV $NAME"!
```

위의 example workflow를 실행하면 아래와 같은 실행 결과가 출력된다

```text
hello, test env!
```

## secrets

조직, 리포지토리 및 리포지토리 환경 등 민감한 정보(인증 토큰, 비밀번호, api 키 등)를 저장하는 변수를 말한다

github actions는 workflow에서 명시적으로 포함한 secrets를 읽을 수만 있으므로 env와 다르게 workflow 내에서 secrets는 만들 수 없으며 yaml 파일에서 직접 값을 확인할 수 없다


## secrets 네이밍 규칙

`a-z` `A-Z` `0-9`와 언더스코어 값만 사용할 수 있으며 스페이스는 허용하지 않는다

`GITHUB_` prefix를 허용하지 않는다

숫자로 시작하는 이름을 허용하지 않는다

대소문자를 구별한다

secrets가 생성되는 레벨(organization-level, repository-level, environment-level)에서 고유한 이름을 가져야 한다

여러 레벨에서 동일한 이름으로 정의된 경우 구체적인 레벨에서 정의한 secret이 우선순위를 가진다 (environment-level > repository-level)


## secrets 개수, 사이즈 제한

organization secrets: 1000개

repository secrets: 100개

environment secrets: 100개

각 secrets의 사이즈: 최대 48KB

[더 큰 사이즈의 secrets가 필요한 경우 참고](https://docs.github.com/en/actions/security-for-github-actions/security-guides/using-secrets-in-github-actions?tool=webui#storing-large-secrets)


## repository secrets/variables 생성

repository secrets 설정은 repository -> settings -> secret and variables -> actions 에서 수행할 수 있다

personal account repository에서 secrets/variables를 생성하는 경우 repository owner이어야 하고 (collaborators X), organization 리포지토리의 경우 admin 접근 권한이 필요하다

rest api를 통해 생성하는 경우(personal account/organization) collaborator 접근 권한이 필요하다 (개인 계정 본인 소유의 리포지토리라면 자동으로 가지는 권한)

[secrets 생성 프로세스](https://docs.github.com/en/actions/security-for-github-actions/security-guides/using-secrets-in-github-actions?tool=webui#creating-secrets-for-a-repository)

[environment secrets 생성 프로세스](https://docs.github.com/en/actions/security-for-github-actions/security-guides/using-secrets-in-github-actions?tool=webui#creating-secrets-for-an-environment)


## secrets context, GITHUB_TOKEN

`secrets context`는 workflow에서 실행될 수 있는 secret의 이름과 값을 포함한 객체다

복합 action을 구축한 경우 보안 상의 이유로 secrets context를 사용할 수 없게 제한하지만 필요한 경우 secret을 input으로 명시하여 다른 action에게 전달할 수 있다

[`GITHUB_TOKEN`](https://docs.github.com/en/actions/security-for-github-actions/security-guides/automatic-token-authentication)은 workflow가 실행될 때마다 자동으로 생성되는 secret으로 항상 secrets context에 포함된다

| 프로퍼티 이름               | 타입     | 설명                                                                                                             |
|-----------------------|--------|----------------------------------------------------------------------------------------------------------------|
| secrets               | object | secrets.GITHUB_TOKEN과 secrest.<secret_name>을 모두 포함한 객체로 workflow에서 실행되는 모든 job에서 동일한 값을 가지며 어느 step에서나 접근할 수 있다 |
| secrets.GITHUB_TOKEN  | string | 각 workflow가 실행될 때 자동으로 생성되는 secret                                                                             |
| secrets.<secret_name> | string | 특정 secret 값                                                                                                    |

#### 주의점

secrets를 job에서 사용하는 경우 github은 자동적으로 secrets를 로그에 출력한다

따라서 로그에 secret을 출력하는 것을 명시적으로 막아줘야 한다 [참고](https://docs.github.com/en/actions/security-for-github-actions/security-guides/using-secrets-in-github-actions?tool=webui#redacting-secrets-from-workflow-run-logs)


## secrets 사용

[rest api github actions secret](https://docs.github.com/en/rest/actions/secrets?apiVersion=2022-11-28)

repository에 생성한 secrets를 input이나 environment variables로 github actions에서 사용하려면 다음과 같이 secrets context를 사용하여 접근할 수 있다

만약 설정되지 않은 secret을 참조한 경우 해당 secrets context는 빈 string을 반환한다

```yaml
steps:
  - name: Hello world action
    with: # with: 입력으로 secret 설정
      super_secret: ${{ secrets.SuperSecret }}
    env: # env: env로 secret 설정
      super_secret: ${{ secrets.SuperSecret }}
```

#### command line에 secret을 전달해야 하는 경우

linux/macos: `"$SECRET"`

windows: `$env:SECRET`

```yaml
steps:
  - shell: bash
    env:
      SUPER_SECRET: ${{ secrets.SuperSecret }}
    run: |
      example-command "$SUPER_SECRET"
```

```yaml
steps:
  - shell: pwsh
    env:
      SUPER_SECRET: ${{ secrets.SuperSecret }}
    run: |
      example-command "$env:SUPER_SECRET"
```









