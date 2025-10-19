FROM eclipse-temurin:21-jre
WORKDIR /app
CMD ["mvn clean package"]
COPY target/grammy-service.jar app.jar
COPY ../resources/truststore.jks /app/configuration/truststore.jks
ENV PASS="q23886000"
ENV JAVA_OPTS="-Djavax.net.ssl.trustStore=/app/configuration/truststore.jks -Djavax.net.ssl.trustStorePassword=$PASS"
EXPOSE 8765
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]


