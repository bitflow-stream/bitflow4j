# teambitflow/bitflow4j:arm-latest
FROM arm64v8/openjdk:11-jre-slim
WORKDIR /
ADD target/bitflow4j-*-jar-with-dependencies.jar bitflow4j.jar
ENTRYPOINT ["java", "-jar", "bitflow4j.jar"]

