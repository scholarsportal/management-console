#! /usr/bin/env bash

if [ -z "$UNIT_DATABASE_HOME" ]; then
  echo 'UNIT_DATABASE_HOME' should be set to the location of the unit database.
  exit
fi

if [ -z "$UNIT_DATABASE_PASSWORD" ]; then
  echo 'UNIT_DATABASE_PASSWORD' should be set to the boot-password of the unit database.
  exit
fi

if [ -z "$BUILD_HOME" ]; then
  echo BUILD_HOME should be set to root of duracloud projects.
  exit
fi

export CUSTOMER_LIB=${BUILD_HOME}/durastore/target/durastore-1.0.0/WEB-INF/lib
export MAIN_LIB=${BUILD_HOME}/mainwebapp/target/mainwebapp-1.0.0/WEB-INF/lib

# Set classpath to jars normally found in durastore war
export CLASSPATH=$CLASSPATH:$CUSTOMER_LIB/aopalliance-1.0.jar
export CLASSPATH=$CLASSPATH:$CUSTOMER_LIB/asm-3.1.jar
export CLASSPATH=$CLASSPATH:$CUSTOMER_LIB/cglib-2.2.jar
export CLASSPATH=$CLASSPATH:$CUSTOMER_LIB/cloudfiles-1.3.0.jar
export CLASSPATH=$CLASSPATH:$CUSTOMER_LIB/common-1.0.0.jar
export CLASSPATH=$CLASSPATH:$CUSTOMER_LIB/commons-codec-1.3.jar
export CLASSPATH=$CLASSPATH:$CUSTOMER_LIB/commons-fileupload-1.2.1.jar
export CLASSPATH=$CLASSPATH:$CUSTOMER_LIB/commons-httpclient-3.1.jar
export CLASSPATH=$CLASSPATH:$CUSTOMER_LIB/commons-io-1.4.jar
export CLASSPATH=$CLASSPATH:$CUSTOMER_LIB/commons-lang-2.4.jar
export CLASSPATH=$CLASSPATH:$CUSTOMER_LIB/commons-logging-1.0.jar
export CLASSPATH=$CLASSPATH:$CUSTOMER_LIB/commons-pool-1.3.jar
export CLASSPATH=$CLASSPATH:$MAIN_LIB/com.springsource.org.apache.derby-10.5.1000001.764942-duracloud.jar
export CLASSPATH=$CLASSPATH:$CUSTOMER_LIB/javassist-3.6.ga.jar
export CLASSPATH=$CLASSPATH:$CUSTOMER_LIB/jaxb-api-2.1.jar
export CLASSPATH=$CLASSPATH:$CUSTOMER_LIB/jaxb-impl-2.1.jar
export CLASSPATH=$CLASSPATH:$CUSTOMER_LIB/jdom-1.1.jar
export CLASSPATH=$CLASSPATH:$CUSTOMER_LIB/log4j-1.2.13.jar
export CLASSPATH=$CLASSPATH:$CUSTOMER_LIB/ognl-2.7.2.jar
export CLASSPATH=$CLASSPATH:$CUSTOMER_LIB/rackspacestorageprovider-1.0.0.jar
export CLASSPATH=$CLASSPATH:$CUSTOMER_LIB/s3storageprovider-1.0.0.jar
export CLASSPATH=$CLASSPATH:$CUSTOMER_LIB/spring-2.5.6.jar
export CLASSPATH=$CLASSPATH:$CUSTOMER_LIB/spring-aop-2.5.6.jar
export CLASSPATH=$CLASSPATH:$CUSTOMER_LIB/spring-beans-2.5.6.jar
export CLASSPATH=$CLASSPATH:$CUSTOMER_LIB/spring-context-2.5.6.jar
export CLASSPATH=$CLASSPATH:$CUSTOMER_LIB/spring-context-support-2.5.6.jar
export CLASSPATH=$CLASSPATH:$CUSTOMER_LIB/spring-core-2.5.6.jar
export CLASSPATH=$CLASSPATH:$CUSTOMER_LIB/spring-dao-2.0.8.jar
export CLASSPATH=$CLASSPATH:$CUSTOMER_LIB/spring-jdbc-2.5.6.jar
export CLASSPATH=$CLASSPATH:$CUSTOMER_LIB/spring-jms-2.5.6.jar
export CLASSPATH=$CLASSPATH:$CUSTOMER_LIB/spring-tx-2.5.6.jar
export CLASSPATH=$CLASSPATH:$CUSTOMER_LIB/spring-web-2.5.6.jar
export CLASSPATH=$CLASSPATH:$CUSTOMER_LIB/standard-1.1.2.jar
export CLASSPATH=$CLASSPATH:$CUSTOMER_LIB/stax-api-1.0-2.jar
export CLASSPATH=$CLASSPATH:$CUSTOMER_LIB/storageprovider-1.0.0.jar
export CLASSPATH=$CLASSPATH:$CUSTOMER_LIB/xbean-spring-3.5.jar
export CLASSPATH=$CLASSPATH:$CUSTOMER_LIB/commons-lang-2.4.jar

export CLASSPATH=$CLASSPATH:$BUILD_HOME/durastore/target/classes

# Just have this call here to ease the logical next action (without args it prints usage)
java -Dunit.database.home=${UNIT_DATABASE_HOME} -Dunit.database.password=${UNIT_DATABASE_PASSWORD} org.duracloud.storage.domain.test.db.UnitTestDatabaseLoaderCLI 


