with open('original.jpeg', 'rb') as source, open('copy.jpeg', 'wb') as target:
    target.write(source.read())