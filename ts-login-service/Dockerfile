FROM amazoncorretto:8


ADD ./target/ts-login-service-1.0.jar /app/
CMD ["java", "-Xmx200m", "-jar", "/app/ts-login-service-1.0.jar"]

EXPOSE 12342