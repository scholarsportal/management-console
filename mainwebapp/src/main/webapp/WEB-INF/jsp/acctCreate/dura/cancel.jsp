<%@include file="/WEB-INF/jsp/include.jsp"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Account Creation Cancelled</title>
</head>
<body>

<table>
	<tr>
		<form method="POST" action="createAcct.htm">

		<h2>Account Creation Cancelled</h2>
		<br />
		<td><input type="hidden" name="_flowExecutionKey"
			value="${flowExecutionKey}"> <input type="submit"
			class="button" name="_eventId_start" value="Start Again?" /></td>

		</form>
		<form method="POST" action="home.htm">
		<td><input type="submit" class="button" name="_eventId_home"
			value="Home" />
		</form>
		</td>

	</tr>
</table>
</body>
</html>