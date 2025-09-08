import sys

def divide(a, b):
    if (b == 0):
        sys.stderr.write('error: division by zero\n')
        return None
    return a / b

print(divide(10, 0))