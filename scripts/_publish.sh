#!/usr/bin/env bash

echo "=========================================="
echo "Starting deployment to Maven Central..."
echo "=========================================="
echo ""

mvn deploy -Pstage

RESULT=$?

echo ""
echo "=========================================="
if [ $RESULT -eq 0 ]; then
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
else
    echo "✗ Deployment failed!"
    echo "=========================================="
    echo ""
    echo "Check the error messages above for details."
    echo ""
fi

exit $RESULT
