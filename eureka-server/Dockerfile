FROM eclipse-temurin:21-jre
WORKDIR /app
CMD ["mvn clean package"]
COPY target/eureka-server-*.jar app.jar
EXPOSE 8761
ENV JAVA_OPTS="-Dserver.port=8761"
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]


