<%@include file="/WEB-INF/jsp/include.jsp"%>


<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
<title>Duraspace - home</title>
</head>
<body>
<h1>Welcome to DuraSpace</h1>

<table>
	<tr>
		<td>
		<h3>Info:</h3>
		</td>
	</tr>
	<tr>
		<td><a href="aboutMarketing.htm">About DuraSpace</a></td>
	</tr>
	<tr>
		<td><a href="aboutTech.htm">About - Technical</a></td>
	</tr>
	<tr>
		<td><a href="contactUs.htm">Contact Us</a></td>
	</tr>
</table>
<br />
<br />
<table>
	<tr>
		<td>
		<h3>Accounts:</h3>
		</td>
	</tr>
	<tr>
		<td><a href="login.htm">My Account</a></td>
	</tr>
	<tr>
		<td><a href="createAcct.htm?_flowId=acct-create-flow">Create an account</a></td>
	</tr>
</table>

<br/><br/><br/><br/>
<p><c:out value="${now}" /></p>
</body>
</html>