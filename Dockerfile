FROM ubuntu:18.04

RUN git clone https://github.com/bitflow-stream/bitflow4j.git
RUN mvn install

