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
