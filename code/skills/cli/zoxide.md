---
layout: default
title: 근두운타고 디렉토리를 이동해보자 (v0.9.7)
---
- [zoxide](#zoxide)
- [how it works](#how-it-works)
- [configuration](#configuration)
- [usage](#usage)


## zoxide

우리는 일반적으로 터미널 환경에서 디렉토리 경로를 이동하기 위해 `cd` 쉘 내장 명령어를 사용한다

간단하게 이동하는 경우엔 사용하기 편하지만 (심지어 zsh는 명시적으로 사용하지 않아도 됨) 여러 계층의 디렉토리를 이동하려면 그 경로를 모두 작성해야하는 번거로움이 생긴다

zoxide는 "smarter cd command" 철학을 기반으로 사용자의 디렉토리 탐색 습관을 분석하고 예측해서 더 효율적인 이동을 가능하게 한다 

zoxide를 사용하면 디렉토리의 모든 경로를 전부 입력할 필요없이, 일부만 입력해도 매칭되는 곳으로 이동할 수 있다

```shell
# cd 명령을 사용하면 모든 디렉토리의 경로를 입력해야 한다
$ cd ~/project/my_project/src/account

# zoxide를 사용하면 세부 경로를 명시하지 않고도 해당 디렉토리로 이동할 수 있다
$ z account
```

디렉토리 방문의 빈도와 최근 방문 지표를 모두 결합하여 균형있게 우선순위(frecency = frequency + recency)를 매기고 사용자에게 더 높은 우선순위를 가지는 디렉토리를 추천한다

사용자가 디렉토리를 이동할 때마다 데이터를 축적해서 점점 더 정확한 예측을 제공한다 (시간이 지날수록 사용자에 맞춰가진다)

rust로 작성돼서 빠르고 메모리 효율이 높으며 다양한 쉘과 플랫폼에서 동작한다

또한 서드파티 도구(fzf 등)와 통합을 적극적으로 지원하여 그 효과를 극대화할 수 있다


## how it works

### 1. database managing

zoxide는 사용자의 방문 기록을 유지하는 데이터베이스를 관리하여 이를 통해 전체 경로를 명시하지 않고 간단하게 디렉토리 이동을 할 수 있게 한다

사용자가 방문한 디렉토리를 기록하며 삭제한 디렉토리나 더 이상 존재하지 않는 경로는 주기적으로 정리한다

데이터베이스 경로(맥): `~/Library/Application Support/zoxide/db.zo`

데이터베이스에는 각 디렉토리의 경로와 함께 frecency 점수를 기록한다

frecency 점수 요소
- 방문 횟수(frequency): 특정 디렉토리의 방문 빈도
- 마지막 방문 시간(recency): 최근에 방문했는지, 방문한지 얼마나 오래됐는지 파악한다
- 가중치: 시간이 지나면 오래된 기록의 점수가 감소하도록 조정한다

### 2. input parsing/matching

사용자가 `z foo`처럼 명령어를 입력하면 zoxide는 아래의 과정을 거쳐서 디렉토리로 이동한다

- 입력된 문자열("foo")을 데이터베이스의 디렉토리 이름들과 비교한다
- 부분 문자열 매칭을 사용해서 "foo"가 포함된 모든 디렉토리를 후보로 추려낸다
- 각 후보의 frecency 점수를 확인해서 가장 높은 점수의 디렉토리를 선택한다

### 3. shell configuraiton

zoxide는 셸에 초기화 스크립트를 추가해서 동작한다

```shell
$ eval "$(zoxide init zsh)"
```

해당 스크립트는 실행하면 사용자가 `z` 명령어를 통해 디렉토리를 이동할 수 있다

이 과정에서 입력(디렉토리 문자열)을 zoxide 바이너리로 전달하면 zoxide의 데이터베이스를 업데이트하며 실제 쉘의 경로를 이동시킨다


## configuration

```shell
# zoxide 설치
$ brew install zoxide

# fzf 설치(optional)
$ brew install fzf

# zsh 쉘 설정
$ eval "$(zoxide init zsh)"
```


## usage

[github 예시](https://github.com/ajeetdsouza/zoxide?tab=readme-ov-file#getting-started)

```shell
z foo              # cd into highest ranked directory matching foo
z foo bar          # cd into highest ranked directory matching foo and bar
z foo /            # cd into a subdirectory starting with foo

z ~/foo            # z also works like a regular cd command
z foo/             # cd into relative path
z ..               # cd one level up
z -                # cd into previous directory

zi foo             # cd with interactive selection (using fzf)

z foo<SPACE><TAB>  # show interactive completions (zoxide v0.8.0+, bash 4.4+/fish/zsh only)
```