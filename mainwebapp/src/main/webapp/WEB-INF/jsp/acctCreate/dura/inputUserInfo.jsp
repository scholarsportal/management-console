<%@include file="/WEB-INF/jsp/include.jsp"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Complete User Details</title>
</head>
<body>



<form method="POST" action="createAcct.htm"><input type="hidden"
	name="_flowExecutionKey" value="${flowExecutionKey}">

<h2>Step 2 of 3: Input User Particulars</h2>
<br />
<p>Please provide contact details:</p>
<table>
	<tr>
		<td><label for="firstName">First name *</label></td>
		<td><input type="text" name="firstName" id="firstName" /></td>
	</tr>
	<tr>
		<td><label for="lastName">Last name *</label></td>
		<td><input type="text" name="lastName" id="lastName" /></td>
	</tr>
	<tr>
		<td><label for="email">Email *</label></td>
		<td><input type="text" name="email" id="email" /></td>
	</tr>
	<tr>
		<td><label for="phoneWork">Phone day</label></td>
		<td><input type="text" name="phoneWork" id="phoneWork" /></td>
	</tr>
	<tr>
		<td><label for="phoneOther">Phone other</label></td>
		<td><input type="text" name="phoneOther" id="phoneOther" /></td>
	</tr>

</table>
<br />

<p>Please provide mailing address:</p>
<table>
	<tr>
		<td><label for="street1">Street1 *</label></td>
		<td><input type="text" name="street1" id="street1" /></td>
	</tr>
	<tr>
		<td><label for="street2">Street2</label></td>
		<td><input type="text" name="street2" id="street2" /></td>
	</tr>
	<tr>
		<td><label for="apt">Apt</label></td>
		<td><input type="text" name="apt" id="apt" /></td>
	</tr>
	<tr>
		<td><label for="city">City *</label></td>
		<td><input type="text" name="city" id="city" /></td>
	</tr>
	<tr>
		<td><label for="state">State * [2-letter]</label></td>
		<td><input type="text" name="state" id="state" /></td>
	</tr>
	<tr>
		<td><label for="zip">ZipCode *</label></td>
		<td><input type="text" name="zip" id="zip" /></td>
	</tr>

</table>
<br />



<input type="submit" class="button" name="_eventId_submit"
	value="Continue" /> <input type="submit" class="button"
	name="_eventId_cancel" value="Cancel" /></form>



</body>
</html>