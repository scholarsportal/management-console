copy pom.xml pom.xml.bak

# - DuraCloud -
call pax-import-bundle -g org.duracloud -a common -v 0.4.0-SNAPSHOT -- -DimportTransitive -DwidenScope
call pax-import-bundle -g org.duracloud -a storeclient -v 0.4.0-SNAPSHOT -- -DimportTransitive -DwidenScope

# - Other -
call pax-import-bundle -g org.slf4j -a com.springsource.slf4j.log4j -v 1.5.0 -- -DimportTransitive -DwidenScope
call pax-import-bundle -g org.apache.commons -a com.springsource.org.apache.commons.io -v 1.4.0 -- -DimportTransitive -DwidenScope

call mvn clean pax:provision -Dmaven.test.skip=true

copy pom.xml pom-run.xml
move pom.xml.bak pom.xml
