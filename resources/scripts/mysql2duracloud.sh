#!/bin/bash
############################
# This script does the following:
#    1) reads in properties files containing db and duracloud config and credentials
#    2) exports a copy of the database specified in the prop files
#    3) tar gzips the resulting .sql file.
#    4) pushes the gzipped file to a duracloud account.
#    
#  The following properties are expected to exist in the specified prop files
#  db.host,db.port,db.name,db.user,db.pass,duracloud.host,duracloud.port,
#  duracloud.user,duracloud.password, duracloud.space
#
# Author: Daniel Bernstein
#   Date: 07/09/2014

echo "Starting mysql2duracloud..."

while getopts :m:d: opt; do
  case $opt in
  m)
      mysqlPropFile=$OPTARG
      ;;
  d)
      duracloudPropFile=$OPTARG
      ;;
  esac
done

if [[ "x$mysqlPropFile" ==  "x"  || 
      "x$duracloudPropFile" == "x" ]]
then
   echo "usage: -m [mysql prop file] -d [duracloud prop file]"
   exit 1
fi

echo "loading prop files..."

for i in $mysqlPropFile $duracloudPropFile; 
do
  while read -r line; do
    key=`echo $line | cut -d = -f 1`
    val=`echo $line | cut -d = -f 2`
    #echo key = $key 
    #echo val= $val 
    case $key in 
    'db.host')
        dbHost=$val
	;;
    'db.port')
        dbPort=$val
	;;
    'db.name')
        dbName=$val
	;;
    'db.user')
        dbUser=$val
	;;
    'db.pass')
        dbPass=$val
	;;
    'duracloud.host')
        duracloudHost=$val
	;;
    'duracloud.port')
        duracloudPort=$val
	;;
    'duracloud.user')
        duracloudUser=$val
	;;
    'duracloud.password')
        duracloudPassword=$val
	;;
    'duracloud.space')
        duracloudSpace=$val
	;;
    esac 
  done <$i
done;

if [[ "x$duracloudPort" != 'x' ]]
then
  echo  duracloudPort=$duracloudPort
  duracloudPort=:$duracloudPort
fi


echo "backing up from $dbHost as $dbUser and  saving to  $duracloudHost$duracloudPort in space $duracloudSpace"

file=mc-db-dump-`date "+%Y%m%d-%H%M%S"`.sql

mysqldump --single-transaction -P $dbPort -h $dbHost -u $dbUser -p$dbPass $dbName > $file

if [ ${PIPESTATUS[0]} -ne "0" ];
then
    echo "the command \"mysqldump\" failed with error: ${PIPESTATUS[0]}";
    exit 1;
else
    echo "Database dumped successfully!";
fi

tarFile=$file.tar.gz
tar cvfz $tarFile  $file
destUrl="https://${duracloudHost}${duracloudPort}/durastore/${duracloudSpace}/$tarFile"
echo Starting upload of tar file to $destUrl

res=`curl -s -I  -u ${duracloudUser}:${duracloudPassword} -T ${tarFile} $destUrl | grep HTTP/1.1 | awk {'print $2'}`

echo "upload result \(http code\) = $res"

#check if contains 201
if [[ $res == *201* ]]
then
  echo Backup completed successfully.
  rm $tarFile $file
  exit 0;
else
  echo Backup failed - upload unsuccessful: http code = $res
  exit 1;
fi

