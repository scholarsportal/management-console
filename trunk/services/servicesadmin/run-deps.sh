perl -pi.bak -e "s!<packaging>war</packaging>!<packaging>bundle</packaging>!" pom.xml

# - Spring DM -
pax-import-bundle -g org.springframework.osgi -a spring-osgi-core -v 1.2.0
pax-import-bundle -g org.springframework.osgi -a spring-osgi-extender -v 1.2.0
pax-import-bundle -g org.springframework.osgi -a spring-osgi-io -v 1.2.0
pax-import-bundle -g org.springframework.osgi -a spring-osgi-web -v 1.2.0 -DimportTransitive
pax-import-bundle -g org.springframework.osgi -a spring-osgi-web-extender -v 1.2.0 -DimportTransitive

# - DuraCloud -
pax-import-bundle -g org.duraspace -a common -v 1.0.0 -- -DimportTransitive -DwidenScope

# - Other -
pax-import-bundle -g org.slf4j -a com.springsource.slf4j.log4j -v 1.5.0 -- -DimportTransitive -DwidenScope
pax-import-bundle -g com.thoughtworks.xstream -a com.springsource.com.thoughtworks.xstream -v 1.3.0 -- -DimportTransitive -DwidenScope
pax-import-bundle -g org.apache.commons -a com.springsource.org.apache.commons.fileupload -v 1.2.0 -- -DimportTransitive -DwidenScope

perl -p -e "s!<packaging>bundle</packaging>!<packaging>war</packaging>!" pom.xml > pom-run.xml
mv pom.xml.bak pom.xml
