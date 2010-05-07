<%@include file="/WEB-INF/jsp/include.jsp"%>

<html>
<head>
<c:if test="${ !empty input.computeAcct}">
	<c:if test="${input.computeAcct.instanceBooting}">
		<title>Please wait... system is coming up.</title>
		<meta http-equiv="refresh"
			content="20;computeStatus.htm?computeAcctId=${input.computeAcct.id}&cmd=Refresh&timer=${input.timer}" />
	</c:if>
</c:if>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<style type="text/css">
p {
	font-style: italic
}
</style>
</head>
<body>

<form method="POST" action="computeStatus.htm"><!-- Add credentials to command object -->
<c:if test="${ !empty input.computeAcct.id}">
	<input type="hidden" name="computeAcctId"
		value="${input.computeAcct.id}" />
</c:if> <!-- Wait for boot of instance server to complete. --> <c:choose>
	<c:when test="${input.computeAcct.instanceBooting}">
		<h4>Please wait... system is coming up.</h4>
		<br />
		<br />
		<h3><c:out value="${input.timer}" /></h3>

	</c:when>
	<c:otherwise>

		<h2>Compute Account Properties</h2>
		<table>



			<tr>
				<td>Account namespace:</td>
				<td><c:out value="${input.computeAcct.namespace}" /></td>
			</tr>
			<tr>
				<td>Compute Provider:</td>
				<td><a href="${input.computeProvider.url}"><c:out
					value="${input.computeProvider.providerName}" /></a></td>
			</tr>
			<tr>
				<td>
				<h5>Webapp properties:</h5>
				</td>
				<c:forEach items="${input.computeAcct.properties}" var="prop">
					<tr>
						<td>.</td>
						<td><c:out value="${prop}" /></td>
					</tr>
				</c:forEach>
			</tr>

			<tr>
				<td>Instance id:</td>
				<td><c:choose>
					<c:when test="${ !empty input.computeAcct.instanceId}">
						<c:out value="${input.computeAcct.instanceId}" />
					</c:when>
					<c:otherwise>
						<p>No running compute instance</p>
					</c:otherwise>
				</c:choose></td>
			</tr>
			<tr>
				<td>Instance state:</td>
				<td><c:choose>
					<c:when test="${input.computeAcct.instanceRunning}">
						<p>Running</p>
						<td><input type="submit" class="button" name="cmd"
							value="Stop" /></td>

					</c:when>
					<c:otherwise>
						<p>Not Running</p>
						<td><input type="submit" class="button" name="cmd"
							value="Start" /></td>
					</c:otherwise>
				</c:choose></td>
			</tr>
			<tr>
				<td><a href="home.htm">Home</a></td>
			</tr>

		</table>
		<c:if test="${input.computeAcct.webappRunning}">
			<c:choose>
				<c:when test="${ !input.computeAppInitialized}">
					<input type="submit" class="button" name="cmd"
						value="View Compute Console" />
				</c:when>
				<c:otherwise>
					<iframe src="${input.spacesURL}" width="100%"
						height="500px" frameborder="0">
					<p>Your browser does not support iframes.</p>
					</iframe>
				</c:otherwise>
			</c:choose>
		</c:if>
	</c:otherwise>
</c:choose></form>
</body>
</html>