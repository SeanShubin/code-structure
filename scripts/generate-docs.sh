#!/usr/bin/env bash

# halt the script if we encounter any errors
set -e -u -o pipefail

# generate documentation
mvn verify -P stage

echo documentation generated in each module\'s target directory
