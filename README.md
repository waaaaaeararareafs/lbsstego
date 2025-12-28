README
# Стеганография в BMP (LSB + XOR)

Программа для скрытия файлов в BMP-изображениях.

## Как запустить

### 1. Скомпилировать
bash
javac *.java


### 2. Скрыть файл
bash
java Main hide image.bmp secret.txt result.bmp password


### 3. Извлечь файл
bash
java Main extract result.bmp extracted.txt password


## Команды
- hide - скрыть файл в BMP
- extract - извлечь файл из BMP
- <пароль> - ключ для шифрования

## Пример
bash
java Main hide cat.bmp message.txt secret_cat.bmp 12345
java Main extract secret_cat.bmp out.txt 12345


## Требования
- Java 8+
- 24-битное BMP без сжатия

