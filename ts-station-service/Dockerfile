FROM amazoncorretto:8


ADD ./target/ts-station-service-1.0.jar /app/
CMD ["java", "-Xmx200m", "-jar", "/app/ts-station-service-1.0.jar"]

EXPOSE 12345