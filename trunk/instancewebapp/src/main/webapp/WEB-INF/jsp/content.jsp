<%@include file="/WEB-INF/jsp/include.jsp" %>

<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <link rel="stylesheet" type="text/css" href="style/main.css" />
  <title><c:out value="${content.metadata.name}"/></title>
</head>
<body>

    <table  border="solid">
        <tr>
          <th>ID</th>
          <th><c:out value="${content.contentId}"/></th>
        </tr>
        <tr>
          <td>Name</td>
          <td><c:out value="${content.metadata.name}"/></td>
        </tr>
        <tr>
          <td>Size</td>
          <td><c:out value="${content.metadata.size}"/></td>
        </tr>
        <tr>
          <td>MIME type</td>
          <td><c:out value="${content.metadata.mimetype}"/></td>
        </tr>
        <tr>
          <td>Checksum</td>
          <td><c:out value="${content.metadata.checksum}"/></td>
        </tr>
        <tr>
          <td>Last Modified</td>
          <td><c:out value="${content.metadata.modified}"/></td>
        </tr>
    </table>
    
    <h4>Update Properties</h4>
    <form action="content.htm" method="post">
      <input type="hidden" name="action" value="update" />
      <input type="hidden" name="accountId" value="<c:out value="${content.accountId}"/>" />
      <input type="hidden" name="spaceId" value="<c:out value="${content.spaceId}"/>" />
      <input type="hidden" name="contentId" value="<c:out value="${content.contentId}"/>" />
      <p>
        <label for="contentName">Content Name</label>
        <input type="text" id="contentName" name="contentName" 
               value="<c:out value="${content.metadata.name}"/>" />
      </p>
      <p>
        <label for="contentMimetype">Content MIME Type</label>
        <input type="text" id="contentMimetype" name="contentMimetype" 
               value="<c:out value="${content.metadata.mimetype}"/>" />
      </p>
      <p>                        
        <input type='submit' value="Update Properties"/>
      </p>
    </form>    

</body>
</html>