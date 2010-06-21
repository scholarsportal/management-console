cp pom.xml pom-orig.xml

# - Spring DM -
pax-import-bundle -g org.springframework.osgi -a spring-osgi-extender -v 1.2.0 -- -DimportTransitive -DwidenScope

cp pom.xml pom-run.xml
mv pom-orig.xml pom.xml
