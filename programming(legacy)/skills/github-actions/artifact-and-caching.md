---
layout: default
title:
---

artifact
- [artifact](#artifact)
- [artifact 보관 정책](#artifact-보관-정책)
- [example](#artifact-action-example)

caching
- [caching](#caching)
- [cache action](#cache-action)
- [managing caches](#managing-caches)

[comparing artifacts and dependency caching](#comparing-artifacts-and-dependency-caching)

## artifact

workflow에 속한 job들은 각각 분리된 runner에서 실행되므로 기본적으로 데이터를 공유할 수 없다

artifact(파일 또는 컬렉션 파일 - zip)는 job이 완료된 후 데이터를 영속할 수 있는 기능과 동일한 workflow에서 다른 job에게 데이터를 공유할 수 있는 기능을 제공한다 (모든 actions와 workflows는 해당 실행하는 아티팩트에 대한 쓰기 권한을 가진다)

또한 workflowr가 끝난 후 아티팩트는 github 서버에 저장되어 다운로드할 수 있다

#### 주요 사용 예시

로그 파일/코어 덤프

테스트 결과, 실패/스크린샷

바이너리 또는 복합 파일

스트레스 테스트 성능 출력/코드 커버리지 결과

### upload-artifact, download-artifact

github는 아티팩트를 업로드/다운로드할 수 있는 두 개의 actions를 제공한다

#### upload-artifact

job이 끝나기 전 파일 이름과 데이터를 기반으로 파일을 업로드하는 액션

retention-day를 통해 아티팩트 유지 기간을 설정할 수 있다 (최대 유지 기간을 넘어서 설정할 수 없다)

job에서 업로드한 파일은 github actions 탭의 해당 workflow에서 확인할 수 있다   

```yaml
# 첫 번째 아티팩트 업로드
- name: Archive production artifacts
    uses: actions/upload-artifact@v4
    with:
    name: dist-without-markdown
    path: |
        dist
        !dist/**/*.md
    retention-days: 5   # 아티팩트 유지 기간 설정
      
# 두 번째 아티팩트 업로드
- name: Archive code coverage results
    uses: actions/upload-artifact@v4
    with:
    name: code-coverage-report
    path: output/test/code-coverage.html
```

#### download-artifact

동일한 workflow 실행 중 업로드된 아티팩트를 다운받는 액션, 파일 이름으로 아티팩트를 참조할 수 있다

파일 이름을 명시하지 않으면 이전 job에서 업로드한 모든 아티팩트를 다운로드한다

```yaml
# 특정 아티팩트 다운로드
- name: Download a single artifact
  uses: actions/download-artifact@v4
  with:
    name: my-artifact
```

```yaml
# 모든 아티팩트 다운로드
- name: Download all workflow run artifacts
  uses: actions/download-artifact@v4
```


## artifact 보관 정책

기본적으로 아티팩트는 github 서버에 90일 동안 보관된다


## artifact action example

```yaml
name: share data between jobs

on: [push]

jobs:

  # math-homework.txt 파일을 homework_pre 이름으로 업로드
  job_1:
    name: add 3 and 7
    runs-on: ubuntu-latest
    steps:
      - shell: bash
        run: |
          expr 3 + 7 > math-homework.txt
      - name: upload math result for job 1
        uses: actions/upload-artifact@v4
        with:
          name: homework_pre
          path: math-homework.txt

  # homework_pre를 다운받고 값을 수정한 math-homework.txt 파일을 homework_final 이름으로 업로드
  job_2:
    name: multiply by 9
    needs: job_1    # 작업 순서 지정: job 1 -> job 2
    runs-on: windows-latest
    steps:
      - name: download math result job 1
        uses: actions/download-artifact@v4
        with:
          name: homework_pre
      - shell: bash
        run: |
          value=`cat math-homework.txt`
          expr $value \* 9 > math-homework.txt
      - name: upload math result for job 2
        uses: actions/upload-artifact@v4
        with:
          name: homework_final
          path: math-homework.txt

  # homework_final을 다운받고 출력
  job_3:
    name: display results
    needs: job_2    # 작업 순서 지정: job 2 -> job 3
    runs-on: macos-latest
    steps:
      - name: download math results for job 2
        uses: actions/download-artifact@v4
        with:
          name: homework_final
      - name: print the final result
        shell: bash
        run: |
          value=`cat math-homework.txt`
          echo the result is $value
```



## caching

github actions에서 자주 사용하는 파일(의존성, 특정 출력)을 저장하여 workflow 속도를 향상시키는 github action이다

기본적으로 workflow의 각 job은 github-hosted runner에서 실행되며 각 runner는 새 프로비저닝된 상태에서 시작하기 때문에 매번 의존성을 다운로드받는다

cache action을 사용하면 package manager (maven/gradle, poetry, npm 등)을 캐시하여 job에서 사용하는 의존성을 캐시하여 효율적으로 workflow를 실행할 수 있다

#### 주의점

read 권한이 있는 계정은 리포지토리 풀 리퀘스트를 생성하고 캐시 컨텐츠에 접근할 수 있다

마찬가지로 fork한 리포지토리에서 base 브랜치에 풀 리퀘스트를 생성하고 캐시 컨텐츠에 접근할 수 있기 때문에 cache에 인증 토큰, 로그인 crendentials 같은 민감한 정보를 넣지 말아야 한다

또한 self-hosted runner를 사용해도 workflow 실행의 캐시는 github 자체 서버에 저장되며 github enterprise server에서만 자체 서버에 저장할 수 있다


## cache action

cache action은 설정한 key를 기반으로 캐시를 찾으려고 시도한다

완벽히 매치되는 키가 있는 경우
- cache hit
- cache action은 설정된 path에 캐시된 파일을 찾는다

완벽히 매치되는 키가 하나도 없는 경우
- cache miss
- restore-keys가 설정된 경우 해당 키들로 캐시를 찾으려고 시도한다
- cache action은 job이 성공적으로 실행되면 자동적으로 새 캐시를 생성하고 설정된 path 저장한다

### cache action 입력 파라미터

#### 필수 입력 파라미터

key
- 캐시를 저장하고 되찾을 때 사용되는 키 값
- 변수, context values, static strings, functions 등을 조합하여 키를 만들 수 있다
- 키는 최대 512 character 길이를 가질 수 있으며 이보다 긴 키를 가진 경우 action이 실패한다

path
- runner에서 캐시를 저장하고 되찾을 경로
- 하나 이상의 경로를 지정할 수 있다
- 디렉토리 또는 단일 파일을 지정할 수 있으며 glob 패턴을 사용할 수도 있다
- 절대 경로와 상대 경로(workspace 디렉토리 기준) 모두 지원한다

```yaml
- name: Cache Gradle Packages
  uses: actions/cache@v4
  with:
    path: |
      ~/.gradle/caches
      ~/.gradle/wrapper
```

#### 선택 입력 파라미터

restore-keys
- cache-hit이 되지 않은 경우 캐시를 찾을 후보 키 값들
- 각 라인에 설정된 값이 하나의 restore-key이며 순차적으로 사용되서 캐시를 찾으려고 시도한다
- 정확히 매치되는 캐시가 있는 경우 path 디렉토리에 저장된 캐시를 되찾는다
- 정확히 매치되는 캐시가 없는 경우 restore key와 부분적으로 매치되는 캐시가 있는지 찾는다 -> 만약 부분 매치되는 캐시가 있다면 그 중 가장 최신 캐시를 가져온다

```yaml
restore-keys: |
  npm-feature-${{ hashFiles('package-lock.json') }}
  npm-feature-
  npm-
```

enableCrossOsArchive
- boolean 값(default: false)
- 활성화하면 windows runner가 운영체제에 상관없이 생성된 캐시를 저장하고 찾는다

### cache action 출력 파라미터

cache-hit: key 값에 완전히 매치되는 캐시를 찾은 경우 true


## managing caches

[참고](https://docs.github.com/en/actions/writing-workflows/choosing-what-your-workflow-does/caching-dependencies-to-speed-up-workflows#managing-caches)


## comparing artifacts and dependency caching

caching: workflow 또는 job 실행 간 변경되지 않는 파일을 재사용하는 경우 사용

artifacts: workflow 또는 job 실행 후 제공된 파일을 저장하고 싶은 경우 사용





