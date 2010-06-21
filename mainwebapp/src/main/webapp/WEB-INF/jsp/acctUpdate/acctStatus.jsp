<%@include file="/WEB-INF/jsp/include.jsp"%>

<html>
<head>
<title>Status of Customer Account</title>

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
</head>
<body>

<h2><c:out value="${wrapper.duraAcct.accountName}" /> Details:</h2>
<table>
	<tr>
		<td>
		<h5>Registered Users</h5>
		</td>
	</tr>

	<c:forEach items="${wrapper.users}" var="user">
		<tr>
			<td class="user"><c:out value="${user.lastname}" />, <c:out
				value="${user.firstname}" /></td>
		</tr>
	</c:forEach>

	<c:if test="${ !empty wrapper.duraAcct}">
		<form method="GET" action="computeStatus.htm"><c:choose>
			<c:when test="${ !empty wrapper.computeAccts}">
				<tr>
					<td>
					<h5>Compute Accounts</h5>
					</td>
				</tr>
				<c:forEach items="${wrapper.computeAccts}" var="computeAcct">
					<tr>
						<td><input type="radio" name="computeAcctId"
							value="${computeAcct.id}" /></td>
						<td class="computeAcct"><c:out
							value="${computeAcct.computeProviderType}" /></td>
					</tr>
				</c:forEach>
				<td><input type="submit" class="button" name="cmd"
					value="View Status" /></td>
			</c:when>
			<c:otherwise>
				<tr>
					<td>No compute accounts :-(</td>
				</tr>
			</c:otherwise>

		</c:choose></form>

		<tr />
		<tr />
			<form method="GET" action="storageStatus.htm"><c:choose>
				<c:when test="${ !empty wrapper.storageAccts}">
					<tr>
						<td>
						<h5>Storage Accounts</h5>
						</td>
					</tr>
					<c:forEach items="${wrapper.storageAccts}" var="storageAcct">
						<tr>
							<td><input type="radio" name="storageAcctId"
								value="${storageAcct.id}" /></td>
							<td class="storageAcct"><c:out
								value="${storageAcct.storageProviderType}" /></td>
						</tr>
					</c:forEach>
					<tr>
						<td><input type="submit" class="button" name="cmd"
							value="View Status"</td>
					</tr>
				</c:when>
				<c:otherwise>
					<tr>
						<td>No storage accounts :-(</td>
					</tr>
				</c:otherwise>
			</c:choose></form>
	</c:if>
	<tr>
		<td>
		<h5>Create more accounts?</h5>
		</td>
	</tr>
	<tr>
		<td>Compute account</td>
		<td>
		<form method="GET" action="computeCreate.htm"><input
			type="hidden" name="duraAcctId" value="${wrapper.duraAcct.id}" /><input
			type="submit" class="button" value="Create" /></form>
		</td>
	</tr>
	<tr>
		<td>Storage account</td>
		<td>
		<form method="GET" action="storageCreate.htm"><input
			type="hidden" name="duraAcctId" value="${wrapper.duraAcct.id}" /><input
			type="submit" class="button" value="Create" /></form>
		</td>
	</tr>
</table>
</body>
</html>