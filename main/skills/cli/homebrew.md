---
layout: default
title: homebrewwwwwwww
---

#### index
- [what is homebrew](#what-is-homebrew)
- [components](#components)
- [installation](#installation)
- [homebrew managing commands](#homebrew-managing-commands)
  - [brew update](#brew-update)
  - [brew doctor](#brew-doctor)
  - [brew config](#brew-config)
  - [brew shellenv](#brew-shellenv)
  - [brew tap/untap](#brew-tapuntap)
- [package managing commands](#package-managing-commands)
  - [brew install](#brew-install)
  - [brew outdated](#brew-outdated)
  - [brew upgrade](#brew-upgrade)
  - [brew uninstall, brew remove](#brew-uninstall-brew-remove)
  - [brew cleanup](#brew-cleanup)
  - [brew list](#brew-list)
  - [brew leave](#brew-leave)
  - [brew info](#brew-info)
  - [brew deps](#brew-deps)
- [brew files](#brew-files)
- [other package managers](#other-package-managers)


## what is homebrew

홈브류는 오픈소스 패키지 관리자로 맥이랑 리눅스에서 사용할 수 있다(wsl2 포함)

소프트웨어를 "포뮬러(formulae)"라는 형태로 관리한다

포뮬러는 소프트웨어 패키지(라이브러리, CLI 도구)의 설치 방법을 정의한 ruby 스크립트로 홈브류가 이를 읽고 필요한 파일을 다운로드하고 컴파일하여 설치한다


## components

Homebrew Core: 기본적으로 제공되는 소프트웨어 패키지 모음

Cask: gui 애플리케이션(visual studio code, google chrome 등)이나 대규모 바이너리 파일을 설치할 수 있는 확장 기능

Tap: 사용자가 커스텀 포뮬러를 추가하거나 타인의 저장소를 연결할 수 있는 기능


## installation

아래의 스크립트는 홈브류 설치 스크립트를 다운로드하고 필요한 환경을 설정한다

```plaintext
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
```

설치 후 `brew doctor` 명령어로 설치 상태를 점검할 수 있다

```shell
$ brew doctor
Your system is ready to brew.
```


## homebrew managing commands

### brew update

`brew update` 명령은 홈브류 자체를 업데이트한다

홈브류의 git 리포지토리 (`homebrew/homebrew-core` `homebrew/homebrew-cask` 등)을 최신 커밋으로 동기화한다

로컬의 홈브류를 최신 상태로 갱신하면서 변경 사항을 사용자에게 알려준다

출력하는 정보 (변경 사항)
- New Formulae: 홈브류 기본 리포지토리(`homebrew/core`)에 새롭게 추가된 포뮬러 목록
- New Casks: 홈브류의 `homebrew/cask` 리포지토리에 새로 추가된 캐스크 목록
- Outdated Formulae: 현재 시스템에 설치된 포뮬러 중 최신 버전보다 오래된 버전을 사용하고 있는 패키지 목록 (업그레이드 가능)

```shell
$ brew update
==> Updating Homebrew...
Updated 2 taps (homebrew/core and homebrew/cask).
==> New Formulae
geesefs           lld@19            llvm@19
==> New Casks
font-lxgw-wenkai-gb-lite   moment
inmusic-software-center    slidepad
==> Outdated Formulae
asdf         glib         neovim       sqlite
fzf          harfbuzz     pcre2        xz
git          icu4c@76     python@3.13  zoxide
```

### brew doctor

`brew doctor` 명령은 홈브류 설치 상태와 환경을 진단해 문제를 찾아준다 (PATH 설정, 권한 문제, 충돌 가능성 등 체크)

설치가 됐는지 확인하거나 문제가 발생했을 때 사용한다

```shell
$ brew doctor
Your system is ready to brew.
```

### brew config

`brew config` 명령은 홈브류의 설정 정보와 시스템 환경을 출력한다

홈브류 버전, git 상태, 의존성 등을 출력한다

```shell
$ brew config
HOMEBREW_VERSION: 4.4.26
ORIGIN: https://github.com/Homebrew/brew
HEAD: f1041842141f20bc701b522d4371d7843d905580
Last commit: 5 days ago
Branch: stable
Core tap JSON: 29 Mar 05:00 UTC
Core cask tap JSON: 29 Mar 05:00 UTC
HOMEBREW_PREFIX: /opt/homebrew
HOMEBREW_CASK_OPTS: []
HOMEBREW_EDITOR: vim
HOMEBREW_MAKE_JOBS: 8
Homebrew Ruby: 3.3.7 => /opt/homebrew/Library/Homebrew/vendor/portable-ruby/3.3.7/bin/ruby
CPU: octa-core 64-bit arm_firestorm_icestorm
Clang: 16.0.0 build 1600
Git: 2.48.1 => /opt/homebrew/bin/git
Curl: 8.7.1 => /usr/bin/curl
macOS: 15.3.2-arm64
CLT: 16.2.0.0.1.1733547573
Xcode: N/A
Rosetta 2: false
```

### brew shellenv

`brew shellenv` 명령은 홈브류가 설치된 환경에서 쉘을 올바르게 설정하기 위해 필요한 환경 변수를 출력한다 (현재 사용하는 쉘(zsh, bash, fish)에 맞는 환경 변수 설정 명령어를 출력함)

주로 새로운 쉘 환경에서 홈브류의 실행 파일과 의존성을 시스템에서 인식되도록 PATH 및 기타 환경 변수를 조정할 때 사용된다

`export PATH="/opt/homebrew/opt/ruby/bin:$PATH"` 명령을 쉘 실행 파일(`~/.zshrc` 등)에 넣으면 홈브류 및 홈브류로 설치된 패키지의 경로를 PATH 환경 변수에 설정할 수 있다

### brew tap/untap

`brew tap`과 `brew untap` 명령은 홈브류에서 외부 저장소(tap)를 추가하거나 제거한다

기본 저장소(`homebrew/homebrew-core`, `homebrew/homebrew-cask`) 외에 다른 소프트웨어 패키지나 특정 버전을 설치할 수 있는 git 리포지토리를 연결할 수 있다

`brew tap <repository>`: git 리포지토리 추가 (일반적으로 깃허브 `user/repo` 형식으로 추가함)

`brew untap <repository>`: 추가한 tap 리포지토리 제거

tap 명령을 사용하면 지정된 리포지토리를 로컬의 `/opt/hombrew/Library/Taps` (실리콘)에 복제한다

이후 `brew install`로 해당 리포지토리의 패키지를 검색하거나 설치할 수 있다

홈브류에서 제공하는 tap 리포지토리 
- homebrew/linux-fonts


## package managing commands

### brew install

`brew install` 명령은 지정한 패키지를 홈브류 리포지토리에서 다운로드하고 컴파일(필요 시)하여 로컬 시스템에 설치한다 (홈브류 기본 리포지토리 또는 tab)

포뮬러(formulae)
- CLI 도구 또는 라이브러리
- `/opt/homebrew/Celler` 디렉토리에 설치된다
- bottle 이라고 하는 미리 컴파일된 바이너리를 사용한다

캐스크(cask)
- GUI 애플리케이션 
- `/Applications` 디렉토리 또는 사용자 지정 경로에 설치된다
- `.dmg` `.pkg` 등의 설치 파일을 다운로드한다

`brew install <package>`: 지정한 패키지를 설치한다

`brew install <package1> <package2> ...`: 지정한 여러 패키지를 한 번에 설치한다

`brew install --cask [--appdir=<path>]<package>`: 지정한 캐스크를 설치한다 (설치 경로 지정 가능, 기본값: `/Applications`)

`brew install -f <package>`: 기존 설치 무시하고 강제로 재설치한다

`brew install -v`: 설치 과정의 상세 로그를 출력하면서 설치한다

`brew install --build-from-source`: 미리 컴파일된 바이너리 대신 소스 코드로부터 빌드한다

`brew install --HEAD <package>`: 최신 개발 버전을 설치한다

### brew outdated

`brew outdated`는 설치된 패키지의 버전과 홈브류 리포지토리의 최신 버전을 비교하여 업데이트 필요 여부를 알려준다

`brew outdated`: 버전을 비교하여 오래된 패키지의 이름만 나열한다

`brew outdated <package>`: 특정 패키지를 확인한다

```shell
$ brew outdated
==> Downloading https://formulae.brew.sh/api/formula.
==> Downloading https://formulae.brew.sh/api/cask.jws

fzf (0.58.0) < 0.60.3
git (2.48.1) < 2.49.0
glib (2.84.0) < 2.84.0_1
```

### brew upgrade

`brew upgrade` 명령은 설치된 패키지의 버전을 홈브류 리포지토리의 최신 버전으로 업그레이드한다

`brew upgrade`: 업그레이드 가능한 모든 패키지를 업그레이드한다

`brew upgrade <package>`: 특정 패키지를 업그레이드한다

`brew upgrade <package1> <package2> ...`: 여러 패키지를 업그레이드한다

`brew upgrade --dry-run`: 실제 업그레이드하지 않고 시뮬레이션만 하여 어떤 패키지가 업데이트될지 확인한다

### brew uninstall, brew remove

`brew uninstall`과 `brew remove` 명령은 설치된 패키지를 삭제한다

두 명령어는 동일한 기능을 수행한다 (brew remove가 brew uninstall의 별칭임)

`brew uninstall <package>`: 특정 패키지를 제거한다

`brew uninstall <package1> <package2> ...`: 여러 패키지를 제거한다

`brew uninstall --cask <package>`: 특정 캐스크를 제거한다

`brew uninstall -f <package>`: 모든 버전을 강제로 제거한다 (포뮬러에 여러 버전이 설치된 경우)

`brew uninstall --cask --zap <package>`: 캐스크 제거 시 관련 설정 파일까지 완전히 제거한다 (일반 제거는 앱만 제거함)

### brew cleanup

`brew cleanup` 명령은 홈브류가 관리하는 디렉토리에서 오래된 패키지 버전과 캐시를 삭제하여 공간을 정리한다

삭제 대상
- `/opt/homebrew/Cellar`에 남아 있는 오래된 버전의 포뮬러
- 캐시 파일 (~/Library/Caches/Hombrew)
- 캐스크 관련 임시 파일

`brew cleanup`: 포뮬러 및 캐시 전체 정리(캐스크 제외)

`brew cleanup <package>`: 특정 포뮬러 패키지에 대한 정리

`berw cleanup --cask`: 캐스크 정리

`brew cleanup --prune=<day>`: 지정된 일수보다 오래된 캐시 파일만 삭제한다

`brew cleanup --dry-run`: 실제 삭제 없이 시뮬레이션하여 정리될 파일과 공간만 확인한다

### brew list

`brew list` 명령은 설치된 포뮬러와 캐스크 목록을 출력한다

설치된 파일의 위치: `/opt/homebrew/Cellar`

`brew list --version`: 설치된 패키지의 버전을 함께 표시한다

`brew list --formula`: 포뮬러 목록만 나열한다

`brew list --cask`: 캐스크 목록만 나열한다

`brew list <package>`: 지정한 패키지의 모든 디렉토리 출력한다

`brew list --pinned`: 고정된 패키지 목록(업데이트를 막은 패키지)을 출력한다

### brew leave

`brew leave` 명령은 의존성으로 인해 자동 설치된 패키지를 제외하고 사용자가 직접 설치한 패키지를 나열한다

포뮬러에만 적용되며 캐스크는 포함되지 않는다 

### brew info

`brew info` 명령은 지정한 패키지에 대한 설치 여부, 설치 경로, 의존성, 설명 등을 포함한 상세 정보를 제공한다

설치되지 않은 패키지도 정보 출력을 할 수 있다

`brew info <package>`: 지정한 패키지의 상세 정보를 출력한다

`brew info`: 설치된 모든 패키지의 요약 정보를 출력한다

`brew info --json <package>`: json 형식으로 출력한다

```shell
$ brew info tmux
==> tmux: stable 3.5a (bottled), HEAD
Terminal multiplexer
https://tmux.github.io/
Installed
/opt/homebrew/Cellar/tmux/3.5a (10 files, 1.2MB) *
  Poured from bottle using the formulae.brew.sh API on 2025-03-28 at 22:36:53
From: https://github.com/Homebrew/homebrew-core/blob/HEAD/Formula/t/tmux.rb
License: ISC
==> Dependencies
Build: pkgconf ✘
Required: libevent ✔, ncurses ✔, utf8proc ✔
==> Options
--HEAD
        Install HEAD version
==> Caveats
Example configuration has been installed to:
  /opt/homebrew/opt/tmux/share/tmux
==> Analytics
install: 21,113 (30 days), 59,846 (90 days), 321,942 (365 days)
install-on-request: 19,812 (30 days), 57,180 (90 days), 314,442 (365 days)
build-error: 14 (30 days)
```

### brew deps

`brew deps` 명령은 지정한 패키지의 의존성을 나열한다

`brew info` 명령과 마찬가지로 설치되지 않은 패키지에 대한 정보도 나타낼 수 있다

`brew deps <package>`: 지정한 패키지 의존성을 출력한다

`brew deps --installed`: 설치된 모든 패키지의 의존성을 나열한다

`brew deps --direct <package>`: 직업적인 의존성만 표시한다

`brew deps --tree <package>`: 의존성을 트리 형태로 표시한다

`brew deps --include-build <apckage>`: 빌드 시 반드시 필요한 의존성을 출력한다

```shell
$ brew deps --tree tmux
tmux
├── libevent
│   └── openssl@3
│       └── ca-certificates
├── ncurses
└── utf8proc
```


## brew files

여러 패키지를 한 번에 설치하거나 기존 환경을 복원하기 위해 (이식 가능한 환경 구성) 포뮬러 및 캐스크 목록을 작성한 뒤 `brew install`로 전달하여 한꺼번에 설치할 수 있다

포뮬러 및 캐스크 목록 파일 생성 

```shell
# 현재 사용하고 있는 포뮬러 목록 저장
$ brew leaves > formulae

# 현재 사용하고 있는 캐스크 목록 저장
$ brew list --cask > casks
```

포뮬러 및 캐스크 설치

```shell
# 포뮬러 설치
# -r: 일부 패키지가 설치 실패해도 계속 진행하게 한다
# -P: 최대 n개의 프로세스를 병렬로 실행하게 한다
$ cat formulae | xargs -r -P 4 brew install

# 캐스크 설치
$ cat casks | xargs -r brew install --cask

# 설치 과정 기록
$ cat formulae | xargs -r -P 4 brew install 2>&1 | tee install_log.txt
```


## other package managers

macos
- MacPorts
- Fink
- Nix
- pkgsrc
  
linux
- apt (debian/ubuntu)
- dnf/yum (fedora/rhel)
- pacman (arch linux)
  
windows
- chocolatey
- Scoop
- winget