#!/usr/bin/env bash

echo "=========================================="
echo "Publishing Gradle plugin to Maven Local..."
echo "=========================================="
echo ""

# Step 1: Install core code-structure modules
echo "Step 1: Installing core code-structure modules..."
mvn install -DskipTests

RESULT=$?

if [ $RESULT -ne 0 ]; then
    echo ""
    echo "=========================================="
    echo "✗ Maven install failed!"
    echo "=========================================="
    exit $RESULT
fi

echo ""
echo "✓ Core modules installed successfully"
echo ""

# Step 2: Publish Gradle plugin to Maven Local
echo "Step 2: Publishing Gradle plugin to Maven Local..."
cd gradle-plugin && ./gradlew publishToMavenLocal

RESULT=$?

cd ..

echo ""
echo "=========================================="
if [ $RESULT -eq 0 ]; then
    echo "✓ Gradle plugin published to Maven Local successfully!"
    echo "=========================================="
    echo ""
    echo "Usage in your test project:"
    echo ""
    echo "1. In settings.gradle.kts, add:"
    echo "   pluginManagement {"
    echo "       repositories {"
    echo "           mavenLocal()"
    echo "           gradlePluginPortal()"
    echo "       }"
    echo "   }"
    echo ""
    echo "2. In build.gradle.kts, add:"
    echo "   plugins {"
    echo "       id(\"com.seanshubin.code.structure\") version \"1.1.2\""
    echo "   }"
    echo ""
    echo "3. Run:"
    echo "   ./gradlew analyzeCodeStructure"
    echo ""
else
    echo "✗ Gradle plugin publication failed!"
    echo "=========================================="
    echo ""
    echo "Check the error messages above for details."
    echo ""
fi

exit $RESULT
