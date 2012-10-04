<%@page import="edu.internet2.middleware.shibboleth.common.profile.AbstractErrorHandler"%>

<%
  Throwable error = (Throwable) request.getAttribute(AbstractErrorHandler.ERROR_KEY);
%>

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
    <%
    if (error != null) {
        org.owasp.esapi.Encoder esapiEncoder = org.owasp.esapi.ESAPI.encoder();
    %>
    Error Message: <%= esapiEncoder.encodeForHTML(error.getMessage()) %>
    <% } %>
  </div>
</body>
</html>