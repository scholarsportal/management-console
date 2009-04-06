#!/bin/sh

echo "****************************"
echo "Starting on-commit tests...."
echo "****************************"
echo ""

scriptdir=`dirname "$0"`
. "$scriptdir"/sanity.sh java5

if [ $? -ne 0 ]; then
  echo ""
  echo "ERROR: On-commit tests failed; see above"
  exit 1
fi

echo ""
echo "***************************************"
echo "Completed on-commit tests successfully!"
echo "***************************************"
