# Проектная работа 3 спринта

## Spring Web MVC приложение My Blog

### Технологии

```
Java 21
Spring Framework 6.2.8 (без Spring Boot)
PostgreSQL 16.9
Apache Tomcat 10
Maven
Testcontainers
Docker & Docker Compose
```

### Требования к окружению

```
Docker Desktop
Maven
JDK 21
Клонировать репозиторий: https://github.com/ksa-git-01/middle-java-sprint-3.git
```

### Запуск тестов

```
# из корня проекта:
mvn clean test
```

### Сборка и запуск

```
# из корня проекта:
docker-compose up
```

### Ресурсы проекта

```
Веб-страницы:
Лента постов: http://localhost:18080/posts
Просмотр посты: http://localhost:18080/posts/{postId}
Создание поста: http://localhost:18080/posts/add
```