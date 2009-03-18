<%@include file="/WEB-INF/jsp/include.jsp"%>

<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link rel="stylesheet" type="text/css" href="style/main.css" />
    <title>DuraSpace Space Listing</title>
  </head>
  
  <body>
    <c:if test="${not empty error}">
      <div id="error"><c:out value="${error}" /></div>
    </c:if>
    
    <h2>Spaces</h2>
    
    <div class="spaces">
    <c:forEach items="${spaces}" var="space">
      <div class="space">
        <table class="space">
          <tr>
            <th>ID</th>
            <th><c:out value="${space.spaceId}" /></th>
          </tr>
          <tr>
            <td>Name</td>
            <td><c:out value="${space.metadata.name}" /></td>
          </tr>
          <tr>
            <td>Access Setting</td>
            <td><c:out value="${space.metadata.access}" /></td>
          </tr>
          <tr>
            <td>Created</td>
            <td><c:out value="${space.metadata.created}" /></td>
          </tr>
          <tr>
            <td>Content Items</td>
            <td><c:out value="${space.metadata.count}" /></td>
          </tr>
        </table>
        <form:form action="contents.htm" method="post">
          <input type="hidden" name="accountId" value="<c:out value="${accountId}"/>" />
          <input type="hidden" name="spaceId" value="<c:out value="${space.spaceId}"/>" />
          <input type='submit' value="List contents of <c:out value="${space.metadata.name}"/>" />
        </form:form>
        <form:form action="spaces.htm" method="post">
          <input type="hidden" name="action" value="update-name" />
          <input type="hidden" name="accountId" value="<c:out value="${accountId}"/>" />
          <input type="hidden" name="spaceId" value="<c:out value="${space.spaceId}"/>" />
          <input type="text" name="name" value="<c:out value="${space.metadata.name}"/>" />
          <input type='submit' value="Update Name" />
        </form:form>
        <form:form action="spaces.htm" method="post">
          <input type="hidden" name="action" value="update-access" />
          <input type="hidden" name="accountId" value="<c:out value="${accountId}"/>" />
          <input type="hidden" name="spaceId" value="<c:out value="${space.spaceId}"/>" />
          <c:choose>
            <c:when test="${space.metadata.access == 'OPEN'}">
              <input type="hidden" name="access" value="CLOSED" />
              <input type='submit' value="Set access to Closed" />
            </c:when>
            <c:otherwise>
              <input type="hidden" name="access" value="OPEN" />
              <input type='submit' value="Set access to Open" />
            </c:otherwise>
          </c:choose>
        </form:form>
        <form:form action="spaces.htm" method="post">
          <input type="hidden" name="action" value="delete" />
          <input type="hidden" name="accountId" value="<c:out value="${accountId}"/>" />
          <input type="hidden" name="spaceId" value="<c:out value="${space.spaceId}"/>" />
          <input type='submit' value="Delete <c:out value="${space.metadata.name}"/>" />
        </form:form>
      </div>
    </c:forEach>
    </div>
    
    <div class="space_action">
      <h2>Add Space</h2>
      <form:form action="spaces.htm" method="post" commandName="add_space">
        <input type="hidden" name="action" value="add" />
        <input type="hidden" name="accountId" value="<c:out value="${accountId}"/>" />
        Space ID <input type="text" name="spaceId" />
        <br />
        Space Name <input type="text" name="name" />
        <br />
        Space Access 
        <select name="access">
          <option value="OPEN" selected>Open</option>
          <option value="CLOSED">Closed</option>
        </select>
        <br />
        <input type='submit' value="Add Space" />
      </form:form>
    </div>
  </body>
</html>