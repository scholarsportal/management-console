<%@include file="/WEB-INF/jsp/include.jsp"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Create Compute Account</title>
</head>
<body>

<form method="POST" action="storageCreate.htm">

<h2>Create Storage Account</h2>
<table>
	<tr>
		<td><label for="namespace">Account namespace:</label></td>
		<td><input type="text" name="storageAcctNamespace" id="namespace" /></td>
	</tr>
	<tr>
		<td><label for="primary">Account as primary:</label></td>
		<td><input type="checkbox" name="isPrimary" id="primary"
			value="1" /></td>
	</tr>
	<tr>
		<td><label for="provider">Storage provider:</label></td>
		<td><select name="storageProviderType" id="provider">
			<option value="test">Select-Provider</option>
			<c:forEach items="${storageProviders}" var="storageProvider">
				<option value="${storageProvider}"><c:out
					value="${storageProvider}" /></option>
			</c:forEach>
		</select></td>
	</tr>
</table>
<br />
<br />
<p>Storage provider credential:</p>
<table>
	<tr>
		<td>Username:</td>
		<td><input type="text" name="storageCred.username" /></td>
	</tr>
	<tr>
		<td>Password:</td>
		<td><input type="password" name="storageCred.password" /></td>
	</tr>
</table>
<br />
<br />
<input type="hidden" name="duraAcctId" value="${param.duraAcctId}" /> <input
	type="submit" class="button" name="cmd" value="Create" /> <input
	type="submit" class="button" name="cmd" value="Cancel" /></form>


</body>
</html>