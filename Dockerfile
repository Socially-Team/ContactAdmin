# Use an official Java runtime as a parent image
FROM openjdk:17-jdk-alpine

# Set the working directory inside the container
WORKDIR /app

# Copy the JAR file into the container, as named app.jar within the container
COPY target/ContactAdmin-0.0.1-SNAPSHOT.jar app.jar

# Expose the port your Spring Boot app runs on
EXPOSE 8084

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]