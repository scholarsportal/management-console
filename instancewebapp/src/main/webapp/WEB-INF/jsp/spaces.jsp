<%@include file="/WEB-INF/jsp/include.jsp" %>

<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <link rel="stylesheet" type="text/css" href="style/main.css" />
  <title>DuraSpace Space Listing</title>
</head>
<body>

    <h2>Spaces</h2>
	<c:forEach items="${spaces}" var="space">
	  <table border="solid">
	    <tr>
	      <th>ID</th>
          <th><c:out value="${space.spaceId}"/></th>
	    </tr>
        <tr>
          <td>Name</td>
          <td><c:out value="${space.metadata.name}"/></td>
        </tr>
        <tr>
          <td>Access Setting</td>
          <td><c:out value="${space.metadata.access}"/></td>
        </tr>
        <tr>
          <td>Created</td>
          <td><c:out value="${space.metadata.created}"/></td>
        </tr>
        <tr>
          <td>Content Items</td>
          <td><c:out value="${space.metadata.count}"/></td>
        </tr>
	  </table>
	  <br />
      <form:form action="contents.htm?customerId=${space.customerId}&spaceId=${space.spaceId}"
                 method="POST"
                 enctype="multipart/form-data"
                 commandName="space">
        <input type='submit' value="List contents of <c:out value="${space.metadata.name}"/>"/>
      </form:form>
      <br />
    </c:forEach>

</body>
</html>