FROM maven:3.6.3 AS maven

WORKDIR /src
COPY . /src
# Compile and package the application to an executable JAR
RUN mvn clean package -Dmaven.test.skip

# environment variable with default value
ENV SERVICE_PORT=443
ENV DOWNLOAD_PORT=8080
ENV NETWORK_INTERFACE=wlan
ENV FOLDER_PATH=/opt/app/file/history.txt
ENV KEYSTORE_PATH=/opt/app/cert/Koncept.p12
ENV KEYSTORE_PASS=pass1234
ENV TRUSTSTORE_PATH=/opt/app/cert/Koncept.p12
ENV TRUSTSTORE_PASS=pass1234
ENV NUMBER_OF_TERMINALS=10000


# For Java 11,
FROM adoptopenjdk/openjdk11:alpine-jre

ARG JAR_FILE=Server-1.0.0-jar-with-dependencies.jar

WORKDIR /opt/app

# Copy the spring-boot-api-tutorial.jar from the maven stage to the /opt/app directory of the current stage.
COPY --from=maven /src/target/${JAR_FILE} /opt/app/mdm-license-server-1.0.0.jar
#-keystore_path "$KEYSTORE_PATH" -keystore_password "$KEYSTORE_PASS" -truststore_path "$TRUSTSTORE_PATH" -truststore_password "$TRUSTSTORE_PASS"
ENTRYPOINT exec java -jar mdm-license-server-1.0.0.jar -n $NETWORK_INTERFACE -service_port $SERVICE_PORT -download_port $DOWNLOAD_PORT -path_h $FOLDER_PATH -number_terminals $NUMBER_OF_TERMINALS -keystore_path $KEYSTORE_PATH -keystore_password $KEYSTORE_PASS -truststore_path $TRUSTSTORE_PATH -truststore_password $TRUSTSTORE_PASS