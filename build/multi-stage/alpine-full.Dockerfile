# bitflowstream/bitflow-pipeline-java
# Build from repository root directory:
# docker build -t bitflowstream/bitflow-pipeline-java -f build/multi-stage/alpine-full.Dockerfile .
FROM maven:3.6-jdk-11 as build
WORKDIR /build-bitflow4j
COPY . .
RUN mvn clean package
RUN cp target/bitflow4j-*-jar-with-dependencies.jar /bitflow4j.jar

FROM azul/zulu-openjdk-alpine:11.0.7-jre-headless
# FROM openjdk:11.0.7-jre-slim-buster
# FROM openjdk:11-jre-slim
COPY --from=build /bitflow4j.jar /
COPY --from=bitflowstream/bitflow-pipeline:static /bitflow-pipeline /
ENTRYPOINT ["/bitflow-pipeline", "-exe", "java;java;-jar /bitflow4j.jar"]
