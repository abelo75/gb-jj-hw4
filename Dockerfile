FROM openjdk:20
COPY out/artifacts/HW_4_DB_jar/HW_4_DB.jar /tmp/HW_4_DB.jar
WORKDIR /tmp
CMD ["java", "-jar", "HW_4_DB.jar"]