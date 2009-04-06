#!/bin/sh

# Install libraries not in maven2 central repository

mvn install:install-file -Dfile=resources/libs/amazon-ec2-2008-12-01-java-library.jar -DgroupId=com.amazonaws -DartifactId=ec2 -Dversion=2008-12-01 -Dpackaging=jar
mvn install:install-file -Dfile=resources/libs/amazon-ec2-2008-12-01-java-library-src.jar -DgroupId=com.amazonaws -DartifactId=ec2-src -Dversion=2008-12-01 -Dpackaging=jar
mvn install:install-file -Dfile=resources/libs/rackspace-java-cloudfiles-1.3.0.jar -DgroupId=com.mosso -DartifactId=cloudfiles -Dversion=1.3.0 -Dpackaging=jar
mvn install:install-file -Dfile=resources/libs/rackspace-java-cloudfiles-1.3.0-src.jar -DgroupId=com.mosso -DartifactId=cloudfiles-src -Dversion=1.3.0 -Dpackaging=jar
