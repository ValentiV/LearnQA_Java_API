FROM maven:3.8.5-openjdk-17
WORKDIR /tests
COPY . .
CMD mvn clean test