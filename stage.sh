#!/usr/bin/env bash

# halt the script if we encounter any errors
set -e -u -o pipefail

# make sure we don't inherit any state from our local repository
rm -rf ~/.m2/repository/com/seanshubin/code/structure/

# make sure we don't inherit any state from previous runs
mvn clean

# deploy with the staging profile, depends on environment variables GPG_KEY_NAME, GPG_KEY_PASSWORD, and MAVEN_STAGING_PASSWORD
mvn deploy -P stage --settings=deploy-to-maven-central-settings.xml -Dgpg.keyname=${GPG_KEY_NAME} -Dgpg.passphrase=${GPG_KEY_PASSWORD}

# all done, emit a link describing where to check the results
echo artifacts staged, see https://oss.sonatype.org/#stagingRepositories
