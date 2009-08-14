<%@include file="/WEB-INF/jsp/include.jsp"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Welcome - Create Account</title>
</head>
<body>

<form method="POST" action="createAcct.htm"><input
	type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}">

<h2>Step 1 of 3: Create Account</h2>
<br />
<br />
<p>Please provide a name for your DuraCloud account:</p>
<input type="text" name="acctName" /><br />
<br />
<br />
<p>Please provide a username and password:</p>
<table>
	<tr>
		<td><label for="username">Username</label></td>
		<td><input type="text" name="username" id="username" /></td>
	<tr>
		<td><label for="password">Password</label></td>
		<td><input type="password" name="password" id="password" /></td>
</table>
<br />
<br />

<input type="submit" class="button" name="_eventId_submit"
	value="Continue" /> <input type="submit" class="button"
	name="_eventId_cancel" value="Cancel" /></form>



</body>
</html>