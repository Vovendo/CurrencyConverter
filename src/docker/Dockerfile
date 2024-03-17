FROM openjdk:17-oracle

ARG JAR_FILE=CurrencyConverter-0.0.1-SNAPSHOT.jar

COPY ${JAR_FILE} application.jar

ENTRYPOINT ["java", "-jar", "application.jar"]