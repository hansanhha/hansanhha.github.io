---
layout: default
title:
---

#### index
- [Subtree](#subtree)
- [workflow](#workflow)
- [Subtree Commands](#subtree-commands)
- [Subtree vs Submodule](#subtree-vs-submodule)


## Subtree

Git Subtree는 외부 저장소의 파일과 히스토리를 부모 리포지토리의 특정 폴더에 병합하여 통합 관리하는 기능이다

서브 모듈은 독립적인 리포지토리로 운영되면서 부모 리포지토리에서 이를 참조하는 방식인데, 이와 반대로 서브 트리는 외부 리포지토리의 코드를 부모 리포지토리의 일부로 가져온다음 하나의 히스토리로 만든다

별도의 서브 리포지토리를 두지 않고 단일 리포지토리로 관리하여 모든 코드를 통합할 수 있고, 부모 리포지토리와 외부 리포지토리 간 변경 사항을 주고받을 수 있다

외부 코드를 부모 리포지토리에 통합해 관리하려는 경우에 적합한 기능이다

다만 부모와 외부의 히스토리와 섞여 혼잡해질 수 있으며 병합 시 발생하는 충돌을 해결하기 어렵다


## workflow

외부 리포지토리의 특정 브랜치를 부모 리포지토리의 하위 디렉토리에 병합한다 (서브 모듈처럼 별도의 `.git` 디렉토리가 존재하지 않음)

외부 리포지토리의 커밋 히스토리를 부모 리포지토리에 병합(merge)하거나 단일 커밋으로 압축한다(squash)

병합 후 부모 리포지토리에서 해당 서브 트리를 변경할 수 있으며 외부 저장소에 푸시하여 변경 사항을 반영할 수 있다

반대로 외부 저장소의 변경 사항을 부모 리포지토리에서 pull하여 업데이트할 수 있다


## Subtree Commands

### add subtree

형식: `git subtree add --prefix=<path> <repository-url> <branch> --squash`

```shell
$ git subtree add --prefix=test git@github.com:hansanhha/test.git main --squash
```

`test/` 디렉토리에 test 리포지토리의 main 브랜치를 병합한다

`--squash` 명령어로 외부 리포지토리의 히스토리를 단일 커밋으로 압축한다

### update subtree changes (external -> parent)

형식: `git subtree pull --prefix=<path> <repository-url> <branch> --squash`

```shell
$ git subtree pull --prefix=test git@github.com:hansanhha/test.git main --sqaush
```

최신 변경 사항을 동기화하는 `pull` 명령어를 제외하면 서브 트리를 추가할 때와 형식이 동일하다

충돌 발생 시 수동으로 해결해야 한다

### push changes to subtree

형식: `git subtree push --prefix=<path> <repository-url> <branch>`

```shell
$ git subtree push --prefix=test git@github.com:hansanhha/test.git main
```

부모 리포지토리 `test/` 디렉토리의 변경 사항을 외부 리포지토리에 푸시한다 (`--squash`없이 전체 히스토리 반영)

### delete subtree

서브 트리는 서브 모듈처럼 별도의 파일이 없으므로 단순히 디렉토리를 삭제한 후 커밋하면 외부 리포지토리와의 연결이 끊긴다

```shell
$ git rm -r test
$ git add .
$ git commit -m "remove lib subtree"
```


## Subtree vs Submodule

|기준|Subtree|Submodule|
|---|----|---|
|리포지토리 수|단일|다중(부모 + 서브)|
|히스토리|통합|분리|
|구조|단일 `.git` 디렉토리, 코드 병합|각 서브 리포지토리마다 `.git` 디렉토리, 커밋 참조|
|초기화|필요 없음|`init` `update` 필요|
|사용 사례|코드 통합|독립 모듈 관리|