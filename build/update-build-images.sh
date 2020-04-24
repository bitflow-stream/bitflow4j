#!/bin/bash

docker build -t bitflowstream/java-build -f build.Dockerfile .
docker push bitflowstream/java-build
