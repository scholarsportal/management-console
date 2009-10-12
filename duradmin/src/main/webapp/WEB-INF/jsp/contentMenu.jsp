<%@include file="/WEB-INF/jsp/include.jsp"%>
<div class="sidebar-actions">
	<ul>
	    <li><a href="contents.htm?spaceId=${contentItem.spaceId}">Back to Space</a></li>
	    <li>
		    <a href="<c:url value="contents/add" >
		   		<c:param name="spaceId" value="${contentItem.spaceId}"/>
		   		<c:param name="returnTo" value="${returnTo}"/>
		    </c:url>">Add Content</a>
		</li>
		<li><a href="${baseURL}/${contentItem.spaceId}/${contentItem.contentId}">Download</a></li>
	</ul>
	
</div>

<div class="sidebar-actions">
	<h4>Modify Properties</h4>
    <form:form commandName="contentItem"  action="content.htm?spaceId=${contentItem.spaceId}&contentId=${contentItem.contentId}" method="post">
      <input type="hidden" name="action" value="update" />
      <p>
        <label for="mimetype">Mime Type</label>
        <form:input id="mimetype" path="contentMimetype"/>  
        <form:errors path="contentMimetype"/>
      </p>
      <p>                        
        <input type='submit' value="Modify Properties"/>
      </p>
    </form:form>    
</div>



