<%@include file="/WEB-INF/jsp/include.jsp" %>


<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Error Creating Account</title>
</head>
<body>

	<form method="post" action="home.htm">
			
		<h3>There was an error with this page.</h3>
		
		<h5>Message</h5>
		<c:out value="${errors.message}"/> 
		<br/>
		<h5>Stack trace</h5>
		<c:out value="${errors.stack}"/>
		<br/>
		
		<input type="submit" class="button" value="O K"/>
	
	
	</form>


</body>
</html>