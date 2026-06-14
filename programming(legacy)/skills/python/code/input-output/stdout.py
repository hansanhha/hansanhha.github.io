import sys

with open('stdout_output.txt', 'w') as file:
    sys.stdout = file # 표준 출력을 파일로 변경
    print('hello python')
    print('standard output is redirected')