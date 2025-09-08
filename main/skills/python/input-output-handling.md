---
layout: default
title:
---

#### index
- [standard i/o](#standard-io)
- [file i/o](#file-io)
- [network i/o](#network-io)
- [database i/o](#database-io)


## standard i/o

파이썬에서 표준 입출력은 기본적으로 키보드 입력(sys.stdin), 콘솔 출력(sys.stdout), 에러 메시지 출력(sys.stderr)으로 구성된다

이를 활용하여 입력 데이터를 처리하고 출력을 조작하거나 리다이렉션하는 작업을 수행할 수 있다

리다이렉션: 특정 명령 실행 결과의 출력을 다른 명령의 입력으로 전달하는 기법

입출력 스트림 번호
- 표준 입력(stdin): 0
- 표준 출력(stdout): 1
- 표준 에러(stderr): 2

### stdin 1. input()

input() 함수는 표준 입력(키보드) 스트림인 경우 사용자의 입력을 기다리다가 엔터를 누르면 실행되며, 전달받은 입력값을 문자열(str)로 반환한다 (스트림이 파일로 변경되면 한 줄을 읽음)

```python
name = input('enter your name: ')

print(f'hello, {name}') # hello hansanhha
```

반환값이 항상 문자열 형태이므로 숫자를 사용하려면 형변환이 필요하다

```python
age = int(input('enter your age: '))
height = float(input('enter your height: '))

print(f'age: {age}, height: {height}')
```

여러 개의 값을 입력하려면 split 함수를 사용하여 구분할 수 있고, 각 값을 map을 통해 형변환할 수 있다

```python
# split(','): 콤마를 기준으로 분리
# map(int, ...): 문자열을 정수로 변환
a, b, c = map(int, input('enter three numbers: ').split(','))

print(a)  # 1
print(b)  # 2
print(c)  # 3
```

`or` 연산자를 사용하여 기본값 처리를 할 수도 있다

```python
name = input('enter your name: ') or 'guest'
print(f'hello, {name}') # hello guest
```

### stdin 2. sys.stdin()

sys.stdin을 사용하면 대량 입력이나 리다이렉션된 입력(`<`)을 처리할 수 있다

아래의 예제는 `sys.stdin.read()` 메서드를 사용하여 간단한 텍스트 파일을 파이썬 스크립트에 리다이렉션하고 입력된 데이터를 출력한다 [github 코드](https://www.github.com/hansanhha/hansanhha.github.io/tree/asdf/code/stack/python/code/input-output/stdin.py)

stdin.py

```python
import sys

data = sys.stdin.read()
print('read from stdin: ', data)
```

stdin_input.txt

```text
hello, python!
hansanhha's python example script
```

```shell
$ python stdin_example.py < stdin_input.txt

hello, python!
hansanhha's python example script
```

`sys.stdin.readlines()` 메서드는 여러 줄을 입력받아, 그 내용들을 리스트로 반환한다

```python
import sys

lines = sys.stdin.readlines()

for line in lines:
    print(line.strip())
```

`sys.stdin`과 반복문을 활용하여 한 줄씩 읽을 수 있다

```python
import sys

for line in sys.stdin:
    print(line.strip())
```

### stdout 1. print()

print() 함수는 표준 출력(sys.stdout)을 사용하여 데이터를 출력한다

```python
# 단일 값 출력 
print('hello python') # hello python


# 다중 값 출력 (각 값을 콤마로 구분한다)
print('hello python', 1.4, True) # hello python 1.4 True (여러 개의 값을 공백 구분자로 출력)

# 출력 구분자 변경
print('hello python', 1.4, True, sep=', ') # hello python, 1.4, True

# 출력 끝 문자 변경 (기본값: 개행 \n)
print('hello', end=' ')
print('python')  # hello python
```

변수의 값을 문자 출력에 포함하고 싶은 경우 다음과 같이 포매팅을 하면 된다

```python
name = 'python'
message = 'hello'

# f-string
print(f'{message} {name}')

# format()
print('{} {}'.format(message, age))
```

출력 정렬 `:<` `:>` 

```python
print(f"{'hello'} {'python':>10}")
# hello     python

print(f"{'hello':<10} {'python':>10}")
# hello          python
```

### stdout 2. sys.stdout.write()

표준 출력을 사용하면 아래와 같이 파일로 출력을 리다이렉션할 수 있다

```python
import sys

with open('stdout_output.txt', 'w') as file:
    sys.stdout = file # 표준 출력을 파일로 변경
    print('hello python')
    print('standard output is redirected')
```

### stderr

표준 에러(sys.stderr)는 에러 메시지를 출력하는 용도로 사용되며 일반적인 출력(sys.stdout)과 분리된 출력 스트림이다


또한 표준 출력과 달리 에러 메시지를 리다이렉션하더라도 콘솔에도 그대로 출력된다

```python
import sys

sys.stderr.write('error occured')
```

다음과 같이 표준 출력과 표준 에러를 각각의 파일에 리다이렉션할 수 있다

```python
import sys

def divide(a, b):
    if (b == 0):
        sys.stderr.write('error: division by zero\n')
        return None
    return a / b

print(divide(10, 0))
```

```shell
python divide.py > divide_output.log 2> divide_error.log
```

### i/o stream redirection

입출력 스트림을 다른 대상으로 변경할 수 있다

표준 출력을 파일로 변경

```python
import sys

with open('output.txt', 'w') as file:
    sys.stdout = file # 표준 출력을 파일로 변경
    print('this output will be written to output.txt')
```

입력을 파일에서 읽기

```python
import sys

with open('input.txt', 'r') as file:
    sys.stdin = file  # 키보드 -> input.txt 입력 스트림 변경
    data = input()   # input.txt의 첫 줄 읽기
    print(f'read from file: {data}')
```

출력과 에러를 하나의 파일로 저장

```shell
python temp_script.py > all.log 2>&1
```


## file i/o

파이썬에서 파일 입출력은 텍스트 파일과 바이너리 파일을 다룰 수 있으며 open() 함수를 이용하여 이 작업들을 수행한다

### open()

파일을 열기 위해 open() 함수를 사용하며 파일 경로와 모드(읽기/쓰기)를 지정한다

```text
file = open('파일 이름', '모드', encoding='utf-8')
```

모드
- 'r': 텍스트 읽기(기본값), 파일없으면 오류 발생
- 'w': 텍스트 쓰기(덮어쓰기), 파일없으면 새로 생성 
- 'a': 텍스트 추가(추가쓰기), 파일없으면 새로 생성
- 'x': 새 파일 생성, 파일이 존재하면 오류 발생
- 'rb': 바이너리 읽기, 파일없으면 오류 발생
- 'wb': 바이너리 쓰기(덮어쓰기), 파일없으면 새로 생성
- 'ab': 바이너리 추가(추가쓰기), 파일 없으면 새로 생성

파일을 열고 닫지 않으면 리소스 누수가 발생할 수 있다

수동으로 닫으려면 다음과 같이 파일을 열고 나서 close() 함수를 명시적으로 호출한다

```python
# 읽기 모드로 파일 열기
file = open('example.txt', 'r')

# 파일 닫기
file.close()
```

with 구문을 사용하면 with 블록을 벗어난 시점에 파일을 자동으로 닫힌다

```python
with open('example.txt', 'r') as file:
    content = file.read()
```

### write(), writelines()

파일에 문자열 데이터를 저장할 때는 'w' 또는 'a' 모드를 사용한다

write() 함수는 문자열 데이터를 쓸 때 사용한다

```python
with open('test.txt', 'w') as file:
    file.write('hello python\n')
    file.write('goodmorning')
```

writelines() 함수는 list[str]를 한 번에 파일에 쓰며, 각 요소에 개행 문자가 포함되면 줄바꿈을 적용한다

```python
lines = ['line 1\n', 'line 2\n', 'line 3\n']

with open('test.txt', 'w') as file:
    file.writelines(lines)
```

### read(), readline(), readlines()

텍스트 파일을 읽을 때는 'r' 모드를 사용한다

read() 함수는 파일 전체를 읽어들인다

대용량 문자열 파일인 경우 메모리 사용량이 많아지므로 readline()이나 readlines() 사용을 권장한다

예시 파일 (test.txt) 내용

```text
hellllllllllllo
pythonnnnnnnnnn
hansanhhhhhhhha
goooooodmorning
```

```python
with open('test.txt', 'r') as file:
    content = file.read() 
    print(content) # 파일에 작성된 내용 그대로 출력한다 (개행 문자 포함)

# 출력 결과
# hellllllllllllo
# pythonnnnnnnnnn
# hansanhhhhhhhha
# goooooodmorning
```

readline() 함수는 파일을 한 줄씩 읽는다

```python
with open('test.txt', 'r') as file:
    line = file.readline()

    while line:
        print(line.strip())
        line = file.readline()

# 출력 결과
# hellllllllllllo
# pythonnnnnnnnnn
# hansanhhhhhhhha
# goooooodmorning
```

readlines() 함수는 전체 줄을 리스트로 읽는다

```python
with open('test.txt', 'r') as file:
    lines = file.readlines()
    print(lines)

# 출력 결과
# ['hellllllllllllo\n', 'pythonnnnnnnnnn\n', 'hansanhhhhhhhha\n', 'goooooodmorning']
```

다음과 같이 반복문을 이용해서 파일의 한 줄씩 읽을 수도 있다

### tell(), seek()

파일을 읽거나 쓸 때에 현재 파일의 어느 위치를 읽고 있는지 가리키는 파일 포인터(커서)가 존재한다

이 커서의 위치를 확인하거나 이동하여 파일 작업을 유연하게 처리할 수 있다

tell() 함수는 현재 파일 포인터의 위치를 확인한다

```python
with open('test.txt' 'r') as file:
    print(file.tell()) # 현재 위치 (0부터 시작)
    file.read(5)       # 5바이트 읽기
    print(file.tell()) # 변경된 위치(5)
```

seek() 함수는 파일의 포인터를 이동시킨다

`seek(0, 0)`: 파일 처음으로 이동

`seek(n, 0)`: n번째 바이트로 이동

`seek(0, 2)`: 파일 끝으로 이동

```python
with open('test.txt', 'r') as file:
    file.seek(10)       # 10번째 바이트로 이동
    print(file.read(5)) # 10 위치에서 5바이트 읽기
```

### binary file i/o

텍스트 파일과 마찬가지로 read(), write() 등의 함수를 사용하지만 open() 함수에서 용도에 따라 'rb', 'wb' 'ra' 모드를 지정하여 바이너리 파일을 다룰 수 있다

이미지 복사

```python
with open('original.jpeg', 'rb') as source, open('copy.jpeg', 'wb') as target:
    target.write(source.read())
```

### file error handling

'r', 'rb' 모드에서 파일이 존재하지 않거나 권한 문제 등으로 인해 오류가 발생할 수 있다

```python
try:
    with open('test.txt', 'r') as file:
        content = file.read()
except FileNotFoundError:
    print('파일을 찾을 수 없다')
except PermissionError:
    pirnt('파일 접근 권한이 없다')
```


## network i/o


## database i/o