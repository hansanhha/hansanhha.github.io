message = 'hello world'

def greet():
    print(message)

if __name__ == '__main__':
    greet()

# 다른 모듈에서 import하는 경우 __name__ 변수에 모듈 파일 이름이 할당됨
print(f'현재 모듈의 __name__ 값: {__name__}')