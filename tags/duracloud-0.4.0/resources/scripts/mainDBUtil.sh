#! /usr/bin/env bash

#-----------------------------------
# Load environment-specific settings
#-----------------------------------

scriptdir=`dirname "$0"`
. "$scriptdir"/osgi/env.sh


if [ -z "$MAIN_DATABASE_HOME" ]; then
  echo MAIN_DATABASE_HOME should be set to the location of the main database.
  exit
fi

if [ -z "$AMAZON_USER" ]; then
  echo AMAZON_USER should be set to the username of the Amazon EC2 account.
  exit
fi

if [ -z "$AMAZON_PASS" ]; then
  echo AMAZON_PASS should be set to the password of the Amazon EC2 account.
  exit
fi

if [ -z "$BUILD_HOME" ]; then
  echo BUILD_HOME should be set to root of duracloud projects.
  exit
fi

if [ -z "$PROJECT_VERSION" ]; then
  echo 'PROJECT_VERSION' should be set to the version of duracloud projects \(e.g. 0.4.0-SNAPSHOT\).
  exit
fi

export MAIN_LIB=${BUILD_HOME}/mainwebapp/target/mainwebapp-$PROJECT_VERSION/WEB-INF/lib

# Set classpath to jars normally found in mainwebapp war
export CLASSPATH=$CLASSPATH:$MAIN_LIB/aopalliance-1.0.jar
export CLASSPATH=$CLASSPATH:$MAIN_LIB/asm-3.1.jar
export CLASSPATH=$CLASSPATH:$MAIN_LIB/common-$PROJECT_VERSION.jar
export CLASSPATH=$CLASSPATH:$MAIN_LIB/commons-codec-1.3.jar
export CLASSPATH=$CLASSPATH:$MAIN_LIB/commons-io-1.4.jar
export CLASSPATH=$CLASSPATH:$MAIN_LIB/commons-lang-2.4.jar
export CLASSPATH=$CLASSPATH:$MAIN_LIB/commons-logging-1.1.1.jar
export CLASSPATH=$CLASSPATH:$MAIN_LIB/commons-pool-1.3.jar
export CLASSPATH=$CLASSPATH:$MAIN_LIB/com.springsource.org.apache.derby-10.5.1000001.764942-duracloud.jar
export CLASSPATH=$CLASSPATH:$MAIN_LIB/javassist-3.6.ga.jar
export CLASSPATH=$CLASSPATH:$MAIN_LIB/jaxb-api-2.1.jar
export CLASSPATH=$CLASSPATH:$MAIN_LIB/jaxb-impl-2.1.jar
export CLASSPATH=$CLASSPATH:$MAIN_LIB/log4j-1.2.13.jar
export CLASSPATH=$CLASSPATH:$MAIN_LIB/spring-2.5.6.jar
export CLASSPATH=$CLASSPATH:$MAIN_LIB/spring-aop-2.5.6.jar
export CLASSPATH=$CLASSPATH:$MAIN_LIB/spring-beans-2.5.6.jar
export CLASSPATH=$CLASSPATH:$MAIN_LIB/spring-context-2.5.6.jar
export CLASSPATH=$CLASSPATH:$MAIN_LIB/spring-context-support-2.5.6.jar
export CLASSPATH=$CLASSPATH:$MAIN_LIB/spring-core-2.5.6.jar
export CLASSPATH=$CLASSPATH:$MAIN_LIB/spring-dao-2.0.8.jar
export CLASSPATH=$CLASSPATH:$MAIN_LIB/spring-jdbc-2.5.6.jar
export CLASSPATH=$CLASSPATH:$MAIN_LIB/spring-web-2.5.6.jar
export CLASSPATH=$CLASSPATH:$MAIN_LIB/standard-1.1.2.jar
export CLASSPATH=$CLASSPATH:$MAIN_LIB/stax-api-1.0-2.jar
export CLASSPATH=$CLASSPATH:$MAIN_LIB/storageprovider-$PROJECT_VERSION.jar
export CLASSPATH=$CLASSPATH:$MAIN_LIB/xstream-1.3.1.jar
export CLASSPATH=$CLASSPATH:$MAIN_LIB/computeprovider-$PROJECT_VERSION.jar
export CLASSPATH=$CLASSPATH:$MAIN_LIB/common-db-$PROJECT_VERSION.jar 
export CLASSPATH=$CLASSPATH:$BUILD_HOME/mainwebapp/target/classes

# Create Main DB
java org.duracloud.mainwebapp.domain.repo.db.MainDatabaseUtil $MAIN_DATABASE_HOME $AMAZON_USER $AMAZON_PASS

