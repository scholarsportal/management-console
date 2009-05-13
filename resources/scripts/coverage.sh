#!/bin/sh

echo "=============================="
echo "Starting Coverage Analysis...."
echo "=============================="
echo ""

scriptdir=`dirname "$0"`
. "$scriptdir"/common.sh

echo ""
echo "================="
echo "Running Clover..."
echo "================="
$M2_HOME/bin/mvn clover2:instrument clover2:aggregate clover2:clover -P profile-clover

if [ $? -ne 0 ]; then
  echo ""
  echo "ERROR: Integration test(s) failed; see above"
#  $CATALINA_HOME/bin/shutdown.sh
  exit 1
fi

echo ""
echo "========================================="
echo "Completed Coverage Analysis Successfully."
echo "========================================="

