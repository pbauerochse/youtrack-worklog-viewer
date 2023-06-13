#!/usr/bin/env bash
echo "Building artifacts for all operating systems"
PROFILES=(linux mac windows mac-silicon)
BUILD_SCRIPT_LOCATION="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"

BUILD_OUTPUT="$BUILD_SCRIPT_LOCATION/build"
mkdir -p "$BUILD_OUTPUT"
rm -f "$BUILD_OUTPUT/*"

pushd "$BUILD_SCRIPT_LOCATION"

for profile in ${PROFILES[*]} ; do
    echo "Building for $profile in $BUILD_SCRIPT_LOCATION"
    ./mvnw clean package -P $profile

    echo "Copying artifact to build directory $BUILD_OUTPUT"
    cp -v $BUILD_SCRIPT_LOCATION/application/target/youtrack-worklog-viewer-*.jar $BUILD_OUTPUT/
done

popd

echo "Done"
