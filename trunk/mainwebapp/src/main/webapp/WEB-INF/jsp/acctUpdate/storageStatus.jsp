<%@include file="/WEB-INF/jsp/include.jsp"%>

<html>
<head>
<title>Storage Account Status</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<style type="text/css">
td.italic {
	font-style: italic
}
</style>
</head>
<body>

<form method="POST" action="storageStatus.htm"><!-- Add credentials to command object -->
<c:if test="${ !empty input.storageAcct.id}">
	<input type="hidden" name="storageAcctId"
		value="${input.storageAcct.id}" />
</c:if>


<h2>Storage Account Properties</h2>
<table>
	<tr>
		<td>Storage Provider:</td>
		<td><a href="${input.storageProvider.url}"><c:out
			value="${input.storageProvider.providerName}" /></a></td>
	</tr>
	<tr>
		<td>Account Namespace:</td>
		<td class="italic"><c:out value="${input.storageAcct.namespace}" /></td>
	</tr>
	<tr>
		<td>Is Primary Account?:</td>
		<td class="italic"><c:out
			value="${input.storageAcct.isPrimaryAsString}" /></td>
	</tr>

</table>
<br />
<br />
<a href="home.htm">Home</a></form>
</body>
</html>