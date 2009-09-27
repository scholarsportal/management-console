#!/bin/sh

echo "========================="
echo "Starting services tests...."
echo "========================="
echo ""

export PATH=$PATH:/opt/pax/pax-construct-1.4/bin

echo "=========================="
echo "Starting Services Admin..."
echo "=========================="
echo ""

cd $BUILD_HOME/services/servicesadmin/runner
run.sh >& provision.log &
sleep 60

echo ""
echo "==============================================================="
echo "Compiling & running unit & integration tests for DuraService..."
echo "==============================================================="
cd $BUILD_HOME/duraservice
$MVN clean install -P profile-servicetest -Dtomcat.port.default=9090 -Dlog.level.default=DEBUG

if [ $? -ne 0 ]; then
  echo ""
  echo "ERROR: DuraService Integration test(s) failed; see above"
  kill %1
  exit 1
fi

echo ""
echo "=================================================================="
echo "Compiling & running unit & integration tests for Service Client..."
echo "=================================================================="
cd $BUILD_HOME/serviceclient
$MVN clean install -P profile-servicetest -Dtomcat.port.default=9090 -Dlog.level.default=DEBUG

if [ $? -ne 0 ]; then
  echo ""
  echo "ERROR: ServiceClient Integration test(s) failed; see above"
  kill %1
  exit 1
fi

echo "======================="
echo "Shutting Down Services Admin..."
echo "======================="
echo ""
kill %1

echo ""
echo "===================================="
echo "Completed services tests successfully!"
echo "===================================="
cd $BUILD_HOME
