# bitflowstream/bitflow-pipeline-java:latest-arm64v8
# Build from repository root directory:
# mvn package
# docker build -t bitflowstream/bitflow-pipeline-java:latest-arm64v8 -f build/arm64v8.Dockerfile .
FROM arm64v8/openjdk:11-jre-slim
WORKDIR /
ADD target/bitflow4j-*-jar-with-dependencies.jar bitflow4j.jar
ENTRYPOINT ["/bitflow-pipeline", "-exe", "java;java;-jar /bitflow4j.jar -shortlog"]
