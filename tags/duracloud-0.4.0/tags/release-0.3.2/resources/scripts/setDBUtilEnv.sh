#! /usr/bin/env bash

if [ -z "$DURACLOUD_HOME" ]; then
  echo DURACLOUD_HOME should be set to: \<path\>/mainwebapp/target
  exit
fi

# Set classpath to jars normally found in mainwebapp war
export CLASSPATH=$CLASSPATH:$DURACLOUD_HOME/mainwebapp-1.0.0/WEB-INF/lib/xstream-1.3.1.jar
export CLASSPATH=$CLASSPATH:$DURACLOUD_HOME/mainwebapp-1.0.0/WEB-INF/lib/xpp3_min-1.1.4c.jar
export CLASSPATH=$CLASSPATH:$DURACLOUD_HOME/mainwebapp-1.0.0/WEB-INF/lib/storageprovider-1.0.0.jar
export CLASSPATH=$CLASSPATH:$DURACLOUD_HOME/mainwebapp-1.0.0/WEB-INF/lib/stax-api-1.0-2.jar
export CLASSPATH=$CLASSPATH:$DURACLOUD_HOME/mainwebapp-1.0.0/WEB-INF/lib/standard-1.1.2.jar
export CLASSPATH=$CLASSPATH:$DURACLOUD_HOME/mainwebapp-1.0.0/WEB-INF/lib/spring-webmvc-2.5.6.jar
export CLASSPATH=$CLASSPATH:$DURACLOUD_HOME/mainwebapp-1.0.0/WEB-INF/lib/spring-web-2.5.6.jar
export CLASSPATH=$CLASSPATH:$DURACLOUD_HOME/mainwebapp-1.0.0/WEB-INF/lib/computeprovider-1.0.0.jar
export CLASSPATH=$CLASSPATH:$DURACLOUD_HOME/mainwebapp-1.0.0/WEB-INF/lib/log4j-1.2.13.jar
export CLASSPATH=$CLASSPATH:$DURACLOUD_HOME/mainwebapp-1.0.0/WEB-INF/lib/jdom-1.1.jar
export CLASSPATH=$CLASSPATH:$DURACLOUD_HOME/mainwebapp-1.0.0/WEB-INF/lib/ec2typicacomputeprovider-1.0.0.jar
export CLASSPATH=$CLASSPATH:$DURACLOUD_HOME/mainwebapp-1.0.0/WEB-INF/lib/ec2computeprovider-1.0.0.jar
export CLASSPATH=$CLASSPATH:$DURACLOUD_HOME/mainwebapp-1.0.0/WEB-INF/lib/ec2-2008-12-01.jar
export CLASSPATH=$CLASSPATH:$DURACLOUD_HOME/mainwebapp-1.0.0/WEB-INF/lib/derby-10.4.2.0.jar
export CLASSPATH=$CLASSPATH:$DURACLOUD_HOME/mainwebapp-1.0.0/WEB-INF/lib/commons-pool-1.3.jar
export CLASSPATH=$CLASSPATH:$DURACLOUD_HOME/mainwebapp-1.0.0/WEB-INF/lib/commons-logging-1.1.1.jar
export CLASSPATH=$CLASSPATH:$DURACLOUD_HOME/mainwebapp-1.0.0/WEB-INF/lib/commons-io-1.4.jar
export CLASSPATH=$CLASSPATH:$DURACLOUD_HOME/mainwebapp-1.0.0/WEB-INF/lib/commons-dbcp-1.2.2.jar
export CLASSPATH=$CLASSPATH:$DURACLOUD_HOME/mainwebapp-1.0.0/WEB-INF/lib/commons-collections-3.2.jar
export CLASSPATH=$CLASSPATH:$DURACLOUD_HOME/mainwebapp-1.0.0/WEB-INF/lib/commons-codec-1.3.jar
export CLASSPATH=$CLASSPATH:$DURACLOUD_HOME/mainwebapp-1.0.0/WEB-INF/lib/common-1.0.0.jar
export CLASSPATH=$CLASSPATH:$DURACLOUD_HOME/mainwebapp-1.0.0/WEB-INF/lib/asm-3.1.jar
export CLASSPATH=$CLASSPATH:$DURACLOUD_HOME/mainwebapp-1.0.0/WEB-INF/lib/aopalliance-1.0.jar
export CLASSPATH=$CLASSPATH:$DURACLOUD_HOME/mainwebapp-1.0.0/WEB-INF/lib/spring-support-2.0.8.jar
export CLASSPATH=$CLASSPATH:$DURACLOUD_HOME/mainwebapp-1.0.0/WEB-INF/lib/spring-security-core-2.0.4.jar
export CLASSPATH=$CLASSPATH:$DURACLOUD_HOME/mainwebapp-1.0.0/WEB-INF/lib/spring-jdbc-2.5.6.jar
export CLASSPATH=$CLASSPATH:$DURACLOUD_HOME/mainwebapp-1.0.0/WEB-INF/lib/spring-dao-2.0.8.jar
export CLASSPATH=$CLASSPATH:$DURACLOUD_HOME/mainwebapp-1.0.0/WEB-INF/lib/spring-core-2.5.6.jar
export CLASSPATH=$CLASSPATH:$DURACLOUD_HOME/mainwebapp-1.0.0/WEB-INF/lib/spring-context-support-2.5.6.jar
export CLASSPATH=$CLASSPATH:$DURACLOUD_HOME/mainwebapp-1.0.0/WEB-INF/lib/spring-context-2.5.6.jar
export CLASSPATH=$CLASSPATH:$DURACLOUD_HOME/mainwebapp-1.0.0/WEB-INF/lib/spring-beans-2.5.6.jar
export CLASSPATH=$CLASSPATH:$DURACLOUD_HOME/mainwebapp-1.0.0/WEB-INF/lib/spring-aop-2.0.8.jar
export CLASSPATH=$CLASSPATH:$DURACLOUD_HOME/mainwebapp-1.0.0/WEB-INF/lib/spring-2.5.6.jar

export CLASSPATH=$CLASSPATH:$DURACLOUD_HOME/classes

# Just have this call here to ease the logical next action (without args it prints usage)
java org.duracloud.mainwebapp.domain.repo.db.MainDatabaseUtil
