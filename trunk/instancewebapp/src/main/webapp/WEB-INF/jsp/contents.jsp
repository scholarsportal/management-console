<%@include file="/WEB-INF/jsp/include.jsp" %>

<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <link rel="stylesheet" type="text/css" href="style/main.css" />
  <title>Contents of <c:out value="${space.metadata.name}"/></title>
</head>
<body>

    <h2><c:out value="${space.metadata.name}"/></h2>
    <table id="content_list">
      <c:forEach items="${space.contents}" var="content">
        <tr>
          <td>
            <c:out value="${content}"/>
          </td>        
          <td>
            <form:form action="content.htm"
                       method="post"
                       target="content_target"
                       commandName="content">
              <input type="hidden" name="customerId" value="<c:out value="${space.customerId}"/>" />
              <input type="hidden" name="spaceId" value="<c:out value="${space.spaceId}"/>" />
              <input type="hidden" name="contentId" value="<c:out value="${content}"/>" />                       
              <input type='submit' value="View Properties"/>
            </form:form>
          </td>          
          <td>
            <form action="content/<c:out value="${space.customerId}/${space.spaceId}/${content}"/>">
              <input type='submit' value="Download Content"/>
            </form>
          </td>
        </tr>
      </c:forEach>
    </table>

    <iframe id='content_target' name='content_target' src='#'></iframe>

</body>
</html>