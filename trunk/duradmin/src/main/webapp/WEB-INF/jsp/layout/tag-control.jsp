<%@include file="/WEB-INF/jsp/include.jsp" %>

<tiles:importAttribute name="spaceId"/>
<tiles:importAttribute name="contentId" ignore="true"/>
<tiles:importAttribute name="tags" ignore="true"/>

<tiles:insertTemplate template="/WEB-INF/jsp/layout/box-control.jsp">
	<tiles:putAttribute name="title">
		<spring:message code="tags"/>
	</tiles:putAttribute>
	<tiles:putAttribute name="miniform">
		
		<form action="<c:url value="/spaces/tag/add"/>" method="post" >
			<input type="hidden" name="spaceId" value="${spaceId}"/>
			<input type="hidden" name="contentId" value="${contentId}"/>
			<input type="hidden" name="returnTo" value="${currentUrl}"/>
			<input type="text" name="tag" type="text" size="13"/> 
			<input type="submit" value="Add"/>
			<input type="button" onclick="hideMiniform(event)" value="<spring:message code='cancel'/>"/>					
		</form>
	</tiles:putAttribute>
	<tiles:putAttribute name="body">
		<c:choose>
			<c:when test="${not empty tags}">
					<c:forEach items="${tags}" var="tag" varStatus="status">
						
						<div class="small tag">
							<input type="button"  class="small minibutton"  value="x" onclick="removeTag('${spaceId}', '${tag}','${contentId}', this.parentNode);"/>
							${tag}  
						</div>
					</c:forEach>					
			</c:when>
			<c:otherwise>
				No tags defined.
			</c:otherwise>
		</c:choose>
	
	</tiles:putAttribute>
</tiles:insertTemplate>
