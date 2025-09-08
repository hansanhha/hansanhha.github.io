with open("test.txt", "r", encoding="utf-8") as file:
    print(file.tell())
    file.read(5)
    print(file.tell())