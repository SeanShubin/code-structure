#!/bin/bash

if [[ -z "$MAVEN_SNAPSHOT_URL" ]]; then
    echo "set MAVEN_SNAPSHOT_URL to something like https://oss.sonatype.org/service/local/staging/deploy/maven2"
    exit 1
fi

mvn org.apache.maven.plugins:maven-dependency-plugin:2.1:get \
    -DrepoUrl="$MAVEN_SNAPSHOT_URL" \
    -Dartifact=com.seanshubin.code.structure:code-structure-console:1.0.3
