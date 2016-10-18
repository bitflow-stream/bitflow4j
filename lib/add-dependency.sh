#!/bin/bash
# These settings must be changed before executing

GROUP=com.jujutsu.tsne
ARTIFACT=tsne
VERSION=0.0.2
REPO=`pwd`
#FILE="$REPO/$GROUP/$ARTIFACT/$VERSION/$ARTIFACT-$VERSION.jar"
FILE="tsne-0.0.2.jar"

mvn install:install-file \
    "-Dfile=$FILE" \
    "-DgroupId=$GROUP" \
    "-DartifactId=$ARTIFACT" \
    "-Dversion=$VERSION" \
    "-Dpackaging=jar" \
    "-DlocalRepositoryPath=$REPO"

