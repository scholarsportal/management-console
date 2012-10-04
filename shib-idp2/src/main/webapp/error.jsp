<%@page import="edu.internet2.middleware.shibboleth.common.profile.AbstractErrorHandler"%>
<%@ taglib uri="urn:mace:shibboleth:2.0:idp:ui" prefix="idpui" %>

<html>
  <head>
    <title>DuraSpace Identity Provider </title>

    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/login.css"/>


  </head>

  <body>
  <div id="dc-logo">
    <!-- placeholder  -->
  </div>
  <div class="clearfix">
    <!-- this is a placeholder -->
  </div>
  <div class="pane-L1-body two-thirds">
     
	<h3>ERROR</h3>
	<p>
	    An error occurred while processing your request.  Please contact your helpdesk or
	    user ID office for assistance.
	</p>
	<p>
	   This service requires cookies.  Please ensure that they are enabled and try your 
	   going back to your desired resource and trying to login again.
	</p>
	<p>
	   Use of your browser's back button may cause specific errors that can be resolved by
	   going back to your desired resource and trying to login again.
	</p>
        <p>
           If you think you were sent here in error,
           please contact <idpui:serviceContact>technical support</idpui:serviceContact>
        </p>       
	<% 
       Throwable error = (Throwable) request.getAttribute(AbstractErrorHandler.ERROR_KEY);
	   if(error != null){
	       org.owasp.esapi.Encoder esapiEncoder = org.owasp.esapi.ESAPI.encoder();
	%>
	<strong>Error Message: <%= esapiEncoder.encodeForHTML(error.getMessage()) %></strong>
	<% } %>
  </div>
</body>
</html>