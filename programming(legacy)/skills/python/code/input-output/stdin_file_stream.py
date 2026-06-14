import sys

with open('stdin_input.txt', 'r') as file:
    sys.stdin = file
    data = input()
    print(f'read from file: {data}')