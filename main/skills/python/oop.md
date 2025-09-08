---
layout: default
title:
---

#### index
- [dynamic object model](#dynamic-object-model)
- [class](#class)
- [attribute control](#attribute-control)
- [access modifier](#access-modifier)
- [inheritance](#inheritance)
- [polymorphism](#polymorphism)
- [magic methods, dunder methods](#magic-methods-dunder-methods)
- [object (built-in)](#object-built-in)
- [metaclass](#metaclass)


## dynamic object model

파이썬은 동적 객체 모델(dynamic object model)이라는 개념을 기반으로 동작한다

동적 객체 모델의 특성
- 모든 것을 객체로 취급: 함수, 클래스, 모듈 등 모두 객체로 취급한다
- 런타임 타입 조작: 객체의 속성, 메서드, 클래스를 동적으로 조작한다 (클래스에 속성을 런타임에 추가할 수 있다)
- 메타클래스 지원: 클래스를 생성하는 클래스를 정의할 수 있다

따라서 파이썬은 모든 것을 객체로 취급하기 때문에 모든 요소(리터럴, 자료구조, 함수, 클래스, 모듈 등)는 [type 메타클래스](#metaclass)를 기반으로 생성된 인스턴스이다

```python
# 클래스를 객체로 취급한다
# 파이썬 내장 클래스는 모두 type 메타클래스의 인스턴스이다
type(int)    # <class 'type'>
type(str)    # <class 'type'> 
type(list)   # <class 'type'>
type(object) # <class 'type'>

# 각 자료형은 해당 클래스의 인스턴스이다
type(10)          # <class 'int'>
type('hello')     # <class 'str'>
type([])          # <class 'list'>
type(lambda x: x) # <class 'function'>
type(Exception)   # <class 'type'>
```

런타임 타입 조작도 지원하므로 객체로 취급되는 함수에 동적으로 속성을 추가할 수도 있다

```python
def foo():
    print('hello')

type(foo)   # <class 'function'>

foo.message = 'python'

print(foo.message) # 'python'
```

클래스도 마찬가지로 런타임에 속성과 메서드를 추가할 수 있다

클래스 속성 추가

```python
class User:
    pass

User.id = 'hansnahha'  # 동적으로 클래스에 속성 추가

u = User()
u.id # 'hansanhha'
```

메서드 추가

```python
class User:
    pass

def greet(self):
    return f'hello, {self.id}'

# 동적으로 클래스에 메서드 추가
User.greet = greet

u = User()
u.id = 'hansanhha'
u.greet()  # 'hello hansanhha'
```

### `__dict__`

파이썬 객체의 속성은 내부적으로 `__dict__`에 저장된다

클래스와 인스턴스의 `__dict__`는 서로 다르다

클래스 속성은 `cls.__dict__`, 인스턴스 속성은 `obj.__dict__`에 저장된다

```python
class User:
    role = 'user'

u = User()
u.id = 'hansanhha'

# 클래스 속성
# mappingproxy({'__module__': '__main__', '__firstlineno__': 1, 'role': 'user', '__static_attributes__': (), '__dict__': <attribute '__dict__' of 'User' objects>, '__weakref__': <attribute '__weakref__' of 'User' objects>, '__doc__': None})
User.__dict__

# 인스턴스 속성
# {'id': 'hansanhha'}
u.__dict__
```

### `__slots__`

`__slots__`를 사용하면 객체에 동적 속성 추가를 제한할 수 있다

```python
class User:
    __slots__ = ['id', 'message']

u = User()
u.id = 'hansanhha'
u.message = 'hello'
u.role = 'user'  # AttributeError: 'User' object has no attribute 'role' and no __dict__ for setting new attributes
```

### getattr(), setattr(), delattr(), hasattr()

파이썬은 객체의 속성을 동적으로 조회할 수 있는 내장 함수를 제공한다

```python
class User:
    role = 'user'

u = User()

# 속성 조회
getattr(u, 'role')  # 'user'

# 속성 설정
setattr(u, 'id', 'hansanhha')
u.id  # 'hansanhha'

# 속성 삭제
delattr(u, 'id')

# 속성 포함 여부 조회
hasattr(u, 'id') # False
```


## class

파이썬 클래스는 class 키워드를 사용하여 정의되며 객체(인스턴스)를 생성하기 위한 blueprint 역할을 한다

모든 클래스는 직접/간접적으로 최상위 부모 클래스인 [object](#object-built-in) 클래스를 상속한다

```python
# 빈 클래스
class User:
    pass
```

### constructor, instance initiation

파이썬에서 클래스의 생성자는 `__init__` 메서드로 정의되며 객체가 생성될 때 자동으로 호출된다

self 변수는 해당 인스턴스를 가리키는 참조(this와 유사)로 클래스 내부에서 속성 및 메서드에 접근할 때 사용한다

```python
class User:

    # 클래스 속성
    role = 'user' 

    def __init__(self, id, password):
        # 인스턴스 속성
        self.id = id   
        self.password = password
```

클래스로부터 인스턴스를 생성하는 과정은 다음과 같다
- `User('hansanhha', '1234')`를 호출하여 클래스의 인스턴스 생성 로직 시작
- 클래스 생성자(`__new__`) 실행: 새로운 객체를 생성하고 `__init__`에 전달 (객체 메모리 할당 ->  `__init__ 실행`)
- `__init__` 메서드는 self 변수를 통해 인스턴스 속성을 초기화 (`self.id` = 'hansanhha', `self.password` = '1234')
- 생성된 인스턴스 반환

### attribute

파이썬의 속성(상태)는 인스턴스 변수와 클래스 변수로 나뉜다

인스턴스 속성은 각 인스턴스마다 개별적으로 저장되는 변수로 생성자에서 선언되며 `self.attr_name`으로 접근할 수 있다

클래스 속성은 클래스 레벨에서 공유되는 변수로 클래스에서 선언되며 각 인스턴스를 통해 접근할 수 있다

파이썬은 자바처럼 멤버 변수를 클래스에서 선언한 뒤 `static` 키워드로 전역 레벨을 설정하지 않고, 클래스에서 선언한 변수들은 모두 클래스 속성이 된다

### method

메서드는 클래스 내부 함수를 뜻하며 인스턴스 메서드, 클래스 메서드, 정적 메서드로 구성된다

메서드 시그니처(함수 정의 방식)
- 메서드 이름
- 매개변수 목록 (필수 위치 인자, 기본값이 있는 인자, 가변 위치 인자, 가변 키워드 인자)
- 반환 타입 힌트 (optional)

인스턴스 메서드는 객체의 속성을 조작할 때 사용하는 첫 번째 위치 인자에 self 변수가 있으면 인스턴스 메서드가 된다

```python
class User:
    role = 'user' 

    def __init__(self, id, password):
        self.id = id   
        self.password = password
    
    def login(self):
        return f'hello {self.id}'
```

클래스 메서드는 클래스 속성을 조작하는 메서드로 `@classmethod`를 메서드에 적용해야 하며 첫 번째 위치 인자에 cls 변수가 있으면 클래스 메서드가 된다

```python
class User:
    role = 'user' 

    def __init__(self, id, password):
        self.id = id   
        self.password = password
    
    @classmethod
    def set_role(cls, new_role):
        cls.role = new_role
```

정적 메서드는 클래스 내부에서 독립적인 기능을 수행하는 메서드로 `@staticmethod`를 적용해야 하며 self나 cls를 매개변수로 받지 않는다

```python
class Math:

    @staticmethod
    def add(x, y):
        return x + y
    
print(Math.add(3, 5)) # 8
```

### Ellipsis

`...`(삼중 점)은 메서드 바디에 사용할 수 있으며 메서드의 구현을 생략하겠다는 의미를 나타내는 문법 요소로 Ellipsis 라고 한다

구현을 보류하는 pass와 거의 유사하지만, 주로 Type Stub 파일 (`.pyi`) 또는 추상 클래스에서 타입 힌팅과 함께 인터페이스를 정의할 때 자주 사용된다

```python
# 구현은 제공하지 않고 인터페이스만 명시한다
# mypy 같은 정적 타입 검사기를 사용할 때 유용하다
class MyClass:
    def __eq__(self, value: object) -> bool: ...
```

### special variable

`self`: 현재 인스턴스를 가리키는 변수, 인스턴스 속성과 메서드에 접근할 때 사용한다

`cls`: 클래스 자체를 가리키는 변수, 클래스 메서드에서 클래스 속성에 접근할 때 사용한다

`__name__`: 모듈, 클래스, 함수 등의 이름을 담고 있는 속성, 클래스/메서드/함수차원에서 `__name__` 속성을 가지며 클래스의 이름을 반환한다

`__dict__`: 객체의 속성을 딕셔너리 형태로 저장한다 (클래스 속성 미포함)

```python
User.__name__        # 'User'
User.login.__name__  # 'login'
print.__name__       # 'print'
```

```python
u = User('hansanhha', '1234')

print(u.__dict__)
# {'id': 'hansanhha', 'password': '1234'}
```

## attribute control

### access  instance attribute 

외부에서 클래스의 인스턴스 속성에 접근하려면 `instance.attr_name` 형식을 사용한다

```python
class User:

    def __init__(self, id):
        self.id = id

u1 = User('hansanhha')
u2 = User('python')

u1.id # 'hansanhha'
u2.id # 'python'
```

### update instance attribute

외부에서 인스턴스 속성을 변경하려면 `instance.attr_name = value` 형식을 사용한다

```python
u1.id = 'hello'
```

### access/update class attribute

클래스 속성은 모든 인스턴스가 공유하는 속성으로 클래스에서 변경하면 모든 인스턴스에 영향을 주게 된다

다만 인스턴스 레벨에서 클래스 속성과 동일한 이름을 가진 값을 조작하면 새로운 인스턴스 속성을 생성하며 해당 인스턴스에만 영향을 끼친다

```python
class User:
    role = 'user'

u1 = User()
u2 = User()

# 클래스 속성 값 확인
User.role # 'user'

# 클래스 레벨에서 클래스 속성 변경
User.role = 'admin'
u1.role   # 'admin'
u2.role   # 'admin'

# u2 인스턴스의 role 속성을 'guest'로 변경
u2.role = 'guest'

# 클래스 및 u1의 속성은 변하지 않았으나 u2 인스턴스만 새로운 role 속성이 할당됨
User.role # 'admin'
u1.role   # 'admin'
u2.role   # 'guest'
```

## access modifier

파이썬에는 자바와 c++ 같은 언어에서 제공하는 private, protected, public 같은 키워드가 없다

대신 이름 규칙(naming convention)을 통해 접근 수준을 암묵적으로 나타낸다

|접근 제어자|표기법|설명|
|---|---|---|
|public|변수명|어디서나 접근 가능|
|protected|_변수명|서브클래스에서 접근 가능|
|private|__변수명|클래스 내부에서만 접근 가능 (서브클래스에서도 접근 불가능)|

private 변수는 파이썬에서 내부적으로 맹글링(name mangling) 기법을 사용해서 이름이 변경되어 외부의 접근을 차단한다 (변환된 속성명으로 접근할 수 있음)

```python
class User:
    def __init__(self, id, password, role):
        # public 
        self.id = id

        # private __password -> _User__password (name mangling)
        self.__password = password

        # protected
        self._role = role
```

### getter/setter

속성을 private으로 설정하고 getter/setter 메서드를 통해 외부의 접근을 허용하면 안전하게 보호할 수 있다

```python
class User:

    def __init__(self, id):
        self.__id = id
    
    def get_id(self):
        return self.__id
    
    def set_id(self, id):
        if isinstance(id, str):
            self.__id = id
        else:
            raise ValueError('아이디는 문자열이어야 한다')

u = User('hansanhha')

u.get_id()          # 'hansanhha'
u.set_id('python')  # 'python'
u.set_id(1234)      # ValueError
```

### @property

@property 데코레이터를 활용하면 더 간결하게 게터, 세터를 간결하게 구현하면서, 외부에서 일반 속성처럼 접근할 수 있게 한다

```python
class User:
    
    def __init__(self, id):
        self.__id = id
    
    # getter 역할
    @property
    def id(self):
        return self.__id
    
    # setter 역할
    @id.setter
    def id(self, new_id):
        if isinstance(new_id, str):
            self.__id = new_id
        else:
            raise ValueError('아이디는 문자열이어야 한다')


u = User('hansanhha')

# 함수 호출없이 속성처럼 게터/세터 사용
u.id  # hansanhha
u.id = 'python'

u.id = 1234 # ValueError
```


## inheritance

상속은 기존 클래스의 속성과 메서드를 물려받은 새 클래스를 통해 기능을 확장하는 개념이다

파이썬에서 클래스를 상속할 때는 클래스 선언 시 괄호 안에 부모 클래스를 지정한다

```python
# 슈퍼 클래스
class User:

    def __init__(self, id):
        self._id = id
    
    def login(self):
        return f'hello {self._id}'

# 서브 클래스
class Admin(User):

    def login(self):
        return f'hello admin {self._id}'

    
user = User('hansanhha')
admin = Admin('python')

user.login()   # hello hansanhha
admin.login()  # hello admin python
```

### super()

부모 클래스의 생성자(`__init__`)를 호출해야 하는 경우 `super()`를 사용한다

```python
class User:
    def __init__(self, id):
        self._id = id
    
class Admin(User):
    def __init__(self, id, permission):
        super().__init__(id)
        self.__permission = permission
    
    def login(self):
        print(f'hello {self._id} ({self.__permission})')
    
admin = Admin('hansanhha', 'poweruser')
admin.login()  # hello hansanhha (poweruser)
```

### method overriding

메서드 오버라이딩이란 자식 클래스에서 부모 클래스의 메서드를 재정의하여 기능을 변경/확장하는 것을 말한다

메서드의 이름, 매개변수 개수 및 순서를 동일하게 유지해야 정상적인 오버라이딩이 이루어진다

```python
class User:
    def login():
        return 'user login'

class Admin(User):
    def login():
        return 'admin login'

user = User()
admin = Admin()

user.login()   # user login
admin.login()  # admin login
```

다음과 같이 가변인자를 사용하여 부모 클래스의 메서드 구조를 유지하면서도 더 유연한 오버라이딩을 할 수 있다

```python
class Parent:
    def greet(self, name):
        return f'hello {name}'

class Child(Parent):
    def greet(self, *args):
        if args:
            return f'hi {args[0]}'
        return 'hi'

c = Child()
c.greet('hansanhha')  # hello hansanhha
c.greet()             # hello
```

### multiple inheritance

파이썬은 하나의 클래스가 여러 부모 클래스로부터 상속받는 다중 상속을 허용한다

```python
class Unit:
    def kill_off(self):
        return 'kill off!'

class Item:
    def drop_item(self):
        return 'item dropped'

class Monster(Unit, Item):
    pass

monster = Monster()
monster.kill_off()    # kill off!
monster.drop_item()   # item dropped
```


## polymorphism

다형성이란 같은 인터페이스를 사용하지만 서로 다른 동작을 수행하는 것을 의미한다

[메서드 오버라이딩](#method-overriding)을 통해서 자식 클래스들마다 서로 다른 동작을 수행할 수 있다

자바의 경우 주로 클래스보다 인터페이스를 통해 다형성을 활용하지만 파이썬은 인터페이스를 직접적으로 지원하지 않는다

대신 abc 모듈의 ABC(Abstract Base Class)를 사용하면 추상 클래스를 만들 수 있는데, 이걸로 인터페이스를 대체한다

추상 클래스에서 자식 클래스가 반드시 특정 메서드를 구현하도록 강제할 수 있다

아래의 User 클래스는 ABC(추상 클래스) 상속을 통해 추상 클래스임을 나타내며 직접 인스턴스화할 수 없다

그리고 login() 메서드에 `@abstracmethod`를 적용하여 자식 클래스에서 구현하도록 강제한다

```python
from abc import ABC, abstractmethod

# 추상 클래스
class User(ABC):

    @abstractmethod
    def login(self):
        pass

class Admin(User):

    def login(self):
        return 'admin login'
    
class Guest(User):

    def login(self):
        return 'guest login'
    
users = [Admin(), Guest()]

for user in users:
    print(user.login())
    # admin login
    # guest login
```

또한 파이썬 3.8 부터 `Typing Protocl`을 이용해 인터페이스 개념을 더 유연하게 적용할 수 있다

```python
from typing import Protocol

class User(Protocol):

    # 인터페이스 역할
    def login(self) -> str:
        pass

class Admin(User):
    def login(self) -> str:
        return 'admin login'

class Guest(User):
    def login(self) -> str:
        return 'guest login'

def user_login(user: User):
    print(user.login())

user_login(Admin())
user_login(Guest())
user_login(User())   # TypeError: Protocols cannot be instantiated
```


## magic methods, dunder methods

magic method 또는 dunder method(double underscore method)는 객체의 동작을 파이썬 내부 규칙에 맞게 사용자 정의할 수 있도록 해준다

파이썬은 모든 것을 객체로 표현하는데, 이 때 연산이나 특정 기능들이 각 객체의 성격에 맞게 동작해야 한다

예를 들어 int 타입의 `+` 연산은 두 정수형 타입을 더하는 동작을 수행한다

```python
10 + 10   # 20
```

str 타입의  `+` 연산은 두 문자열 타입을 연결(concat)하는 동작을 수행한다

```python
'1234' + '5678'  # '12345678'
```

이외에도 객체 생성/소멸, 객체 표현, 인덱싱 등 다양한 파이썬의 내장 기능을 해당 객체에 맞게 활용하기 위해 매직 메서드라는 특수 메서드가 존재한다

즉, 파이썬의 내장 기능 또는 해당 자료형의 메서드를 사용하면 그에 매핑되는 클래스의 매직 메서드를 호출하는 셈이다

기본 데이터 타입에서는 매직 메서드가 구현되어 있어서 잘 동작하지만, 사용자 정의 클래스는 object가 제공하는 매직 메서드만 지원하며 그 이상의 기능을 사용하려면 직접 구현해야 한다

이 점을 잘 이용해서 커스텀 클래스를 특정 데이터 타입처럼 동작하게 할 수 있다

### object initiation/destroy magic method

`__init__(self, ...)` 및 `__new__(cls, ...)` 메서드와 `__del__(self)` 메서드는 객체의 생성과 소멸 과정을 제어한다

`__new__(cls, ...)` 메서드는 객체 생성하는 단계에 호출되어 객체의 생성 흐름을 제어할 수 있다

`__init__` 전에 호출되며 아래와 같이 오버라이딩을 통해 객체를 싱글톤 패턴으로 만들 수 있다

```python
class User:
    __instance = None

    def __new__(cls):
        if cls.__instance is None:
            cls.__instance = super().__new__(cls)
        return cls.__instance

u1 = User()
u2 = User()
u3 = User()

u1 is u2 is u3   # True
```

`__init__(self, ...)` 메서드는 클래스의 생성자를 의미하며 `__new__(cls)` 이후에 호출된다

일반적으로 객체의 초기 속성을 설정하는 데 사용된다

```python
class User:
    def __init__(self, id):
        self.id = id

u = User('hansanhha')
u.id # 'hansanhha'
```

`__del__(self)` 메서드는 클래스의 인스턴스가 소멸될 때 호출된다

```python
class User:
    def __del__(self):
        print('instance will be deleted')

u = User()
del u
```

### object representation magic method

`__str__(self)` 메서드는 사람이 읽기 쉬운 문자열을 반환하며, `print(obj)` 또는 `str(obj)` 함수 호출 시 사용된다

```python
class User:
    def __init__(self, id):
        self.id = id
    
    def __str__(self):
        return f'user id: {self.id}'

u = User('hansanhha')
print(u)    # user id: hansanhha
```

`__repr__(self)` 메서드는 공식적인 문자열을 표현하며 디버깅을 목적으로 사용되는 `repr(obj)` 호출 시 사용된다

`eval(repr(obj))`를 실행하면 원래 객체를 복원할 수 있도록 하는 것이 원칙이다

```python
class User:
    def __init__(self, id):
        self.id = id
    
    def __repr__(self):
        return f"User('{self.id}')"

u1 = User('hansanhha')
print(repr(u1))   # User('hansanhha')

u2 = eval(repr(u1))
print(repr(u2))   # User('hansanhha')
```

### operator overloading magic method

연산자 오버로딩 메서드를 구현하면 객체 간 연산자를 직접 정의할 수 있다

매직 메서드를 트리거하는 연산자 목록
- `__add__(self, other)`: `+` 
- `__sub__(self, other)`: `-`
- `__mul__(self, other)`: `*`
- `__eq__(self, other)`: `==`
- `__ne__(self, other)`: `!=`
- `__lt__(self, other)`: `<`
- `__le__(self, other)`: `<=`
- `__gt__(self, other)`: `>`
- `__ge__(self, other)`: `>=`

### collection-related magic method

컬렉션 관련 매직 메서드를 트리거하는 메서드 목록
- `__len__(self)`: `len(obj)`
- `__getitem__(self, key)`: `obj[key]`
- `__setitem__(self, key, value)`: `obj[lkey] = value`
- `__delitem__(self, key)`: `del obj[key]`
- `__contains__(self, item)`: `item in obj`
- `__iter__(self)`: `iter(obj)` (for 루프 지원)

```python
class UserGroup:

    def __getitem__(self, index):
        return self.members[index]

print(user_group[3])  # user_group.__getitem__(self, 1) 호출
```


## object (built-in)

object 클래스는 파이썬 내장 클래스로 모든 클래스의 기반이 된다

모든 클래스가 암묵적으로 object 클래스를 상속받음으로써 파이썬의 객체 시스템과 호환되는 기본 기능을 사용할 수 있다

```python
class MyClass:
    pass

print(issubclass(MyClass, object))
```

### object magic method

object는 모든 객체가 공통적으로 가져야 할 기본적인 동작을 정의한다

`dir(object)`를 통해 object가 제공하는 기본 매직 메서드를 확인할 수 있다

매직 메서드 목록
- `__new__`: 메모리 할당 및 객체 반환 (인스턴스 생성)
- `__init__`: 아무 동작을 하지 않는 생성자 메서드 (아)
- `__class__`: 객체의 클래스 정보 반환 (`type(obj)`)
- `__str__`: `str(obj)` 호출 시 실행 (`<__main__.MyClass object at 0x...>Â 
` 형식으로 반환)
- `__repr__`: `repr(obj)` 호출 시 실행 (`<__main__.MyClass object at 0x...>Â 
` 형식으로 반환)
- `__eq__`: `==` 비교 연산 시 실행 (메모리 주소 비교, is 연산과 유사함)
- `__hash__`: 객체 해시값 반환 (`id(obj)` 기반으로 해시 생성)
- `__setattr__`: `obj.attr = value` 표현식 사용 시 실행
- `__getattribute__`: `obj.attr` 표현식 사용 시 실행
- `__delattr__`: `del obj.attr` 표현식 사용 시 실행
- `__sizeof__`: `sys.getsizeof(obj)` 호출 시 실행 (객체 메모리 크기 반환)

전체 매직 메서드 목록

```text
['__class__', '__delattr__', '__dir__', '__doc__', '__eq__', '__format__',
 '__ge__', '__getattribute__', '__gt__', '__hash__', '__init__', '__init_subclass__',
 '__le__', '__lt__', '__ne__', '__new__', '__reduce__', '__reduce_ex__',
 '__repr__', '__setattr__', '__sizeof__', '__str__', '__subclasshook__']
 ```

### object instantiate

object 클래스를 직접 인스턴스로 생성할 수 있다

```python
obj = object()
print(obj)
```

다만 속성을 가질 수 없도록 설계되어 동적으로 추가할 수 없다

```python
obj.attr = 10  # AttributeError: 'object' object has no attribute 'attr' and no __dict__ for setting new attributes
```



## metaclass

메타클래스란 클래스를 생성하는 클래스를 말한다

파이썬은 모든 것을 객체로 관리하므로 클래스 그 자체도 객체로 취급한다

일반적으로 클래스가 인스턴스를 생성하는 것처럼 클래스도 다른 클래스에 의해 생성되어야 하는데, 그러한 역할을 하는 클래스를 메타클래스라고 한다

즉, 메타클래스는 다른 클래스를 생성하는 클래스를 말한다

파이썬에서 기본적으로 사용되는 메타클래스는 type 클래스이다

커스텀 클래스를 정의하고 `type(obj)`로 확인해보면 type 클래스의 인스턴스인 것을 알 수 있다

```python
class Foo:
    pass

type(Foo) # <class 'type'>
```

내장된 클래스(str, int 등)도 마찬가지로 type 클래스의 인스턴스로 표현된다

```python
type(str) # <class 'type'>
type(int) # <class 'type'>
```

type 메타클래스를 이용하여 다음처럼 동적으로 클래스를 생성할 수 있다

```python
MyClass = type('MyClass', (object, ), {'message': 'hello'})

print(MyClass)          # <class '__main__.MyClass'>
print(MyClass.message)  # hello
print(type(MyClass))    # <class 'type'>

m = MyClass()
m.message = 'python'
m.message # 'python'
```

### custom metaclass

기본 메타클래스 type을 확장하여 커스텀 메타클래스를 만들어서 클래스 생성 시 속성 추가 등의 부가적인 기능을 덧붙일 수 있다

아래의 MyMeta 메타 래스는 클래스 생성 시 해당 클래스의 이름과 함께 생성 문구를 출력한다

```python
class MyMeta(type):
    def __new__(cls, name, bases, dct):
        print(f'creating class {name}')
        return super().__new__(cls, name, bases, dct)

# 커스텀 메타 클래스 지정
class MyClass(metaclass=MyMeta):
    pass

# 출력 문구
# creating class MyClass
```

메타 클래스는 클래스가 정의될 때 다음 단계를 거쳐 실행된다
- `class MyClass(metaclass=MyMeta):` 실행
- `MyMeta.__new__()`가 호출되어 클래스 생성
- `MyMeta.__init__()`이 호출되어 초기화 수행
- 이후 MyClass가 완성된다

아래의 ValidateMethodsMeta 클래스는 클래스에 run 메서드가 없으면 클래스 생성 자체를 차단한다

```python
class ValidateMethodsMeta(type):
    
    def __new__(cls, name, bases, dct):
        if 'run' not in dct:
            raise TypeError(f"{name} class must define a 'run' method")
        return super().__new__(cls, name, bases, dct)

class IncorrectClass(metaclass=ValidateMethodsMeta):
    pass # run 메서드 없음 -> TypeError 발생
```

### metdadata use case

메타클래스는 보통 라이브러리/프레임워크 개발 및 설계에서 활용된다

django orm: django model(models.Model)은 메타클래스를 활용하여 자동 필드 등록 및 검증을 수행한다

ABC (Abstract Base Class): 추상 클래스의 구현을 강제한다 