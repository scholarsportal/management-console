<%@include file="/WEB-INF/jsp/include.jsp" %>

<tiles:importAttribute name="spaceId"/>
<tiles:importAttribute name="contentId" ignore="true"/>
<tiles:importAttribute name="tags" ignore="true"/>

<tiles:insertTemplate template="/WEB-INF/jsp/layout/box-control.jsp">
	<tiles:putAttribute name="title">
		<spring:message code="tags"/>
	</tiles:putAttribute>
	<tiles:putAttribute name="titlebuttons">
		<input  type="button" class="miniform-button minibutton" value="+"/>
	</tiles:putAttribute>
	
	<tiles:putAttribute name="miniform">
		
		<form action="<c:url value="/spaces/tag/add"/>" method="post" >
			<p>
				To add multiple tags at a time, separate the values with a "|" (pipe) character.
			</p>
			<div>
			<input type="hidden" name="spaceId" value="<c:out value="${spaceId}"/>"/>
			<input type="hidden" name="contentId" value="<c:out value="${contentId}"/>"/>
			<input type="hidden" name="returnTo" value="<c:out value="${currentUrl}"/>"/>
				<input type="text" name="tag" type="text" size="13"/> 
			</div>
			<div class="miniform-buttons">
				<input type="submit" value="Add"/>
				<input type="button" onclick="hideMiniform(event)" value="<spring:message code='cancel'/>"/>					
			</div>
		</form>
	</tiles:putAttribute>
	<tiles:putAttribute name="body">
		<c:choose>
			<c:when test="${not empty tags}">
					<c:forEach items="${tags}" var="tag" varStatus="status">
						<div class="small tag">
							${tag}  
							<input style="visibility:hidden" type="button"  class="small minibutton"  value="x" onclick="removeTag('<c:out value="${spaceId}"/>', '<c:out value="${tag}"/>','<c:out value="${contentId}"/>', this.parentNode);"/>
						</div>
					</c:forEach>					
			</c:when>
			<c:otherwise>
				<p>No tags defined.</p>
			</c:otherwise>
		</c:choose>
	
	</tiles:putAttribute>
</tiles:insertTemplate>
