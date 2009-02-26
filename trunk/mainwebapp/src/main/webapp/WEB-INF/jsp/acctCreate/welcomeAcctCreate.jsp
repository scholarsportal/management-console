<%@include file="/WEB-INF/jsp/include.jsp"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Welcome - Create Account?</title>
</head>
<body>

<form:form>
	<input type="hidden" name="_flowExecutionKey"
		value="${flowExecutionKey}">

	<p>Would you like to create a DuraSpace account?</p>
	<input type="submit" class="button" name="_eventId_submit"
		value="Create Account" />
	<input type="submit" class="button" name="_eventId_cancel"
		value="Cancel" />

</form:form>



</body>
</html>