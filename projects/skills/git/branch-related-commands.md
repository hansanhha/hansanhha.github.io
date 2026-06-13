---
layout: default
title:
---

#### index
- [git branch](#git-branch)
- [git checkout](#git-checkout)
- [git switch](#git-switch)
- [git sparse-checkout](#git-sparse-checkout)
- [git show-branch](#git-show-branch)


## git branch

`git branch`는 git 리포지토리에서 브랜치 생성, 삭제, 나열하거나 상태를 확인하는 데 사용되는 명령어이다

branch
- 로컬 리포지토리의 브랜치는 `.git/refs/heads/` 디렉토리에 저장된 파일로 특정 커밋의 SHA-1 해시를 가리킨다
- `.git/refs/heads/main` -> `a1b2c3d4e5...`
- 각 브랜치는 독립적인 커밋 히스토리를 가지며 병합(`merge`)이나 리베이스(`rebase`)로 다른 브랜치의 커밋 히스토리와 통합할 수 있다

HEAD(`.git/HEAD`): 현재 작업 중인 브랜치를 가리키는 포인터 역할을 한다

branch command works
- 브랜치 생성: 새 브랜치 파일을 `.git/refs/heads/` 디렉토리에 추가 (HEAD 기반)
- 브랜치 이름 변경: 브랜치 파일 이동 및 참조 업데이트
- 브랜치 조회: `.git/refs/heads/`와 `.git/remotes/` (원격 참조)를 읽어 현재 로컬 리포지토리의 브랜치들을 표시한다
- 브랜치 삭제: 브랜치 파일 제거 및 참조 정리 (병합 여부 확인)

### check

`git branch` 또는 `git branch --list`: 로컬 브랜치 목록 표시(현재 브랜치 앞에 `*` 표시)

`git branch -a`: 로컬 + 원격 브랜치 조회

`git branch -r`: 원격 브랜치만 조회

`git branch -v`: 각 브랜치의 마지막 커밋 메시지 표시

`git branch --merged`: 현재 브랜치에 병합된 브랜치 표시

`git branch --no-merged`: 현재 브랜치에 병합되지 않은 브랜치 표시


### create/delete

`git branch <branch-name>`: 현재 HEAD에서 새 브랜치 생성 (브랜치 전환 X)

`git branch -d <branch-name>`: 병합된 브랜치 삭제(병합이 안 된 경우 오류 발생)

`git branch -D <branch-name>`: 병합 여부와 상관없이 브랜치 강제 삭제

### util

`git branch -m <old-name> <new-name>`: 브랜치 이름 수정

### remote

`git branch --set-upstream-to=<remote-branch>` 또는 `git branch -u <remote-branch>`: 현재 브랜치와 원격 브랜치 연결 (`git branch --set-up-to=origin/main`)

`git branch --unset-upstream`: 현재 브랜치와 연결된 원격 브랜치 연결 해제


## git checkout

`git checkout` 명령은 git 리포지토리에서 브랜치나 커밋으로 작업 환경(워킹 디렉토리)을 전환하거나 파일의 상태를 특정 시점에 복원한다

주로 브랜치 전환이나 파일 복구에 사용된다

구성 요소
- HEAD: 현재 작업 중인 브랜치나 커밋 포인터
- 워킹 디렉토리: 파일의 현재 상태
- 스테이징 영역(인덱스): 다음 커밋에 포함될 변경 사항

브랜치 전환: clean 상태인 경우 HEAD를 지정된 브랜치로 이동하고 워킹 디렉토리와 스테이징 영역을 해당 브랜치의 최신 커밋으로 업데이트한다 (dirty 상태인 경우 브랜치 전환 오류)

커밋 전환: HEAD를 특정 커밋으로 이동시키며 Detached HEAD 상태가 된다 (clean, dirty 작동)

파일 복구: 지정된 커밋이나 브랜치에서 파일 내용을 가져와 작업 디렉토리/스테이징 영역에 적용시킨다 -> 해당 시점의 파일 내용 복구

### HEAD checkout

`git checkout <branch-name>`: 지정된 브랜치로 HEAD를 전환한다

`git checkout -`: 이전 브랜치로 전환한다

`git checkout <commit-hash>`: 지정된 커밋 해시로 HEAD를 이동시킨다 (Detached HEAD)

`git checkout -b <branch-name>`: 현재 브랜치를 기반으로 새 브랜치를 생성하고 HEAD를 즉시 전환한다 (`git branch` + `git checkout`)

`git checkout -f <branch-name>`: 현재 브랜치의 변경 사항을 폐기하고 강제로 HEAD를 전환한다

`git checkout --merge <branch-name>`: 전환 시 변경 사항을 병합 시도한다

### remote branch checkout

`git checkout --track <remote-branch>`: 원격 리포지토리의 브랜치를 로컬에 생성한 후 HEAD를 전환한다 (`git checkout origin/feature` -> `feature` 생성 후 트래킹 및 전환)

`git checkout --no-track <remote-branch>`: 원격 리포지토리의 브랜치를 기준으로 브랜치를 생성하고 HEAD를 전환하지만 추적하지 않는다

### restore file

`git checkout -- <file>`: modified 상태인 파일을 원래 상태로 되돌린다 (대체 명령어: `git restore <file>` 또는 `git restore --staged <file>`)

`git checkout <commit hash> -- <file>`: 특정 커밋에서 파일 복원 (대체 명령어: `git restore --source=<commit> <file>`)


## git switch

`git checkout` 명령어가 브랜치 전환, 커밋 확인, 파일 복원 등의 다양한 기능들을 수행하지만 오히려 사용의 혼란을 초래할 수 있다

git은 2.23 버전부터 직관적이고 명확한 기능 분리를 위해 브랜치 전환 및 생성에 특화된 `git switch` 명령어를 도입했다

`git switch` 명령의 브랜치 전환/생성에 대한 동작 과정은 `git checkout`과 동일하지만, 파일 복원 기능이 없다 (`restore` 명령 사용)

브랜치 전환: HEAD를 지정된 브랜치로 이동시키며 워킹 디렉토리와 스테이징 영역을 해당 브랜치의 최신 커밋으로 업데이트한다 (clean, dirty 적용)

브랜치 생성: `.git/refs/heads`에 새 브랜치 파일을 생성하고 HEAD를 새 브랜치로 이동 후 워킹 디렉토리를 반영한다

`git switch <branch-name>`: 지정된 브랜치로 전환한다

`git switch -`: 이전 브랜치로 전환한다

`git switch -f <branch-name>`: 현재 브랜치의 변경 사항을 폐기하고 지정한 브랜치로 전환한다

`git swith -c <branch-name>`: 현재 커밋을 기준으로 새 브랜치를 생성하고 즉시 HEAD를 이동시킨다 (`git branch ` + `git switch`)

`git switch --detach <commit-hash>`: 특정 커밋으로 이동한다 (Detached HEAD)

`git switch --track <remote-banch>`: 원격 리포지토리의 브랜치를 로컬에 생성한 후 전환한다 (추적 설정)


## git sparse-checkout

## git show-branch

`git show-branch` 명령은 리포지토리의 브랜치와 태그를 기준으로 커밋 히스토리를 비교하고, 각 브랜치의 분기점과 커밋을 표 형식으로 표시한다 (가독성이 떨어져서 다른 시각화 도구를 사용하는 것을 권장함)

용도
- 브랜치 관계 확인: 브랜치가 어디서 갈라졌는지, 어떤 커밋이 포함됐는지 파악
- 히스토리 시각화: 로그를 간단한 표로 비교
- 디버깅: 병합 상태나 분기 흐름 분석

동작 원리
- `.git/refs/heads`에서 로컬 브랜치를 읽고 각 브랜치의 커밋을 추적해 비교한다
- 모든 브랜치의 최신 커밋부터 공통 조상까지 역순으로 탐색한다 (기본 동작)
- 브랜치 간 병합 기반점(merge base)를 계산한다
- 브랜치 별 커밋 존재 여부를 기호로 표시한다

출력 형식
- 열: 각 브랜치
- 행: 시간 순으로 커밋 나열
- `!`: 해당 브랜치의 고유 커밋
- `*`: 현재 브랜치의 커밋
- `+`: 다른 브랜치의 커밋
- `-`: 병합 커밋


`git show-branch`: 현재 브랜치를 기준으로 커밋 히스토리 표 출력

`git show-branch <branch1> <branch2>`: 특정 브랜치를 지정하여 커밋 히스토리 표 출력

`git show-branch --all`: 모든 참조(로컬/원격 브랜치 및 태그 모두 포함)