# bitflowstream/bitflow-pipeline-java:latest-arm32v7
# Build from repository root directory:
# mvn package
# docker build -t bitflowstream/bitflow-pipeline-java:latest-arm32v7 -f build/arm32v7.Dockerfile .
FROM arm32v7/openjdk:11-jre-slim
ADD target/bitflow4j-*-jar-with-dependencies.jar /bitflow4j.jar
COPY --from=bitflowstream/bitflow-pipeline:static-arm32v7 /bitflow-pipeline /
ENTRYPOINT ["/bitflow-pipeline", "-exe", "java;java;-jar /bitflow4j.jar -shortlog"]
