FROM openjdk:17-oracle
COPY ./build/libs/mssaem_backend-0.0.1-SNAPSHOT.jar mssaem.jar
ENTRYPOINT ["java", "-jar","-Dspring.profiles.active=main","mssaem.jar"]