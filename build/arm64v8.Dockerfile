# bitflowstream/bitflow-pipeline-java:latest-arm64v8
# Build from repository root directory:
# mvn package
# docker build -t bitflowstream/bitflow-pipeline-java:latest-arm64v8 -f build/arm64v8.Dockerfile .
FROM arm64v8/openjdk:11-jre-slim
ADD target/bitflow4j-*-jar-with-dependencies.jar /bitflow4j.jar
COPY --from=bitflowstream/bitflow-pipeline:static-arm64v8 /bitflow-pipeline /
ENTRYPOINT ["/bitflow-pipeline", "-exe", "java;java;-jar /bitflow4j.jar -shortlog"]
