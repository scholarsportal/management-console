July 3, 2012

This fork of the Internet2 Shibboleth Identity Provider is based off of the
following version:

URL: https://svn.shibboleth.net/java-shib-idp2/branches/REL_2
Repository Root: https://svn.shibboleth.net/java-shib-idp2
Repository UUID: 64f62902-bb16-0410-b959-baa05c2843ad
Revision: 3115

The only initial changes made to bring it into the Duraspace Management Console
baseline were to the pom.xml and bin.xml/dep.xml.

The group-id of the pom.xml was changed from 
  edu.internet2.middleware
to
  org.duracloud

Several dependency group-ids and versions were made explicit, including
  spring
  xerces
  xalan
  ant

The 'release' profile was removed.
The bin.xml assembly was renamed to dep.xml, along with the assembly plugin 
descriptor.

No functional changes to logic or Java classes were made for the initial import
of this project into the Management Console baseline.

