# teambitflow/bitflow4j-build
# docker build -t teambitflow/bitflow4j-build -f build.Dockerfile .
FROM maven:3.6-jdk-11
WORKDIR /build-bitflow4j
COPY . .
RUN mvn clean install
RUN cp /build-bitflow4j/target/bitflow4j-*-jar-with-dependencies.jar /build-bitflow4j/bitflow4j-with-dependencies.jar
