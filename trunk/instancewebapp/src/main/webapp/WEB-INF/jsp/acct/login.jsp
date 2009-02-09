<%@include file="/WEB-INF/jsp/include.jsp"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Login</title>
</head>
<body>

<form:form method="post" action="login.htm" commandName="user">


	<table>
		<tr>
			<td>
			<h3>Please login:</h3>
			</td>
		</tr>
		<tr>
			<td>Username</td>
			<td><form:input path="firstname" /></td>
		</tr>
		<tr>
			<td>Password</td>
			<td><form:password path="lastname" /></td>
		</tr>
	</table>

	<input type="submit" class="button" value="Login" />

</form:form>


</body>
</html>