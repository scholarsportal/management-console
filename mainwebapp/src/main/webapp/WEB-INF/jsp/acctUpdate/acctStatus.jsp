<%@include file="/WEB-INF/jsp/include.jsp"%>

<html>
<head>
<title>Status of Customer Account</title>

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
</head>
<body>

<h2>Registered Account Users</h2>
<table>
	<c:forEach items="${wrapper.users}" var="user">
		<tr>
			<td class="user"><c:out value="${user.lastname}" />, <c:out
				value="${user.firstname}" /></td>
		</tr>
	</c:forEach>

</table>
<h2>Account Details</h2>
<table>

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
						<td class="computeAcct"><c:out
							value="${computeAcct.computeProviderType}" /></td>
						<td><input type="hidden" name="computeAcctId"
							value="${computeAcct.id}" /><input type="submit" class="button"
							name="cmd" value="View Status" /></td>
					</tr>
				</c:forEach>
			</c:when>
			<c:otherwise>
				<tr>
					<td>No compute accounts :-(</td>
				</tr>
			</c:otherwise>
		</c:choose></form>
		<form method="GET" action="storageStatus.htm"><c:choose>
			<c:when test="${ !empty wrapper.storageAccts}">
				<tr>
					<td>
					<h5>Storage Accounts</h5>
					</td>
				</tr>
				<c:forEach items="${wrapper.storageAccts}" var="storageAcct">
					<tr>
						<td class="storageAcct"><c:out
							value="${storageAcct.storageProviderType}" /></td>

						<td><input type="hidden" name="storageAcctId"
							value="${storageAcct.id}" /><input type="submit" class="button"
							name="cmd" value="View Status" /></td>
					</tr>
				</c:forEach>
			</c:when>
			<c:otherwise>
				<tr>
					<td>No storage accounts :-(</td>
				</tr>
			</c:otherwise>
		</c:choose></form>
	</c:if>
</table>
</body>
</html>