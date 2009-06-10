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
        <form action="contents.htm" method="get">
          <input type="hidden" name="spaceId" value="<c:out value="${space.spaceId}"/>" />
          <input type='submit' value="List contents of <c:out value="${space.metadata.name}"/>" />
        </form>
        <form action="spaces.htm" method="post">
          <input type="hidden" name="action" value="update-name" />
          <input type="hidden" name="spaceId" value="<c:out value="${space.spaceId}"/>" />
          <input type="text" name="name" value="<c:out value="${space.metadata.name}"/>" />
          <input type='submit' value="Update Name" />
        </form>
        <form action="spaces.htm" method="post">
          <input type="hidden" name="action" value="update-access" />
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
        </form>
        <form action="spaces.htm" method="post">
          <input type="hidden" name="action" value="delete" />
          <input type="hidden" name="spaceId" value="<c:out value="${space.spaceId}"/>" />
          <input type='submit' value="Delete <c:out value="${space.metadata.name}"/>" />
        </form>
      </div>
    </c:forEach>
    </div>
    
    <div class="space_action">
      <h2>Add Space</h2>
      <form action="spaces.htm" method="post">
        <input type="hidden" name="action" value="add" />
        <p>
          <label for="spaceId">Space ID</label>
          <input type="text" id="spaceId" name="spaceId" />
        </p>
        <p>
          <label for="name">Space Name</label>          
          <input type="text" id="name" name="name" />
        </p>
        <p>
          <label for="access">Space Access</label> 
          <select id="access" name="access">
            <option value="OPEN" selected>Open</option>
            <option value="CLOSED">Closed</option>
          </select>
        </p>
        <p>
          <input type='submit' value="Add Space" />
        </p>
      </form>
    </div>
  </body>
</html>