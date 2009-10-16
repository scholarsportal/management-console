<%@include file="/WEB-INF/jsp/include.jsp"%>
<tiles:insertDefinition name="base-content-header">
	<tiles:putAttribute name="title">
		<spring:message code="spaces"/>
	</tiles:putAttribute>

</tiles:insertDefinition>
<div class="main-content-body">
	<table class="standard" id="spacesTable">
		<tr>
			<th><a href="<c:url value="spaces.htm?sortField=spaceId&asc=true"/>"><spring:message code="space"/></a></th>
			<th><spring:message code="metadata"/></th>
	
		</tr>
		<tbody>
			<c:forEach items="${spaces}" var="spaceId" varStatus="status">
				<tr id="${spaceId}" onmouseover="loadSpaceMetadata('metadata-div-${status.count}', '${spaceId}');">
					<td id="actionColumn">
						<div>
							<b><a href="contents.htm?spaceId=${spaceId}"><c:out value="${spaceId}" /></a></b>
						</div>
					
						<div id="actionDiv" class="actions">
							<ul>
								<li><a style="font-weight:bold" href="<c:url value="contents/add?spaceId=${spaceId}&returnTo=${returnTo}"/>">
										<spring:message code="add.contentItem"/>
									</a>
								</li>
								
								<li><a href="<c:url value="removeSpace.htm">
										   		<c:param name="spaceId" value="${spaceId}"/>
										   		<c:param name="returnTo" value="${returnTo}"/>
										    </c:url>" onclick="return confirmDeleteOperation();">
										<spring:message code="remove"/>
									</a>
								</li>
							</ul>
						</div>

						
					</td>					
					<td>
						<div id="metadata-div-${status.count}" style="min-height:2.0em; font-size:0.9em">
							<!--empty-->
						</div>
					</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
</div>
