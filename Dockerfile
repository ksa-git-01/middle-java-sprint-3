# Этап 1: Сборка WAR файла
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
RUN gradle clean war -x test --no-daemon

# Этап 2: Запуск в Tomcat
FROM tomcat:10-jdk21-temurin

# Удаляем дефолтные приложения Tomcat
RUN rm -rf /usr/local/tomcat/webapps/*

# Копируем WAR файл
COPY --from=build /app/build/libs/my-blog*.war /usr/local/tomcat/webapps/ROOT.war

# Открываем порт 8080
EXPOSE 8080

# Запускаем Tomcat
CMD ["catalina.sh", "run"]