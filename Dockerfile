# ==========================================
# Stage 1: Build stage using Maven
# ==========================================
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copy the pom.xml and source code
COPY pom.xml .
COPY src ./src

# Compile and package the application
RUN mvn clean package -DskipTests

# ==========================================
# Stage 2: Production runtime stage
# ==========================================
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Copy the generated JAR file from build stage
COPY --from=build /app/target/trendsole-1.0.0.jar app.jar

# Expose port 8080 (standard Spring Boot port)
EXPOSE 8080

# Run the jar file
ENTRYPOINT ["java", "-jar", "app.jar"]
