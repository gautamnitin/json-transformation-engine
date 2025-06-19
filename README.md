# JSON Transformation Framework

This project demonstrates a Java-based framework for transforming JSON using Freemarker. It includes features like:
- Template caching and dynamic reloading
- Pre- and post-processing hooks
- JSON schema validation
- RESTful integration using Spring Boot
- Performance metrics logging

## How to Build and Run

1. Ensure you have a compatible JDK installed.
2. Use Maven or Gradle to build the project:
    - Maven: `mvn clean package`
    - Gradle: `gradle build`
3. Run the Spring Boot application (if using the REST controller):
    - `mvn spring-boot:run`
4. You can test the transformer via the `/transform` endpoint.


## Use following to test using swagger
http://localhost:8080/swagger-ui/index.html
- Use sample json which is under resource folder to test from
  above swagger