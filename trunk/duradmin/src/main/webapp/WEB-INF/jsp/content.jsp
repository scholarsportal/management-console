<%@include file="/WEB-INF/jsp/include.jsp" %>
    <table  class="property-list">
        <tr>
          <td class="label">ID</td>
          <td class="value"><c:out value="${contentItem.contentId}"/></td>
        </tr>
        <tr>
          <td class="label">Size</td>
          <td class="value"><c:out value="${contentItem.metadata.size}"/> bytes</td>
        </tr>
        <tr>
          <td class="label">MIME type</td>
          <td class="value"><c:out value="${contentItem.metadata.mimetype}"/></td>
        </tr>
        <tr>
          <td class="label">Checksum</td>
          <td class="value"><c:out value="${contentItem.metadata.checksum}"/></td>
        </tr>
        <tr>
          <td class="label">Last Modified</td>
          <td class="value"><c:out value="${contentItem.metadata.modified}"/></td>
        </tr>
    </table>
