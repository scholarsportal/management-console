#!/bin/sh

# Install libraries not in maven2 central repository

mvn install:install-file -Dfile=resources/libs/amazon-ec2-2008-12-01-java-library.jar -DgroupId=com.amazonaws -DartifactId=ec2 -Dversion=2008-12-01 -Dpackaging=jar
mvn install:install-file -Dfile=resources/libs/amazon-ec2-2008-12-01-java-library-src.jar -DgroupId=com.amazonaws -DartifactId=ec2-src -Dversion=2008-12-01 -Dpackaging=jar
mvn install:install-file -Dfile=resources/libs/aws-typica-1.5.1.jar -DgroupId=com.google.code.typica -DartifactId=typica -Dversion=1.5.1 -Dpackaging=jar
# mvn install:install-file -Dfile=resources/libs/amazon-ec2-2008-12-01-java-library-src.jar -DgroupId=com.amazonaws -DartifactId=ec2-src -Dversion=2008-12-01 -Dpackaging=jar
mvn install:install-file -Dfile=resources/libs/rackspace-java-cloudfiles-1.3.0.jar -DgroupId=com.mosso -DartifactId=cloudfiles -Dversion=1.3.0 -Dpackaging=jar
mvn install:install-file -Dfile=resources/libs/rackspace-java-cloudfiles-1.3.0-src.jar -DgroupId=com.mosso -DartifactId=cloudfiles-src -Dversion=1.3.0 -Dpackaging=jar
mvn install:install-file -Dfile=resources/libs/emcesu-0.1.jar -DgroupId=com.emc -DartifactId=emcesu -Dversion=0.1 -Dpackaging=jar
mvn install:install-file -Dfile=resources/libs/sun-object-client-0.2.jar -DgroupId=com.sun.cloud.api -DartifactId=object-client -Dversion=0.2 -Dpackaging=jar
mvn install:install-file -Dfile=resources/libs/sun-object-client-0.2-src.jar -DgroupId=com.sun.cloud.api -DartifactId=object-client-src -Dversion=0.2 -Dpackaging=jar
mvn install:install-file -Dfile=resources/libs/com.springsource.org.apache.derby-10.5.1000001.764942-duracloud.jar -DgroupId=org.apache.derby -DartifactId=com.springsource.org.apache.derby -Dversion=10.5.1000001.764942-duracloud -Dpackaging=jar

