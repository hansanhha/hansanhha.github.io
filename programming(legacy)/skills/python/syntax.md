---
layout: default
title:
---

#### index
- [basic rule](#basic-rule)
- [keywords](#keywords)
- [operators](#operators)
- [conditional statement](#conditional-statement)
- [loop](#loop)
- [match statement](#match-statement)
- [function](#function)
- [exception, exception-handling](#exception-exception-handling)
  

## basic-rule

#### indentation

파이썬은 언어의 슬로건에 맞게 프로그래밍을 간편하게 할 수 있는 문법 규칙을 지원한다

중괄호를 사용하지 않고 코드 블록으로 들여쓰기(indentation)를 구분한다

때문에 파이썬에서는 들여쓰기를 맞추지 않으면 `IndentationError`가 발생할 정도로 들여쓰기가 중요하다

```python
# IndentationError 발생
def greet():
print("hello")
```

#### semicolon

코드의 끝에 붙이는 세미콜론은 선택적으로 사용할 수 있어서 대부분 생략하며, 주로 한 줄에 여러 개의 문장을 쓸 때 각 문장을 구분하는 용도로 사용한다

```python
# 일반적으로 세미콜론을 생략한다
print("hello")

# 한 줄에 여러 문장을 사용하는 경우 세미콜론으로 구분한다
# PEP 8에서는 한 줄에 여러 문장을 쓰는 것을 권장하지 않는다
x = 10; y = 20; print(x + y)
```

#### variable assignment

파이썬은 동적 타입 언어이므로 변수를 선언할 때 자료형을 명시하지 않는다 (런타임에 변수의 타입이 결정된다. 실행 시점에 잘못된 자료형 사용으로 인해 오류가 발생할 수 있으며 이를 방지하기 위해 [타입 힌트](./core-concepts.md#type-hint)를 사용할 수 있다)

```python
x = 10      # int
y = "hello" # str
z = 1.1     # float
```

또한 한 줄에 여러 개의 변수를 동시에 할당하는 다중 할당(multiple assignment) 기법을 제공한다

```python
x, y = True, False

print(x)  # True
print(y)  # False
```

다중 할당은 파이썬이 튜플 언패킹을 지원하기 때문에 가능한데, 위의 코드는 다음과 같이 동작한다
- True, False는 튜플 (True, False)로 변환된다
- x, y에 튜플의 각 요소가 차례로 할당된다
- `x, y = True, False` 코드는 `x, y = (True, False)`와 동일하다

변수 개수가 리스트 요소 개수보다 적을 때 `*`를 활용해 남은 값을 리스트로 받을 수도 있다

```python
a, *b = [1, 2, 3, 4, 5]
print(a) # 1
print(b) # [2, 3, 4, 5]

a, *b, c = [1, 2, 3, 4, 5]
print(a) # 1
print(b) # [2, 3, 4]
print(c) # 5
```

동일한 값을 한 번에 여러 변수에 할당할 수도 있다

```python
x = y = z = 100
print(x, y, z) # 100, 100, 100

a = b = c = []
a.append(1)
print(b) # [1]
```

변수 이름으로 영문자, 숫자, 언더스코어를 사용할 수 있으며 대소문자를 구별한다

예약어 또는 숫자로 시작하는 변수 이름을 사용할 수 없다

```python
# 잘못된 변수명
11value = 11         # 숫자 시작
class = "my_class"   # 예약어 사용
my-var = 22          # 하이픈 사용
```

#### comments

파이썬은 주석을 작성하기 위해 `#` `""" """` `''' '''` 사용한다

```python
# 한 줄 주석


"""
여러 줄 주석
"""


'''
여러 줄 주석
'''
```

#### line continuation

긴 코드를 백슬래시 또는 괄호를 이용하여 나눠 쓸 수 있다

```python
# + 연산자와 \를 함께 사용한다
total = 1 + 2 + 3 \
        4 + 5 + 6
```

괄호(`{}` `()` `[]`) 안에서는 백슬래시없이 자동으로 줄바꿈을 지원한다

```python
total = (
    1 + 2 + 3 +
    4 + 5 + 6
)
```

#### string representation

파이썬에서는 다양한 방법으로 문자열을 표현할 수 있다

```python
str1 = 'Hello'      # 작은따옴표
str2 = "Hello"      # 큰따옴표
str3 = '''Hello'''  # 여러 줄 문자열
str4 = """Hello"""  # 여러 줄 문자열
```

## keywords

파이썬의 예약어는 keyword 모듈을 이용해 확인할 수 있다

```python
import keyword
print(keyword.kwlist)
```

3.13.2 버전 기준 예약어 리스트

|예약어|설명|
|---|----|
|True|불리언 값 True|
|False|불리언 값 False|
|None|None 값|
|and|논리 연산자|
|or|논리 연산자|
|not|논리 부정|
|as|별칭 지정 `import numpy as np`|
|assert|디버깅 조건 검사 `assert x > 0`|
|async|비동기 함수 정의 `async def func()`|
|await|비동기 함수 호출 `await func()`|
|break|반복문 종료|
|continue|다음 반복 실행|
|class|클래스 정의|
|del|객체 삭제 `del obj`|
|def|함수 정의|
|lambda|익명 함수 정의 `labmda x: x + 1`|
|return|함수 값 반환|
|if|조건문|
|elif|조건문 `else if`|
|else|조건문|
|import|모듈 가져오기|
|from|모듈에서 특정 요소만 선택 `from module import x`|
|raise|예외 발생 `raise ValueError()`|
|try|예외 처리 블록 시작|
|except|예외 처리|
|finally|예외 발생 여부와 상관없이 실행|
|for|반복문|
|while|반복문|
|global|전역 변수 선언|
|nonlocal|지역 변수 변경 `nonlocal x`|
|in|멤버십 연산자 `if x in list`|
|is|객체 비교 `if x is y`|
|with|컨텍스트 매니저 `with open() as f:`|
|yield|제너레이터에서 값 반환 `yield x`|
|pass|코드 실행 스킵|

## operators

#### arithmetic operators

`+` `-` `*` `/` `//`(몫, 정수 나눗셈) `%`(나머지 연산) **(거듭 제곱)

#### comparison operators

`==` `!=` `>` `<` `<=` `>=`

#### logical operators

`and` `or` `not`

```python
x, y = True, False

print(x and y) # False
print(x or y)  # True
print(not x)   # False
```

#### assignment operators

`=` `+=` `-=` `*=` `/=` `//=` `%=` `**=`

#### bitwise operators

`&` (AND) `|` (OR) `^` (XOR) `~` (NOT) `<<` (left shift ) `>>` (right shift)

#### membership operators

멤버심 연산자는 값이 특정 자료형(리스트, 튜플 등)에 포함되어 있는지 확인한다

`in` 포함되어 있는지 확인하는 연산자

`not in` 포함되어 있지 않은지 확인하는 연산자

```python
numbers = [1, 2, 3, 4]

print(3 in numbers)      # True
print(5 not in numbers)  # True
```

#### identity operators

식별 연산자는 두 변수가 같은 객체를 가리키는지 비교한다 (동일성 비교)

`is` 같은 객체인지 비교하는 연산자

`is not` 다른 객체인지 비교하는 연산자


```python
x = [1, 2, 3] 
y = [4, 5, 6]
z = y

print(x is y) # False
print(y is z) # True
print(y == z) # True (y와 z는 동등한 값을 가진다)
```

## conditional statement

#### basic conditional statement

파이썬에서는 if, elif, else 문을 사용해 조건문을 작성하며 {} 대신 들여쓰기로 코드 블록을 구분한다

```python
if condition:
    code
elif condition:
    code
else:
    code
```

#### teneray operator

다른 프로그래밍 언어와 달리 삼항 연산자를 사용하는 대신 아래와 같이 표현한다

```python
x = 10
result = "positive" if x > 0 else "negative"
print(result) # positive
```

#### compound condition

논리 연산자를 사용해서 복합 조건을 설정할 수도 있다

```python
x = 15

if x > 10 and x < 20:
    print("x는 10보다 크고 20보다 작다")

if x < 10 or x > 20:
    print("x는 10보다 작거나 20보다 크다")

if not x == 10:
    print("x는 10이 아니다")
```

#### inclusion condition

in과 not in을 활용해서 리스트, 튜플, 문자열 포함 여부를 확인할 수 있다

```python
fruits = ["apple", "banana", "cherry"]

if "apple" in fruits:
    print("사과가 리스트에 포함되어 있다")

if "grape" not in fruits:
    print("포도는 리스트에 없다")
```

#### pass keyword

파이썬은 빈 블록을 허용하지 않기 때문에 pass 키워드로 빈 블록을 대신할 수 있다

보통 미완성 코드 자리를 나타낼 때 pass를 사용한다

```python
x = 10

if x > 0:
    print("positive")
else:
    pass
```

#### identity and equality comparison

조건문에서 두 객체에 대한 비교를 할 때 `==` 연산자는 값 비교(동등성), `is` 연산자는 객체의 id인 메모리 주소(동일성)를 비교한다

```python
x = [1, 2, 3]
y = [1, 2, 3]
z = x

print(x == y)  # True
print(x is y)  # False
print(x is z)  # True
```


## loop

#### for in

파이썬에서 일반적으로 사용하는 반복문은 `for in`문이며 반복 가능한 객체(iterable)를 순회하면서 실행되는 루프를 말한다

값을 받는 변수를 괄호로 감싸지 않는 특징을 가진다

```python
fruits = ["apple", "banana", "cherry"]

for fruit in fruits:
    print(fruit)

# apple
# banana
# cherry
```

컬렉션을 반복(이터레이트)하는 동안 수정될 수 있는 경우 컬렉션의 복사본을 만들어 루프를 도는 것이 안전하다

```python
api_health_statuses = {"account": "active", "order": "inactive", "ship": "active", "review": "inactive"}

for api, status in api_health_statuses.copy().items():
    if status == "inactive":
        raise InactiveApiStatusError()
```

#### range()

c, java에서 제공하는 전통적인 for문(3항 연산 기반)대신 range 함수를 제공한다

range 함수는 숫자를 시퀀스로 반복할 수 있는 수열을 만든다

함수의 매개변수는 `range(start, stop, step)`와 같으며 start와 step을 생략한 경우 각각 0과 1로 설정된다

참고로 stop의 끝 값은 수열에 포함되지 않고 step을 음수로 지정할 수도 있다

```python
for i in range(1, 3):
    print(i)

# 1
# 2
# 3
```

아래의 코드처럼 리스트를 생성할 때도 요긴하게 사용할 수 있다

```python
print(list(range(5)))   # [0, 1, 2, 3, 4] 0부터 4까지 순차적으로 증가
print(list(range(1, 10, 2))) # [1, 3, 5, 7, 9] 1부터 10까지 2씩 증가
```

#### enumerate()

enumerate 함수는 `for in`문에서 반복문을 돌 때 인덱스와 값을 동시에 가져올 수 있다

```python
fruits = ["apple", "banana", "cherry"]

for index, fruit in enumerate(fruits):
    print(index, fruit)

# 0 apple
# 1 banana
# 2 cherry
```

#### zip()

zip() 함수를 사용하면 여러 리스트를 동시에 순회할 수 있다

```python
coffees = ["americano", "latte", "cold brew"]
amount_list = ["4500", "5000", "5500"]

for coffee, amount in zip(coffees, amonut_list):
    print(coffee, amount)

# americano 4500
# latte 5000
# cold brew 5500
```

#### while

while문은 조건이 참(True)인 동안 계속 실행된다

```python
x = 3

while x > 3:
    print(x)
    x -= 1

# 3
# 2
# 1
```

#### loop control statement

break: 루프 강제 종료

continue: 다음 반복으로 건너뛰기

else: 루프가 정상 종료되었을 때 실행 (break로 종료되면 else 블록이 실행되지 않는다)

```python
# break
for num in range(10):
    if (num == 5):
        break
```

```python
# continue
for num in range(10):
    if n % 2 == 0:
        continue
```

```python
# else
for num in range(5):
    pass
else:
    print("반복문 정상 종료됨")
```


## match statement

파이썬 3.10부터 도입된 구조적 패턴 매칭(structural pattern matching) 기능은 기존 `if-elif-else`문을 더 간결하고 강력하게 표현할 수 있는 기능이다

`switch-case` 문과 유사하지만 더 풍부한 패턴 매칭 기능을 제공한다

기본적인 사용법은 다음과 같다

```python
match value:
    case pattern1:
        code
    case pattern2 | pattern3:
        code
    case _:
        code
```

`match`: 비교할 값 배치

`|`: 복수 패턴 매칭

`case`: 비교할 패턴 지정

`_`: default case 역할을 하며 어떤 패턴과도 일치하지 않을 경우 실행된다 (예외를 발생시킬 수도 있다)

```python
def check_status(code):
    match code:
        case 200:
            print("OK")
        case 400:
            print("Bad Request")
        case 404:
            print("Not Found")
        case 500:
            print("Internal Error")
        case _:
            print("Unknown Error")

check_status(200)  # OK
check_status(404)  # Bad Request
check_status(1000) # Unknown Error
```

#### collection, class pattern matching

case 뒤에 튜플, 리스트 패턴을 사용하여 매칭할 수 있다

변수 패턴은 값을 매칭하지 않고 해당 컬렉션에 맞는 형태로 특정 값을 추출할 수 있다

아래의 x, y는 튜플 데이터 중 특정 데이터를 추출한다

```python
# tuple pattern matching
def check_coordinate(data):
    match data:
        case (0, 0):
            print("원점")
        case (x, 0):
            print(f"x축 위의 점: {x}")
        case (0, y):
            print(f"y축 위의 점: {y}")
        case (x, y):
            print(f"좌표: {x} {y}")
        case _:
            raise ValueError("알 수 없는 데이터")

check_coordinate((0, 0))     # 원점
check_coordinate((5, 0))     # x축 위의 점: 5
check_coordinate((0, -10))   # y축 위의 점: -10
check_coordinate((10, 20))   # 좌표: 10 20
check_coordinate(("hello", "python")) # ValueError
```

변수 패턴을 사용할 때 가변 인자를 사용하여 리스트의 남은 요소를 전부 추출할 수도 있다

```python
# list pattern matching
def list_match(data):
    match data:
        case []:
            print("빈 리스트")
        case [first, second, *rest]:
            print(f"첫 번째: {first}, 두 번째: {second}, 나머지: {rest}")
        case _:
            raise ValueError("알 수 없는 데이터")

list_match([]) # 빈 리스트
list_match([1, 2, 3, 4, 5])  # 첫 번째: 1, 두 번째: 2, 나머지: [3, 4, 5]
list_match([42]) # ValueError
```

딕셔너리를 패턴 매칭할 때는 `case {"key": value}` 형태로 키를 기준으로 매칭한다

키의 값이 다르면 매칭되지 않으며 , 매칭되면 값이 해당 변수에 바인딩된다

```python
# dictionary pattern matching
def check_user(user):
    match user:
        case {"name": name, "position": position}:
            print(f"이름: {name}, 나이: {position}")
        case {"name": name}:
            print(f"이름: {name}, 포지션 미상")
        case _:
            raise ValueError("알 수 없는 데이터")

check_user({"name": "hansanhha", "position": "backend"}) # 이름: hansanhha, 나이: backend
check_user({"name": "hansanhha"}) # 이름: hansanhha, 포지션 미상
check_user({"id": 1234}) # ValueError
```

객체 패턴 매칭에서는 속성이 모두 일치하는 경우 매칭된다

속성을 변수 패턴으로 선언하면 값이 바인딩된다

```python
# class pattern matching
class User:
    def __init__(self, name, role):
        self.name = name
        self.role = role

def check_permission(user):
    match user:
        case User(name="admin", role="superuser"):
            print("관리자 접근")
        case User(name=name, role="user"):
            print(f"{name}의 일반 사용자 접근")
        case _:
            raise ValueError("알 수 없는 데이터")

admin = User("admin", "superuser") # 관리자 접근
user = User("hansanhha", "user")   # hansanhha의 일반 사용자 접근

check_permission(admin)
check_permission(user)
```

#### additional if condition

```python
def check_number(num):
    match num:
        case n if n > 0:
            print(f"{n}은 양수")
        case n if n < 0:
            print(f"{n}은 음수")
        case 0:
            print("0")
        case _:
            raise ValueError("알 수 없는 데이터")

check_number(10)  # 10은 양수
check_number(-10) # -10은 음수
check_number(0)   # 0
```


## function

#### function definition, calling

파이썬은 클래스를 만들지 않고 함수를 정의할 수 있다

함수를 정의하려면 def 키워드를 사용한다

함수 이름에 괄호 `()`를 붙여서 정의된 함수를 호출할 수 있다

```python
def greet():
    print("hello python")

greet()
```

#### parameter, argument

함수를 정의할 때 괄호 안에 작성하는 변수를 매개변수(parameter)라고 하며 호출할 때 넘겨주는 값을 인자(argument)라고 한다

아래와 같이 매개변수에 기본 값을 설정하지 않으면 해당 매개변수는 호출할 때 필수적으로 전달받아야 하는 것을 의미한다

기본값 매개변수로 설정하면 선택적으로 받아도 되는 매개변수임을 나타낼 수 있다

```python
def greet(name):
    print(f"hello, {name}")

greet()  # 오류 발생
```

```python
def greet(name="hansnahha"):
    print(f"hello, {name}")

greet()  # hello hansanhha
```

#### special parameters

인자를 넘길 때 변수의 이름을 명시적으로 선언할 수 있으며 이를 **키워드 인자**라고 한다

키워드 인자를 사용하면 함수의 매개변수 순서를 바꿔도 정상적으로 값을 전달할 수 있다

```python
def subtract(x, y):
    return x - y

print(subtract(y=10, x=20)) # 10
```

**가변 인자 (args)** 또는 위치 인자는 인자가 몇 개 들어올 지 모를 때 값을 튜플로 받는 방법을 말한다

관례상 가변 인자는 args라는 이름으로 사용한다

```python
def add_all(*args:int) -> int:
    return sum(args)

add_all(1, 2, 3, 4, 5) # 15
```

**키워드 가변 인자 (kwargs)**는 키워드 인자를 딕셔너리로 받을 수 있다

일반적으로 키워드 가변 인자는 kwargs 또는 kwds라는 이름으로 사용한다

```python
def show_info(**kwargs):
    for key, value in kwargs.items():
        print(f"{key}: {value}")

show_info(name="americano", amount=4500, type="ice")

# name: americano
# amount: 4500
# type: ice
```

함수에서 가변 인자와 키워드 가변 인자를 모두 받을 수 있다

다만 항상 *args가 먼저 오고, **kwargs가 나중에 와야 한다

```python
def my_function(*args, **kwargs):
    print("가변 인자: ", args)
    print("키워드 가변 인자: ", kwargs)

my_function(1, 2, 3, name="americano", amount=4500)

# 가변 인자:  (1, 2, 3)
# 키워드 가변 인자:  {'name': 'americano', 'amount': 4500}
```

키워드 인자와 반대로 오직 위치 인자로만 전달할 수 있게 하는 위치 전용 매개변수도 있다

함수 정의시 `/`는 **위치 전용 매개변수(positional-only parameter)**를 의미하며 `/` 앞에 오는 매개변수들은 위치 인자로만 전달할 수 있다

```python
def greet(name, /, message):
    print(f"{message}, {name}")

greet("hansanhha", "hello") # 정상
greet(name="hansannha", message="hello") # TypeError
```

아래와 같이 일반적인 매개변수와 함께 키워드 가변 인자를 사용하면 매개변수 이름이 충돌할 수 있다

```python
def foo(name, **kwargs):
    return "name" in kwargs

foo(1, **{"name": 2}) # TypeError
```

이 때 위치 인자를 통해 name을 위치 인자로 사용하면서 name을 키워드 인자의 키로 사용할 수 있다

```python
def foo(name, /, **kwargs):
       return "name" in kwargs

foo(1, **{"name": 2}) # True 
```

#### unpacking positional argument

리스트나 튜플을 위치 인자로 언패킹하거나 딕셔너리를 키워드 인자로 언패킹하는 경우 인자값에 반드시 `*` 또는 `**`를 붙여줘야 한다

```python
def add_all(a, b, c, d):
    return a + b + c + d

# unpacking list positional argument 
lst = [1, 2, 3]
add_all(*lst, 4) # 10

# unpacking dictionary positional argument
dic = {"b":2, "c":3, "d":4}
add_all(1, **dic) # 10
```

#### return

파이썬은 함수의 실행 결과에 대한 값을 **한 개** 또는 **여러 개**로 반환할 수 있다

```python
# 단일값 반환
def add(a, b):
    return a + b

# 다중값 반환
def calculate(a ,b):
    return a + b, a - b, a * b, a / b

calculate(1, 2) # (3, -1, 2, 0.5)
```

#### lambda

파이썬은 함수를 일급 객체로 다루기 때문에 함수를 변수에 담을 수 있다

이 점을 이용하여 함수 정의를 하지 않고 간단한 익명 함수를 작성할 수 있는데 이러한 함수를 **람다 함수**라고 한다

람다를 정의할 때는 lambda 키워드를 가장 앞에 선언하고 `:` 콜론을 기준으로 매개변수와 반환 값을 정의한다

람다 함수를 변수에 담으면 이후에 해당 변수를 통해 람다 함수에 접근하여 사용할 수 있다

```python
add = lambda x, y : x + y

print(add(10, 20)) # 30
```

#### nested function, closure

함수 안에 또 다른 함수를 정의할 수 있으며, 내부 함수가 외부 함수의 변수를 기억하는 현상을 클로저라고 한다

```python
def outer():
    msg = "hello"

    def inner():
        print(msg) # 외부 함수 사용
    
    print(inner)   # <function outer.<locals>.inner at 0x1038545e0>
    return inner   # 내부(inner) 함수 반환

func = outer()  # 내부 함수(inner)
print(func)     # <function outer.<locals>.inner at 0x1038545e0>
func()          # hello
```

#### decorator

데코레이터는 함수의 기능을 확장하는 기법으로 매개변수로 함수를 받아서 해당 함수의 실행 전/후로 확장 기능을 수행하는 프록시와 유사하다

`@decorator_name`을 선언하면 함수에 데코레이터를 적용할 수 있다

데코레이터 내부에서 호출된 원본 함수의 반환값을 받아서 추가적인 기능을 수행하거나 그대로 외부로 전달할 수 있다

데코레이터 함수는 일반적으로 내부 함수를 선언하는데, 이는 원본 함수를 감싸는 새로운 함수를 만들어야 하기 때문이다

이 함수가 원본 함수를 실행하기 전/후에 추가적인 기능을 수행하며 데코레이터에서 내부 함수를 반환하면 데코레이터를 적용한 함수가 자동으로 이 내부 함수로 치환된다

클로저 덕분에 외부 함수가 종료되어도 데코레이터 내부 함수가 매개변수(func)를 사용할 수 있게 된다

```python
# 로깅 데코레이터
def log(func):
    def log_wrapper():
        print("before function call")
        func() # closure
        print("after function call")
    return log_wrapper

@log
def greet():
    print("hello hansannha")

greet()

# before function call
# hello hansannha
# after function call
```

```python
# 반환값 조작 데코레이터
def multiply_result_by_two(func):
    def wrapper(*args, **kwargs):
        result = func(*args, **kwargs)
        print(f"원본 반환값: {result}")
        return result * 2
    return wrapper

@multiply_result_by_two
def get_number():
    return 10

print("최종 결과:", get_number())

# 원본 반환값: 10
# 최종 결과: 20
```


## exception, exception handling

예외(exception): 애플리케이션 차원에서 발생한 오류(파일 없음, 0으로 나누기, 잘못된 인덱스 접근 등)

오류(error): 시스템 차원에서 발생한 오류 (메모리 부족, 스택 오버플로우 등)

파이썬은 예외를 `XXXError` 형식으로 표현한다

주요 예외
- `ZeroDivisionError`: 0으로 나누기
- `IndexError`: 리스트, 튜플, 무낮열에서 잘못된 인덱스 접근
- `KeyError`: 딕셔너리에 존재하지 않는 키 접근
- `ValueError`: 형 변환 실패
- `TypeError`: 잘못된 타입 사용
- `AttributeError`: 존재하지 않는 속성에 접근
- `NameError`: 존재하지 않는 변수에 접근

```python
# 정의되지 않는 변수 접근 시 NameError 발생
asdf

Traceback (most recent call last):
  File "<python-input-75>", line 1, in <module>
    asdf
```

#### try-except-finally

try-except를 사용하여 예외를 처리할 수 있으며, finally 블록에 위치한 코드는 예외 처리 결과에 상관없이 무조건 실행된다

try 블록 내에서 예외가 발생하면 그 즉시 코드 실행 흐름이 except 블록으로 넘어가게 되어 기존 로직의 실행 흐름은 멈춘다

```python
try:
    result = 10 / 0   # ZeroDivisionError 
except ZeroDivisionError:
    print("0으로 나눌 수 없음")

# 0으로 나눌 수 없음
```

아래와 같이 여러 개의 예외를 한 번에 처리하거나 예외 별로 다르게 처리할 수 있다

```python
try:
    x = int("hello")  # ValueError
    y = []
    print(y[3])       # IndexError
except (ValueError, IndexError) as e:
    print(f"예외 발생: {e}")
```

```python
try:
    x = int("hello")  # ValueError
    y = []
    print(y[3])       # IndexError
except ValueError:
    print("ValueError 발생")
except IndexError:
    print("IndexError 발생")
```

#### Exception

Exception을 사용하면 모든 예외를 한 번에 처리할 수 있다

다만 어떤 예외가 발생했는지 명확하지 않아 유지보수성을 떨어뜨리므로 가능한 구체적인 예외를 명시하는 것이 좋다

```python
try:
    result = 10 / 0
except Exception as e:
    print(f"예외 발생: {e}")

# 예외 발생: division by zero
```

#### raise

명시적으로 예외를 발생시켜야 하는 경우 raise 키워드를 사용하면 된다

```python
def check_amount(amount):
    if (amount < 1000):
        raise ValueError("최소 1000원을 넘는 가격이어야 합니다")
    return f"가격: {amount}"

print(check_amount(500))

# ValueError: 최소 1000원을 넘는 가격이어야 합니다
```

#### traceback message

파이썬에서 예외가 발생하면 어디에서 발생했는지를 보여주는 디버깅 메시지 트레이스백(traceback)이 출력된다

최근 실행된 함수부터 역순으로 추적하며 traceback의 가장 마지막 줄에 최종 예외와 예외 메시지를 출력한다

traceback은 아래와 같은 구조로 구성되어 있다

```python
Traceback (most recent call last):
  File "<python-input-81>", line 1, in <module>
    hello
NameError: name 'hello' is not defined. Did you mean: 'help'?
```

`Traceback (most recent call last):`: traceback 시작

`File "<python-input-81>", line 1, in <module>`: 예외가 발생한 파일명과 코드 위치 (python-input은 파이썬 REPL을 의미한다, `<module>`은 파일의 최상단을 의미하며, 함수 안에서 발생한 예외는 해당 함수의 이름을 명시한다)

`hello`: 예외가 발생한 코드

`NameError: name 'hello' is not ~`: 예외 유형과 메시지
