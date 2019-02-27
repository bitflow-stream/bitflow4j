
FROM ubuntu:18.04 as build_1
RUN apt-get update
RUN apt-get install -y git
RUN apt-get install -y maven
RUN git clone https://github.com/bitflow-stream/bitflow4j.git
RUN cd bitflow4j/
RUN ls
RUN mvn install

