FROM maven:3.6.3 AS maven

WORKDIR /src
COPY . /src
# Compile and package the application to an executable JAR
RUN mvn clean package -Dmaven.test.skip

# For Java 11,
FROM adoptopenjdk/openjdk11:alpine-jre

ARG JAR_FILE=Server-1.0.0-jar-with-dependencies.jar

WORKDIR /opt/app

# Copy the spring-boot-api-tutorial.jar from the maven stage to the /opt/app directory of the current stage.
COPY --from=maven /src/target/${JAR_FILE} /opt/app/mdm-admin-server-1.0.0.jar

ENTRYPOINT ["java","-jar","mdm-admin-server-1.0.0.jar"]