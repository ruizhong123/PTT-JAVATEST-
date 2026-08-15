# --- 第一階段：使用官方 Maven 映像檔來編譯打包 ---
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# 複製設定檔與原始碼
COPY pom.xml .
COPY src ./src

# 在 Docker 內進行 Maven 打包
RUN mvn clean package -DskipTests

# --- 第二階段：使用輕量級 Java 執行環境來跑程式 ---
FROM eclipse-temurin:21-jre
WORKDIR /app

# 從第一階段把編譯好的 jar 檔案複製過來
COPY --from=build /app/target/*.jar app.jar

# 啟動應用程式
CMD ["java", "-jar", "app.jar"]