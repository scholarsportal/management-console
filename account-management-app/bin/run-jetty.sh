#!/bin/bash 

export MAVEN_OPTS="-Xrunjdwp:transport=dt_socket,address=4000,server=y,suspend=n"
mvn jetty:run --offline