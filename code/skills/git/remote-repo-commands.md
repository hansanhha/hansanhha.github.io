---
layout: default
title:
---

#### index
- [git remote](#git-remote)
- [git ls-remote](#git-ls-remote)
- [git push](#git-push)
- [git fetch](#git-fetch)
- [git pull](#git-pull)
- [git archive](#git-archive)


## git remote

`git remote` 명령은 로컬 리포지토리와 연결된 원격 리포지토리를 관리한다 (다중 원격 리포지토리 관리 가능)

원격 리포지토리는 github, gitlab, bitbucket 같은 호스팅 서비스에 있는 리포지토리를 말한다

로컬 리포지토리에 설정된 특정 연결을 통해 `git push`, `git fetch`, `git pull` 등의 명령어로 원격 리포지토리와 상호작용한다 

### how git remote works

구성 요소
- `.git/config` 파일에 저장된 원격 리포지토리 정보
- 원격 리포지토리 별칭
- 원격 리포지토리 실제 주소(HTTPS/SSH)

`git remote` 명령으로 원격 리포지토리를 추가하면 `.git/config`에 이름과 URL을 기록하고 이름 변경, URL 갱신, 삭제 등으로 기록된 값을 조정할 수 있다

`fetch` `push` 등의 원격 리포지토리 상호작용 관련 명령어들은 `.git/config` 파일에 저장된 정보를 기반으로 작업을 동작한다 (`git remote` 자체는 네트워크 통신을 하지 않음)

### remote check commands

`git remote`: 연결된 원격 리포지토리들의 별칭을 나열한다

`git remote -v`: 연결된 원격 리포지토리의 별칭과 URL을 (fetch/push)을 표시한다

`git remote get-url <name>`: 원격 리포지토리 URL을 조회한다

`git remote show origin`: fetch, push 등 원격 리포지토리 상세 정보를 출력한다 (네트워크 통신 필요)

### remote managing commands

`git remote add <name> <url>`: 새 원격 리포지토리 연결을 추가한다 (HTTPS/SSH)

`git remote remove <name>`: 저장된 원격 리포지토리 연결을 제거한다

`git remote rename <old-name> <new-name>` 원격 리포지토리 별칭을 변경한다

`git remote set-url <name>`: 원격 리포지토리 URL을 갱신한다

`git remote prune <name>`: 원격 리포지토리에서 삭제된 브랜치를 정리한다

### remote connecting naming

관례상 첫 원격 리포지토리의 이름을 `origin`이라고 한다

다중 원격 리포지토리를 관리하는 경우 포크 프로젝트를 `upstream` 이름을 짓는다


## git ls-remote

`git ls-remote` 명령은 원격 리포지토리의 참조(브랜치, 태그 등)와 그에 연결된 커밋 해시를 나열한다 (네트워크 연결 필요)

로컬 리포지토리와 동기화하지 않고 원격의 현재 상태를 확인할 수 있다

### how git ls-remote works

구성 요소
- 원격 URL: `.git/config` 파일에 저장된 원격 리포지토리의 주소(HTTPS/SSH)
- 참조: 원격 리포지토리에 저장된 `refs/heads`, `refs/tags` 등 (호스팅 서비스에서 원격 리포지토리의 `.git` 디렉토리를 관리함)
- 커밋 해시: 각 참조가 가리키는 SHA-1 값

지정된 원격 리포지토리에 네트워크 요청을 하여 모든 참조와 해시 정보(해당 참조가 가리키는 커밋)를 가져온다 

`git ls-remote` 옵션에 따라 특정 참조만 추출한 뒤, 가져온 해시와 참조 이름 쌍으로 정보를 출력한다

실제 커밋 데이터를 가져오지 않고 별도의 원격 리포지토리 URL 주소를 지정한 경우 로컬 리포지토리(`.git` 디렉토리)와 무관하게 동작한다

### git ls-remote commands

`git ls-remote <remote>`: 로컬 리포지토리에 연결된 원격 리포지토리의 모든 참조를 출력한다

`git ls-remote <url>`: 로컬 리포지토리없이 지정된 url의 모든 참조를 출력한다 (`.git` 디렉토리 없이 동작)

`git ls-remote <remote> <pattern>`: 지정된 패턴에 맞는 참조만 표시한다

`git ls-remote --tags <remote>`: 태그만 조회한다

`git ls-remote --heads <remote>`: 브랜치만 조회한다


## git push

`git push` 명령은 로컬 리포지토리의 커밋, 브랜치, 태그 등의 데이터를 원격 리포지토리에 업로드한다

로컬에서 작업한 변경 사항을 원격에 동기화하여 팀원과 작업 결과를 공유하거나 CI/CD 파이프라인을 가동시킨다

또한 로컬 리포지토리에서 삭제한 참조를 원격 리포지토리에서도 삭제하기 위해 사용할 수도 있다

### how git push works

구성 요소
- 로컬 리포지토리의 `.git/objects/` 디렉토리에 저장된 Commit, Tree, Blob 객체와 `.git/refs/` 디렉토리의 참조(브랜치, 태그)
- 원격 리포지토리의 `.git` 디렉토리
- 네트워크 프로토콜(HTTPS 또는 SSH)
- Refspec: `<src>:<dst>` 형식으로 어떤 로컬 참조가 원격 참조로 매핑되는지 정의

#### push-preparation

git은 `git push <remote> <branch>` 명령에서 `<remote>`와 `<branch>`를 확인한다
  
`.git/refs/heads/<branch>`에서 로컬 브랜치의 SHA-1 커밋 해시를 읽어서 현재 브랜치(HEAD)와 전송할 원격 브랜치의 커밋 히스토리를 비교하여 전송할 커밋을 결정한다

로컬에만 있는 Commit, Tree, Blob 객체를 식별하고 Packfile로 전송할 데이터들을 압축(델타 압축)한다

#### authenticate, upload

HTTPS/SSH 프로토콜로 원격 서버에 연결하고 인증(SSH 키, 토큰 등) 후 Packfile을 업로드한다

원격 서버는 수신된 객체를 `.git/objects/` 디렉토리에 압축 해제하여 풀어낸다

로컬 브랜치의 커밋 해시를 원격의 `refs/heads/<branch>`에 반영하도록 요청하고, 원격 서버는 참조 업데이트가 가능한지 확인한다 (fast-forward, 강제 여부 등)

로컬 커밋이 원격 브랜치의 선형 후속이면 바로 fast-forward 병합이 이뤄진다

병합 성공 시 원격 브랜치 포인터가 이동하고, 실패 시 Non-fast-forward 오류 메시지 응답을 반환한다

#### trigger hooks

원격 서버에서 push 관련 훅을 설정하면 그에 따른 훅을 호출하여 CI/CD 파이프라인을 가동한다

### git push commands

`git push <remote> <branch>`: 현재 브랜치의 변경 사항을 지정한 원격의 브랜치로 업로드한다

`git push -u <remote> <branch>`: 현재 브랜치를 지정한 원격의 브랜치에 추적 설정하고 변경 사항을 업로드한다 (이후에 `git push`만 해도 자동 전송됨)

`git push <remote> <tag>`: 태그를 원격에 업로드한다

`git push --all <remote>`: 로컬 리포지토리의 모든 브랜치의 변경 사항을 업로드한다

`git push --tags <remote>`: 로컬 리포지토리의 모든 태그를 업로드한다

`git push -f <remote> <branch>`: 원격 리포지토리의 커밋 히스토리를 로컬 리포지토리 것으로 덮어씌운다 (팀원의 로컬 리포지토리의 커밋 히스토리와 충돌을 일으킬 수 있음)

`git push --delete <remote> <reference>`: 원격 브랜치/태그 삭제

`git push --dry-run`: 실제 푸시 없이 시뮬레이션

### --force-with-lease option

`git push --force-with-lease <remote> <branch>` 명령은 `git push` 시 원격 브랜치의 상태를 확인하고, 로컬에서 알고 있는 원격 브랜치의 상태와 일치할 때만 강제로 푸시를 허용한다

사용자가 원격의 커밋 히스토리를 로컬로 덮어쓰기 전에 다른 개발자가 추가한 커밋을 보호하기 위해 사용한다

로컬에서 원격 브랜치의 마지막 커밋 해시를 기억(fetch 기준)하고, push 요청 시 서버에 로컬에서 알고 있는 최신 원격 해시를 전송한다

로컬 예상과 일치하면 강제로 업데이트하고, 다른 사용자에 의해 원격의 마지막 해시가 수정되면 일치하지 않기 때문에 작업을 강제 푸시를 중단한다


## git fetch

`git fetch` 명령은 원격 리포지토리에서 최신 데이터(커밋, 브랜치, 태그 등)를 로컬 리포지토리로 가져온다

가져온 데이터는 로컬 리포지토리(`.git/objects)와 원격 추적 브랜치(`origin/main` 등)에 반영되지만 현재 작업 브랜치(HEAD)에 자동으로 병합되지 않는다

`git fetch <remote>`: 지정된 원격의 모든 데이터(브랜치, 태그, 커밋)를 가져온다

`git fetch --all`: 설정된 모든 원격 리포지토리에서 데이터를 가져온다

`git fetch <remote> <branch>`: 지정된 브랜치의 데이터만 가져온다

`git fetch --tags`: 원격 리포지토리의 태그를 동기화한다

`git fetch --prune`: fetch를 수행하면서 원격에서 삭제된 브랜치를 로컬 리포지토리의 추적 브랜치에서도 제거한다

`git fetch --dry-run`: 실제 fetch를 하지 않고 시뮬레이션만 한다


## git pull

`git pull` 명령은 fetch 동작을 수행하고 가져온 데이터들을 로컬 브랜치에 즉시 병합한다 (`git fetch` + `git merge`)

`git pull <remote> <branch>`: 지정한 원격 브랜치의 데이터를 가져와 현재 브랜치에 병합한다

`git pull`: 현재 브랜치에 추적 설정된 원격 브랜치를 자동으로 가져온다음 병합한다

`git pull --tags`: 태그도 함께 가져온다음 병합한다

`git pull --rebase`: 3-way 병합 대신 리베이스를 적용한다 (선형 히스토리 유지, 원격 변경 사항 위에 로컬 커밋을 재적용함)

`git pull --ff-only`: fast-forward 병합만 허용한다 (병합 커밋 생성 방지)


## git archive

`git archive` 명령은 리포지토리의 특정 커밋, 브랜치, 태그 등의 파일 트리를 압축 파일(`.tar.gz` `.zip` 등)로 내보낸다

원격 리포지토리와 상호작용하거나 로컬 저장소의 스냅샷을 추출해 배포, 공유, 백업 용도로 사용할 수 있다

커밋 히스토리 같은 메타데이터는 포함하지 않고 파일 내용만 추출한다

`git archive --list`: 지원하는 압축 파일 형식을 표시한다

`git archive <tree-ish> -o <output-file>`: 지정된 트리(브랜치, 커밋 등)를 압축 파일로 출력한다

`git archive <tree-ish> <path>`: 지정 경로만 포함하여 압축 파일로 출력한다

`git archive --format=<format>`: 출력 형식을 지정한다

`git archive --remote<remote>`: 원격 리포지토리로터 아카이브를 생성한다 (네트워크 연결 필요, github 미지원)

### example

```shell
# 태그 v1.0.0 압축 파일 생성
$ git archive --format=tar.gz v1.0.0 -o release-v1.0.0.tar.gz
```

```shell
# docs 디렉토리만 tar로 생성
$ git archive main docs/ -o docs.tar
```

```shell
# 현재 HEAD 상태를 백업 파일로 추출
$ git archive HEAD -o backup.tar
```