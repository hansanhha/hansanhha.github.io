---
layout: default
title:
---

#### index
- [module](#module)
- [import](#import)
- [sys.path](#syspath)
- [`__name__`](#__name__)
- [`__all__`](#__all__)
- [package](#package)


## module

파이썬에서 모듈이란 함수, 클래스, 변수 등의 코드가 정의된 `.py` 파일을 말한다

모듈은 다음과 같은 특징을 가진다
- 코드 재사용: 한 번 정의한 코드를 여러 곳에서 사용할 수 있다
- 가독성 향상: 긴 코드를 여러 파일로 나누어 유지보수하기 쉬워진다
- 네임스페이스 관리: 모듈별로 변수와 함수를 관리하여 충돌을 방지한다

### create module


`.py` 확장자를 가진 파일이 모듈이 된다

```python
# math_utils.py

PI = 3.141592

def add(a, b):
    return a + b

def subtract(a, b):
    return a - b
```

## import

자신의 모듈에서 다른 모듈을 가져오는 것을 import라고 한다

모듈을 가져오는 방법은 크게 4가지가 있다

### 1. `import module`

일반적으로 사용하는 방법으로 `module.name` 형식을 이용하여 모듈에 정의된 데이터에 접근할 수 있다

```python
# 위에서 만든 math_utils.py 파일 import
import math_utils

math_utils.PI          # 3.141592
math_utils.add(10, 5)  # 15
```

### 2. `from module import target...`

모듈의 특정 부분만 가져오고 싶은 경우 사용하는 방법이다

모듈명을 생략하고 직접 접근할 수 있다

```python
from math_utils import add, PI

PI           # 3.141592
add(10, 5)   # 15
```

### 3. `from module import *`

모듈의 모든 변수 및 함수를 가져오는 방법

두 번째 방법과 마찬가지로 모듈의 네임스페이스를 별도로 명시하지 않아도 되지만 변수와 함수에 대한 네임스페이스 모호성이 짙어질 수 있다

```python
from math_utils import *

print(PI)               # 3.141592
print(add(10, 5))       # 15
print(subtract(10, 5))  # 5
```

### 4. `import module as alias`

가져온 모듈의 네임스페이스명을 수정할 수 있다

```python
import math_utils as mu


print(mu.PI)       
print(mu.add(10, 5))
```

### module cache

파이썬은 같은 모듈을 여러번 import 해도 한 번만 로드한다

로드된 모듈은 sys.modules()를 통해 확인할 수 있다

```python
import sys

print(sys.modules)
```

### module reload

모듈을 수정한 후 즉시 반영하고 싶을 때는 importilb.reload()를 사용할 수 있다

```python
import importlib
import math_utils

importlib.reload(math_utils)
```

## sys.path

파이썬은 모듈을 찾을 때 `sys.path`를 통해 특정 경로를 탐색한다

모듈의 기본 경로
- 현재 실행 중인 스크립트의 디렉토리
- 환경 변수 PYTHONPATH에 설정된 경로
- 파이썬 표준 라이브러리
- 설치된 서드파티 패키지 (site-packages)

```python
import sys

print(sys.path)

# ['/Users/hansanhha/blog/code/stack/python/code/module', 
# '/Users/hansanhha/.asdf/installs/python/3.13.2/lib/python313.zip', 
# '/Users/hansanhha/.asdf/installs/python/3.13.2/lib/python3.13', 
# '/Users/hansanhha/.asdf/installs/python/3.13.2/lib/python3.13/lib-dynload', 
# '/Users/hansanhha/.asdf/installs/python/3.13.2/lib/python3.13/site-packages']
```

아래와 같이 모듈 검색 경로를 추가할 수도 있다

```python
import sys

sys.path.append('/my/custom/path')
```

## `__name__`

모듈은 직접 실행하거나 다른 모듈에 의해 import된다

파이썬에서는 이를 구분하기 위해 `__name__` 변수를 사용한다

만약 모듈이 직접 실행된다면 `__name__` 변수의 값은 `__main__`으로 할당된다

다른 모듈에서 import된다면 `__name__`값은 해당 모듈의 파일 이름이 된다

```python
def greet():
    print('hello word')

# 모듈이 직접 실행되는 경우에만 greet() 실행
if __name__ == '__main__':
    greet()
```

## `__all__`

`__all__` 특수 변수는 다른 모듈에서 `from module import *` 시 공개할 항목을 정의한다

다만 다른 모듈에서 `import module`할 때는 `__all__` 변수가 적용되지 않는다

아래와 같이 모듈 레벨에서 `__all__` 변수에 공개할 요소들을 리스트 형태로 명시한다

```python
# all_dunder.py
__all__ = ['public_func', 'public_var']

def public_func():
    return '공개 함수'

def _private_func():
    return '비공개 함수'

public_var = '공개 변수'
_private_var = '비공개 변수'
```

```python
# all_dunder_import.py
from all_dunder import *

print(public_func())   # '공개 함수'
print(public_var)      # '공개 변수'   

print(private_func())  # NameError (import 되지 않음)
print(private_var)     # NameError (import 되지 않음)
```

## package

패키지는 여러 개의 모듈(.py 파일)을 포함하는 디렉토리를 말한다

기본 구조

```plaintext
mypackage/
|── __init__.py
|── greeting.py
|── math_utils.py
|── subpackage/
    |── __init__.py
    |── submodule1.py
    |── submodule2.py
```

특정 모듈에서 패키지를 사용하려면 `import 패키지.모듈` 형식으로 가져온다

```python
import mypackage.greetings
import mypackage.math_utils
```

서브 패키지 import

```python
import mypackage.subpackage
```

특정 함수 import

```python
from mypackage.greetings import hello
```

as 키워드 별칭 사용

```python
import mypackage.math_utils as mu
```

### `__init__.py`

패키지 내부에 있는 `__init__.py` 파일은 패키지를 초기화하는 역할을 하는 파일로 패키지 초기화 코드 실행 및 모듈 import 작업을 수행한다 (모듈 임포트는 캐싱됨)

파이썬 3.3 버전 이후부터 패키지 내부에 없어도 패키지가 정상 동작하지만, `import mypakcage`처럼 패키지 전체를 불러올 수 없고 개별 모듈만 import 할 수 있다

`__init__.py`를 이용한 패키지 초기화

```python
print('mypackage loaded')

__all__ = ['greetings', 'math_utils']
```

### `__all__`

`__all__`은 다른 모듈에서 `from package import *` 구문으로 패키지의 모든 모듈을 import할 때 어떤 모듈을 공개할지 결정하는 리스트이다

`__init__.py` 파일에서 공개할 모듈 리스트 설정

```python
__all__ = ['greetings']
```

`from mypakcage import *` 사용

```python
from mypackage import *

print(greeting.hello('hansanhha'))  # hello hansanhha
print(math_utils.add(2, 3))         # NameError
```

### simpel package deploy

패키지를 zip 파일로 압축하고, sys.path의 모듈 검색 경로에 추가하면 다른 모듈에서 import할 수 있다

패키지 압축

```shell
zip -r mypackage.zip mypackage/
```

압축된 패키지 사용

```python
import sys

sys.path.append('mypackage.zip')

import mypackage.greetings

print(mypackage.greetings.hello('hansanhha')) # hello hansanhha
```

