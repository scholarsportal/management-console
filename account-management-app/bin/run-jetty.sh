#!/bin/bash 

export MAVEN_OPTS="-Xrunjdwp:transport=dt_socket,address=4000,server=y,suspend=n -Xms256m -Xmx1024m -XX:MaxPermSize=256m"
mvn jetty:run --offline
