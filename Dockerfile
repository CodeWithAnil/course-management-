FROM eclipse-temurin:17
ADD build/libs/*.jar plasma-lms-course-service.jar
ENTRYPOINT ["java","-jar","/plasma-lms-course-service.jar"]
EXPOSE 8080