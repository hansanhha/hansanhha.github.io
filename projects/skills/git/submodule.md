---
layout: default
title:
---

#### index
- [Git Submodule](#git-submodule)
- [.gitmodules](#gitmodules)
- [Submodule Commands](#submodule-commands)


## Git Submodule

서브 모듈은 Git 리포지토리(부모 리포지토리) 안에 포함된 독립적인 Git 리포지토리를 말한다

즉, 리포지토리 안의 또 다른 리포지토리를 서브 모듈(또는 하위 모듈)이라고 한다

부모 리포지토리는 서브 모듈의 특정 커밋을 참조하며, 서브 모듈은 자체적으로 별도의 히스토리와 브랜치를 가진다

서브 모듈이 부모 리포지토리로부터 독립되어 업데이트를 할 수 있는 특성을 이용하여 큰 프로젝트를 작은 단위로 나눠 관리할 수 있다

또한 여러 프로젝트에서 동일한 서브 모듈을 공유할 수 있다


## .gitmodules

`.gitmodules` 파일은 부모 리포지토리의 루트 디렉토리에 위치하며 부모 리포지토리에 포함할 서브 모듈의 URL, 경로, 브랜치 정보 등을 설정하는 역할을 한다

일반적으로 `git submodule add` 명령을 통해 생성된다

```plaintext
[submodule "test"]
    path = test
    url = https://github.com/user/test.git test
```

path: 서브 모듈을 참조할 부모 리포지토리의 경로

url: 서브 모듈 리포지토리의  주소


## Submodule Commands

### add submodule

`git submodule add <repository-url> <path>`: 부모 리포지토리의 path 경로에 지정한 url을 기반으로 서브 모듈을 추가하며 `.gitmodules` 파일을 생성한다

### init/udpate submodule

`git submodule init` & `git submodule update` 또는 `git clone --resurse-submodules <repository-url>`: `git clone`은 기본적으로 서브 모듈을 빈 폴더로만 가져오므로 리포지토리를 클론할 때 서브 모듈을 초기화하는 명령어를 사용한다

`git submodule update --remote`: 서브 모듈의 원격 리포지토리에서 최신 커밋을 가져온다 (서브 모듈 동기화)

서브 모듈을 최신화하거나 서브 모듈의 변경사항을 커밋하면 부모 리포지토리에 별도로 반영해야 한다 -> 2단계 작업

서브 모듈 수정

```shell
$ cd test

# 수정 작업 실시 (생략)
# 변경 사항 반영 (서브 모듈)
$ git add .
$ git commit -m "update test code"
$ git push origin main

# 부모 리포지토리 갱신
$ cd ..
$ git add test
$ git commit -m "update test submodule reference"
```

### delete submodule

부모 리포지토리의 `.gitmodules`에서 해당 서브 모듈을 제거한다

```shell
# git submodule deinit -f <path>
$ git submodule deinit -f test
```

그 다음 부모 리포지토리에서 삭제할 서브 모듈의 디렉토리를 삭제한다

```shell
# git rm <path>
$ git rm test
```

변경 사항을 반영한다

```shell
$ git commit -m "remove test submodule"
```

