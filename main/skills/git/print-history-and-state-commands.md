---
layout: default
title:
---

#### index
- [git status](#git-status)
- [git diff](#git-diff)
- [git log](#git-log)
- [git reflog](#git-reflog)


## git status

`git status`는 현재 리포지토리의 상태를 보여주는 명령어이다

### how git status works

git은 스테이징 영역과 워킹 디렉토리를 HEAD와 비교하여 각 파일의 추가/수정/삭제 등의 변경을 감지한다

감지된 변경의 상태 값에 따라 분류한 뒤 메시지를 생성하고 브랜치 정보와 함께 상태를 출력한다

### messages

Changes not staged: 변경 사항이 워킹 디렉토리에만 존재

Changes to be committed: 변경 사항을 스테이징 영역에 추가

Untracked: git이 관리하지 않는 파일

### `git status` command

`git status` 명령은 브랜치 정보 및 변경 감지한 파일들을 상태별로 분류하고, 각 상태마다 처리할 수 있는 다른 명령어와 함께 메시지를 출력한다

```shell
$ git status

# 브랜치 정보
On branch main

# 로컬 main 브랜치와 원격 main 브랜치 동기화 정보 (up to date)
Your branch is up to date with 'origin/main'.

# 스테이징 영역 정보
Changes to be committed:
  (use "git restore --staged <file>..." to unstage)
        new file:   test2/a

# 워킹 디렉토리 변경 사항 정보
Changes not staged for commit:
  (use "git add <file>..." to update what will be committed)
  (use "git restore <file>..." to discard changes in working directory)
        modified:   main.py

# 추적 중이지 않은 파일 정보
Untracked files:
  (use "git add <file>..." to include in what will be committed)
        test.py
```

### `git status -s` command

`git status -s` 명령은 변경 감지된 파일에 대해 짧은 형식으로 상태를 표시한다

기호
- `M` 왼쪽: 스테이징된 변경 사항 
- `M` 오른쪽: 스테이징 안 된 변경 사항
- `A` 왼쪽: 스테이징된 추적되지 않은 파일
- `R` 왼쪽: 이름 변경된 파일
- `??`: 추적되지 않은 파일

```shell
$ git status -s
M  test.py   # 스테이징 영역에 추가된 변경 사항
 M main.py   # 스테이징 영역에 추가되지 않은 변경 사항
A  test2/a   # 스테이징 영역에 추가된 추적되지 않은 파일 
?? test.py   # 추적되지 않은 파일
```

### state list

로컬 브랜치 상태
- untracked: 추적하지 않는 상태
- modified: 파일 내용이 변경된 상태
- staged: 스테이징 영역에 추가된 상태
- stashed: 스태시된 상태
- renamed: 파일 이름 또는 경로 변경 상태
- deleted: 삭제된 상태
- typechanged: 파일 유형이 변경된 상태
- diverged: 분기된 상태
- conflicted: 브랜치 병합 도중 충돌이 발생한 상태

원격 브랜치와의 동기화 상태
- up_to_date: 원격 브랜치와 동기화된 상태
- ahead: 원격 브랜치의 마지막 커밋 이후 로컬 브랜치에 추가 커밋이 있는 상태
- behind: 로컬 브랜치의 마지막 커밋 이후 원격 브랜치에 추가 커밋이 있는 상태

리포지토리 작업 상태
- rebase: 리베이스 작업을 처리 중인 상태
- merge: 병합 작업을 처리 중인 상태
- revert: 복구 작업을 처리 중인 상태
- cherry_pick: 체리 피킹 작업을 처리 중인 상태
- bisect: bisect 작업을 처리 중인 상태


## git diff 

`git diff`는 git 리포지토리에서 파일 간 또는 워킹 디렉토리, 스테이징 영역, 커밋 간의 변경 사항 차이점을 표시하는 명령어로 텍스트 파일만 비교할 수 있다

비교 대상(워킹 디렉토리 vs 스테이징 또는 스테이징 vs HEAD)을 설정하면 Diff 알고리즘을 이용하여 파일별 줄 단위 변경 사항을 계산한다

추가된 줄은 `+`로 삭제된 줄은 `-` 기호를 사용하여 출력한다

### commands

`git diff [<file>]`: 워킹 디렉토리 vs 스테이징 영역의 변경 사항을 비교한다 (워킹 디렉토리와 스테이징 모두 변경 사항이 있어야 출력되며, 만약 스테이징만 변경 사항이 있는 경우 `--staged` 옵션을 사용해야 함) 

`git diff --staged [<file>]` 또는 `git diff --cached [<file>]`: HEAD와 스테이징 영역의 변경 사항을 비교한다 

`git diff <commit1> <commit2>`: 지정된 두 커밋 간의 변경 사항을 비교한다

`git diff --stat`: 변경 사항 통계 요약

### `git diff` command

파일 변경 사항

```shell
# file.txt의 변경 사항을 스테이징 영역에 추가한다
$ echo "hello" > file.txt
$ git add file.txt

# 이후 워킹 디렉토리에서 파일을 한 번 더 변경한다
$ git "git" >> file.txt
```

명령 실행

```shell
$ git diff file.txt

# a: 변경 전 파일 (스테이징의 file.txt 변경 사항)
# b: 변경 후 파일 (워킹 디렉토리의 file.txt 변경 사항)
diff --git a/file.txt b/file.txt

# 파일 해시 및 모드 정보
# ce01362: 변경 전 파일의 Blob 객체 SHA-1 해시
# 23509e0: 변경 후 파일의 Blob 객체 SHA-1 해시
# 100644: 일반 파일(읽기/쓰기 권한, 실행 불가)
index ce01362..23509e0 100644

# ----: 변경 전 상태의 파일 경로, 삭제된 줄을 표시할 기준 파일 (스테이징의 a/file.txt)
# ++++: 변경 후 상태의 파일 경로, 추가된 줄을 표시할 대상 파일 (워킹 디렉토리의 b/file.txt)
--- a/file.txt
+++ b/file.txt

# @@ -1 +1,2 @@: 변경이 발생한 줄 범위를 나타내는 청크 헤더
# -1: 변경 전 파일의 시작 줄 번호와 범위 (-1은 첫 번째 줄부터 1줄을 의미함)
# +1,2: 변경 후 파일의 시작 줄 번호와 범위(+1,2는 첫 번째 줄부터 2줄을 의미함)
# 변경 전에 1줄이었던 파일이 변경 후 2줄로 늘어난 것을 의미한다
@@ -1 +1,2 @@

# hello: 추가(+)나 삭제(-)없이 그대로 유지됨
# +git: 변경 후 파일(워킹 디렉토리)에서 새로 추가된 줄
 hello
+git
```

### `git diff --staged` command

파일 변경 사항
```shell
$ cat file.txt
hello
git

$ echo "hello\nhansanhha" >> file.txt
$ git add file.txt
```

명령 실행

```shell
$ git status --staged file.txt

diff --git a/file.txt b/file.txt
index 23509e0..cf2a5ac 100644
--- a/file.txt
+++ b/file.txt

# -1,2: 변경 전 파일의 줄 범위
# +1,3: 변경 후 파일의 줄 범위 (2줄 늘어남)
@@ -1,2 +1,4 @@
 hello
 git
+hello
+hansanhha
```

### `git diff <commit1> <commit2>` command

```shell
# 마지막 커밋과 직전 커밋 비교
$ git diff HEAD^ HEAD

diff --git a/file.txt b/file.txt
index 23509e0..968993b 100644
--- a/file.txt
+++ b/file.txt
@@ -1,2 +1,4 @@
 hello
 git
+hansanhha
+hello
```


## git log 

`git log` 명령은 현재 브랜치(HEAD)를 기준으로 커밋 히스토리를 표시한다

각 커밋의 SHA-1 해시, 작성자, 날짜, 커밋 메시지 등 메타데이터를 출력한다

`git log`: 커밋 히스토리의 각 커밋에 대한 상세 내역을 출력한다

`git log -p`(patch): 각 커밋의 변경 내역(diff)을 포함하여 출력한다

`git log --oneline`: 커밋 해시 일부와 메시지를 한 줄로 표시한다

`git log --graph --all`: 모든 브랜치를 포함하고 각 브랜치의 분기 구조를 시각화한다

`git log --follow <file>`: 특정 파일의 히스토리를 출력한다

`git log --grep=<pattern>`: 지정한 패턴을 커밋 메시지로 포함한 히스토리를 출력한다

`git log -n <number>`: 최근 n개 커밋만 표시한다

`git log <start>..<end>`: start부터 end까지의 로그를 출력한다

## git show

`git show` 명령은 지정된 커밋의 메타데이터(작성자, 커밋 메시지 등)와 변경 내용(diff)을 출력한다

기본적으로 HEAD의 커밋을 대상으로 동작하며 특정 커밋이나 브랜치, 태그, 파일을 지정할 수 있다 (Blob 같은 저수준 객체도 표시 가능)

`git log -1 -p` 명령과 동일한 동작을 수행한다


## git reflog

`git flog` 명령은 HEAD와 브랜치 참조가 이동한 모든 기록을 표시한다

`.git/logs/` 디렉토리에 저장된 로그(`.git/logs/HEAD`, `.git/logs/refs/`)를 기반으로 동작하며 기본적으로 최근 30일 간의 기록을 유지한다

`git log`와 달리 삭제되거나 덮어씌워진 커밋(Detached HEAD, 고아 커밋 포함)까지 포함해 저장소의 모든 동작을 보여준다

용도
- 실수로 삭제된 커밋이나 브랜치를 복원한다
- HEAD의 과거 이동 경로(커밋, 리셋, 리베이스 등)를 확인한다

동작 과정
- 최신 작업부터 역순으로 나열하여 로그를 출력한다
- 각 항목에 커밋 해시, 포인터 정보, 커밋 메시지 등을 표시한다

`git reflog`: 최근 HEAD 이동 기록을 표시한다

`git reflog <branch>`: 특정 브랜치의 로그를 출력한다

`git reflog -n <number>`: 최근 n개의 항목만 표시한다

### example

`git reflog`와 `git reset --hard` 명령을 사용하여 삭제된 커밋의 이전 상태를 복구할 수 있다

```shell
# main 브랜치의 커밋 히스토리 확인
$ git log --oneline
5a0df1a add main.py
231734c add util.py
85a44ff add test.py

# 가장 최근 커밋 삭제
$ git rebase -i HEAD^

# main 브랜치의 커밋 히스토리 확인 (가장 최근 커밋 삭제됨)
$ git log --oneline
231734c add util.py
85a44ff add test.py

# reflog로 모든 로그 확인
$ git reflog main
760f761 (HEAD -> main) main@{0}: rebase (finish): refs/heads/main onto 760f761be966564fcc6f59c4a537438813b70f56
5a0df1a (HEAD -> main) main@{1}: add main.py 
231734c (HEAD -> main) main@{2}: add util.py 
85a44ff (HEAD -> main) main@{3}: add test.py 

# 삭제된 커밋의 상태로 하드리셋하여 상태 복구
$ git reset --hard main@{1}
```