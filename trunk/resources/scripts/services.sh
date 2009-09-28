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

SERVICESADMIN_DIR=$BUILD_HOME/services/servicesadmin

cd $SERVICESADMIN_DIR
$MVN clean install -f pom-run.xml -Dmaven.test.skip=true pax:provision >& $SERVICESADMIN_DIR/provision.log 

cd $SERVICESADMIN_DIR/runner
./run.sh >> $SERVICESADMIN_DIR/provision.log &

# Find child pid of PAX_PID (the PAX_PID will not kill the osgi-container)
PAX_PID=$!
PAX_GID=`ps -p $PAX_PID -o pgid=`

sed_cmd="s/\(\d*\)\s.*/\1/p"
CONTAINER_PID=`ps -e -o pid= -o pgid= -o comm= | grep "java.*felix.fileinstall" | grep $PAX_GID | sed -n -e $sed_cmd`
echo PAX container pid: $CONTAINER_PID >> $SERVICESADMIN_DIR/provision.log

sleep 20


echo ""
echo "==============================================================="
echo "Compiling & running unit & integration tests for DuraService..."
echo "==============================================================="
cd $BUILD_HOME/duraservice
$MVN clean install -P profile-servicetest -Dtomcat.port.default=9090 -Dlog.level.default=DEBUG

if [ $? -ne 0 ]; then
  echo ""
  echo "ERROR: DuraService Integration test(s) failed; see above"
  kill $PAX_PID
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
  kill $PAX_PID
  exit 1
fi

echo ""
echo "========================================================================="
echo "Compiling & running unit & integration tests for Services Admin Client..."
echo "========================================================================="
cd $BUILD_HOME/servicesadminclient
$MVN clean install -P profile-servicetest -Dtomcat.port.default=9090 -Dlog.level.default=DEBUG

if [ $? -ne 0 ]; then
  echo ""
  echo "ERROR: ServicesAdminClient Integration test(s) failed; see above"
  kill $PAX_PID
  exit 1
fi

echo "======================="
echo "Shutting Down Services Admin..."
echo "======================="
echo ""
kill $PAX_PID

echo ""
echo "===================================="
echo "Completed services tests successfully!"
echo "===================================="
cd $BUILD_HOME
