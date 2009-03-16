<%@include file="/WEB-INF/jsp/include.jsp" %>

<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link rel="stylesheet" type="text/css" href="style/main.css" />
    <title>Error Creating Account</title>
  </head>
  <body>
	<form:form method="post" action="/spaces.htm">
		<h3>An error prevented this page from loading correctly.</h3>

		<h5>Message</h5>
		<c:out value="${error.message}"/>
		<br/>
		<h5>Stack trace</h5>
		<c:out value="${error.stack}"/>
		<br/>

		<input type="submit" class="button" value="Return to Spaces"/>
	</form:form>
  </body>
</html>