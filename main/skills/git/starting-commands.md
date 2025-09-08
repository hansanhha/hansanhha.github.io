---
layout: default
title:
---

#### index
- [git init](#git-init)
- [git clone](#git-clone)
- [partial-clone](#partial-clone)
- [shallow-clone](#shallow-clone)


## git init

`git init` 명령은 현재 디렉토리에 git 리포지토리를 초기화하는 명령어이다

빈 프로젝트를 git으로 관리하기 시작하거나 이미 파일이 있는 디렉토리를 git 리포지토리로 전환할 때 주로 사용한다

`.git` 디렉토리를 생성하여 git의 버전 관리 기능을 활성화하고 파일의 변경 추적 및 커밋 기록이 가능해진다

`git init`: 현재 디렉토리에 `.git` 생성 및 초기화한다


`git init <directory>`: 지정한 디렉토리에 `.git` 생성 후 초기화한다

`git init --bare`: 워킹 디렉토리 없이`.git` 디렉토리의 구성 요소만 생성한다 (주로 원격 저장소처럼 사용함)

`git init --initial-branch=<branch-name>`: git 초기화와 함께 기본 브랜치 이름을 설정한다


## git clone

`git clone`은 원격 리포지토리를 로컬로 복제해 작업 가능한 git 리포지토리를 만드는 명령어이다

가져오는 요소
- 작업 디렉토리: 최신 커밋의 파일 상태
- `.git` 디렉토리: 전체 커밋 히스토리, 브랜치 태그 등의 메타데이터

#### how clone works

- 원격 연결: SSH 또는 HTTP로 지정된 URL과 원격 리포지토리에 접속한다 (접근 가능 여부 검증)
- 데이터 수신: 객체(`objects/`), 참조(`refs/`), 설정 등을 다운로드한다 (Packfile)
- 로컬 리포지토리 생성: `.git` 디렉토리를 생성하고 설정을 복사한다
- 체크아웃: 기본 브랜치(HEAD)로 작업 디렉토리를 구성한다
- 원격 설정: `origin`이라는 이름으로 원격 리포지토리와의 연결을 설정한다 (`.git/config`에 origin 설정)

`git clone` 명령이 정상적으로 수행되면 로컬에 완전한 git 리포지토리가 생성되고 기본 브랜치가 체크아웃된 상태로 시작한다

### git clone commands

`git clone <repository-url>`: 원격 리포지토리를 현재 디렉토리 경로에 복제한다 (리포지토리 이름으로 디렉토리 생성)

`git clone <repository-url> <directory>`: 지정된 디렉토리 경로에 원격 리포지토리를 복제한다

`git clone --branch <branch-name> <repository-url>`: 원격 리포지토리의 특정 브랜치를 현재 디렉토리 경로에 복제한다

`git clone --single-branch <repository-url>`: 리포지토리를 복제할 때 단일 브랜치만 가져온다

`git clone --depth <n> <repository-url>`: 얕은 복제(shallow clone), 전체 커밋 히스토리를 가져오지 않고 최근 n개의 커밋만 가져온다

`git clone --recurse-submodules <repository-url>`: 원격 리포지토리를 복제하면서 서브 모듈도 함께 초기화 및 업데이트한다


### tip

대규모 리포지토리를 복제할 때`--depth`와 `single-branch`로 최소 복제하여 다운로드 속도를 개선할 수 있다

```shell
# SSH 연결 테스트
$ ssh -T git@github.com
```

```shell
# HTTPS GitHub PAT(Personal Access Token) 사용
$ git clone https://<username>:<token>@github.com/user/repo.git
```

## partial clone

## shallow clone