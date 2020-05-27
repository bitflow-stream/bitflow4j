[![Build Status](https://ci.bitflow.team/jenkins/buildStatus/icon?job=Bitflow%2Fbitflow4j%2Fmaster&build=lastBuild)](http://wally144.cit.tu-berlin.de/jenkins/blue/organizations/jenkins/Bitflow%2Fbitflow4j/activity)
[![Code Coverage](https://ci.bitflow.team/sonarqube/api/project_badges/measure?project=bitflow4j&metric=coverage)](http://wally144.cit.tu-berlin.de/sonarqube/dashboard?id=bitflow4j)
[![Maintainability](https://ci.bitflow.team/sonarqube/api/project_badges/measure?project=bitflow4j&metric=sqale_rating)](http://wally144.cit.tu-berlin.de/sonarqube/dashboard?id=bitflow4j)
[![Reliability](https://ci.bitflow.team/sonarqube/api/project_badges/measure?project=bitflow4j&metric=reliability_rating)](http://wally144.cit.tu-berlin.de/sonarqube/dashboard?id=bitflow4j)

# bitflow4j
<b>bitflow4j</b> is a plugin for [`go-bitflow`](https://github.com/bitflow-stream/go-bitflow) that allows writing and executing datastream operators in Java.
Java operators can be used inside the Bitflowscript executed by `go-bitflow`.
The main `go-bitflow` dataflow graph runs as a single Go process, while each `bitflow4j` operator is executed in a separate child process that receives input data over the standard input and produces results on the standard output.

# Installation

All commands are executed in the repository root.

### Full build (slow but stable, Alpine only)

A full Docker image for the amd64 platform (based on Alpine Linux) can be built with the following command:

```
docker build -t IMAGE_NAME -f build/multi-stage/alpine-full.Dockerfile .
```

### Cached build (ARM or Alpine)

Build the Jar file natively and then build the Docker container.
Choose a target platform, one of `[alpine|arm32v7|arm64v7]`.

```
TARGET=[arm32v7|arm64v8|alpine]
mvn install
docker build -t IMAGE_NAME -f build/$TARGET.Dockerfile .
```

# Dockerhub

Docker container images are available in the [`bitflowstream/bitflow-pipeline-java`](https://hub.docker.com/repository/docker/bitflowstream/bitflow-pipeline-java) Dockerhub repository:

```
docker pull bitflowstream/bitflow-pipeline-java
docker run -ti bitflowstream/bitflow-pipeline-java --help
```

The Docker manifest will select the appropriate platform (amd64/arm32v7/arm64v8) automatically.

# Usage

For the usage of the [`bitflowstream/bitflow-pipeline-java`](https://hub.docker.com/repository/docker/bitflowstream/bitflow-pipeline-java) container, see the [`go-bitflow`](https://github.com/bitflow-stream/go-bitflow) documentation.

`bitflow4j` allows to use the `java(step=NAME, args={ a1=v1, a2=v2 }, exe-args=JAVA_ARGS)` operator.
When starting, `bitflow4j` scans for implementations of the [`bitflow4j.ProcessingStep`](src/main/java/ProcessingStep.java) interface.
All non-abstract classes can be used as `NAME` in the `java()` operator, by default referred to through their simple class name.

Example:

```
docker run -p 8888 -ti bitflowstream/bitflow-pipeline-java ':8888 -> java(step=echo, args={msg=hello}) -> text://-'
```
