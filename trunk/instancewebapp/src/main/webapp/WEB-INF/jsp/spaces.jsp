<%@include file="/WEB-INF/jsp/include.jsp" %>

<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <link rel="stylesheet" type="text/css" href="style/main.css" />
  <title>DuraSpace Space Listing</title>
</head>
<body>

    <h2>Spaces</h2>
    
    <c:if test="${not empty error}">
      <div id="error">
        <c:out value="${error}" />
      </div>
    </c:if>
    
	<c:forEach items="${spaces}" var="space">
	  <table class="space">
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
      <form:form action="contents.htm" method="post" commandName="spaces">             
        <input type="hidden" name="accountId" value="<c:out value="${accountId}"/>" />
        <input type="hidden" name="spaceId" value="<c:out value="${space.spaceId}"/>" />
        <input type='submit' value="List contents of <c:out value="${space.metadata.name}"/>"/>
      </form:form>    
    </c:forEach>

    <h3>Add Space</h3>
    <form:form action="addSpace.htm" method="post" commandName="add_space">          
      <input type="hidden" name="accountId" value="<c:out value="${accountId}"/>" />
      Space ID <input type="text" name="spaceId" />
      <input type='submit' value="Add Space"/>
    </form:form>

</body>
</html>