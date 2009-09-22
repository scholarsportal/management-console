<%@include file="/WEB-INF/jsp/include.jsp" %>
    <table  border="solid">
        <tr>
          <th>ID</th>
          <th><c:out value="${content.contentId}"/></th>
        </tr>
        <tr>
          <td>Size</td>
          <td><c:out value="${content.metadata.size}"/> bytes</td>
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
      <input type="hidden" name="spaceId" value="<c:out value="${content.spaceId}"/>" />
      <input type="hidden" name="contentId" value="<c:out value="${content.contentId}"/>" />
      <p>
        <label for="contentMimetype">Content MIME Type</label>
        <input type="text" id="contentMimetype" name="contentMimetype" 
               value="<c:out value="${content.metadata.mimetype}"/>" />
      </p>
      <p>                        
        <input type='submit' value="Update Properties"/>
      </p>
    </form>    