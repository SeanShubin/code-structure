#!/usr/bin/env bash

echo "=========================================="
echo "Deploying to Maven Central (clean build)"
echo "=========================================="
echo ""

# halt the script if we encounter any errors
set -e -u -o pipefail

echo "Step 1: Cleaning local Maven repository cache..."
# make sure we don't inherit any state from our local repository
rm -rf ~/.m2/repository/com/seanshubin/code/structure/

echo "Step 2: Running mvn clean..."
# make sure we don't inherit any state from previous runs
mvn clean

echo ""
echo "Step 3: Deploying with stage profile..."
echo "(requires credentials in ~/.m2/settings.xml with server id 'central')"
echo ""
# deploy with the stage profile
mvn deploy -P stage

RESULT=$?

echo ""
echo "=========================================="
if [ $RESULT -eq 0 ]; then
    echo "✓ Deployment completed successfully!"
    echo "=========================================="
    echo ""
    echo "Next Steps:"
    echo ""
    echo "1. Log in: https://central.sonatype.com"
    echo "2. Go to 'Deployments' in left sidebar"
    echo "3. Review and click 'Publish'"
    echo "4. After publishing, artifacts appear in ~10-30 minutes at:"
    echo "   https://central.sonatype.com/search?q=com.seanshubin.code.structure"
    echo ""
else
    echo "✗ Deployment failed!"
    echo "=========================================="
    echo ""
    echo "Check the error messages above for details."
    echo ""
fi

exit $RESULT
