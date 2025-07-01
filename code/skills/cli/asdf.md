---
layout: default
title: agnostic한 도구로 런타임 버전을 관리해보자 (v0.16.7)
---

#### index

core concepts
- [asdf](#asdf)
- [adsf data directory ($HOME/.asdf)](#adsf-data-directory-homeasdf)
- [.tool-versions](#tool-versions)
- [environment variables](#environment-variables)
- [.asdfrc](#asdfrc)
  
commands
- [plugin](#plugin)
- [list](#list)
- [install](#install)
- [uninstall](#uninstall)
- [set](#set)
- [which, where](#which-where)
- [reshim](#reshim)
- [current](#current)
- [info](#info)
- [help](#help)


## asdf

asdf 또는 asdf-vm은 다중 런타임 버전 관리 도구로 다양한 프로그래밍 언어와 도구를 한 시스템에서 설치, 관리할 수 있는 도구이다 (시스템 패키지 매니저가 아님)

각종 언어에서 제공하는 CLI 버전 관리 도구를 사용하면 서로 다른 API와 구성 파일 등을 설정해야 하지만, asdf는 쉽게 개발 워크플로우를 설정할 수 있도록 단일 인터페이스와 구성 파일을 제공한다

런타임 버전 파일을 이용하면 모든 팀원들의 개발 환경과 CI/CD 파이프라인에서 동일한 버전을 사용하는 환경을 만들 수 있다

또한 asdf의 기본 제공 값 이외에도 플러그인 시스템을 통해 
다른 도구와 런타임을 추가할 수 있다

사용하는 방법
- asdf 설치 및 쉘 설정
- asdf를 통해 관리할 도구에 대한 플러그인 추가 (asdf는 플러그인 기반으로 동작함)
- 플러그인에서 제공하는 특정 버전 설치
- 설치한 버전 목록 중 사용할 버전 지정

asdf는 **동적으로 [.tool-versions](#tool-versions) 파일에 명시되어 있는 버전을 PATH 환경 변수에 설정**한다

따라서 사용자가 도구마다 특정 버전을 환경 변수에 추가할 필요가 없으며 특정 디렉토리마다 실행할 버전을 선택할 수도 있다

### installation

```shell
$ brew install asdf
```

### how it works

요약
- 터미널에서 명령을 실행하면 [심이 호출](#shims)된 후 asdf가 현재 디렉토리에서 상위 디렉토리로 올라가면서 `.tool-versions` 파일을 찾는다
- 발견되면 그 파일의 설정을 적용하고 없으면 글로벌 설정(`$HOME/.tool-versions`)을 참조하여 명시된 버전에 맞춰 PATH를 동적으로 업데이트한다
- 명령은 asdf에 의해 업데이트된 PATH를 참조하여 설정된 버전으로 실행된다

asdf가 제대로 작동하려면 코어 부분이 설치되고 사용 중인 쉘 통합 설정을 위해 asdf 스크립트를 실행해야 한다

```shell
# 직접 설치한 경우
$ ."$HOME/.asdf/asdf.sh"

# homebrew로 설치한 경우
$ $(brew --prefix asdf)/libexec/asdf.sh
```

asdf와 쉘 통합이 되면 디렉토리를 이동할 때마다 asdf가 `.tool-versions` 파일을 감지하고 환경을 동적으로 바꿀 수 있다

asdf 자체는 런타임을 직접 설치하지 않고 각 언어나 도구를 관리하기 위한 플러그인(github 리포지토리)을 기반으로 동작한다

즉, 특정 런타임을 다루려면 해당 플러그인을 asdf에 추가해야 하며 asdf가 이 플러그인을 인식하여 런타임을 관리할 수 있게 된다

각 플러그인은 해당 도구의 버전 목록을 가져오고, 설치/삭제를 처리하는 스크립트를 제공한다

```shell
# nodejs 플러그인은 node.js 공식 배포 사이트에서 버전을 다운로드하는 로직을 포함한다
$ asdf plugin add nodejs
$ sadf plugin add python
```

플러그인을 통해 도구(e.g java temurin-21.0.5+11.0.LTS)가 설치되면 그 도구의 실행 파일(java, javac, javap 등)마다 shim(심)이라는 중간 레이어가 생성된다

**심**은 실제 실행 파일을 직접 호출하지 않고 asdf가 중간에서 개입할 수 있게 하는 가벼운 스크립트이다

예를 들어 사용자가 `java --version`을 실행하면 심은 실제 `/usr/local/bin/java`가 아니라 `~/.asdf/shims/java`를 먼저 호출해서 asdf가 적절한 버전을 찾아 실행하도록 연결한다

```shell
# asdf의 shim이 시스템의 기본 실행 파일보다 우선 실행하도록 설정한다
export PATH="$HOME/.asdf/shims:$PATH"
```

심이 호출되면 asdf는 `.tool-versions` 파일을 확인하여 어떤 버전을 사용할지 결정하고, 그 버전의 실제 실행 파일을 호출한다

`.tool-versions` 파일은 사용할 런타임 버전을 명시하는 파일로 현재 디렉토리에 존재하면 명시된 해당 버전을 없으면 글로벌 설정(`~/.tool-versions`)을 참조한다

```shell
$ cat ~/.tool-versions
java temurin-21.0.5+11.0.LTS
python 3.13.2
lua 5.4.7
```


## adsf data directory ($HOME/.asdf)

`$HOME/.asdf` 디렉토리는 asdf의 데이터베이스로 플러그인, 런타임, 심 파일 등 asdf의 모든 로컬 데이터를 저장한다

어떤 방법으로 설치했든 asdf가 동작하면서 이 디렉토리를 기본 작업 공간으로 사용한다

```shell
$ tree -L 1 ~/.asdf
/Users/hansanhha/.asdf
├── installs
├── downloads
├── plugin-index
├── plugins
├── repository
├── shims
└── tmp
```

### installs

`.asdf/installs` 디렉토리에는 asdf를 통해 설치된 런타임의 실제 바이너리와 파일들이 저장된다

각 도구 이름과 버전별로 하위 디렉토리가 생성되고 바이너리 파일이 저장된다

이 디렉토리에 있는 실행 파일은 직접 호출되지 않고 `shims/`를 통해 간접적으로 사용된다

```shell
$ tree -L 2 ~/.asdf/installs
/Users/hansanhha/.asdf/installs
├── java
│   ├── liberica-21.0.5+11
│   ├── oracle-graalvm-21.0.5
│   └── temurin-21.0.5+11.0.LTS
├── lua
│   └── 5.4.7
└── python
    └── 3.13.2
```

### shims

`.asdf/shims` 디렉토리에는 실행 명령어(`java`, `javac` `python` 등)에 대한 심 파일이 저장된다

설치된 도구의 실행 파일마다 대응하는 심 파일이 생성되며, 각 심은 실제 런타임을 호출하기 전에 asdf가 `.tool-versions` 파일을 읽고 적절한 버전의 실제 바이너리를 선택하도록 중간에서 연결한다

또한 심은 동적으로 생성되며 도구를 설치하거나 제거할 때마다 업데이트된다

PATH에 `.asdf/shims`를 우선 적용하면 터미널에서 심을 호출한다

### plugins

`.asdf/plugins` 디렉토리는 도구를 관리하기 위해 설치한 플러그인들을 저장한다

각 플러그인은 도구 이름으로 된 디렉토리로 저장되며 내부에는 플러그인의 git 리포지토리에서 가져온 스크립트 파일을 포함한다

```shell
$ tree -L 2 ~/.asdf/plugins
/Users/hansanhha/.asdf/plugins
├── java
│   ├── LICENSE
│   ├── README.md
│   ├── bin
│   ├── data
│   ├── set-java-home.bash
│   ├── set-java-home.fish
│   ├── set-java-home.nu
│   ├── set-java-home.xsh
│   ├── set-java-home.zsh
│   └── update_data.bash
├── lua
│   ├── ...
└── python
    ├── ...
```

### plugin-index

`.asdf/plugin-index` 디렉토리는 asdf가 사용 가능한 플러그인 목록(플러그인 리포지토리 주소 목록)을 로컬에 캐싱한다

[asdf의 공식 플러그인 리포지토리](https://github.com/asdf-vm/asdf-plugins)의 최신 상태를 반영하며, 플러그인 설치나 업데이트 시 빠르게 참조할 수 있는 인덱스 역할을 한다

인터넷 연결 없이도 플러그인 목록을 빠르게 확인할 수 있게 해준다

`asdf plugin update --all`을 실행하면 이 디렉토리가 최신 상태로 갱신된다

기본적으로 1시간마다 플러그인 리포지토리를 동기화하며 `.asdfrc`의 `plugin_repository_last_check_duration` 설정으로 조정할 수 있다

### repository (legacy)

`.asdf/repository` 디렉토리도 plugin-index 디렉토리 처럼 asdf의 공식 플러그인 리포지토리를 로컬에 캐싱한다

과거 버전(0.15.x 이하, asdf-bash)에서 플러그인 목록을 저장하던 디렉토리로 사용했으며 호환성을 위해 남겨두었다


### downloads

`.asdf/downloads` 디렉토리는 런타임 설치 시 다운로드한 원본 파일(`tar.gz` `zip`)을 캐싱한다

도구와 버전별로 정리되며, 동일 버전을 재설치할 때 로컬 캐시를 사용하여 성능을 최적화한다


## .tool-versions

`.tool-versions` 파일은 보통 프로젝트 루트 디렉토리나 홈 디렉토리에 위치하여 asdf에게 어떤 버전의 도구를 사용할지 알려주는 역할을 한다

### related-commands

`asdf set <tool-name> <version>`: 현재 디렉토리에 `.tool-versions` 파일을 생성하여 버전을 지정하거나 업데이트한다 [set 명령](#set)

`asdf install`: `.tool-versions` 파일을 읽고 필요한 모든 도구를 설치한다 [install 명령](#install)

### syntax

첫 번째 버전이 기본으로 사용되며 그 이후의 버전은 선택적으로 명시할 수 있는 대체 옵션이다

```plaintext
# comment
<tool-name> <version> [<version 2>]
```

```shell
# 홈 디렉토리에서 java를 호출하면 temurin-21.0.5+11.0.LTS 버전이 실행된다
$ cat ~/.tool-versions
java temurin-21.0.5+11.0.LTS
python 3.13.2
lua 5.4.7
```

특정 디렉토리에 `.tool-versions` 파일에 다른 버전을 명시하면 해당 버전으로 실행 가능한 파일이 동적으로 선택된다

```shell
# ~./asdf-test 디렉토리의 자바 버전
$ echo "java oracle-graalvm-21.0.5" > .tool-versions
$ java --version
java 21.0.5 2024-10-15 LTS
Java(TM) SE Runtime Environment Oracle GraalVM 21.0.5+9.1
Java HotSpot(TM) 64-Bit Server VM Oracle GraalVM 21.0.5+9.1 

# 홈 디렉토리의 자바 버전
$ ~
$ java --version
openjdk 21.0.5 2024-10-15 LTS
OpenJDK Runtime Environment Temurin-21.0.5+11
OpenJDK 64-Bit Server VM Temurin-21.0.5+11 
```

또한 `system` 키워드로 시스템에 설치된 기본 버전을 사용하거나 git의 태그나 브랜치를 직접 참조(`ref`)할 수 있다 (플러그인 지원 필요)

```plaintext
# asdf가 관리하는 버전 대신 시스템에 설치된 버전 사용
nodejs system    

# node.js 20.11.0 git 태그 사용
nodejs ref:20.11.0
```


## environment variables

asdf는 환경 변수를 통해 adsf의 동작을 커스터마이징할 수 있도록 지원한다

`asdf info` 명령을 통해 asdf에 설정된 환경 변수 값을 확인할 수 있다

`ASDF_DEFAULT_TOOL_VERSIONS_FILENAME`
- asdf가 도구와 버전을 읽는 기본 파일 이름을 지정하는 환경 변수
- 기본값: `.tool-versions`
- 현재 디렉토리와 상위 디렉토리에서 지정된 이름의 파일을 검색하고, 없으면 `$HOME/` 아래의 동일 이름 파일을 글로벌 설정으로 참조한다

`ASDF_DATA_DIR`
- asdf의 데이터 디렉토리 경로를 지정하는 환경 변수
- 기본값: `$HOME/.asdf`

`ASDF_CONFIG_FILE`
- asdf 설정 파일(.asdfrc)의 위치를 지정하는 환경 변수
- 기본값: `$HOME/.asdfrc`


## .asdfrc

`.asdfrc` 파일은 zshrc, vimrc 파일처럼 기본 동작이나 특정 환경에 맞는 세부 옵션을 조정한다

기본적으로 사용자 홈 디렉토리(`$HOME/.asdfrc`)에 위치하며 만약 없으면 asdf는 내부적으로 정의된 기본 설정을 사용한다

이 파일은 만드는 명령이 따로 제공되지 않고 사용자가 명시적으로 만들어야 한다

파일의 내용은 키-값 쌍(`key = value`)으로 구성된다

asdf가 실행될 때 `$HOME/.asdfrc` 파일을 읽어서 설정을 적용한다 (asdf의 모든 명령어에 영향을 끼침)

`.asdfrc`의 설정은 기본값을 덮어쓰지만 환경 변수(`ASDF_DATA_DIR` 등)가 `.asdfrc` 파일보다 더 높은 우선순위를 가진다

주요 설정

```plaintext
# asdf가 .tool-versions 외에 다른 도구별 레거시 버전 파일을 인식한다 (nvm, rbenv 같은 도구에서 마이그레이션할 때 유용함)
# .node-version, .ruby-version 등
legacy_version_file = yes



# 플러그인 저장소(plugin-index)를 동기화하는 주기를 분 단위로 설정한다 (1440 = 하루)
# 기본값(60분) 
plugin_repository_last_check_duration = 1440



# asdf 업데이트 시 릴리즈 후보(RC) 버전을 포함한다
use_release_candidates = yes



# 작업을 병렬로 실행할 코어 수 2개로 지정한다
# 시스템 코어 수에 따라 동적으로 결정 필요
jobs = 2


# asdf 데이터 디렉토리를 사용자 지정 경로로 변경한다
# 기본값: `$HOME/.asdf`
asdf_data_dir = /custom/asdf-data
```


## plugin

`adsf plugin` 명령은 asdf에 특정 플러그인을 추가, 제거 등 관리하는 데 사용된다

플러그인은 asdf의 공식 플러그인 리포지토리 (로컬 asdf 데이터 디렉토리의 [plugin-index](#plugin-index))에서 가져오며 `.asdf/plugins/` 디렉토리에 저장된다

각 플러그인은 해당 도구에 대한 설치, 관리 스크립트 등을 제공하여 사용자가 asdf를 통해 버전을 관리할 수 있도록 한다

### plugin commands

기본 명령 형식: `asdf plugin <subcommand> [arguments]` 

조회
- `asdf plugin list`: 현재 설치된 플러그인 목록을 표시한다
- `asdf plugin list all`: 공식 플러그인 리포지토리에서 사용 가능한 모든 플러그인 목록을 표시한다

관리
- `asdf plugin add <name> [git-url]`: git 리포지토리 주소를 생략하면 공식 플러그인 리포지토리에서 플러그인을 가져와 asdf에 추가한다 -> 공식 리포지토리에서 지원하지 않더라도 커스텀 플러그인 리포지토리를 통해 버전을 관리할 수 있음
- `asdf plugin update <name>`: 특정 플러그인을 최신 버전으로 업데이트한다 (해당 플러그인의 리포지토리를 최신 커밋으로 갱신함)
- `asdf plugin remove <name>`: 추가한 플러그인을 제거한다


## list

`asdf list` 명령은 지정된 옵션에 따라 다양한 목록을 표시한다
- 설치된 도구 목록 표시 (`.asdf/installs`)
- 특정 도구의 설치된 버전 목록 표시 (`.asdf/installs/<tool>`)
- 특정 도구의 설치 가능 버전 목록 표시  (플러그인 스크립트 호출)

### list commands

`asdf list`: 현재 시스템에 설치된 모든 도구와 그 버전 목록을 표시한다

`asdf list <tool> [<version-filter>]`: 지정된 도구에 대해 설치된 버전 목록을 표시한다 (현재 디렉토리의 `.tool-versions`에 설정된 버전에 `*` 표시) version-filer 인자가 주어지면 해당 패턴에 맞는 버전만 필터링한다

`asdf list all <tool>`: 지정된 도구의 플러그인이 지원하는 모든 설치 가능한 버전을 표시한다 (플러그인이 설치되어 있어야 하며 버전 설치 여부와 관계없음) `.asdf/plugins/<tool>/bin/list-all` 스크립트를 호출한다


## install

`asdf install` 명령은 특정 도구의 특정 버전을 로컬 시스템에 설치한다

각 도구의 설치 로직은 `.adsf/plugins/<tool>/bin/` 디렉토리에 있는 스크립트(`install` 등)에 의해 실행된다

이 스크립트는 플러그인이 정의한 소스(node.js 공식 사이트, python ftp 등)에서 바이너리를 다운로드하고 `.asdf/installs/<tool>/<version>/` 디렉토리에 저장한다

설치 후 `.asdf/shims/` 디렉토리에 해당 도구의 심 파일(`java` `node` `npm` 등)을 생성한다

이미 설치된 버전은 재설치하지 않고 건너뛰며 [.asdfrc](#asdfrc) 파일의 jobs 설정에 따라 여러 버전을 동시에 설치할 수 있다

설치된 버전은 [.asdf/installs](#installs) 디렉토리에 저장되며 이후 [.tool-versions](#tool-versions) 파일을 통해 참조할 수 있다

### install, uninstall commands

기본 명령 형식: `asdf install [<tool> <version>]`

설치
- `adsf install <tool> <version>`: 지정된 도구의 특정 버전을 로컬 시스템에 설치한다
- `asdf install`: 현재 디렉토리의 `.tool-versions` 파일을 읽고 명시된 모든 도구와 버전을 설치한다
- `asdf install <tool> latest`: 해당 도구의 최신 버전을 설치한다


## uninstall

`asdf uninstall` 명령은 설치된 도구의 특정 버전을 시스템에서 제거한다

`.asdf/installs/` 디렉토리에 있는 해당 버전이 존재하는지 확인하고 디렉토리 전체를 제거한다 (복구 불가)

삭제 후 `asdf`가 더 이상 해당 버전을 참조하지 않도록 심을 업데이트한다

`asdf install` 명령과 달리 별도의 플러그인 로직없이 asdf 차원에서 단순 디렉토리를 삭제하는 것으로 동작한다

`asdf uninstall <tool> <version>`: 해당 도구의 특정 버전을 삭제한다


## set

`asdf set` 명령은 `.tool-versions` 파일에 도구와 버전을 설정한다

`asdf set <tool> <version>`: 현재 디렉토리의 `.tool-versions` 파일에 지정한 도구와 버전을 설정한다 (파일 생성 포함)

`asdf set -u <tool> <version>`: 사용자 홈 디렉토리의 `.tool-versions` 파일에 지정한 도구와 버전을 설정한다

`asdf set -p <tool> <version>`: 상위 디렉토리 중 가장 가까운 `.tool-versions` 파일을 찾아 설정한다


## which, where

`asdf which`와 `asdf where` 명령은 현재 사용 중인 런타임의 세부 정보를 확인하는데 사용된다

`asdf which <tool>`: 특정 도구에 대해 현재 활성화된 버전의 실행 파일 경로를 반환한다 (심 파일이 아닌 설치된 버전의 실제 바이너리 파일의 절대 경로) `.tool-versions`에 `system`으로 설정된 경우 시스템 경로를 반환한다

`asdf where <tool> [<version>]`: 특정 도구와 버전에 대한 설치 디렉토리 경로를 반환한다 (버전이 생략되면 현재 디렉토리의 `.tool-versions` 참고)


## reshim

`asdf reshim` 명령은 심 파일을 다시 생성하거나 업데이트한다

asdf에 설치된 도구의 실행 파일에 대해 `.asdf/shims/` 디렉토리에 있는 해당 심 파일을 재생성하거나 동기화하여 런타임과 심이 일치하도록 조정한다

asdf는 각 도구의 특정 버전을 동적으로 관리하기 위해 중간 레이어인 심 파일을 이용하는데, 심이 올바르게 동작하도록 보장하기 위해 이 명령을 사용한다

런타임 설치 후 새로운 실행 파일(`npm` `pip` 등)이 추가되거나 수동으로 파일을 수정했을 때 심이 최신 상태를 반영하지 못할 수 있으며 asdf가 자동 심 생성에 실패하거나 누락된 경우 수동으로 조절할 필요가 있다

`asdf reshim`: 설치된 모든 도구의 모든 버전에 대해 심을 전부 재생성한다

`asdf reshim <tool>`: 특정 도구의 모든 버전에 대해 대해서만 심을 재생성한다

`asdf reshime <tool> <version>`: 특정 도구의 특정 버전에 대해서만 심을 재생성한다

## current

`asdf current` 명령은 현재 로컬 시스템에 추가된 플러그인과 현재 활성화된 버전 및 설치 여부 목록을 출력한다

asdf는 심을 이용해 동적으로 실행할 도구의 버전을 선택할 수 있는데 현재 디렉토리에 설정된 런타임을 확인할 때 사용한다

`asdf current`: 현재 디렉토리에서 실행될 모든 도구에 대한 버전 정보를 출력한다

`asdf current <tool>`: 현재 디렉토리에서 실행될 지정된 도구에 대한 버전 정보를 출력한다

```shell
$ asdf current
Name            Version               Source                                    Installed
golang          ______                ______
java            oracle-graalvm-21.0.5 /Users/hansanhha/asdf-test/.tool-versions true
lua             5.4.7                 /Users/hansanhha/.tool-versions           true
python          3.13.2                /Users/hansanhha/.tool-versions           true
```


## info

`asdf info` 명령은 asdf 자체에 대한 정보와 시스템 환경에 대한 정보를 출력한다

출력 정보
- 운영체제
- 쉘
- asdf 버전/환경 변수/설치된 플러그인

```shell
$ asdf info
OS:
Darwin localhost.local 24.3.0 Darwin Kernel Version 24.3.0: Thu Jan  2 20:24:06 PST 2025; root:xnu-11215.81.4~3/RELEASE_ARM64_T8103 arm64

SHELL:
zsh 5.9 (arm64-apple-darwin24.0)

BASH VERSION:
3.2.57(1)-release

ASDF VERSION:
0.16.7

ASDF INTERNAL VARIABLES:
ASDF_DEFAULT_TOOL_VERSIONS_FILENAME=.tool-versions
ASDF_DATA_DIR=/Users/hansanhha/.asdf
ASDF_CONFIG_FILE=/Users/hansanhha/.asdfrc

ASDF INSTALLED PLUGINS:
golang    https://github.com/asdf-community/asdf-golang.git e2527a31714da7bc671a684308579f4ef8863281
java      https://github.com/halcyon/asdf-java.git          6dd80f3108e65fc333b9836aa2485da7565a125f
lua       https://github.com/Stratus3D/asdf-lua.git         1ebd84967ed6b7aefeb4300cfcb211f414be0226
python    https://github.com/danhper/asdf-python.git        a3a01856098d6d2b9642e382f5b38e70275726d1
```


## help

`asdf help` 명령은 asdf 버전 및 asdf에서 사용 가능한 명령어 목록을 출력한다

0.16.7 버전 기준 지원되는 명령어 목록

```shell
$ asdf help
COMMANDS:
   cmd
   completion
   current
   env
   exec
   help
   info
   version
   install
   latest
   list
   plugin
   reshim
   set
   shimversions
   uninstall
   update
   where
   which
```
