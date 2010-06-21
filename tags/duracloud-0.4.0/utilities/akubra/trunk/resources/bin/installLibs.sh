#!/bin/sh

mvn install:install-file -DgroupId=org.duracloud -DartifactId=common -Dversion=1.0.0 -Dpackaging=jar -Dfile=resources/lib/common-1.0.0.jar -DgeneratePom=true
mvn install:install-file -DgroupId=org.duracloud -DartifactId=storageprovider -Dversion=1.0.0 -Dpackaging=jar -Dfile=resources/lib/storageprovider-1.0.0.jar -DgeneratePom=true
mvn install:install-file -DgroupId=org.duracloud -DartifactId=storeclient -Dversion=1.0.0 -Dpackaging=jar -Dfile=resources/lib/storeclient-1.0.0.jar -DpomFile=resources/lib/storeclient-1.0.0.pom
