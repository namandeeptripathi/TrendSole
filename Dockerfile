# ==========================================
# Stage 1: Build stage using Maven
# ==========================================
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copy the pom.xml and source code
COPY pom.xml .
RUN mvn dependency:go-offline -B
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

# Production optimized JVM flags for Render Free Tier (limited CPU/RAM):
# -XX:+TieredCompilation -XX:TieredStopAtLevel=1 : Uses C1 compiler for fast startup (reduces JIT CPU overhead by ~60%)
# -Xms128m -Xmx384m : Limits heap size to avoid memory thrashing on Render Free 512MB RAM
# -XX:+UseSerialGC : Uses low-overhead GC suited for single-core/small memory container environments
ENTRYPOINT ["java", \
    "-XX:+TieredCompilation", \
    "-XX:TieredStopAtLevel=1", \
    "-Xms128m", \
    "-Xmx384m", \
    "-XX:+UseSerialGC", \
    "-Dspring.profiles.active=prod", \
    "-jar", "app.jar"]
