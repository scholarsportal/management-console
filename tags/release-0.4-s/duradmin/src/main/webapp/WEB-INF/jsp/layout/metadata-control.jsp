<%@include file="/WEB-INF/jsp/include.jsp" %>
<tiles:importAttribute name="spaceId"/>
<tiles:importAttribute name="contentId" ignore="true"/>
<tiles:importAttribute name="metadata" ignore="true"/>

<tiles:insertTemplate template="/WEB-INF/jsp/layout/box-control.jsp">
	<tiles:putAttribute name="title">
		<spring:message code="metadata"/>
	</tiles:putAttribute>
	<tiles:putAttribute name="titlebuttons">
		<input type="button" class="minibutton miniform-button" value="+"/>
	</tiles:putAttribute>

	<tiles:putAttribute name="miniform">
		<form action="<c:url value="/spaces/metadata/add"/>" method="post" >
			<input type="hidden" name="spaceId" value="<c:out value="${spaceId}"/>"/>
			<input type="hidden" name="contentId" value="<c:out value="${contentId}"/>"/>
			<input type="hidden" name="returnTo" value="<c:out value="${currentUrl}"/>"/>
			<div>
				<input style="min-width:10em" name="name" type="text" />
				<textarea style="min-width:15em" name="value" rows="3" cols="10"></textarea>
			</div>
			<div class="miniform-buttons">
				<input type="submit" class="blocking-action" value="<spring:message code="add"/>"/>
				<input type="button" onclick="hideMiniform(event)" value="<spring:message code="cancel"/>"/>					
			</div>
			
		</form>
	</tiles:putAttribute>
	<tiles:putAttribute name="body">
			<c:choose>
				<c:when test="${not empty metadata}">
						<table class="small extended-metadata">
						<c:forEach items="${metadata}" var="m" varStatus="status">
							<tr>
								<td style="font-weight:bold">
								${m.name}:
								</td>
								<td>
									${m.value}
								</td>
								<td>
									<input class="minibutton" style="visibility:hidden"
									type="button" value="x" 
									onclick="duracloud.durastore.removeMetadataByKey('<c:out value="${spaceId}"/>', '<c:out value="${m.name}"/>','<c:out value="${contentId}"/>', this.parentNode.parentNode);"/>								
								</td>
							</tr>
						</c:forEach>					
						</table>

				</c:when>
				<c:otherwise>
					<p>No metadata defined.</p>
				</c:otherwise>
			</c:choose>
	</tiles:putAttribute>
</tiles:insertTemplate>
