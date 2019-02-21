FROM ubuntu:18.04

RUN apt-get update
RUN apt-get install git
RUN git clone https://github.com/bitflow-stream/bitflow4j.git
RUN mvn install

