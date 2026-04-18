# Library CLI

Это мое тестовое задание для управления книгами.

Я использовал гексагональную архитектуру: отдельно домен, сервисный слой и инфраструктура (CLI + in-memory репозиторий).

## Требования
- JDK 17 и выше
- Maven

## Запуск

```bash
git clone https://github.com/1Fiolet1/LectonTestTask.git
cd LectonTestTask
mvn clean package
java -cp target/lecton-test-task-1.0-SNAPSHOT.jar ru.seleznev.App
```

## Тесты

```bash
mvn test
```
