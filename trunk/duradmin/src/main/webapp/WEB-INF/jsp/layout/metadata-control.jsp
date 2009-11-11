<%@include file="/WEB-INF/jsp/include.jsp" %>
<tiles:importAttribute name="spaceId"/>
<tiles:importAttribute name="contentId" ignore="true"/>
<tiles:importAttribute name="metadata" ignore="true"/>

<tiles:insertTemplate template="/WEB-INF/jsp/layout/box-control.jsp">
	<tiles:putAttribute name="title">
		<spring:message code="metadata"/>
	</tiles:putAttribute>
	<tiles:putAttribute name="miniform">
		<form action="<c:url value="/spaces/metadata/add"/>" method="post" >
			<input type="hidden" name="spaceId" value="${spaceId}"/>
			<input type="hidden" name="contentId" value="${contentId}"/>
			<input type="hidden" name="returnTo" value="${currentUrl}"/>
			<input name="name" type="text" size="13"/> 
			<textarea name="value" rows="3" cols="13"></textarea>
			<input type="submit" value="Add" />
			<input type="button" onclick="hideMiniform(event)" value="Cancel"/>					
		</form>
	</tiles:putAttribute>
	<tiles:putAttribute name="body">
			<c:choose>
				<c:when test="${not empty metadata}">
						<c:forEach items="${metadata}" var="m" varStatus="status">
							<table class="small extended-metadata">
							<tr>
								<td >${m.name}
									<input class="minibutton" 
											type="button" value="x" 
											onclick="removeMetadataByKey('${spaceId}', '${m.name}','${contentId}', this.parentNode.parentNode.parentNode);"/>
								</td>
							</tr>
							<tr>
								<td>
									${m.value }
								</td>
							</tr>
							</table>
						</c:forEach>					
				</c:when>
				<c:otherwise>
					No metadata defined.
				</c:otherwise>
			</c:choose>
	</tiles:putAttribute>
</tiles:insertTemplate>
