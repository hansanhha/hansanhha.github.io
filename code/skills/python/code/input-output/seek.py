with open("test.txt", "r", encoding="utf-8") as file:
    file.seek(10)
    print(file.read(5))