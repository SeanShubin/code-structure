#!/usr/bin/env bash

set -e

BASE_DIR="generated/stress-test-project/depth-$1-breadth-$2"
echo "$BASE_DIR"

rm -rf "$BASE_DIR"
java -jar stress/target/code-structure-stress.jar depth "$1" breadth "$2"
pushd "$BASE_DIR" || exit
mvn package
java -jar ../../../console/target/code-structure-console.jar
popd || exit
