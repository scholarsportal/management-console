#! /usr/bin/env bash

# Install libraries not in maven2 central repository
mvn install:install-file -Dfile=resources/libs/amazon-ec2-2008-12-01-java-library.jar -DgroupId=com.amazonaws -DartifactId=ec2 -Dversion=2008-12-01 -Dpackaging=jar
mvn install:install-file -Dfile=resources/libs/amazon-ec2-2008-12-01-java-library-src.jar -DgroupId=com.amazonaws -DartifactId=ec2-src -Dversion=2008-12-01 -Dpackaging=jar

