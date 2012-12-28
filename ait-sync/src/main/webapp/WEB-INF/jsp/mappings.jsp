<%-- Copyright (c) 2009-2012 DuraSpace. All rights reserved.--%>
<%-- Author: Daniel Bernstein --%>
<%@include file="./include/libraries.jsp"%>
<html>
<body>
  <h1>Archive-It Sync: Mappings</h1>
  <c:choose>
    <c:when test="${not empty mappings}">
      <table>
        <tr>
          <th>Archive-It Account</th>
          <th>Duracloud Host</th>
          <th>Duracloud Port</th>
          <th>Space Id</th>
        </tr>
        <c:forEach
          var="m"
          items="${mappings}">
          <tr>
            <td>${m.archiveItAccountId}</td>
            <td>${m.duracloudHost}</td>
            <td>${m.duracloudPort}</td>
            <td>${m.duracloudSpaceId}</td>
          </tr>
        </c:forEach>
      </table>
    </c:when>
    <c:otherwise>
      <p>There are no mapping configured at this time.</p>
    </c:otherwise>
  </c:choose>


</body>
</html>
