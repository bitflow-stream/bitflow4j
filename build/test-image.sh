#!/usr/bin/env sh

test $# = 1 || { echo "Need 1 parameter: image tag to test"; exit 1; }
IMAGE="bitflowstream/bitflow-pipeline-java"
TAG="$1"

# Sanity check: image starts, outputs expected line, and exits cleanly.
2>&1 docker run "$IMAGE:$TAG" 'closed://- -> java(step=echo, args={msg="hello world"})' |\
    tee /dev/stderr |\
    grep '\[java/echo\] hello world' > /dev/null

# Sanity check 2: Test -fork-tag functionality.
2>&1 docker run "$IMAGE:$TAG" 'closed://- -> java(step=echo, args={msg="hello world"}, exe-args=["-fork-tag", "test"])' |\
    tee /dev/stderr |\
    grep '\[java/echo\] hello world' > /dev/null
