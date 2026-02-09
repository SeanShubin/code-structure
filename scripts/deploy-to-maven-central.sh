#!/usr/bin/env bash

# halt the script if we encounter any errors
set -e -u -o pipefail

echo "=========================================="
echo "Deploying to Maven Central..."
echo "=========================================="
echo ""
echo "Cleaning local repository state..."

# make sure we don't inherit any state from our local repository
rm -rf ~/.m2/repository/com/seanshubin/code/structure/

# make sure we don't inherit any state from previous runs
mvn clean

echo ""
echo "Starting Maven deployment..."
echo "Requires credentials in ~/.m2/settings.xml with server id 'central'"
echo ""

# deploy with the stage profile
mvn deploy -Pstage

echo ""
echo "=========================================="
echo "✓ Deployment completed successfully!"
echo "=========================================="
echo ""
echo "Next Steps:"
echo ""
echo "1. Maven Central (36 modules):"
echo "   → Log in: https://central.sonatype.com"
echo "   → Go to 'Deployments' in left sidebar"
echo "   → Review and click 'Publish'"
echo "   → After publishing, artifacts appear in ~10-30 minutes at:"
echo "     https://central.sonatype.com/artifact/com.seanshubin.code.structure/code-structure-parent"
echo ""
echo "2. Gradle Plugin Portal:"
echo "   → Already submitted for approval (if not already approved)"
echo "   → Check status: https://plugins.gradle.org/plugin/com.seanshubin.code.structure"
echo "   → You will receive email notification when approved"
echo ""
