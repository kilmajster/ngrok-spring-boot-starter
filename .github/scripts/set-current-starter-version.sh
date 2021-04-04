#/bin/bash

CURRENT_STARTER_VERSION=$(xmllint --xpath "//*[local-name()='project']/*[local-name()='version']/text()" ../../pom.xml)

echo "Current ngrok-spring-boot-starter version is $CURRENT_STARTER_VERSION"

sed -i "s/ci-version-placeholder/$CURRENT_STARTER_VERSION/" ../test-app/pom.xml

echo "Starter dependency version updated successfully!"
