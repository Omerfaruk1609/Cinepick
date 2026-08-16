# Stage 1: Build stage with Maven and Java 21
FROM maven:3.9-eclipse-temurin-21-alpine AS builder
WORKDIR /app

# Add Spring Milestone repository for Spring AI
RUN mkdir -p /root/.m2 && cat > /root/.m2/settings.xml <<'EOF'
<settings>
  <profiles>
    <profile>
      <id>spring-milestones</id>
      <repositories>
        <repository>
          <id>spring-milestones</id>
          <url>https://repo.spring.io/milestone</url>
          <snapshots><enabled>false</enabled></snapshots>
        </repository>
      </repositories>
      <pluginRepositories>
        <pluginRepository>
          <id>spring-milestones</id>
          <url>https://repo.spring.io/milestone</url>
          <snapshots><enabled>false</enabled></snapshots>
        </pluginRepository>
      </pluginRepositories>
    </profile>
  </profiles>
  <activeProfiles>
    <activeProfile>spring-milestones</activeProfile>
  </activeProfiles>
</settings>
EOF

COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests -B && cp target/cinepick-*.jar /app/app.jar

# Stage 2: Lightweight runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
COPY --from=builder /app/app.jar app.jar
USER appuser
EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=5s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]
