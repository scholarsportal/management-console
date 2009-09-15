#!/bin/sh

echo "========================="
echo "Starting services tests...."
echo "========================="
echo ""

export PATH=$PATH:/opt/pax/pax-construct-1.4/bin

echo "=================="
echo "Starting Services Admin..."
echo "=================="
echo ""

cd $BUILD_HOME/services/servicesadmin
$MVN clean install -f pom-run.xml -Dmaven.test.skip=true pax:provision > provision.log &
sleep 60

echo ""
echo "==================================="
echo "Compiling & running unit & integration tests for DuraService..."
echo "==================================="
cd $BUILD_HOME/duraservice
$MVN clean install -Dtomcat.port.default=9090 -Dlog.level.default=DEBUG

echo ""
echo "==================================="
echo "Compiling & running unit & integration tests for Service Client..."
echo "==================================="
cd $BUILD_HOME/serviceclient
$MVN clean install -Dtomcat.port.default=9090 -Dlog.level.default=DEBUG

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
