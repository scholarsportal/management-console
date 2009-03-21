<%@include file="/WEB-INF/jsp/include.jsp"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Account Successfully Created</title>
</head>
<body>

<form method="post" action="home.htm">

<h2>DuraSpace Account Successfully Created.</h2>
<br />
<p>Congratulations, your account <b><c:out
	value="${wrapper.duraAcctName}" /></b> has been created.</p>
<p>Please <a href="login.htm">login</a> to configure your Compute
and Storage accounts.</p>
<input type="submit" class="button" value="O K" /></form>


</body>
</html>