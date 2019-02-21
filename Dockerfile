FROM ubuntu:18.04

RUN git clone https://github.com/bitflow-stream/bitflow4j
RUN mvn install

