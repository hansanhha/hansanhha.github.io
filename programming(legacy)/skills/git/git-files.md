---
layout: default
title:
---

#### index
- [.gitignore](#gitignore)
- [.gitattributes](#gitattributes)
- [.gitkeep](#gitkeep)
- [.gitmodules](#gitmodules)
- [.mailmap](#mailmap)


git은 리포지토리 관리와 작업 흐름을 효율적으로 만들기 위해 `.git` 디렉토리 외에도 프로젝트 루트에 특정 설정 파일들을 사용한다 


## .gitignore

`.gitignore` 파일은 프로젝트 루트 또는 하위 디렉토리에 위치하며 git이 추적하지 않을 파일이나 패턴을 지정한다 (불필요한 파일 또는 민감한 정보)

하위 디렉토리에 두면 해당 경로에만 적용된다

패턴: *(와일드카드), !(예외), /(경로 구분)

```plaintext
# 로그 파일 무시
*.log

# 빌드 디렉토리 무시
/build/

# 특정 파일 무시
secrets.txt
```

참고로 이미 추적 중인 파일은 무시되지 않으며 제거하려면 `git rm --cached` 명령 실행이 필요하다


## .gitattributes

`.gitattributes` 파일은 프로젝트 루트 또는 하위 디렉토리에 위치하며 파일 속성을 정의하여 git의 동작을 커스터마이징한다

줄 끝 처리 통일, 병합 전략 등을 설정할 수 있다

```plaintext
# 텍스트 파일의 줄 끝을 LF로 강제한다
.txt eol=lf

# png 파일을 바이너리 파일로 처리
.png binary

# 커스텀 병합 드라이버 지정
config.xml merge=ours
```

`eol=lf` 또는 `eol=crlf`를 설정하여 줄 끝 형식을 통일하면 os 플랫폼 간 충돌을 방지할 수 있다

`binary`: 파일을 바이너리로 인식시켜서 `diff/merge`를 비활성화한다

`merge`: 병합 전략을 지정하며 `ours`는 로컬 우선 병합 전략을 의미한다


## .gitkeep

git은 기본적으로 비어 있는 디렉토리를 추적하지 않는다

`.gitkeep` 파일은 git이 빈 디렉토리 추적을 할 수 있도록 관례적으로 사용하는 비어 있는 파일이다

실제 git 기능과 무관하며, 대안으로 `.gitignore`에 주석만 넣는 방법도 있다


## .gitmodules

`.gitmodules` 파일은 프로젝트 루트에 위치하며 서브 모듈을 정의한다

외부 저장소를 하위 모듈로 포함할 때 사용한다

```plaintext
[submodule "lib"]
    path = lib
    url = https://github.com/user/ilb.git
```

`git submodule add`로 자동 생성할 수 있다


## .mailmap

`.mailmap` 파일은 프로젝트 루트에 위치하며 커밋 작성자 이름과 이메일을 매핑해 통일한다

```plaintext
hansanhha <xxxxxx@xxxxxx.com> <yyyyyy@yyyyyy.com>
```

여러 이메일을 하나로 정리할 수 있으며 `git log`나 `git blame`에서 동일한 사용자로 표시한다