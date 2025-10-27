FROM eclipse-temurin:21-jre
WORKDIR /app
COPY target/api-gateway-*.jar app.jar
COPY application.keystore /app/configuration/application.keystore
ENV PASS="q23886000"
ENV JAVA_OPTS="-Dserver.port=5317 -Dserver.ssl.enabled=true -Dserver.ssl.key-store=/app/configuration/application.keystore -Dserver.ssl.key-store-type=PKCS12 -Dserver.ssl.key-store-password=$PASS -Dserver.ssl.key-alias=server2 -Dspring.config.import=optional:configserver:http://config-server:7843 -Deureka.client.serviceUrl.defaultZone=http://eureka-server:8761/eureka/"
EXPOSE 5317
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]


