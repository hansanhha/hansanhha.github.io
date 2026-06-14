from all_dunder import *

print(public_func())   # '공개 함수'
print(public_var)      # '공개 변수'   

print(private_func())  # NameError (import 되지 않음)
print(private_var)     # NameError (import 되지 않음)