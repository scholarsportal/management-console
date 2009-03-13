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

</body>
</html>