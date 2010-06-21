copy pom.xml pom.xml.bak

REM - DuraCloud -
call pax-import-bundle -g org.duracloud -a common -v 1.0.0 -- -DimportTransitive -DwidenScope

REM - Other -
call pax-import-bundle -g org.slf4j -a com.springsource.slf4j.log4j -v 1.5.0 -- -DimportTransitive -DwidenScope
call pax-import-bundle -g com.thoughtworks.xstream -a com.springsource.com.thoughtworks.xstream -v 1.3.0 -- -DimportTransitive -DwidenScope

call mvn clean pax:provision -Dmaven.test.skip=true

copy pom.xml pom-run.xml
move pom.xml.bak pom.xml

