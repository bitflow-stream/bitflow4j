# teambitflow/bitflow4j
# docker build -t teambitflow/bitflow4j .
FROM maven:3.6-jdk-11 as build
WORKDIR /build-bitflow4j
COPY . .
RUN mvn clean install
RUN cp /build-bitflow4j/target/bitflow4j-*-jar-with-dependencies.jar /build-bitflow4j/bitflow4j-with-dependencies.jar

FROM java:11-jre-alpine
WORKDIR /
COPY --from=build /build-bitflow4j/bitflow4j-with-dependencies.jar .
ENTRYPOINT ["java", "-jar", "bitflow4j-with-dependencies.jar"]
