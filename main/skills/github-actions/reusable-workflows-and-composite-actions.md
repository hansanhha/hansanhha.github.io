---
layout: default
title:
---

[reusable workflows](#reusable-workflows)
- [example](#example-for-reusable-workflows)
- [matrix strategy](#matrix-strategy)
- [nesting reusable workflows](#nesting-reusable-workflows)
- [using outputs from a reusable workflow](#using-outputs-from-a-reusable-workflow)
- [limitations](#limitations)
- [github docs](https://docs.github.com/en/actions/sharing-automations/reusing-workflows)

[composite actions](#composite-actions)
- [example](#example-for-composite-actions)


[reusable workflows vs composite actions](#reusable-workflows-vs-composite-actions)


## reusable workflows

어떤 클래스를 정의한 뒤 다른 클래스에서 이 클래스를 포함하여 코드 중복을 방지하면서 유지보수성을 높이듯이, 워크플로우 역시 다른 워크플로우를 포함시켜 자신의 워크플로우의 일부로 재사용할 수 있다 

이 때 다른 워크플로우를 호출하는 워크플로우를 caller workflow라고 하고 호출된 워크플로우를 called 워크플로우라고 한다

caller 워크플로우에 의해 트리거된 called 워크플로우의 github context는 항상 caller 워크플로우와 관련되어 있으며 수행하는 어느 action이든 caller workflow의 일부분으로 수행된다

e.g) 다른 리포지토리에 존재하는 called 워크플로우가 actions/checkout을 수행하는 경우 호스트한 caller 워크플로우의 리포지토리 컨텐츠를 체크아웃한다

### example for reusable workflows

reusable workflow 파일은 다른 workflow와 유사하게 yaml 형식으로 정의하며 `.github/workflows` 디렉토리에 위치한다 (하위 디렉토리 X)

#### reusable workflow example

`on.workflow_call` 이벤트를 지정하여 reusable workflow임을 명시한다

하위에 inputs와 secrets 키워드를 선언하여 caller workflow에서 정의한 입력값과 비밀변수를 사용할 수 있다 (또는 caller workflow에서 secrets: inherit을 사용하여 암묵적으로 secrets 사용)

```yaml
name: reusable workflow example

on:
  workflow_call:
    # inputs와 secrets 키워드를 통해 caller workflow에서 정의한 입력값과 비밀변수를 resuable workflow 내에서 사용할 수 있다
    # 또는 caller workflow 내에서 secrets: inherit을 설정하면 아래와 같이 명시적으로 secrets를 선언하지 않아도 사용할 수 있다
    inputs:
      config-path:
        required: true
        type: string
    secrets:
      personal_access_token:
        required: true

jobs:
  reusable-workflow-job:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/labeler@v4
        with:
          repo-token: ${{ secrets.token }}
          configuration-path: ${{ inputs.config-path }}
```

#### caller workflow example

```yaml
name: call a reusable workflow

on:
  push:
    branches:
      example-reusable-workflow

# reusable workflow를 참조하는 두 가지 방법(여러 workflow 파일을 참조하는 경우 각각 사용 가능)

# 1. public 또는 private 리포지토리의 reusable workflow 참조
# {owner}/{repo}/.github/workflows/{filename}@{ref}
# {ref}: SHA, release 태그 또는 브랜치 이름

# 2. 같은 리포지토리의 reusable workflow 참조
# ./.github/workflows/{filename}

jobs:
  call-workflow-passing-data:
    permissions:
      contents: read
    uses: ./.github/workflows/reusable-workflow.yml
    with:
      config-path: .github/labeler.yml
    secrets:
      token: ${{ secrets.GITHUB_TOKEN }}

```

### matrix strategy

matrix 전략을 사용하면 단일 reusable workflow을 정의한 변수만큼 여러 개의 job으로 실행할 수 있다

```yaml
name: using the matrix strategy call a reusable workflow

on:
  push:
    branches:
      - example-reusable-workflow

jobs:
  reusable-matrix-job-for-development:
    strategy:
      matrix:
        target: [dev, stage, prod]
    uses: ./.github/workflows/reusable-workflow.yml
    with:
      target: ${{ matrix.target }}
```

### nesting reusable workflows

github actions는 최대 4개의 중첩된 워크플로우를 호출할 수 있다

top-level workflow -> nested workflow 1 -> nested workflow 2 -> nested workflow 3

중첩된 workflow는 직접적으로 호출한 workflow로부터만 secrets를 받을 수 있다

만약 top-level workflow에서 `secrets: inherit`으로 모든 비밀 변수를 전달했으나 nested workflow 1에서 nested workflow 2에게 특정 secrets만 전달하면 nested workflow 2는 해당 secrets에만 접근할 수 있다

중첩된 workflow에서 top-level workflow(initial caller workflow)를 접근할 수 없다면 해당 워크플로우는 실패한다

또한 GITHUB_TOKEN의 권한은 같거나 더 제한될 수 있다

```yaml

on:
  workflow_call:

jobs:
  # reusable workflow에서 다른 reusable workflow 호출
  call-another-reusable:
    uses: ./.github/workflows/another-workflow.yml
    
    # 중첩된 reusable workflow에게 secrets를 전달한다
    secrets:
      repo-token: ${{ secrets.personal_access_token }}
```

### using outputs from a reusable workflow

on.workflow_call 레벨에서 출력할 값을 outputs 키워드로 정의한다

해당 키워드는 정의한 job 레벨의 출력 값을 기반으로 워크플로우의 출력값을 정의하며, job 레벨의 출력 값은 자신의 steps에서 출력한 값을 기반으로 정의할 수 있다 

```yaml
name: reusable workflow example

on:
  workflow_call:
    # job 레벨에서 정의한 출력값을 reusable workflow의 출력값으로 정의한다
    outputs:
      firstword:
        description: "the first output string"
        value: ${{ jobs.example-output-job.outputs.output1 }}
      secondword:
        description: "the second output string"
        value: ${{ jobs.example-output-job.outputs.output2 }}

jobs:
  example-output-job:
    name: generate output
    runs-on: ubuntu-latest

    # steps의 출력값을 job 레벨의 출력값로 정의한다
    outputs:
      output1: ${{ steps.step1.outputs.firstword }}
      output2: ${{ steps.step2.outputs.secondword }}
    steps:
      - id: step1
        run: echo "firstword=hello" >> $GITHUB_OUTPUT
      - id: step2
        run: echo "secondword=world" >> $GITHUB_OUTPUT
```

아래와 같이 동일한 워크플로우에서 reusable workflow의 출력값을 다른 job에서 사용할 수 있다

```yaml
jobs:
  call-workflow-passing-data:
    permissions:
      contents: read
    uses: ./.github/workflows/reusable-workflow.yml
    with:
      config-path: ./.github/labeler.yml
    secrets:
      token: ${{ secrets.GITHUB_TOKEN }}

  # reusable workflow에서 출력한 값을 같은 워크플로우의 job에서 사용할 수 있다
  print-outputs-from-a-job:
    runs-on: ubuntu-latest
    needs: call-workflow-passing-data
    steps:
      - run: echo ${{ needs.call-workflow-passing-data.outputs.firstword }} ${{ needs.call-workflow-passing-data.outputs.secondword }}
```


### limitations

reusable workflows를 최대 4번 중첩할 수 있고 최상단 워크플로우 파일에서 최대 20개의 reusable workflow 파일들을 포함할 수 있다 

각각의 caller workflow와 called workflow에서 정의한 env 컨텍스트는 서로에게 전파되지 않는다 -> 대신 reusable workflow의 출력을 사용할 수 있다

여러 워크플로우 파일에서 변수를 재사용하려면 organization, repository 또는 environment 레벨에서 정의하고 vars 컨텍스트를 통해 접근할 수 있다

reusable workflow은 job 레벨에서 environment 키워드를 사용할 수 있으나 on.workflow_call 레벨에선 사용할 수 없기 때문에 caller workflow로부터 enviroment secrets를 전달받을 수 없다

## composite actions

reusable workflows가 클래스라면, composite actions는 메서드라고 비유할 수 있다

composite actions는 여러 steps를 결합하여 하나의 action으로 정의하는 것을 말하여 어떤 워크플로우의 특정 job 내에서 하나의 step으로써 호출되어 정의한 step들을 수행한다

주로 하나 이상의 워크플로우에서 순차적인 step들이 중복적으로 수행할 필요가 있을 때 사용한다

방대한 yaml 워크플로우 파일을 잘게 나눠 리팩토링하여 각 워크플로우 파일의 중복된 코드를 줄일 수 있다 

### example for composite actions

아래와 같이 action.yml에 composite action을 정의한다 

```yaml
name: hello world
description: greet someone

# composite action의 입력값 정의
inputs:
  who-to-greet:
    description: who to greet
    required: true
    default: 'world'

# steps 출력값을 기반으로 composite action 출력값 정의
outputs:
  random-number:
    description: the random number
    value: ${{ stpes.random-number-generator.outputs.random-number }}

runs:
  using: composite
  steps:
    - name: set greeting
      run: echo "hello  $INPUT_WHO_TO_GREET"
      shell: bash
      env:
        INPUT_WHO_TO_GREET: ${{ inputs.who-to-greet }}

    - name: generate random number
      id: random-number-generator
      run: echo "random-number=$(echo $RANDOM)" >> $GITHUB_OUTPUT
      shell: bash

    # goodbye.sh 파일의 위치를 지정하기 전에 action의 경로를 runner 시스템 경로에 추가한다
    - name: set github path
      run: echo "$GITHUB_ACTION_PATH" >> $GITHUB_PATH
      shell: bash
      env:
        GITHUB_ACTION_PATH: ${{ github.action_path }}

    - name: run goodbye.sh
      run: goodbye.sh
      shell: bash
```

정의한 action을 다음과 같이 workflow에서 하나의 step으로 사용할 수 있다

```yaml
on: [push]

jobs:
  hello-world-job:
    runs-on: ubuntu-latest
    name: a job to say hello

    steps:
      - uses: actions/checkout@v4
      - id: foo
        uses: ./.github/action/action.yml
        with:
          who-to-greet: 'hansanhha'
      - run: echo random-number "$RANDOM_NUMBER"
        shell: bash
        env:
          RANDOM_NUMBER: ${{ steps.foo.outputs.random-number }}
```


## reusable workflows vs composite actions

[reference github docs](https://docs.github.com/en/actions/sharing-automations/avoiding-duplication)

### workflow jobs

composite actions는 caller workflow 내에서 단일 step으로써 동작하는 step 리스트를 구성한다

다만 reusable workflow처럼 파일 내에서 job을 포함할 수 없다

### logging

composite action이 실행되면 파일 내에 정의된 각각의 step들의 실행 로그 대신 caller workflow에서 하나의 step이 실행된 것으로만 로그를 출력한다

resuable workflow는 모든 job과 step이 각각 로그로 출력된다

### specifying runners

composite action과 달리 reusable workflow의 경우 job을 실행할 runner를 선택할 수 있다

### passing output to steps

composite action은 workflow job내의 실행되는 step으로 다른 step들과 함께 실행할 수 있다 (composite action 실행 이전/이후 모두)

따라서 composite action 내부에서 GITHUB_ENV 환경 변수를 설정(`"VAR=value" >> $GITHUB_ENV` 등)하면 이후 step에서도 이걸 사용할 수 있다

reusable workflow는 job 내에서 실행되는 step이 아니라 개별 job 수준에서 호출되며, 호출한 이후 같은 job 내에 새로운 step을 추가할 수 없기 때문에 reusable workflow 내부에서 설정한 환경변수는 후속 step에서 사용할 수 없다

#### composite actions의 환경변수 재사용

```yaml
jobs:
  example-job:
    runs-on: ubuntu-latest
    steps:
      - name: Checkout repository
        uses: actions/checkout@v4

      - name: Run Composite Action
        
        # my-org/my-composite-action@v1 내에서 환경변수 설정
        uses: my-org/my-composite-action@v1

        # composite action에서 설정한 환경변수 사용
      - name: Use output from Composite Action
        run: echo "The output is $MY_OUTPUT"
```

#### reusable workflow의 환경변수 재사용 (불가능)

```yaml
jobs:
  call-reusable-workflow:
    uses: my-org/my-reusable-workflow/.github/workflows/reusable.yml@v1

  another-job:
    runs-on: ubuntu-latest
    needs: call-reusable-workflow
    steps:
      - name: Try to use output

        # reusable workflow 호출 이후 step을 추가할 수 없기 때문에 내부적으로 정의한 환경변수를 사용할 수 없다
        run: echo "The output is $MY_OUTPUT"
```

### reusable workflows와 composite actions 주요 차이점

| reusable workflows                                       | composite actions                        |
|----------------------------------------------------------|------------------------------------------|
| standard workflow 파일과 유사한 yaml file                      | workflow steps를 포함한 action               |
| 리포지토리의 `.github/workflows` 디렉토리에 위치한 단일 파일(하위 디렉토리 허용 X) | `action.yml` 파일을 포함한 분리된 리포지토리 또는 디렉토리   |
| 특정 yaml 파일을 참조하여 호출됨                                     | action이 정의된 특정 리포지토리 또는 디렉토리를 참조하여 호출됨   |
| job 수준에서 호출됨                                             | job의 step 수준에서 호출됨                       |
| 파일 내에 여러 job을 포함할 수 있음                                   | job을 포함할 수 없음                            |
| 각 step의 실행이 모두 로그에 출력됨                                   | 하나의 step 실행으로만 로그에 출력됨                   |
| 최대 4단계 중첩된 워크플로우 호출                                      | 하나의 워크플로우에 최대 10개의 composite action 중첩 가능 |
| secrets 사용 가능                                            | secrets 사용 불가능                           |







