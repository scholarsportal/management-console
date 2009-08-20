#!/bin/sh

scriptdir=`dirname "$0"`
. "$scriptdir"/common.sh


sed_cmd="s/.*<localRepository>\(.*\)<\/localRepository>.*/\1/p"
localrepo=`sed -n -e $sed_cmd $SETTINGS_XML`
echo "Maven localrepository: '$localrepo'"
if [ $? -ne 0 ]; then
  echo ""
  echo "ERROR: Unable to find local repository."
  exit 1
fi

echo "Cleaning localrepository..."
rm -rf $localrepo/*

echo "Proceeding with build..."
. "$scriptdir"/on-commit.sh


