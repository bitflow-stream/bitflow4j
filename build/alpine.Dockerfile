# bitflowstream/bitflow-pipeline-java
# Build from repository root directory:
# mvn package
# docker build -t bitflowstream/bitflow-pipeline-java -f build/alpine.Dockerfile .
FROM azul/zulu-openjdk-alpine:11.0.7-jre-headless
# FROM openjdk:11.0.7-jre-slim-buster
ADD target/bitflow4j-*-jar-with-dependencies.jar /bitflow4j.jar
COPY --from=bitflowstream/bitflow-pipeline:static /bitflow-pipeline /
ENTRYPOINT ["/bitflow-pipeline", "-exe", "java;java;-jar /bitflow4j.jar"]
