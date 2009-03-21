<%@include file="/WEB-INF/jsp/include.jsp"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Account Summary</title>
<style type="text/css">
td.italic {
	font-style: italic
}
</style>
</head>
<body>

<form method="post" action="createAcct.htm">

<h2>Step 3 of 3: Review Summary</h2>

<input type="hidden" name="_flowExecutionKey"
	value="${flowExecutionKey}"> <br />


<table>
	<tr>
		<td>DuraSpace account name:</td>
		<td class="italic"><input type="text" readonly
			value="${wrapper.duraAcctName}" /></td>
	</tr>
	<tr />
	<tr />
	<tr>
		<td>DuraSpace account username:</td>
		<td><input type="text" readonly id="username"
			value="${wrapper.duraCred.username}" /></td>
	</tr>
</table>
<br />

<br />
<p>Contact details:</p>
<table>
	<tr>
		<td><label for="firstName">First name:</label></td>
		<td><input type="text" readonly id="firstName"
			value="${wrapper.user.firstname}" /></td>
	</tr>
	<tr>
		<td><label for="lastName">Last name:</label></td>
		<td><input type="text" readonly id="lastName"
			value="${wrapper.user.lastname}" /></td>
	</tr>
	<tr>
		<td><label for="email">Email:</label></td>
		<td><input type="text" readonly id="email"
			value="${wrapper.user.email}" /></td>
	</tr>
	<tr>
		<td><label for="phoneWork">Phone day:</label></td>
		<td><input type="text" readonly id="phoneWork"
			value="${wrapper.user.phoneWork}" /></td>
	</tr>
	<tr>
		<td><label for="phoneOther">Phone other:</label></td>
		<td><input type="text" readonly id="phoneOther"
			value="${wrapper.user.phoneOther}" /></td>
	</tr>

</table>
<br />

<p>Mailing address:</p>
<table>
	<tr>
		<td><label for="street1">Street1:</label></td>
		<td><input type="text" readonly id="street1"
			value="${wrapper.addrShipping.street1}" /></td>
	</tr>
	<tr>
		<td><label for="street2">Street2:</label></td>
		<td><input type="text" readonly id="street2"
			value="${wrapper.addrShipping.street2}" /></td>
	</tr>
	<tr>
		<td><label for="apt">Apt:</label></td>
		<td><input type="text" readonly id="apt"
			value="${wrapper.addrShipping.apt}" /></td>
	</tr>
	<tr>
		<td><label for="city">City:</label></td>
		<td><input type="text" readonly id="city"
			value="${wrapper.addrShipping.city}" /></td>
	</tr>
	<tr>
		<td><label for="state">State:</label></td>
		<td><input type="text" readonly id="state"
			value="${wrapper.addrShipping.state}" /></td>
	</tr>
	<tr>
		<td><label for="zip">ZipCode:</label></td>
		<td><input type="text" readonly id="zip"
			value="${wrapper.addrShipping.zip}" /></td>
	</tr>

</table>
<br />

<h4>Account information is complete and ready for creation.</h4>

<input type="submit" class="button" name="_eventId_submit"
	value="Create" /> <input type="submit" class="button"
	name="_eventId_cancel" value="Cancel" /></form>


</body>
</html>