<%@include file="/WEB-INF/jsp/include.jsp"%>

<html>
<head>
<c:if test="${ !empty computeAcct}">
	<c:if test="${computeAcct.instanceBooting}">
		<title>Please wait... system is coming up.</title>
		<meta http-equiv="refresh"
			content="60;computeStatus.htm?computeAcctId=${computeAcct.id}&cmd=Refresh" />
	</c:if>
</c:if>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<style type="text/css">
  p {font-style: italic}
</style>
</head>
<body>

<form method="POST" action="computeStatus.htm"><!-- Add credentials to command object -->
<c:if test="${ !empty computeAcct.id}">
	<input type="hidden" name="computeAcctId" value="${computeAcct.id}" />
</c:if> 

<!-- Wait for boot of instance server to complete. --> 
<c:choose>
	<c:when test="${computeAcct.instanceBooting}">
		<h3>Please wait... system is coming up.</h3>
	</c:when>
	<c:otherwise>

		<h3>Compute account properties</h3>
		<table>


			<tr>
				<td>Instance id:</td>
				<td><c:choose>
					<c:when test="${ !empty compAcct.instanceId}">
						<c:out value="${compAcct.instanceId}" />
					</c:when>
					<c:otherwise>
						<p>No running compute instance</p>
					</c:otherwise>
				</c:choose></td>
			</tr>


			<tr>
				<td>Instance state:</td>
				<td><c:choose>
					<c:when test="${computeAcct.instanceRunning}">
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
		<c:if test="${computeAcct.webappRunning}">
			<iframe src="${computeAcct.webappURL}" width="100%" height="500px"
				frameborder="0">
			<p>Your browser does not support iframes.</p>
			</iframe>
		</c:if>
	</c:otherwise>
</c:choose></form>
</body>
</html>