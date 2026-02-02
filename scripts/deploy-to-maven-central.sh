#!/usr/bin/env bash

# halt the script if we encounter any errors
set -e -u -o pipefail

# make sure we don't inherit any state from our local repository
rm -rf ~/.m2/repository/com/seanshubin/code/structure/

# make sure we don't inherit any state from previous runs
mvn clean

# deploy with the stage profile
# requires credentials in ~/.m2/settings.xml with server id "central"
mvn deploy -P stage

# all done, check results at Maven Central Portal
echo artifacts published, check status at https://central.sonatype.com/publishing
