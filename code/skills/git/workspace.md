---
layout: default
title:
---

#### index
- [git add](#git-add)
- [git rm](#git-rm)
- [git mv](#git-mv)
- [git restore](#git-restore)
- [git reset](#git-reset)
- [git stash](#git-stash)
- [git clean](#git-clean)
- [git bisect](#git-bisect)


## git add

`git add` 명령은 워킹 디렉토리의 변경 사항을 스테이징 영역에 추가한다

`git add <file>`: 특정 파일 스테이징

`git add .`: 현재 디렉토리 경로 하위에 있는 모든 변경 사항 스테이징

`git add -u`: 추적된 파일만 스테이징

`git add -A`: 추적/비추적을 포함한 모든 변경 사항 스테이징


## git rm

`git rm`은 git에서 추적 중인 파일을 삭제하고 그 삭제 이력을 스테이징 영역에 추가해 다음 커밋에서 반영되도록 준비한다

주요 작업
- 파일 삭제: 워킹 디렉토리와 리포지토리에서 파일 제거
- 삭제 이력 기록: 삭제된 이력을 스테이징에 올려 커밋 준비
- 추적 해제: 특정 옵션을 사용하면 워킹 디렉토리에 파일을 유지하고 파일 추적만 중단

### git rm commands

`git rm <file>`: 파일을 워킹 디렉토리에서 삭제하고 스테이징 영역에 삭제 이력을 추가한다

`git rm -f <flie>`: 수정된 파일 강제 삭제 및 스테이징

`git rm -r <directory>`: 디렉토리와 하위 파일 모두 삭제 및 스테이징

`git rm --cached <file>`: 워킹 디렉토리의 파일은 유지하면서 스테이징과 리포지토리에서만 제거한다 (git 추적 중단, 단 이전 커밋 이력에 해당 파일은 남아있음)

`git rm --dry-run <file>`: 실제 삭제 없이 시뮬레이션만 한다

### `rm <file>`

`git rm` 명령어 대신 운영체제 명령어나 gui로 워킹 디렉토리의 파일을 제거하면 해당 파일의 상태에 따라 다음과 같이 동작한다

스테이징 영역에 추가되어 있던 파일 삭제: 파일이 스테이징에 여전히 추가되어있으면서, 동시에 파일 삭제를 감지한다

HEAD에 있던 파일 삭제: 파일 삭제를 감지한다

git은 파일 삭제를 감지하지만 자동으로 삭제 이력을 스테이징 영역에 반영하지 않는다 ( 스테이징 영역 추가 부분만 제외하면 `git rm`과 유사함)


## git mv

`git mv` 명령은 git에서 추적 중인 파일이나 디렉토리의 경로 또는 이름을 변경하고 변경 이력을 스테이징 영역에 자동으로 추가한다 (추적되지 않은 파일은 처리 불가)

### git mv commands

`git mv <source> <target>`: 파일/디렉토리 이름 변경 또는 경로를 이동하고 그 이력을 스테이징에 반영한다

`git mv -f <source> <target>`: 대상 위치에 동일한 이름의 파일이 있으면 강제로 덮어쓰며 명령 수행

`git mv -v <source> <target>`: 이름 변경 또는 경로 변경에 대한 상세 내용을 출력하며 명령 수행

### how git mv works

`git mv`는 내부적으로 `mv` + `git add/rm` 동작을 수행한다

`git mv main.py new-main.py` (main.py 파일을 new-main.py 라는 이름으로 변경)
- mv main.py new-main.py (파일명 변경)
- `git rm --cached main.py` (원래 파일 추적 중단)
- `git add new-main.py` (새 파일 추가)

운영체제 명령어를 통해 파일명을 변경한 후 기존 파일에 대한 추적을 중단하고, 새 파일을 스테이징한다

git은 파일 내용의 해시를 기반으로 이동/변경을 인식하므로 파일 내용이 동일하면 히스토리 연결을 유지할 수 있다

`git status`로 확인해보면 이름 변경 이력이 스테이징 영역에 추가된 것을 볼 수 있다

```shell
$ git status
    renamed:    main.py -> new-main.py
```

다만 이 상태에서 `git restore --staged new-main.py` 명령으로 이름 변경을 취소해도 원래 상태로 복구되지 않는다

```shell
# 기존 이름을 가진 파일은 삭제 상태로 스테이징 영역에 추가되어 있고, 새로운 파일은 untracked가 된다
$ git restore --staged new-main.py
$ git status
Changes to be committed:
    deleted:    main.py

Untracked files:
    new-main.py
```

`git restore --staged main.py` 및 `git restore main.py`로 삭제 이력까지 취소하면 main.py와 그 복사본인 new-main.py가 생성된다

```shell
$ git restore --staged main.py
$ git restore main.py
$ ls 
main.py     new-main.py
```


## git restore

`git restore`도 `git switch` 명령과 마찬가지로 2.23 버전에 추가되어 파일 복원 기능에 특화된 기능을 제공하여 기존 `git checkout` 명령어의 모호함을 해결한다

워킹 디렉토리나 스테이징 영역의 파일 상태를 특정 시점(커밋, 브랜치) 등으로 복원하는 역할을 한다 (`git checkout` 파일 복원 기능 대체)

브랜치 전환 기능이 없다 (`git switch`로 분리)

커밋된 변경은 수정할 수 없다 (대신 `git reset` 사용) 

### git restore commands

`git restore <file>` 또는 `git restore -- <file>`: 워킹 디렉토리의 파일을 HEAD 상태로 복원한다 (변경사항 폐기)

`git restore --staged <file>`: 스테이징 영역에서 파일 제거한다 (워킹 디렉토리는 유지 -> 변경 사항 유지) (파일 이름 변경 스테이징 이력을 취소하고 싶은 경우 변경된 파일 이름을 기준으로 함)

`git restore --staged --worktree <file>`: 스테이징 영역과 워킹 디렉토리의 파일을 모두 HEAD로 복원한다

`git restore --source=<commit-or-branch> <file>`: 지정된 커밋/브랜치에서 파일을 워킹 디렉토리로 복원한다

### git restore components

HEAD: 현재 작업 중인 브랜치나 커밋 포인터

워킹 디렉토리: 현재 수정 중인 파일 상태

스테이징 영역(인덱스): 다음 커밋에 포함될 변경 사항

### how git restore works

워킹 디렉토리에서 사용하는 경우: HEAD에서 변경하기 전의 원래 파일 내용을 가져와서 워킹 디렉토리에 적용시키며, modified 상태의 변경 사항을 폐기한다

스테이징 영역에서 사용하는 경우: 스테이징 영역에서 파일을 제거하고 modified 상태로 되돌린다
  
특정 파일을 복원하는 경우: 지정된 커밋/브랜치에서 파일을 가져와 워킹 디렉토리나 스테이징 영역에 적용시킨다


## git reset

`git reset` 명령은 현재 브랜치의 HEAD를 특정 커밋으로 이동시켜 스테이징 영역과 워킹 디렉토리를 조정한다

용도
- 커밋 되돌리기: 잘못된 커밋 삭제 또는 히스토리 수정
- 스테이징 취소: `git add`로 추가된 변경 제거
- 작업 초기화: 수정된 파일을 원래 상태로 복원
- 워킹 디렉토리 및 스테이징의 상태 변경: 특정 시점(커밋, 브랜치)의 상태로 워킹 디렉토리와 스테이징을 변경

동작 과정
- HEAD 이동: 브랜치의 포인터를 지정된 커밋으로 이동
- 상태 조정: 모드에 따라 스테이징과 워킹 디렉토리 업데이트
- 히스토리 변경: 이동 후 커밋은 `git reflog`에 남기 때문에 복구 가능

### git reset mode

reset 명령은 크게 세 가지 동작 모드를 지원하며 각 모드에 따라 HEAD, 스테이징 영역, 워킹 디렉토리에 미치는 영향이 달라진다

동작 모드 
- `--soft`: HEAD만 이동(스테이징과 워킹 디렉토리 유지) 
- `--mixed`(defalut): HEAD와 스테이징 이동(워킹 디렉토리 유지)
- `--hard`: HEAD, 스테이징, 워킹 디렉토리 이동(모든 변경 사항 폐기)


### soft reset 

```shell
$ git reset --soft <commit>
```

소프트 리셋은 **HEAD를 지정한 커밋으로 이동**시키기만 한다

스테이징 영역과 워킹 디렉토리는 유지되며, 되돌린 커밋의 변경 사항이 스테이징 상태가 된다

즉 현재 커밋을 취소하고 변경 사항을 모두 유지한 채로 특정 커밋으로 이동한다

각 커밋과 그에 따른 README.md 파일의 내용이 아래와 같다고 가정해보자

```shell
$ git log --oneline
* 05132ef (HEAD -> main) third commit
* d9d42fb second commit
* dc86492 first commit
```

```plaintext
third commit README.md:  hello hello hello
second commit README.md: hello hello
first commit README.md:   hello
```

`git reset --soft HEAD^` 명령으로 마지막 커밋을 취소하면 세 번째 커밋 히스토리가 삭제되며, HEAD가 두 번째 커밋으로 이동된다

이 때 세 번째 커밋의 워킹 디렉토리에 있던 README.md 파일 내용과 스테이징 영역이 third commit의 상태 그대로 유지된다

```shell
$ git log --oneline
* d9d42fb (HEAD -> main) second commit
* dc86492 first commit

# third commit의 워킹 디렉토리와 스테이징 영역 상태가 그대로 유지된다
$ git status
Changes to be committed:
    modified:   README.md

$ cat README.md
hello hello hello  
```

### mixed reset 

```shell
$ git reset <commit>
$ git reset --mixed <commit>
```

믹스드 리셋은 HEAD를 지정한 커밋으로 이동시키며 스테이징 영역에 올라간 파일들을 모두 해제시킨다

소프트 리셋은 워킹 디렉토리와 스테이징 영역을 유지하지만 믹스드 리셋은 워킹 디렉토리만 유지하고 스테이징 영역은 유지하지 않는다

[soft rest](#soft-reset)의 예시와 동일하게 세 개의 커밋과 각 커밋의 README.md 파일의 내용이 아래와 같다고 가정해보자

```shell
$ git log --oneline
* 05132ef (HEAD -> main) third commit
* d9d42fb second commit
* dc86492 first commit
```

```plaintext
third commit README.md:  hello hello hello
second commit README.md: hello hello
first commit README.md:   hello
```

이 상태에서 `main.py` 파일을 하나 추가하여 스테이징 상태로 만들고 README 파일의 내용을 "hansanhha hansanhha hansanhha"로 바꾼다 (스테이징 X)

그럼 각 파일이 아래와 같은 git 상태를 가지며 로컬 리포지토리에 속해있을 것이다

```shell
$ tree .
.
├── README.md
└── main.py

$ git status
Changes to be committed:
        new file:   main.py

Changes not staged for commit:
        modified:   README.md
```

이제 `git reset HEAD^` 명령을 통해 두 번째 커밋으로 믹스드 리셋하면 워킹 디렉토리는 유지하여 main.py 파일과 내용이 변경된 리드미 파일이 있지만 이전에 새로 추가한 후 스테이징 영역에 놓인 main.py이 해제되어 untracked 상태가 된다

```shell
$ git status
Changes not staged for commit:
        modified:   README.md

Untracked files:
        main.py
```

이 점을 이용하면 `git reset file` 명령으로 특정 파일의 스테이징을 취소할 수 있다 (`git restore --staged <file>` 명령으로 안전하게 스테이징을 해제하는 방법도 있다)

### hard reset

하드 리셋은 HEAD와 스테이징 영역, 워킹 디렉토리를 리셋한 커밋의 상태로 되돌린다

리셋하기 전 워킹 디렉토리와 스테이징 영역의 변경 사항은 모두 버려진다

이전 예시들과 똑같이 세 개의 커밋과 각 커밋의 README.md 파일의 내용이 아래와 같다고 가정해보자

```shell
$ git log --oneline
* 05132ef (HEAD -> main) third commit
* d9d42fb second commit
* dc86492 first commit
```

```plaintext
third commit README.md:  hello hello hello
second commit README.md: hello hello
first commit README.md:   hello
```

이 상태에서 `main.py` 파일을 하나 추가하여 스테이징 상태로 만들고 README 파일의 내용을 "hansanhha hansanhha hansanhha"로 바꾼다 (스테이징 X)

그럼 각 파일이 아래와 같은 git 상태를 가지며 로컬 리포지토리에 속해있을 것이다

```shell
$ tree .
.
├── README.md
└── main.py

$ git status
Changes to be committed:
        new file:   main.py

Changes not staged for commit:
        modified:   README.md
```

`git reset --hard HEAD^` 명령으로 이전 커밋(두 번째 커밋)으로 하드 리셋하면 스테이징 영역에 놓였던 main.py는 스테이징에서 해제되어 untracked 상태로 놓이고, 워킹 디렉토리에만 존재했던 리드미의 변경 사항이 완전히 버려지고 두 번째 커밋의 리드미 파일로 돌아간다

여기서 알 수 있는 점은 세 번째 커밋에서 아직 스테이징 영역에 추가되긴 했지만 git으로부터 추적되지 않았던 main.py 파일은 하드 리셋이후에도 워킹 디렉토리에 존재하고, git이 추적 중이었던 리드미 파일의 변경사항은 두 번째 커밋으로 덮어씌워졌다 -> 리셋한 시점으로 워킹 디렉토리와 스테이징 영역의 상태를 변경할 뿐 git이 추적 중이지 않은 파일은 삭제하지 않는다

혹시 특정 커밋으로 하드 리셋하면 삭제된 파일을 복구할 수 있을까?

그렇다. 하드 리셋은 워킹 디렉토리를 해당 커밋의 스냅샷 상태로 되돌려내기 때문이다

아래와 같이 main.py 파일을 추가한 후 커밋(fourth commit)한 다음, 다시 삭제하고 커밋한다 (fifth commit)

```plaintext
* s3k4sd1 (HEAD -> main) fifth commit (main.py 삭제)
* 2174f1b fourth commit (main.py 추가)
* b575d1a third commit
* fd45711 second commit
* 33152e3 first commit
```

HEAD는 main.py가 삭제된 다섯 번째 커밋을 가리키고 있으므로 현재 워킹 디렉토리에는 main.py 파일이 없다

```shell
$ ls
README.md
```

이 때 `git reset --hard HEAD^` 명령으로 네 번째 커밋으로 하드 리셋하면 main.py 파일을 가지고 있던 상태가 워킹 디렉토리에 반영되어 삭제한 파일을 다시 되찾을 수 있다

```shell
$ git reset --hard HEAD^

$ ls
README.md main.py
```

워킹 디렉토리 상태를 해당 시점으로 되돌리는 하드 리셋의 특징을 이용하면 아래와 같이 로컬 브랜치를 원격 리포지토리의 브랜치와 강제로 동기화시킬 수 있다

```shell
# 원격 리포지토리 다운로드
$ git fetch

# 로컬 브랜치를 원격 main 브랜치와 동일한 상태로 강제 동기화
$ git reset --hard origin/main
```


## git stash

`git stash` 명령은 Git에서 작업 중인 변경 사항을 임시로 저장한다

주로 브랜치 전환을 위해 워킹 디렉토리를 clean 상태로 되돌리거나 다른 작업을 처리할 때 현재 작업을 잠시 보류하기 위해 활용한다

### stash components

워킹 디렉토리: 수정 중인 파일 상태

스테이징 영역: `git add`로 추가된 변경 사항

스태시 스택: 저장된 변경 사항의 목록 `.git/refs/stash`

### how stash works

- 워킹 디렉토리와 스테이징 영역의 변경 사항(tracked)을 커밋으로 만들고 스태시 스택에 푸시한다 (`git/refs/stash`에 가장 최신 스태시 커밋 해시 저장)
- 현재 상태를 마지막 커밋(HEAD)으로 복원한다
- 이후 스택에서 변경 사항을 꺼내 작업 디렉토리나 스테이징 영역에 복사한다

각 스태시는 세 개의 커밋으로 저장된다
- 워킹 디렉토리 상태 (working tree)
- 스테이징 상태 (index)
- untracked 파일 상태 (`-u` 또는 `-a` 옵션 사용 시 생성됨)

기본적으로 tracked 파일만 포함하며, untracked 파일은 `-u` 옵션으로 포함할 수 있다

추적되지 않는 파일과 무시된 파일을 모두 포함하려면 `-a` 옵션을 사용한다

### example repository

예시 리포지토리 구조와 각 파일의 상태

```shell
# 세 개의 파일
$ tree
.
├── README.md
├── app.py
└── main.py

# README.md: staging
# main.py: modified
# app.py: untracked
$ git status
Changes to be committed:
        modified:   README.md

Changes not staged for commit:
        modified:   main.py

Untracked files:
        app.py
```

스태시하기 전 커밋 히스토리

```shell
$ git log --oneline
* b8876f1 (HEAD -> main) third commit
* fa43c40 second commit
* fe62995 first commit
```

### `git stash [push]` command

`git stash`: `git stash push`와 동일한 명령어로 현재 모든 변경 사항을 스태시 스택에 저장하고 워킹 디렉토리와 스테이징 상태를 HEAD로 복원한다 

```shell
# untracked 파일 포함 모든 변경 사항 스태시
$ git stash -u 
Saved working directory and index state WIP on main: b8876f1 third commit


# 스태시 후 커밋 히스토리 (세 개의 스태시 커밋 생성)
$ git log --oneline
*-.   0394530 (refs/stash) WIP on main: b8876f1 third commit
|\ \
| | * 7a61b2f untracked files on main: b8876f1 third commit
| * 0e34051 index on main: b8876f1 third commit
|/
* b8876f1 (HEAD -> main) third commit
* fa43c40 second commit
* fe62995 first commit


# 워킹 디렉토리와 스테이징 영역이 HEAD 상태로 되돌아간다
$ git status
nothing to commit, working tree clean
```

위의 `git log --oneline`에 나타난 가장 마지막 커밋이 모든 스태시 커밋을 병합한다

"WIP"는 Work In Progree의 약자로 메인 브랜치에서 작업이 진행 중이지만 완료되지 않은 상태를 의미한다

그리고 이 커밋의 해시 값이 `.git/refs/stash` 파일에 저장된다

```shell
$ cat .git/refs/stash
0394530a045ce546cebf387f9cf5b1a105f15a46
```

`git stash [push] -m "<message>"`: 메시지와 함께 스태시

기본적으로 스테이징 영역에 추가된 변경 사항을 스태시 한 후 꺼내면, 스테이징이 해제되는데 `git stash [push] -k` 명령을 사용하면 푸시할 때 스테이징 상태를 유지한 채로 스태시하여 다시 꺼낼 때 그대로 적용되어 있다


### `git stash list` command

`git stash list`: 스태시 목록 확인

아래와 같이 임시 파일을 하나 만들어서 스테이징 영역에 추가시킨 다음 스태시 스택에 추가해보자

```shell
$ touch math_utils.py
$ git stash -m "work in progress on math utils"
Saved working directory and index state On main: work in progress on math utils
```

스태시는 스택 구조로 동작하므로 `git stash list` 명령어로 스태시 목록을 확인하면 먼저 저장된 스태시가 아래에 위치하며 더 높은 값을 가진다 

```shell
$ git stash list
stash@{0}: On main: work in progress on math utils
stash@{1}: WIP on main: b8876f1 third commit
```

또한 커밋 히스토리를 조회하면 가장 최신의 스태시만 표시되는 것을 볼 수 있다

```shell
$ git log --oneline
*   5d8182c (refs/stash) On main: work in progress on math utils
|\
| * 482a6f4 index on main: b8876f1 third commit
|/
* b8876f1 (HEAD -> main) third commit
* fa43c40 second commit
* fe62995 first commit
```

### others

`git stash apply`: 최신 스태시 `stash@{0}`를 워킹 디렉토리에 적용한다 (스택에서 스태시가 삭제되지 않으며 이전에 스테이징 영역이 해제됨)

`git stash apply stash@{n}`: 특정 스태시를 적용한다

`git stash pop`: 최신 스태시를 적용하고 스택에서 제거한다

`git stash drop stash@{n}`: 특정 스태시를 스택에서 제거한다

`git stash clear`: 모든 스태시 삭제

`git stash branch <branch-name>`: 새 브랜치를 생성하고 최신 스태시를 적용한 뒤 스택에서 제거한다


## git clean

`git clean` 명령은 워킹 디렉토리에서 git이 추적하지 않는 파일과 디렉토리를 삭제한다

Untracked 파일은 `.git` 디렉토리에 포함되지 않은 파일을 말하며 커밋이나 스테이징에 추가되지 않은 경우에 해당한다

참고 사항
- `.gitignore`에 명시된 파일은 기본적으로 삭제 대상에서 제외된다
- 삭제 후 복구할 수 없다

`git clean -f`: 추적되지 않은 파일 삭제 (강제 실행 필요)

`git clean -fd`: 추적되지 않은 디렉토리 포함 삭제

`git clean -n`: 시뮬레이션(삭제할 파일 목록만 표시함)

`git clean -fx`: `.gitignore`에 명시된 파일까지 포함하여 삭제


## git bisect