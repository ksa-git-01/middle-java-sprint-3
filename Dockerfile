# Этап 1: Сборка JAR файла
FROM gradle:8.5-jdk21 AS build

# Устанавливаем рабочую директорию внутри контейнера
WORKDIR /app

# Копируем конфигурационные файлы
COPY build.gradle .
COPY settings.gradle .

# Предварительно скачиваем зависимости для кэширования
RUN gradle dependencies --no-daemon

# Копируем исходный код и собираем проект
COPY src ./src
RUN gradle clean build -x test --no-daemon

# Этап 2: Запуск Spring Boot приложения
FROM openjdk:21-jdk-slim

# Создаем директорию для приложения
WORKDIR /app

# Копируем JAR файл
COPY --from=build /app/build/libs/*.jar app.jar

# Создаем директорию для uploads
RUN mkdir -p /app/uploads

# Открываем порт 8080
EXPOSE 8080

# Запускаем Spring Boot приложение
CMD ["java", "-jar", "app.jar"]