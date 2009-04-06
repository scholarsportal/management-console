#!/bin/sh

echo "========================="
echo "Starting sanity tests...."
echo "========================="
echo ""

scriptdir=`dirname "$0"`
. "$scriptdir"/common.sh

echo "============"
echo "Compiling..."
echo "============"
echo ""
cd $BUILD_HOME
$M2_HOME/bin/mvn clean compile

if [ $? -ne 0 ]; then
  echo ""
  echo "ERROR: Failed to compile; see above"
  exit 1
fi

echo ""
echo "====================="
echo "Running unit tests..."
echo "====================="
$M2_HOME/bin/mvn package

if [ $? -ne 0 ]; then
  echo ""
  echo "ERROR: Unit test(s) failed; see above"
  exit 1
fi

echo ""
echo "===================================="
echo "Completed sanity tests successfully!"
echo "===================================="
