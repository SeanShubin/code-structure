#!/bin/bash

if [[ -z "$MAVEN_REPO_URL" ]]; then
    echo "set MAVEN_REPO_URL to something like https://oss.sonatype.org/service/local/staging/deploy/maven2"
    exit 1
fi

mvn org.apache.maven.plugins:maven-dependency-plugin:2.1:get \
    -DrepoUrl="$MAVEN_REPO_URL" \
    -Dartifact=com.seanshubin.code.structure:code-structure-console:1.0.0-SNAPSHOT
