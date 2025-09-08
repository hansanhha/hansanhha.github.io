---
layout: default
title:
---

#### index
- [project](#project)
- [project environment](#project-environment)
- [pip](#pip)
- [pipx](#pipx)
- [venv](#venv)
- [poetry](#poetry)
- [conda](#conda)


## project

파이썬 프로젝트란 특정 기능을 수행하는 하나의 독립된 작업 단위를 의미하며, 여러 개의 모듈과 패키지 및 환경 설정 등을 포함한다

파이썬의 코드 단위
- 스크립트: 독립적으로 실행 가능한 .py 파일
- 모듈: 함수, 클래스, 변수를 포함하는 .py 파일
- 패키지: 여러 개의 모듈을 포함하는 디렉토리 (`__init__.py` 포함)
- 프로젝트: 전체 코드 베이스

`.py` 파일에서 import하면 파이썬은 `sys.path`를 참조하여 아래와 같은 순서로 모듈을 탐색한다

#### 1. current workding directory

```python
import my_module
```

파이썬은 실행된 python 파일과 같은 경로에 있는 모듈을 우선적으로 찾는다

`/home/user/my_project/main.py` 파일에서 위와 같이 my_module을 import하면 `/home/user/my_project/` 내부를 먼저 검색한다

#### 2. PYTHONPATH os environment variable

운영체제 환경 변수 중 PYTHONPATH가 설정되어 있다면 해당 경로에서 검색한다

사용자는 특정 디렉토리를 모듈 검색 경로로 추가할 수 있다

```shell
export PYTHONPATH="/home/user/custom_modules"
```

```python
import my_custom_module # /home/user/custom_modules에서 모듈 탐색
```

#### 3. stdlib

```python
import math
```

파이썬 내장 모듈(os, sys, math 등)은 sys.path의 기본 경로인 표준 라이브러리 디렉터리에서 탐색된다

#### 4. site-packages

사용자가 의존성 관리 도구로 설치한 패키지들이 저장되는 디렉토리이다

```python
import requests  # pip로 설치한 패키지
```

시스템(전역) site-packages와 가상 환경 site-packages로 나뉘며 각각 서로 다른 경로에 위치한다 (macos 기준)

시스템 site-packages: `/usr/local/lib/pythonX.X/site-packages` 또는 '/Users/username/.asdf/installs/python/X.X.X/lib/pythonX.X/site-packages` (asdf 사용 시)

가상 환경 site-packages: `my_project/lib/pythonX.X/site-packages`


## project environment

프로젝트 환경이란 파이썬 코드가 실행되는 공간을 의미한다

환경에는 파이썬 인터프리터, 패키지, 의존성, 설정 파일 등이 포함된다

환경을 관리한다는 것은 프로젝트 간 패키지 버전 충돌을 방지하고, 다른 환경(다른 호스트/cicd 파이프라인 등)에서도 문제없이 실행되게끔 일관적인 실행 환경을 가꾸는 것을 말한다

파이썬의 환경은 크게 두 개로 구분된다

#### global environment 

시스템 환경(전역)은 호스트에 설치된 파이썬 환경이며 시스템 전역에 걸쳐서 작용하며 os가 기본적으로 사용하는 파이썬을 말한다

`pip install`로 설치한 패키지가 전체 시스템에 적용되기 때문에 시스템 오류를 일으키지 않도록 유의해야 한다

여러 프로젝트에서 전역 환경을 공유하면 의존성 충돌 문제가 발생할 수 있다

또한 os 업데이트로 인해 패키지가 삭제될 수도 있다

#### virtual environment

가상 환경은 프로젝트마다 독립적인 파이썬 환경을 말한다

venv, virtualenv, conda 등의 도구를 사용하여 가상 환경을 생성하고 해당 환경 내에 의존성을 설치하여 다른 환경과의 버전 충돌을 방지하며 시스템 환경을 보호한다 

개발 환경과 배포 환경을 동일하게 유지할 수 있다


## pip

pip(package installer for python)는 파이썬에 내장된 공식 패키지 관리 도구이다

주요 기능
- pypi(python package index)에서 패키지 설치
- 패키지 의존성 자동 해결
- 설치된 패키지 목록 확인
- 버전 지정 설치
- 패키지 업그레이드

pip는 파이썬 3.4 이상부터 기본적으로 내장된다

```shell
$ pip --version

# pip 24.3.1 from /Users/hansanhha/.asdf/installs/python/3.13.2/lib/python3.13/site-packages/pip (python 3.13)
```

pip 버전 업그레이드

```shell
pip install --upgrade pip
```

가상 환경이 아닌 환경에서 pip를 통해 패키지를 설치하면 전역 환경에 패키지가 설치된다

전역 환경에 설치된 패키지는 모든 프로젝트에서 공유되므로 패키지 버전 충돌 위험이 높아질 수 있다

### dependency management

pip에 설치된 패키지 확인

```shell
$ pip list
```

특정 패키지 정보 확인

```shell
pip show requests
```

패키지 설치

```shell
# requests 라이브러리 설치
$ pip install requests

# 특정 버전 설치
$ pip install requests==2.28.1

# 버전의 최소/최대 범위 지정 설치 (2.0이상 3.0미만 버전)
$ pip install "requests>=2.0,<3.0"
```

패키지 제거

```shell
$ pip uninstall requests
```

패키지 업그레이드

```shell
$ pip install --upgrade requests
```

### requirements.txt

requirements.txt 파일은 파이썬 프로젝트의 의존성 패키지 목록을 저장하는 파일이다

이 파일을 사용하면 명시적으로 버전 관리를 할 수 있으며 의존성 충돌 방지 등의 환경 일관성을 유지할 수 있다

또한 ci/cd 파이프라인 환경에서 프로젝트 환경을 손쉽게 복제할 수 있다

#### requirements.txt usage

현재 설치된 패키지를 requirements.txt로 저장

```shell
$ pip freeze > requirements.txt
```

requirements.txt 기반 패키지 설치

```shell
$ pip install -r requirements.txt
```

패키지 업그레이드

```shell
$ pip install --upgrade -r requirements.txt
```


## pipx

pipx는 파이썬 패키지의 독립적인 실행 환경을 관리하는 도구로 주로 파이썬 CLI 애플리케이션을 독립적인 가상 환경에서 실행하도록 도와준다

pip는 일반적으로 라이브러리나 의존성을 설치할 때 사용되며 설치된 패키지는 전역/가상 환경 별로 관리된다

pipx는 주로 독립 실행형 애플리케이션을 설치할 때 사용되며 각 애플리케이션을 격리된 가상 환경에 설치하여 충돌을 방지하고 시스템 환경을 깨끗하게 유지한다 (시스템의 site-packages 오염 방지)

```plaintext
~/.local/
    |── pipx/
        |── venvs/
            |── black/
            |── poetry
    |── bin/
        |── black
        |── poetry
```

설치한 애플리케이션은 두 디렉토리에 저장된다
- 애플리케이션 실행 파일: `~/.local/bin`
- 가상 환경(각 애플리케이션의 독립된 가상 환경): `~/.local/pipx/venvs`

기본적으로 pipx는 설치된 애플리케이션과 그 애플리케이션의 가상 환경을 `~/.local/pipx` 디렉토리 아래에 저장한다

이로 인해 여러 버전의 동일한 애플리케이션을 동시에 실행할 수 있으며 시스템의 파이썬 환경으로부터 독립적으로 애플리케이션을 관리할 수 있다

### pipx installation

```shell
# pipx 설치
$ pip insatll --user pipx

# 설치 확인
$ pipx --version 

# 환경 변수 설정
$ pipx eusurepath 
```

### pipx usage

애플리케이션 설치 (독립된 가상 환경에 애플리케이션을 설치하고 즉시 직접 실행할 수 있게 해준다)

```shell
# 애플리케이션 설치
$ pipx install black

# 특정 파이썬 버전을 사용하여 패키지 설치
$ pipx install black --python python3.13

# 커스텀 가상 환경에 설치
$ pipx install black --python /path/to/custom/python

# 로컬에 있는 파이썬 패키지 설치
$ pipx install /path/to/my_package
```

설치된 목록 확인

```shell
pipx list
```

애플리케이션 업데이트 및 제거
```shell
$ pipx upgrade black
$ pipx uninstall black
```


## venv

venv(virtual environment)는 파이썬 프로젝트별로 독립적인 패키지 환경을 제공하는 가상 환경 도구이다

파이썬 3.3부터 내장되어 있으며 프로젝트마다 별도의 파이썬 인터프리터와 패키지를 사용할 수 있도록 도와준다

### venv directory structure

가상 환경 디렉토리 내에서만 패키지가 관리되므로 시스템 환경으로부터 완전히 독립된 환경을 구축할 수 있다

```plaintext
myproject
|── code/        
|── my_env/         
    |── bin/
        |── python (pythonX.X)
        |── activate
        |── pip
    |── include/  
    |── lib
        |── site-packages
|── pyvenv.cfg
```

code: 프로젝트 소스 코드

my_env: 가상 환경 루트 디렉토리
- bin: 활성화 스크립트 및 실행 파일
  - python: 가상 환경 파이썬 실행 파일
  - activate: 가상 환경 활성화 스크립트
  - pip: 가상 환경 내 pip
- include: c 헤더 파일
- lib/site-packages: 의존성 관리 도구로 설치된 라이브러리
- pyvenv.cfg: 가상 환경 구성 파일

### venv usage

#### 가상 환경 생성

```shell
# myproject/my_env 디렉토리에 가상 환경 생성
python -m venv myproject/my_env

# 특정 파이썬 버전으로 가상 환경 생성 (해당 파이썬 버전이 시스템에 설치되어 있어야 됨)
python3.13 -m venv my_env
```

#### 가상 환경 활성화 (macos)

가상 환경이 활성화되면 전역 환경의 파이썬 대신 가상 환경의 파이썬 시스템을 사용한다

```shell
source my_env/bin/activate
```

#### 가상 환경 활성화 확인

일반적으로 가상 환경을 활성화시키면 IDE 상에서 활성화 상태가 표시되거나 터미널의 프롬프트가 아래와 같이 변경된다

```shell
(my_env) $
```

starship 같은 프롬프트 커스텀 도구를 사용하면 프롬프트가 변경되지 않는데, 그럴 땐 `which python` 명령어 통해 파이썬 실행 경로를 보아 가상 환경 활성화 여부를 알 수 있다

```shell
# 가상 환경 비활성화 상태일 때는 시스템에 설치된 파이썬 경로가 출력된다
$ which python
/Users/hansanhha/.asdf/installs/python/3.13.2/bin/python

# 가상 환경 활성화 상태일 때는 가상 환경의 파이썬 경로가 출력된다
$ source my_env/bin/activate
$ which python
/Users/hansanhha/blog/code/stack/python/code/project-environment/myproject/my_env/bin/python
```

#### 가상 환경 비활성화

```shell
deactivate
```

#### 가상 환경 삭제 (venv 디렉토리 삭제)

```shell
rm -rf my_env
```


## poetry

poetry는 기존 `pip + requirements.txt` 또는 venv 조합보다 더 강력하고 편리한 의존성 관리 및 패키지 배포 기능을 제공하는 도구이다

기존 의존성 관리 방식의 문제점
- 어려운 의존성 충돌 해결: pip는 패키지 간 버전 충돌을 자동으로 해결하지 않는다
- requirements.txt 동기화: `pip freeze > requirements.txt`를 실행하면 불필요한 패키지가 포함될 수 있다
- 별도의 가상 환경 구축: 프로젝트별로 독립적인 환경을 유지하기 위해 venv 또는 virtualenv를 직접 설정해야 한다

poetry는 위의 문제를 해결하고 추가적인 기능을 제공한다
- 의존성 충돌 자동 해결
- 자동 가상 환경 설정 및 관리
- 의존성 고정(lock 파일) 자동 생성(poetry.lock)
- `poetry publish`로 간단히 배포
- `poetry add <package>`로 간단히 의존성 추가

### poetry project structure

```plaintext
my_poetry_project
├── pyproject.toml
├── poetry.lock
├── README.md
├── src
│   └──my_poetry_project
│      └── __init__.py
└── tests
    └── __init__.py
```

`pyproject.toml`: 프로젝트 메타데이터 및 의존성 관리 파일

`poetry.lock`: 의존성 버전 고정 파일

`src/my_poetry_project`: 실제 파이썬 코드가 들어가는 디렉토리

`tests`: 테스트 코드 디렉토리

### pyproject.toml

```toml
[project]
name = "my_poetry_project"
version = "0.1.0"
description = "hello poetry project"
authors = ["hansanhha <hansanhha@example.com>"]

[tool.poetry]

[build-system]

[tool.poetry.dependencies]
python = "^3.9"
requests = "^2.26.0"
numpy = "^1.22.0"

[tool.poetry.dev-dependencies]
pytest = "^6.2.5"
black = "^22.1.0"
```

설명 추가 예정

### poetry usage

#### poetry installation

```shell
$ pipx install poetry

$ pipx upgrade poetry

$ pipx uninstall poetry
```

#### create new poetry project

```shell
# 새로운 poetry 프로젝트 생성
$ poetry new my_poetry_project

# 기존 프로젝트에 poetry 사용
$ poetry init
```

#### dependency management

설치된 패키지 목록 확인

```shell
$ poetry show
```

패키지 설치

```shell
$ poetry add requests      # 최신 버전 설치
$ poetry add numpy@1.22.0  # 특정 버전 설치
$ poetry add flask --dev    # 개발용 패키지 설치
$ poetry install           # pyporject.toml 기반 설치
```

패키지 삭제

```shell
$ poetry remove requets
```

패키지 업데이트

```shell
$ poetry update
```

#### virtual environment management

poetry는 기본적으로 자동으로 가상 환경을 생성/관리한다

가상 환경 활성화

```shell
$ eval $(poetry env activate)
```

활성화된 가상 환경 정보 확인

```shell
$ poetry env info
```

기존 가상 환경 제거

```shell
$ poetry env remove python3.13
$ poetry env remove <env-id>
$ poetry env remove --all
```

특정 파이썬 버전으로 가상 환경 생성

```shell
$ poetry env use python3.13
```

프로젝트의 가상 환경 목록

```shell
$ poetry env list
```

    
## conda

conda는 패키지 및 환경 관리 시스템으로 파이썬 뿐만 아니라 R, Ruby, Lua, Scala, Java, C/C++ 등의 다양한 프로그래밍 언어의 라이브러리와 의존성을 관리한다

Anaconda, MiniConda와 같은 배포판에서 제공되며 데이터 과학, 머신 러닝, 과학 계산 분야에서 주로 사용된다

주요 기능
- 의존성 관리: 다양한 언어의 의존성을 관리하며, 설치된 의존성은 그 자체로 독립된 환경에서 실행된다 (각 운영체제에 최적화된 의존성 제공)
- 환경 관리: 가상 환경을 관리하는 기능을 제공하여 서로 격리된 공간에서 독립적으로 동작할 수 있도록 한다
- 의존성 리포지토리: Anaconda Repository와 Conda-Forge와 같은 다양한 의존성 리포지토리를 제공한다
- 성능 최적화: 의존성 및 환경 관리 성능을 최적화하기 위해 컴파일된 바이너리 형식의 패키지를 제공한다 -> 소스 코드에서 컴파일하는 데 드는 시간을 줄이고 빠르게 설치할 수 있음

이후 내용 추후 작성

### conda installation

### environment mangament

### dependency management

### dependency repository

### Anaconda

### Miniconda 
