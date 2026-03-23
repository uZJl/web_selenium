FROM maven:3.9.6-eclipse-temurin-11 AS builder

WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:11-jre

WORKDIR /app

# 安装必要的工具
RUN apt-get update && apt-get install -y \
    curl \
    && rm -rf /var/lib/apt/lists/*

# 复制构建产物
COPY --from=builder /app/target/web_selenium-1.0-SNAPSHOT.jar app.jar
COPY --from=builder /app/src/test/resources ./config
COPY --from=builder /app/src/test/resources/testng.xml ./testng.xml
COPY --from=builder /app/src/test/resources/testng-parallel.xml ./testng-parallel.xml

# 创建目录
RUN mkdir -p /app/allure-results /app/screenshots

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
