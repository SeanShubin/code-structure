#!/usr/bin/env bash

set -e

BASE_DIR="generated/stress-test-project/depth-$2-breadth-$4"
echo "$BASE_DIR"

rm -rf "$BASE_DIR"
java -jar stress/target/code-structure-stress.jar "$@"
pushd "$BASE_DIR" || exit
mvn package
java -Xmx16g -jar ../../../console/target/code-structure-console.jar
popd || exit
