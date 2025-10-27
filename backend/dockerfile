FROM openjdk:17-jdk
WORKDIR /HamsiniTech/backend

# Copy the built jar from your target folder into the image
COPY target/backend-0.0.1-SNAPSHOT.jar app.jar

# Run the jar
ENTRYPOINT ["java", "-jar", "app.jar"]
