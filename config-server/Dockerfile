FROM eclipse-temurin:21-jre
WORKDIR /app
CMD ["mvn clean package"]
COPY target/config-server-*.jar app.jar
EXPOSE 7843
ENV JAVA_OPTS="-Dspring.cloud.config.server.native.search-locations=file:/config-repo -Deureka.client.serviceUrl.defaultZone=http://eureka-server:8761/eureka/"
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]


