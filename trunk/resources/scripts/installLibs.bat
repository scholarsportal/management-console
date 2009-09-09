REM - Install libraries not in maven2 central repository -

call mvn install:install-file -Dfile=../libs/amazon-ec2-2008-12-01-java-library.jar -DgroupId=com.amazonaws -DartifactId=ec2 -Dversion=2008-12-01 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=../libs/amazon-ec2-2008-12-01-java-library-src.jar -DgroupId=com.amazonaws -DartifactId=ec2-src -Dversion=2008-12-01 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=../libs/aws-typica-1.5.1.jar -DgroupId=com.google.code.typica -DartifactId=typica -Dversion=1.5.1 -Dpackaging=jar -DgeneratePom=true
REM call mvn install:install-file -Dfile=../libs/amazon-ec2-2008-12-01-java-library-src.jar -DgroupId=com.amazonaws -DartifactId=ec2-src -Dversion=2008-12-01 -Dpackaging=jar
call mvn install:install-file -Dfile=../libs/rackspace-java-cloudfiles-1.3.0.jar -DgroupId=com.mosso -DartifactId=cloudfiles -Dversion=1.3.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=../libs/rackspace-java-cloudfiles-1.3.0-src.jar -DgroupId=com.mosso -DartifactId=cloudfiles-src -Dversion=1.3.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=../libs/emcesu-1.2.0.90.jar -DgroupId=com.emc -DartifactId=emcesu -Dversion=1.2.0.90 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=../libs/sun-object-client-0.2.jar -DgroupId=com.sun.cloud.api -DartifactId=object-client -Dversion=0.2 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=../libs/sun-object-client-0.2-src.jar -DgroupId=com.sun.cloud.api -DartifactId=object-client-src -Dversion=0.2 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=../libs/com.springsource.org.apache.derby-10.5.1000001.764942-duracloud.jar -DgroupId=org.apache.derby -DartifactId=com.springsource.org.apache.derby -Dversion=10.5.1000001.764942-duracloud -Dpackaging=jar -DgeneratePom=true

