<%@include file="/WEB-INF/jsp/include.jsp" %>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Account Summary</title>
</head>
<body>

	<form method="post" action="flow.htm">
	
		<input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}">
			
		<p>Account information is complete and ready for creation?</p>
		<input type="submit" class="button" name="_eventId_submit" value="Confirm"/>
		<input type="submit" class="button" name="_eventId_cancel" value="Cancel"/>
	
	
	</form>


</body>
</html>