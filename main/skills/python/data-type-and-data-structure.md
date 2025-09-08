---
layout: default
title:
---

#### index
- [int](#int)
- [float](#float)
- [complex](#complex)
- [bool](#bool)
- [str](#str)
- [None](#none)
- [sequence type](#sequence-type)
- [comprehension](#comprehension)
- [unpacking](#unpacking)
- [list](#list)
- [tuple](#tuple)
- [dictionary](#dictionary)
- [iterator](#iterator)
- [generator](#generator)


파이썬은 동적 타입 언어로 변수에 값을 할당하면 런타임에 자동으로 자료형이 결정된다

또한 원시 자료형을 포함한 모든 자료형을 객체로 취급한다

기본/원시 자료형(primitive type)은 변수에 직접 값을 저장하는 불변 데이터 타입을 의미한다

파이썬의 기본 자료형은 int, float, bool, complex, str이 있으며 다음과 같은 특징을 가진다

- 불변(immutable): 값을 변경할 수 없으며 새로운 값을 할당하면 새로운 객체가 생성된다
- 메모리에 직접 값을 저장한다
- 다른 자료형의 기본 구성 요소로 사용된다

기본 자료형을 제외한 list, tuple, dict, set 같은 자료형은 컬렉션 자료형(collections)라고 한다


## int

int형은 정수(integer)를 표현하는 데이터 타입으로 크기 제한 없이 큰 정수를 처리할 수 있다

자바에서는 int, long, short 등으로 나뉘지만 파이썬은 모두 int 하나로 통합되어 있다

기본적인 10진수 이외에도 2진수, 8진수, 16진수를 지원한다

```python
x = 10
y = 0b1010  # 2진수 표현
z = 0o12    # 8진수 표현
w = 0xA     # 16진수 표현

print(x, y, z, w)
```

### int()

`int()`는 정수로 변환하는 내장 함수(생성자)로 다음과 같은 값을 숫자로 형변환할 수 있다

```python
# float -> int, float을 변환할 때는 소수점 이하를 아예 버린다 (truncation)
int(3.8)   # 3
int(-2.7)  # -2

# str -> int, 숫자로 이루어진 문자열을 넣으면 정수로 변환할 수 있다
int("42")
int("15")

# 다른 진법 -> 10진수
int("1010", 2)  # 2진수 -> 10진수 (10)
int("12", 8)    # 8진수 -> 10진수 (10)
int("A", 16)    # 16진수 -> 10진수 (10)
```

### is_integer

`is_integer()` 내장 함수는 정수가 float 형식일 때 실제로 정수인지 확인할 수 있다

```python
10.0.is_integer() # True
10.5.is_integer() # False
```

## float

실수형은 부동소수점(실수, floating ponint number)를 표현하는 데이터 타입이다

IEEE 754 부동소수점 형식(double precision, 64bit)을 사용하여 숫자를 저장한다

```python
x = 3.14
y = -2.71
z = 1.0e5 # 1.0 * 10^5 = 100000.0
```

### float()

`float()`는 다른 타입을 float로 형변환하는 내장 함수(생성자)이다

```python
# int -> float
float(10)  # 10.0
float(-3)  # -3.0

# str -> float
float("3.14")  # 3.14
float("-2.71") # -2.71 
float("1e4")   # 10000.0
```

### infinity, NaN

float은 무한대(inf)와 NaN (Not a Number)도 지원한다

```python
a = float("inf")
b = float("-inf") 
c = float("nan")

print(a, b, c)

# inf, -inf, nan
```

### Decimal

파이썬의 float은 2진수 기반 부동소수점 연산을 하기 때문에 정확한 연산이 어려울 수 있다

정확한 계산이 요구되는 경우 Decimal을 사용하면 된다 

```python
print(0.1 + 0.2)  # 0.30000000000000004


import decimal from Decimal
print(Decimal("0.1") + Decimal("0.2"))  # 0.3
```

## complex

complex는 복소수(complex number)를 표현하는 자료형이다

복소수는 실수부(real part)와 허수부(imaginary part)로 이루어진 숫자인데 일반적으로 `a + bj`와 같이 표현된다

여기서 a는 실수부, b는 허수부, j는 허수 단위(파이썬에서는 i 대신 ㅓ를 사용함)를 의미한다

복소수는 아래와 같이 직접 선언하거나 complex() 생성자를 이용하여 생성할 수 있다

```python
z1 = 3 + 4j
z2 = complex(3, 4)

print(z1) # (3+4j)
print(z2) # (3+4j)
```

### real, imag attribute

complex 객체에는 실수부와 허수부를 접근하는 real과 imag라는 속성이 있는데, 이 속성은 float 타입으로 반환된다

```python
z = 3 + 4j

print(z.real) # 3.0
print(z.imag) # 4.0
```

### complex number calculation

복소수는 기본적인 사칙 연산이 가능하며 복소수 연산 규칙(실수부끼리, 허수부끼리)을 따른다

```python
z1 = 3 + 4j
z2 = 1 - 2J

# 기본적인 덧셈, 뺄셈
print(z1 + z2) # (4+2j)
print(z1 - z2) # (2+6j)

# 곱셈, 나눗셈
print(z1 * z2) # 11-2j
print(z1 / z2) # -1+2j

# 켤레 복소수 (복소수의 켤레(complex conjugate)는 허수부의 부호를 바꾼 값을 말한다)
print(z1.conjugate()) # (3-4j)
```

## bool

bool 타입은 논리(boolean) 값을 표현하는 자료형으로 True, Flase 두 개의 값만 존재한다

int의 서브 클래스이므로 True는 1, False는 0으로 동작한다

항상 대문자로 시작해야 하며 true/false를 사용하면 NameError 오류가 발생한다

```python
a = True
b = False

print(int(a), int(b))
```

### bool()

bool(x)를 사용하면 다른 자료형을 논리값으로 변환할 수 있다

```python
# int -> bool
bool(0)   # False
bool(1)   # True
bool(-10) # True (0이 아니면 모두 True를 반환한다) 

# str -> bool
bool("")      # False (빈 문자열은 False)
bool("hello") # True  (비어있지 않은 문자열은 True)

# list, tuple, dictionary -> bool
bool([])  # False (비어있는 컬렉션은 모두 False)
bool(())  # False
bool({})  # False

bool([1, 2])   # True (비어있지 않은 컬렉션은 모두 True)
bool((1, 2))   # True
bool({"1": 2}) # True

# None -> bool
bool(None) # False (None은 항상 False)
```


## str

str 타입은 문자들의 연속적인 시퀀스를 나타내며 기본적으로 유니코드를 지원한다

여러 가지 방법을 사용하여 문자열을 생성할 수 있다

```python
# 한 줄 문자열 (홑/겹따옴표)
s1 = 'hello'
s2 = "python"

# 여러 줄 문자열 (홑/겹따옴표)
s3 = '''a multiple
string line
!!!'''
s4 = """hello
python
string"""

# str() 함수를 이용한 변환
s5 = str(123)   # '123'
s6 = str(3.14)  # '3.14'
```

### immutable string

파이썬의 문자열은 불변(immutable)이므로 한 번 생성되면 수정할 수 없다

따라서 문자열을 변경하려면 새로운 문자열을 만들어야 한다

```python
s = 'hello'
s[0] = 'H' # TypeError: 'str' object does not support item assignment

s = 'H' + s[1:]  # 'Hello'
```

### string indexing, slicing

문자열은 시퀀스 자료형이므로 인덱스를 사용하여 특정 문자를 가져오거나(인덱싱) 부분 문자열을 추출할 수 있다 (슬라이싱)

```python
s = "python"

# 인덱싱 []
s[0]  # 'p'
s[-1] # 'n'

# 슬라이싱 [:]
s[0:4]  # 'pyth'
s[:4]   # 'pyth'
s[2:]   # thon
s[::2]  # pto
s[::-1] # nohtyp
```

### string manipulation

문자열끼리 `+` 연산자를 사용하면 새로운 문자열로 결합할 수 있다

문자열 결합 시 공백이 추가되지 않으며 다른 자료형과 문자열로 결합할 수 없다

```python
s1 = 'hello'
s2 = 'python'
s3 = s1 + s2  # hellopython
```

`*` 연산자를 사용하면 문자열을 여러 번 반복할 수 있다

```python
s = "h" * 5 # 'hhhhh' 
```

문자열 조작 내장 메서드 모음

```python
# 대소문자 변환
s = 'hello python'
s.upper()       # HELLO PYTHON
s.lower()       # hello python
s.capitalize()  # Hello python
s.title()       # Hello Python
s.swapcase()    # HELLO PYTHON (대소문자 반전)


# 공백 제거
s = '   hello   '
s.strip()   # 'hello'
s.lstrip()  # 'hello   ' 
s.rstrip()  # '   hello'


# 문자열 찾기 및 변경
s = 'hello world'
s.find('o')    # 4 (왼쪽에서 첫 번째 'o'의 인덱스 반환)
s.rfind('o')   # 7 (오른쪽에서 첫 번재 'o'의 인덱스 반환)
s.count('o')  # 2 ('o'의 개수)
s.replace('world', 'python') # 'hello python'


# 문자열 분할 및 결합
s = 'apple,banana,grape'
s.split(',')    # ['apple', 'banana', 'grape']

words = ['hello', 'python']
" ".join(words) # 'hello python'


# 문자열 검사
s = 'python1234'
s.isalpha()         # False (알파벳만 포함하는지 검사)
s.isdigit()         # False (숫자만 포함하는지 검사)
s.isalnum()         # True (알파벳 + 숫자로 구성되는지 검사)
s.startswith('py')  # True (특정 문자열로 시작하는지 검사)
s.endswith('1234')  # True (특정 문자열로 끝나는지 검사)
```

### f-string

파이썬 3.6부터 도입된 f-string은 문자열을 쉽게 포맷할 수 있는 기능을 제공한다

```python
menu = "americano"
amount = 4500

# 단일, 다중 문자열 모두 지원한다
order = f'''order info 
menu: {menu}
amonut: {amount}
'''

print(order)
# order info
# menu: americano
# amonut: 4500
```

### encoding, decoding

파이썬의 문자열은 기본적으로 유니코드(UTF-8)로 인코딩된다

```python
# str -> byte
s = '헬로우 파이썬'
b = s.encode()

# b'\xed\x97\xac\xeb\xa1\x9c\xec\x9a\xb0 \xed\x8c\x8c\xec\x9d\xb4\xec\x8d\xac'

# byte -> str
decoded = b.decode()

# '헬로우 파이썬'
```


## None

파이썬은 null이나 undefined가 없고 이를 대신할 None 타입을 제공한다

None은 값이 없거나 미정인 상태를 나타내며 파이썬에서 유일한 NonType 객체이다

변수에 할당 가능하지만 변경할 수 없는 불변 객체이다

```python
type(None)  # <class 'NoneType'>
```

### None is False?

None은 조건식에서 False로 평가된다

```python
if not None:
    print('hello python')

# 'hello python'
```

다만 조건식에서 사용될 경우 False로 취급되는 것에 불과하며 실제로는 False와 동일한 타입이 아니다

```python
None is False  # False
None is True   # False
None is None   # True
```

None을 숫자형으로 변환을 하면 오류가 발생하며 문자열 변환만 가능하다

반대로 'None' 문자열을 none() 함수로 변환하는 경우 TypeError가 발생한다

```python
int(None)  # TypeError
float(None) # TypeError
str(None)  # 'None'
```

### None comparison

None을 비교할 때는 `==` 대신 `is` 연산자를 사용하는 것을 권장한다

is는 객체 자체를 비교하고, ==는 값만 비교하기 때문에 None과의 비교에서는 is가 더 정확하다

```python
x = None

if x is None:
    print('x는 None이다')

if x == None:
    print('x는 None이다')
```

### None in function

함수에서 특정 값을 반환하지 않으면 암묵적으로 None을 반환한다

타입 힌트를 사용하여 None을 반환한다는 것을 명시적으로 표현할 수도 있다

```python
def nothing():
    pass

result = nothing()
print(result) # None


# int 또는 None 반환
def something() -> int | None:
```

매개변수의 기본 값을 None으로 설정할 수도 있다

```python
def greet(name=None):
    if name is None:
        return 'hello guest'
    return f'hello {name}'

greet()  # 'helllo guest'
```

### None in assignment


None을 변수 또는 클래스의 속성에 할당하여 아직 초기화되지 않은 속성이라는 것을 표현할 수 있다

```python
class Person:
    def __init__(self):
        self.value = None
    
hansanhha = Person()
hansanhha.value is None # True    

config = None
config is None # True
```


## sequence type

파이썬에서는 데이터를 순서대로 저장하고 인덱싱과 슬라이싱을 지원하는 자료형을 개념적으로 시퀀스 자료형(sequence data type)이라고 분류하며, 대표적으로 list, tuple, str, range()가 있다

시퀀스 자료형은 다음과 같은 특징을 가진다

|자료형|변경 가능성|특징|
|---|---|---|
|list|가변|다양한 자료형 저장|
|tuple|불변|리스트와 유사하지만 불변|
|str|불변|문자열|
|bytes|불변|바이너리 데이터 저장|
|bytearray|가변|bytes 가변 버전|
|range|불변|숫자의 연속된 범위 (수열)|

### indexing

시퀀스 자료형에 저장된 요소들은 저마다 인덱스가 부여되며, `[]` 괄호 안에 인덱스 번호를 지정하여 요소에 접근할 수 있다

인덱스는 정방향 인덱스와 역방향 인덱스를 가진다

정방향 인덱스는 0부터 시작하여 오른쪽으로 증가하는 인덱스를 말하며, 역방향 인덱스는 -1부터 시작하여 왼쪽으로 감소하는 인덱스를 말한다

```text
['a', 'b', 'c', 'd', 'e', 'f', 'g']

  0    1    2    3    4    5    6    // positive indexing (정방향 인덱스)
  -7   -6   -5   -4  -3    -2   -1   // negative indexing (역방향 인덱스)
```

```python
s = "python"

s[0]  # 'p'
s[2]  # 't'
s[-1] # 'n'
s[-3] # 'h'
```

### slicing

슬라이싱은 시퀀스 자료형의 인덱스를 기반으로 특정 범위의 요소를 잘라낼 수 있는 기법을 말한다

인덱스처럼 `[]` 괄호를 사용하며 괄호 안에 콜론과 슬라이싱 범위를 지정하여 컬렉션의 일부분 요소들을 추출한다

참고로 슬라이싱을 한다고 컬렉션 요소에 영향을 주지 않는다

`[start:stop:step]`의 구조를 가지며 생략하는 부분은 기본 값으로 적용된다
- start: 첫 인덱스 (0)
- stop: 마지막 인덱스
- step: 1

파이썬은 대부분 stop을 지정할 수 있는 연산에서 stop 전까지의 범위만 연산에 포함하는 특징을 가진다

```python
nums = [1, 2, 3, 4, 5, 6]

nums[1:]    # [2, 3, 4, 5, 6]
nums[:4]    # [0, 1, 2, 3]
nums[1:4]   # [2, 3]
nums[::2]   # [1, 3, 5]
nums[::-1]  # [6, 5, 4, 3, 2, 1]
nums[::-2]  # [6, 4, 2]
```

### iterable

시퀀스 자료형은 자신의 요소에 순차적으로 접근할 수 있는 "반복 가능한" 특징을 가진다

이는 내부적으로 **이터레이터 프로토콜 (iterator protocl)**을 구현하고 있기 때문에 가능한 것이다

이터레이터 프로토콜이란 `__iter__()` 또는 `__getitem__()` 메서드를 의미하며 객체 자신의 상태 값을 반복하려면 이 메서드를 구현해야 한다

`__iter__` 메서드는 다음 요소에 접근할 수 있는 이터레이터 객체(`iter()`)를 반환하고 `__getitem__` 메서드는 인덱스 기반 접근 기능을 내부적으로 구현하여 제공한다

```python
# list는 반복 가능한 객체(시퀀스 자료형)이다
nums = [1, 2, 3]

# for 문을 이용한 반복
for num in nums:
    print(num)

# 위의 작업을 이터레이터로 수동으로 수행하는 경우
iter_nums = iter(nums)
print(next(iter_nums))
print(next(iter_nums))
print(next(iter_nums))
```

시퀀스의 반복 가능한 특징을 이용하여 다양한 반복 관련 기능을 활용할 수 있다

```python
# enumerate(): 인덱스와 함께 요소 반복
fruits = ['apple', 'grape', 'orange']

for idx, fruit in enumerate(fruits):
    print(idx, fruit)

# 0 apple
# 1 grape
# 2 orange


# zip(): 여러 개의 시퀀스를 동시에 반복
fruits = ['apple', 'grape', 'orange']
amount_list = [1000, 2000, 3000]

for fruit, amount in zip(fruits, amount_list):
    print(fruit, amount)

# apple 1000
# grape 2000
# orange 3000


# map(): 요소 조작
numbers = [1, 2, 3, 4, 5]
doubled = list(map(lambda x: x * 2, numbers))
print(doubled) # [2, 4 ,6, 8, 10]


# filter(): 요소 필터링
evens = list(filter(lambda x: x% 2 == 0, numbers))
print(evens) # [2, 4]
```

### contatenation

시퀀스 자료형끼리 `+` 연산자를 사용하여 연결하거나 `*`를 사용하여 해당 시퀀스를 반복할 수 있다

```python
[1, 2] + [3, 4]  # [1, 2, 3, 4]
'hi' * 3         # hihihi
```

### len()

len() 함수를 사용하여 요소의 개수를 확인할 수 있다

```
len([1, 2, 3])  # 3
len('python')   # 6
```

## comprehension

컴프리헨션은 반복문을 간결하게 작성하는 문법으로 특정 데이터 구조를 짧고 효율적으로 생성할 수 있다

파이썬에서 총 4가지 종류의 컴프리헨션을 사용할 수 있다
- 리스트 컴프리헨션
- 딕셔너리 컴프리헨션
- 세트 컴프리헨션
- 제너레이터 표현식

튜플과 문자열은 컴프리헨션을 직접 지원하지 않고 각각 generator 표현식과 join 함수를 사용하여 대신할 수 있다

### list comprehension

리스트 컴프리헨션을 사용하면 반복문 없이 리스트를 생성할 수 있다

```text
[표현식 for 변수 in 반복 가능한 객체 if 조건식]
```

리스트 컴프리헨션 문법
- 표현식: 새로운 리스트의 요소를 생성하는 연산 (중첩 리스트 컴프리헨션을 사용할 수도 있음)
- 변수: 반복문에서 사용할 변수
- 반복 가능한 객체: iterable 객체
- if 조건식(optional): 특정 조건을 만족하는 요소만 추가

기본적인 리스트 컴프리헨션

```python
nums = [x for x in range(5)] 
# [0, 1, 2, 3, 4]
```

제곱수를 저장하는 리스트 컴프리헨션

```python
squares = [x**2 for x in range(5)]
# [0, 1, 4, 9, 16]
```

조건을 추가한 리스트 컴프리헨션

```python
evens = [x for x in range(10) if x > 0 and x % 2 ==0]
# [2, 4, 6, 8]
```

중첩 리스트 컴프리헨션 (2차원 리스트)

```python
matrix = [[x * y for x in range(1, 4)] for y in range(1, 4)]
# [[1, 2, 3], [2, 4, 6], [3, 6, 9]]
```

### dictionary comprehension

딕셔너리 컴프리헨션을 사용하면 반복문 없이 딕셔너리를 생성할 수 있다

```text
{키 표현식: 값 표현식 for 변수 in 반복 가능한 객체 if 조건식}
```

`[]` 괄호 대신 `{}` 괄호를 사용하는 점과 두 개의 표현식을 `:` 콜론으로 구분하여 사용하는 점이 리스트 컴프리헨션의 문법과 차이를 가지며 나머지는 동일하다

기본적인 딕셔너리 컴프리헨션

```python
square_dict = {x: x**2 for x in range(5)}}
# {0: 0, 1: 1, 2: 4, 3: 6, 4: 16}
```

조건을 추가한 딕셔너리 컴프리헨션

```python
even_sqaure_dict = {x: x**2 for x in range(10) if x > 0 and x % 2 ==0}
# {2: 4, 4: 16, 6: 36, 8: 64}
```

문자열을 딕셔너리로 변환하는 컴프리헨션

```python
word = "hello"
char_count = {char: word.count(char) for char in word}
# {'h': 1, 'e': 1, 'l': 2, 'o': 1}
```

### set comprehension

세트 컴프리헨션은 반복문없이 집합을 생성할 수 있다

```text
{표현식 for 변수 in 반복 가능한 객체 if 조건식}
```

`[]` 괄호 대신 `{}` 괄호를 사용하는 점이 리스트 컴프리헨션 문법과의 유일한 차이이고 나머지는 동일하다

중복 제거 기능을 활용한 세트 컴프리헨션

```python
unique_chars = {char for char in 'hello'}
# {'h', 'e', 'l', 'o'}
```

### generator

[generator](#generator-expression)


## unpacking

언패킹은 여러 개의 값을 한 꺼번에 꺼내어 각각의 변수에 자동으로 배치하는 기법을 말하며 반복 가능한(iterable) 객체(모든 시퀀스 및 컬렉션 자료형)에 적용할 수 있다

### basic unpacking

```python
# 리스트 언패킹
x, y, z = [10, 20, 30]

# 튜플 언패킹
x, y, z = (10, 20, 30)

# 문자열 언패킹
a, b, c = "abc"

# 딕셔너리 언패킹 (키가 자동으로 언패킹됨)
k1, k2 = {'name': 'python', 'message': 'hello'}

# 세트 언패킹
x, y, z = {10, 20, 30}
```

### dictionary unpacking

기본적으로 딕셔너리를 언패킹하면 딕셔너리의 키가 언패킹된다

키-값 쌍을 언패킹하고 싶은 경우 items() 메서드를 사용한다

```python
d = {'name': 'python', 'message': 'hello'}

for k, v in d.items():
    print(f'{k}: {v}')

# name: python
# messsage: hello
```

값만 언패킹하려면 values() 메서드를 사용한다

```python
d = {'x': 100, 'y': 200, 'z': 300}

a, b, c = d.values()
# 100, 200, 300
```

### variable unpacking

변수 개수와 요소 개수가 다를 경우 애스터리스크(`*`) 연산자를 활용하여 나머지 리스트를 묶을 수 있다

```python
nums = (1, 2, 3, 4, 5)

first, *middle, last = nums
# first: 1
# middle: [2, 3, 4]
# last: 5
```

### nested unpacking

튜플이나 리스트같은 컬렉션 자료형에 또 다른 컬렉션 자료형이 있는 경우 중첩 언패킹을 할 수 있다

```python
data = (1, (2, 3), 4)

a, (b, c), d = data
# 1, 2, 3, 4
```

### unpacking in function

파이썬은 함수의 실행 결과값을 단일 값 뿐만 아니라 여러 값으로 나눠 반환할 수 있다

이 때 튜플 언패킹을 활용하면 유용하게 반환값을 받을 수 있다

```python
def get_point():
    return (3, 5)

x, y = get_point()
# 3, 5
```

반대로 함수 인자를 가변 인자 또는 키워드 가변 인자로 전달하고 함수에서 이를 언패킹해서 받을 수도 있다

```python
# 가변 인자 언패킹
def add(a, b, c):
    return a + b + c

nums = (1, 2, 3)
result = add(*nums) # 6


# 키워드 가변 인자 언패킹
def greet(name, name2):
    print(f'hello {name}, {name2}')

data = {'name': 'python', 'name2': 'hansanhha'}
greet(**data) # hello python, hansanhha
```


## list

list는 가변(mutable) 시퀀스 자료형으로 요소의 자료형을 구분하지 않고 순서대로 저장한다

[시퀀스 자료형 인덱싱](#indexing)

[시퀀스 자료형 슬라이싱](#slicing)

리스트의 특징은 다음과 같다
- 가변: 요소 추가, 삭제, 수정 가능
- 순서 유지: 요소들이 입력된 순서를 유지한다
- 중복 허용: 같은 값을 여러 개 저장한다
- 다양한 데이터 저장: 숫자, 문자열, 리스트, 객체 등 혼합해서 저장할 수 있다
- 시퀀스 자료형 특징: 인덱싱, 슬라이싱, 이터레이트(for, enumerate, zip 등) 기능 지원

### list creation

기본적인 리스트 생성

```python
# 비어있는 새 리스트 생성
empty_list = []
empty_list2 = list()

# 요소를 삽입하면서 새 리스트 생성
numbers = [1, 2, 3, 4, 5]
mixed = [1, 2, 'hello', 'python']
fruits = list(['apple', 'grape', 'banana'])
```

중첩 리스트 생성

```python
matrix = [[1, 2, 3], ['hello', 'python'], [True, False]]

matrix[0]     # [1, 2, 3]
matrix[2][0]  # True
```

### list methods

요소 삽입 메서드

```python
nums = [1, 2, 3]

nums.append(4)      # 요소를 리스트 끝에 추가  [1, 2, 3, 4]
nums.extend([5, 6]) # 리스트 확장           [1, 2, 3, 4, 5 ,6]
nums.insert(6, 7)   # 특정 인덱스에 요소 삽입  [1, 2, 3, 4, 5, 6]
```

요소 삭제 메서드

```python
nums = [1, 2, 3, 4, 5, 6]

nums.remove(6)  # 특정 값(6) 제거     [1, 2, 3, 4, 5]
del nums[4]     # 특정 인덱스 요소 제거 [1, 2, 3, 4]
nums.pop()      # 마지막 요소 제거     [1, 2, 3, 4]
nums.clear()    # 전체 요소 제거       []  
```

요소 검색 메서드

```python
nums = [1, 2, 3, 4, 5, 6]

nums.index(3)  # 3의 첫 번째 인덱스 반환 -> 2
nums.conut(1)  # 1의 개수 -> 1
``` 

요소 정렬 메서드

```python
nums = [4, 5, 2, 6, 1, 3]

nums.sort()              # 오름차순 정렬     [1, 2, 3, 4, 5, 6]
nums.sort(reverse=True)  # 내림차순 정렬     [6, 5, 4, 3, 2, 1]
nums.reverse()           # 리스트 순서 뒤집기 [1, 2, 3, 4, 5, 6]
```

### list operations

리스트 간의 연결

```python
a = [1, 2, 3]
b = [4, 5, 6]

a + b  # [1, 2, 3, 4, 5, 6]
```

반복

```python
a = [1, 2, 3]

a * 3 # [1, 2, 3, 1, 2, 3, 1, 2, 3]
```

멤버십 연산

```python
a = [1, 2, 3]

2 in a      # True
10 not in a # True
```

길이 구하기

```python
a = [1, 2, 3]

len(a) # 3
```

### list: dynamic array

리스트는 동적 배열을 사용한다

요소가 추가될 때마다 내부적으로 요소를 저장하고 있는 용량이 기준치를 넘어서면 기존보다 더 큰 메모리 블록을 할당하고 데이터를 복사하여 저장한다

```python
import sys

nums = []
sys.getsizeof(nums) # 56

nums.append(1)
sys.getsizeof(nums) # 88
```


## tuple

튜플은 시퀀스 자료형으로써 리스트와 대부분 일치하는 특징을 가지지만 두 가지 큰 차이점을 가진다
- 변경 불가능(immutable): 한 번 생성하면 요소를 추가, 삭제, 수정할 수 없다
- 괄호 `()`: 튜플을 만들 때 소괄호를 사용하며 생략할 수 있다

그 외에 순서 보장, 중복 허용, 시퀀스 자료형 특징, 다양한 데이터 타입 저장 지원 기능은 리스트와 동일하게 지원된다

변경 불가능한 특징을 이용하여 보통 읽기 전용으로 사용할 때 튜플을 사용한다

### tuple creation

```python
# 비어있는 새 튜플 생성
t1 = ()
t2 = tuple()

# 요소를 삽입하면서 새 튜플 생성
t3 = (1, 2, 3)
t4 = 1, 2, 3
t5 = tuple([1, 2, 3])

# 요소가 하나인 튜플을 생성하려면 쉼표를 반드시 추가해야 한다
# 쉼표가 없으면 일반 숫자 또는 문자열로 인식된다
t6 = (1,)  
```

### tuple operations

튜플은 리스트처럼 인덱싱, 슬라이싱, 반복문, 연산 등을 지원하지만 요소를 변경할 수 없다

[인덱싱](#indexing)

[슬라이싱](#slicing)

[리스트 연산](#list-operations)

### tuple methods

특정 요소 개수 세기

```python
t = (1, 2, 3, 3, 4, 5, 6, 6, 6)
t.count(3) # 2
```

특정 요소의 첫 번째 위치 찾기

```python
t = (10, 20, 30, 40, 50)
t.index(40) # 3 
```

### immutable tuple

튜플은 한 번 생성되면 요소를 변경할 수 없다

다만, 각 요소의 참조값을 기준으로 불변성을 유지하기 때문에 딕셔너리같은 가변 컬렉션 자료형의 내부 요소는 조작할 수 있다

```python
t = (1, 2, [3, 4], {'name': 'python'})

t[2].append(5)
# (1, 2, [3, 4 ,5])

t[3]['message'] = 'hello'
# (1, 2, [3, 4 ,5], {'name': 'python', 'message': 'hello'})
```

### tuple comprehension

튜플은 직접적으로 컴프리헨션을 지원하지 않고, [제너레이터 표현식](#generator-expression-1)으로 대체한다


## dictionary

딕셔너리는 키와 값 쌍을 저장하는 컬렉션 자료형으로 다음과 같은 특징을 가진다
- 순서 보장(python 3.7 ~ )
- 키는 고유해야 한다
- 키는 불변해야 한다

### dictionary creation

```python
# 비어있는 새 딕셔너리 생성
empty_dict = {}
empty_dict2 = dict()

# 요소와 함께 새 딕셔너리 생성
d1 = {'name': 'python', 'message': 'hello'}
d2 = dict('a': 1, 'b': 2)

# zip을 활용한 딕셔너리 생성
keys = ['name', 'message']
values = ['python', 'hello']

d3 = dict(zip(keys, values)) # {'name': 'python', 'message': 'hello'}

# fromkeys()를 활용한 기본값 설정
keys = ['a', 'b', 'c']
default_value = 0

d4 = dict.fromkeys(keys, default_value) # {'a': 0, 'b': 0, 'c': 0}
```

### dictionary key rules

딕셔너리의 키에는 변경 불가능한 자료형만 사용될 수 있다 (원시형 자료형 및 tuple)

또한 키는 중복될 수 없으며 중복 키가 있다면 마지막 값으로 덮어씌워진다

```python
# TypeError
d1 = {[1, 2]: 'list'}

d2 = {1: 'one', 2.0: 'two', (3, 4): 'three', '4': 'four', True: 'five', None: 'six'}
```

### dictionary operations

요소 조회

```python
d = {'name': 'python', 'message': 'hello'}

d['name']     # python
d['message']  # hello
d['data']     # 존재하지 않는 키 접근 시 KeyError 발생
d.get('data') # get()을 사용하면 None 반환
d.get('data', 'not fonud')  # 기본값을 지정할 수도 있다
```

요소 추가 및 수정

```python
# 키가 존재하면 값이 수정됨
d['message'] = 'goodbye'

# 키가 없으면 새로운 요소가 추가됨
d['data'] = 'life is short'
```

요소 삭제

```python
del d['message'] # del 키워드 사용
d.pop('name')    # 딕셔너리의 pop 함수는 특정 키를 입력받는다
d.popitem()      # 마지막 요소 제거
d.clear          # 모든 요소 삭제
```

### dictionary methods

```python
d = {'name': 'python', 'message': 'hello'}

d.keys()            # 모든 키 반환
d.values()          # 모든 값 반환
d.items()           # dict_items([(키, 값), (키, 값)], ...) 반환
d.get(key, defalut) # 키 조회, KeyError를 발생시키지 않으며 기본값 설정 가능

d.setdefault(key, defalut) # 키가 없으면 추가 후 반환
d.update(dict2)            # 다른 딕셔너리 병합

d.pop(key, default) # 키 삭제 및 반환
d.popitem()         # 마지막 요소 제거
d.clear()           # 모든 요소 삭제
```

## iterator

## generator

제너레이터는 데이터를 필요할 때만 하나씩 생성(yield)하여 반환하는 이터레이터를 생성하는 함수다

이터레이터 프로토콜을 따르므로 next()를 사용하여 값을 순차적으로 가져올 수 있다

일반 함수와 다르게 한 번에 모든 결과를 메모리에 저장하지 않는다

lazy evaluation(지연 평가)을 통해 필요한 시점에 값을 생성하는 특징으로 인해 메모리 효율성이 뛰어나다

### yield keyword

yield는 제너레이터 함수에서 값을 반환하면서 함수의 실행 상태를 유지하기 위해 사용되는 키워드이다

return과 달리 함수 실행을 완전히 종료하지 않고 중단했다가 흐름을 이어서 실행할 수 있다

한 번에 모든 데이터를 메모리에 저장하지 않고 필요할 때마다 값을 생성한다

또한 이터레이터 프로토콜을 따르기 때문에 next()를 사용하여 하나씩 값을 가져올 수 있다

### generator definition

제너레이터는 함수를 중단하고 실행 상태를 유지한다

다음 next() 호출 시 유지된 이전 상태에서 다음 yield부터 실행된다

모든 데이터를 반환한 후 next()를 호출하면 StopIteration 예외가 발생한다

```python
def my_generator():
    print("start counter, yield 1")
    yield 1
    print("yield 2")
    yield 2
    print("end counter, yield 3")
    yield 3

gen = my_generator()

next(gen)  # start counter, yield 1
next(gen)  # yield 2
next(gen)  # end counter, yield 3
next(gen)  # StopIteration 예외 발생
```

### generator vs function

|구분|함수|제너레이터|
|----|----|----|
|동작 방식|한 번 실행되면 실행 결과 값을 return 문을 통해 반환한 뒤 종료한다|실행 상태를 유지하며 중단할 수 있고 yield 키워드를 사용하여 데이터를 하나씩 반환한다|
|메모리 사용|모든 결과를 한 번에 저장한다|필요할 때만 데이터를 생성하여 메모리를 절약한다|

```python
def my_func():
    return [1, 2, 3]

my_func()  # [1, 2, 3]

def my_generator():
    yield 1
    yield 2
    yield 3

gen = my_generator()

next(gen)  # 1
next(gen)  # 2
next(gen)  # 3
```

### for statement generator

제너레이터는 이터레이터의 일종이므로 for 문에서 직접 사용할 수 있으며 for 문에서 StopIteration 예외를 자동으로 처리해준다

```python
def my_generator():
    yield 10
    yield 20
    yield 30

for value in my_generator():
    print(value) 

# 10
# 20
# 30
```

### sub generator

`yield from`은 반복 가능한 객체(리스트, 튜플, 제너레이터 등)를 반환할 때 사용하는데, 이를 통해 다른 서브 제너레이터를 호출할 수 있다

```python
def sub_generator():
    yield 1
    yield 2
    yield 3

def main_generator():
    yield 'start'
    yield from sub_generator()
    yield 'end'

for value in main_generator():
    print(value)

# start
# 1
# 2
# 3
# end
```

### send()

제너레이터는 send() 메서드를 통해 값을 함수 내부로 전달할 수 있다

```python
def my_generator():
    value = yield "start"
    yield f'received: {value}'

gen = my_generator()
next(gen)      # start
gen.send(10)   # received: 10 
```

### close()

close()를 호출하면 제너레이터가 종료되며, 이후에 호출하면 StopIteration 예외가 발생한다

```python
def my_generator():
    yield 1
    yield 2

gen = my_generator()
next(gen)      # 1
gen.close()    # 제너레이터 종료
next(gen)      # StopIteration 발생
```

### throw()

제너레이터 내부에서 예외를 발생시키려면 throw() 메서드를 사용한다

```python
def my_generator():
    try:
        yield 1
        yield 2
    except ValueError as e:
        print(f'exception caught: {e}')
    
gen = my_generator()
gen.throw(ValueError)
```

### generator expression

제너레이터 표현식은 리스트 컴프리헨션과 유사하지만 제너레이터 객체를 반환하여 메모리를 효율적으로 사용할 수 있다

튜플의 경우 제너릭 표현식으로 컴프리헨션을 대체한다

```python
gen = (x**2 for x in range(5))

type(gen) # <class generator>

next(gen) # 0
next(gen) # 1
next(gen) # 4
```

### generator example

파일 읽기

```python
def read_large_file(file_path):
    with open(file_path, 'r') as file:
        for line in file:
            yield line.strip()

for line in read_large_file('large.txt'):
    print(line)
```

데이터 스트리밍

```python
import time

def data_stream():
    for i in range(1, 6):
        yield f'data {i}'

for data in data_stream():
    print(data)
```