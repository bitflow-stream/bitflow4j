FROM ubuntu:18.04

RUN apt install git
RUN git clone https://github.com/bitflow-stream/bitflow4j.git
RUN mvn install

