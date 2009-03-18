<%@include file="/WEB-INF/jsp/include.jsp"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Add Address</title>
</head>
<body>

<form:form commandName="address" method="post">
	
	<p>Please complete address:</p>
	<form:input path="city"/>
	<form:input path="zip"/>
	<form:input path="street1"/>
	
	<input type="submit" class="button" name="_eventId_submit"
		value="Create Account" />
	<input type="submit" class="button" name="_eventId_cancel"
		value="Cancel" />

</form:form>



</body>
</html>