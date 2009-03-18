<%@include file="/WEB-INF/jsp/include.jsp"%>

<html>
<head>
<title>Status of Customer Account</title>

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
</head>
<body>

<form method="POST" action="computeStatus.htm">

<p /><!-- Add credentials to command object --> <c:if
	test="${ !empty duraAcct.computeAcctId}">
	<input type="hidden" name="computeAcctId"
		value="${duraAcct.computeAcctId}" />
</c:if>
<h3>Registered account users</h3>
<table>
	<c:forEach items="${duraAcct.users}" var="user">
		<tr>
			<td class="user"><c:out value="${user.lastname}" />, <c:out
				value="${user.firstname}" /></td>
		</tr>
	</c:forEach>

</table>
<h3>Account Details</h3>
<table>
	<c:choose>
		<c:when test="${ !empty duraAcct.computeAcctId}">
			<tr>
				<td><input type="submit" class="button" name="cmd"
					value="View Compute Status" /></td>
			</tr>

		</c:when>
		<c:otherwise>
			<tr>
				<td>No compute account created :-(</td>
			</tr>
		</c:otherwise>
	</c:choose>

</table>
</body>
</html>