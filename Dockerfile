# Этап 1: Сборка WAR файла
FROM maven:3.9.9-amazoncorretto-21 AS build

 # Устанавливаем рабочую директорию внутри контейнера
WORKDIR /app

# Копируем pom.xml и скачиваем зависимости
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Копируем исходный код и собираем проект
COPY src ./src
RUN mvn clean package -DskipTests

# Этап 2: Запуск в Tomcat
FROM tomcat:10-jdk21-temurin

# Удаляем дефолтные приложения Tomcat
RUN rm -rf /usr/local/tomcat/webapps/*

# Копируем WAR файл
COPY --from=build /app/target/my-blog.war /usr/local/tomcat/webapps/ROOT.war

# Открываем порт 8080
EXPOSE 8080

# Запускаем Tomcat
CMD ["catalina.sh", "run"]