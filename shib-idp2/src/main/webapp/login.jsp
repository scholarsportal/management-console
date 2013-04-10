<%@ taglib uri="urn:mace:shibboleth:2.0:idp:ui" prefix="idpui" %>

<%@ page import="java.net.URL" %>
<%@ page import="java.io.InputStream" %>
<%@ page import="java.util.Properties" %>
<%@ page import="java.lang.String" %>

<%
URL myURL=application.getResource("/WEB-INF/app.properties");
InputStream in = myURL.openStream();
Properties p = new Properties();
p.load( in );

String mcHost = (String)p.getProperty("managementConsoleHost");
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
    <div id="error-holder">
      <% if ("true".equals(request.getAttribute("loginFailed"))) { %>
        <div class="global-errors">
          <p>The username/password combination is not valid.<br/>
          Please reenter your credentials.</p>
        </div>
      <% } %>
    </div>

    <div class="title"><h1>Login</h1></div>
       <%
    	String action = (String)request.getAttribute("actionUrl");
        if(action == null) { 
            action = "j_security_check";
		}
		%>
    <div class="form login">
		
		<form
        id="loginForm"
        action="<%=action%>"
        method="POST">
        <fieldset>
          <ol>
            <li><label for="username">Username</label> <input
              name="j_username"
              type="text"
              autofocus="true"
              id="username" /></li>

            <li><label>Password</label> <input
              name="j_password"
              type="password"
              id="password" /></li>
          </ol>
        </fieldset>

        <fieldset>
          <button
            class="primary"
            type="submit"
	    value="Login"
            id="login-button">Login</button>
        </fieldset>
      </form>
      <ul class="horizontal-list">

<li><a href="http://<%=mcHost%>/ama/users/forgot-password">Forgot
            Password</a>&nbsp; </li><li>&nbsp; <a href="http://<%=mcHost%>/ama/users/new" id="new-user-link">Create New
            Profile</a></li>
      </ul>
    </div>
  </div>
  <div class="ft">
    <div id="footer-content">
      <div id="footer-links" class="float-l">
        <ul class="horizontal-list">
          <li><a
            target="_blank"
            href="http://www.duracloud.org">DuraCloud</a></li>
          <li><a
            target="_blank"
            href="http://www.duraspace.org">DuraSpace</a></li>
          <li><a
            target="_blank"
            href="https://wiki.duraspace.org/display/DURACLOUD/DuraCloud+Help+Center">Help
              Center</a></li>
          <li><a
            target="_blank"
            href="mailto:info@duracloud.org">Contact Us</a></li>
        </ul>
      </div>
  </div>
  </div>
<%--
           <div id="spName"><idpui:serviceName/></div>
           <!-- pick the logo.  If its between 64 & max width/height display it
                If its too high but OK wide clip by height
                If its too wide clip by width.
                We should not clip by height and width since that skews the image.  Too high an image will just show the top.
            -->
           <idpui:serviceLogo  minWidth="64" minHeight="64" maxWidth="350" maxHeight="147" cssId="splogo">
              <idpui:serviceLogo  minWidth="64" minHeight="64" maxWidth="350" cssId="clippedsplogoY">
                  <idpui:serviceLogo  minWidth="64" minHeight="64" cssId="clippedsplogoX"/>
              </idpui:serviceLogo>
           </idpui:serviceLogo>
           <div id="spDescription">
             <idpui:serviceDescription>You have asked to login to <idpui:serviceName/></idpui:serviceDescription>
           </div>
--%>

  </body>
</html>

