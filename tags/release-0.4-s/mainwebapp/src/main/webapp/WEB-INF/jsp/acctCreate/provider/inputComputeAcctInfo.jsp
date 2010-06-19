<%@include file="/WEB-INF/jsp/include.jsp"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Create Compute Account</title>
</head>
<body>

<form method="POST" action="computeCreate.htm">

<h2>Create Compute Account</h2>
<table>
	<tr>
		<td><label for="namespace">Account namespace:</label></td>
		<td><input type="text" name="computeAcctNamespace" id="namespace" /></td>
	</tr>
	<tr>
		<td><label for="provider">Compute provider:</label></td>
		<td><select name="computeProviderType" id="provider">
			<option value="test">Select-Provider</option>
			<c:forEach items="${computeProviders}" var="computeProvider">
				<option value="${computeProvider}"><c:out
					value="${computeProvider}" /></option>
			</c:forEach>
		</select></td>
	</tr>
</table>
<br />
<br />
<p>Compute provider credential:</p>
<table>
	<tr>
		<td>Username:</td>
		<td><input type="text" name="computeCred.username" /></td>
	</tr>
	<tr>
		<td>Password:</td>
		<td><input type="password" name="computeCred.password" /></td>
	</tr>
	<tr>
		<td>Image ID:</td>
		<td><input type="text" name="imageId" /></td>
		<td>*modify at your own risk</td>
	</tr>
</table>
<br />
<br />
<input type="hidden" name="duraAcctId" value="${param.duraAcctId}" /> <input
	type="submit" class="button" name="cmd" value="Create" /> <input
	type="submit" class="button" name="cmd" value="Cancel" /></form>


</body>
</html>