
FROM maven:3.6-jdk-8 as build
WORKDIR /build-bitflow4j
COPY . .
RUN mvn install

FROM java:8-jre-alpine
WORKDIR /
COPY --from=build /build-bitflow4j/target/bitflow4j-0.1-jar-with-dependencies.jar .
ENTRYPOINT ["java", "-jar", "bitflow4j-0.1-jar-with-dependencies.jar"]
