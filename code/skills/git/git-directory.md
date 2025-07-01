---
layout: default
title: 
---

#### index
- [.git directory](#git-directory)
- [.git directory structure](#git-directory-structure)
- [workflow](#workflow)
- [HEAD](#head)
- [FETCH_HEAD](#fetch_head)
- [ORIG_HEAD](#orig_head)
- [config](#config)
- [index](#index-1)
- [description](#description)
- [packed-refs](#packed-refs)
- [refs](#refs)
- [objects](#objects)
- [logs](#logs)
- [info](#info)


## .git directory

`.git` 디렉토리는 git 로컬 저장소의 핵심 데이터베이스로, 프로젝트 루트 디렉토리에 위치하며 git이 버전 관리에 필요한 모든 정보(커밋, 브랜치, 파일 상태, 설정 등)를 저장한다

`git init` 명령어를 실행하여 만들 수 있다

주요 역할
- 코드 변경 이력(히스토리) 저장
- 브랜치, 태그, HEAD 같은 참조(포인터) 관리
- 스테이징 상태와 설정 저장


## .git directory structure

```plaintext
.git/
 └── HEAD
 └── index
 └── FETCH_HEAD
 └── ORIG_HEAD
 └── index
 └── config
 └── objects/
 └── refs/
```

HEAD: 현재 작업 위치(커밋/브랜치) 파일

index: 스테이징 영역 정보 파일

config: git 설정 파일

FETCH_HEAD: `git fetch` 명령 시 원격 리포지토리의 브랜치 정보 임시 저장 파일 (영구 저장 X)

ORIG_HEAD: 특정 작업 직전의 HEAD 위치를 기록하는 파일

objects: 커밋, 파일 내용 등의 객체 저장소 디렉토리

refs: 브랜치, 태그 등의 참조 디렉토리


## workflow

`refs`: 브랜치와 태그로 커밋 참조 -> HEAD와 연결

`objects`: 실제 데이터(Blog, Tree, Commit) 저장 -> 스냅샷 구현

`info`: 로컬 환경 설정

`logs`: 작업 기록 -> 복구 및 추적

예시 워크플로우
- `git commit` -> `objects`에 새 git object 생성 -> `refs/heads/main` 업데이트 -> `logs/HEAD`에 기록
- `git fetch` -> `refs/remotes/` 갱신 -> `FETCH_HEAD` 작성


## HEAD

용도: 워킹 디렉토리와 스테이징 영역을 HEAD가 가리키는 스냅샷에 맞춰 조정한다

`HEAD` 파일은 현재 작업 중인 브랜치 또는 커밋을 가리키는 포인터이다

git의 현재 상태를 나타내며 브랜치 참조 시 `ref: refs/heads/main`으로 표시되고 Detached HEAD 상태인 경우 커밋 해시를 포함한다

`git checkout`으로 브랜치 전환 시 업데이트되며 새 커밋을 만들면 HEAD가 가리키는 브랜치도 함께 이동한다


## FETCH_HEAD

용도: `git merge FETCH_HEAD`로 가져온 커밋을 병합할 때 사용한다

`FETCH_HEAD` 파일은 `git fetch` 또는 `git pull` 명령어를 실행할 때 원격 리포지토리에서 가져온 브랜치 정보를 임시로 기록한다

원격 브랜치 이름과 해당 커밋 해시를 포함하며, 여러 브랜치를 동시에 가져오면 각 줄에 기록된다

영구 저장이 아니라 매번 fetch할 때 덮어씌워진다


## ORIG_HEAD

용도: 실수로 변경을 잃어버렸을 때 복구 (`git reset --hard ORIG_HEAD`)

`ORIG_HEAD` 파일은 특정 작업(merge, rebase, reset) 직전의 HEAD 위치를 기록한다 (이전 HEAD가 가리켰던 커밋 해시)

`FETCH_HEAD`와 마찬가지로 임시적으로 기록하며 다음 작업 시 ORIG_HEAD 값이 덮어씌워진다

위험한 작업 후 되돌릴 수 있게 백업 역할을 한다

`git reset --hard` 실행 시 이전 HEAD가 ORIG_HEAD에 저장된다


## config

용도: 저장소별 설정 관리 및 전역 설정

`config` 파일은 ini 형식으로 작성되어 로컬 저장소의 설정 정보를 저장하여 git 동작 방식을 커스터마이징할 수 있다

로컬 리포지토리 별 설정은 `root_project/.git/config`에 설정한다

```plaintext
[core]
        repositoryformatversion = 0
        filemode = true
        bare = false
        logallrefupdates = true
        ignorecase = true
        precomposeunicode = true
[branch "asdf"]
        remote = origin
        merge = refs/heads/asdf
[remote "origin"]
        url = git@github.com:hansanhha/hansanhha.github.io
        fetch = +refs/heads/*:refs/remotes/origin/*
```

전역 설정은 `~/.gitconfig`에 저장한다

`git config --global`로 설정한 값이 `~/.gitconfig`에 저장된다

```plaintext
# This is Git's per-user configuration file.
[user]
# Please adapt and uncomment the following lines:
name = hansanhha
email = xxxx@xxxx
[core]
        autocrlf = input
```


## index

용도: 워킹 디렉토리와 커밋 사이의 중간 상태 관리

`index` 파일은 스테이징 영역의 현재 상태를 저장하는 바이너리 파일이다

커밋할 준비가 된 파일을 목록을 관리하기 위해 존재하며 파일 이름, 해시 값, 타임스탬프, 상태 값 등이 포함된다

사용자가 `git add`로 파일을 스테이징하면 git 내부적으로 index가 업데이트되며, `git commit`시 index 내용이 커밋으로 리포지토리에 기록된다

`git ls-files --stage`로 내용을 확인할 수 있다


## packed-refs

용도: 대규모 리포지토리의 참조 관리 효율성 최적화

`packed-refs` 파일은 git이 참조(브랜치, 태그 등)를 효율적으로 저장하기 위해 압축한 파일이다

개별 참조 파일(`refs/heads/main` 등)이 많아지면 `git pack-refs`로 통합할 수 있다

```shell
cat .git/packed-refs
# pack-refs with: peeled fully-peeled sorted
39aabb75c7aa0a5c91819e95136ea28d02c4c7e7 refs/heads/asdf
164ee1231abee6c8aa1848c515b9618549084d54 refs/heads/main
```


## description

`description` 파일에 리포지토리에 대한 설명을 적을 수 있다

주로 git 호스팅 서비스에서 표시용으로 사용되었으나 요즘은 대부분 REAMDE.md 파일을 사용한다

기본값

```plaintext
Unnamed repository; edit this file 'description' to name the repository.
```


## refs

용도: HEAD가 간접적으로 이 디렉토리에 저장된 참조들을 통해 커밋을 가리킨다

`refs` 디렉토리는 git의 참조(reference)를 저장하는 디렉토리이다 (텍스트 파일로 관리되어 직접 편집할 수 있으나 권장되지 않음)

참조는 브랜치, 태그, 원격 리포지토리 등을 특정 커밋에 연결해주는 포인터를 말한다

참조가 많아지면 성능 최적화를 위해 `.git/packed-refs`로 압축될 수 있다


```plaintext
.git/refs
└── heads
└── remotes
└── tasgs
```

### `refs/heads/`

로컬 브랜치의 참조를 저장하는 디렉토리이다

각 파일은 브랜치 이름에 해당하며, 그 브랜치의 최신 커밋 해시를 포함한다

```shell
$ cat .git/refs/heads/asdf
da0f734f357cb64782d3afbbf2c74cf5d7a45513
```

`git branch`나 `git switch` `git checkout`으로 브랜치 작업 시 업데이트된다

### `refs/tags`

태그를 저장하며 주로 릴리즈 버전(v1.0 등)을 표시한다

태그는 보통 고정된 커밋을 가리키며 lightweight 태그(단순 해시)와 annotated 태그(메시지 포함)가 있다

`.git/refs/tags/v1.0` -> `a1b2c3d4...` 특정 커밋에 태그를 연결하는 방식


### `refs/remotes/`

원격 리포지토리의 브랜치를 트래킹하는 디렉토리로 로컬과 원격 브랜치 동기화 상태를 나타낸다

`git fetch` 또는 `git pull` 시 원격 브랜치 정보가 이 디렉토리에 저장된다

`./git/refs/remotes/origin/asdf` -> `e5f6g7h8...` (원격 origin의 adsf 브랜치)

```shell
$ cat .git/refs/remotes/origin/asdf
da0f734f357cb64782d3afbbf2c74cf5d7a45513
```


## objects

용도: 커밋 히스토리와 파일 내용 복원에 사용된다

```plaintext
.git/objects
└── [0-9a-f][0-9a-f]/
└── pack/
```

`objects` 디렉토리는 git의 모든 데이터(파일 내용, 디렉토리 구조, 커밋 정보)를 저장하는 객체(Blob, Tree, Commit, Tag) 데이터베이스로 git의 스냅샷과 델타 기반 저장소의 핵심이다

객체는 SHA-1 해시로 식별되며 해시의 앞 2자리로 폴더 이름, 나머지로 파일 이름을 생성한다

해시 `a1b2c3d4e5f6...` -> `./git/objects/a1/b2c3d4e5f6...`

초기에는 개별 파일로 저장되지만 `git gc`로 Packfile로 압축되어 pack 하위 디렉토리에 저장된다 

pack 디렉토리는 Pakcfile(압축된 객체 모음)과 인덱스 파일을 저장한다

```shell
$ cat .git/objects/pack/
pack-4f5b792256fffa863ed6f8d1bac07a2d08d82793.idx
pack-4f5b792256fffa863ed6f8d1bac07a2d08d82793.pack
```


## logs

용도: 실수로 삭제한 커밋 복구, 작업 히스토리 디버깅

`log` 디렉토리는 git 작업(커밋, 브랜치 이동 등)의 로그 기록을 저장하여 히스토리 추적과 복구 작업에 쓰인다

```plaintext
.git/logs
├── HEAD
└── refs
    ├── heads
    │   ├── asdf
    │   └── main
    └── remotes
        └── origin
            └── asdf
```

### `logs/HEAD`

HEAD의 이동 기록을 시간 순으로 저장하는 파일이다

이전 커밋 해시, 새 커밋 해시, 작업 시간, 메시지 형식으로 표현한다

```shell
$ cat .git/logs/HEAD

# 이전 내용 생략
51ddbdfaf704da7b6feb88e83692659cdd52b35d da0f734f357cb64782d3afbbf2c74cf5d7a45513 hansanhha <xxxxx@xxxxx> 1742912535 +0900  commit: D+100: add 0325 task
```

### `logs/refs`

브랜치의 변경 로그를 담는 디렉토리로 하위 디렉토리 `logs/refs/heads` `logs/refs/remotes` `logs/refs/tags`로 구성된다

`logs/refs/heads`: 로컬 브랜치의 변경 로그

`logs/refs/remotes`: 리모트 브랜치의 변경 로그

`.git/logs/refs/heads/main` 파일은 로컬 `main` 브랜치의 커밋과 이동을 기록한다

`.git/logs/refs/remotes/origin/main` 파일은 리모트 `main` 브랜치의 커밋과 기타 정보를 기록한다


## info

용도: `.gitigonore`와 별도로 로컬 리포지토리의 무시 패턴 관리

`info` 디렉토리는 리포지토리의 추가적인 메타데이터나 설정을 저장한다

```plaintext
.git/info/
└── exclude
```

### `info/exclude/`

로컬 리포지토리에만 적용되는 `.gitignore` 같은 파일이다

`.gitignore` 파일은 커밋되지만 `exclude` 파일은 로컬 전용으로 사용된다