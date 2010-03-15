<%@include file="/WEB-INF/jsp/include.jsp" %>
<tiles:insertDefinition name="base-space" >
	<tiles:putAttribute name="title">
		<spring:message code="spaces" />	
	</tiles:putAttribute>
	<tiles:putAttribute name="menu">
	</tiles:putAttribute>
	<tiles:putAttribute name="main-content">
		<tiles:insertDefinition name="base-main-content">
			<tiles:putAttribute name="header">
				<tiles:insertDefinition name="base-content-header">
					<tiles:putAttribute name="title">
						<spring:message code="spaces"/>
					</tiles:putAttribute>	
					<tiles:putAttribute name="subtitle">
						<a id="addSpaceLink" href="<c:url value="/spaces/add"/>"><spring:message code="add.space"/></a>
					</tiles:putAttribute>
				</tiles:insertDefinition>
			</tiles:putAttribute>
			<tiles:putAttribute name="body">
				<!-- 
				<table class="standard" id="spacesTable">
					<tbody>

					<tr>
						<th>
							<a href="<c:url value="spaces.htm?sortField=spaceId&asc=true"/>"><spring:message code="space"/></a>
						</th>
						<th>
							<spring:message code="metadata"/>
						</th>
					</tr>
					-->
						<c:forEach items="${spaces}" var="spaceId" varStatus="status">
							<div id="${spaceId}" class="space-list-item actionable-item" onmouseover="loadSpaceMetadata('metadata-div-${status.count}', '${spaceId}');">
									<div class="list-item-header">
										<div class="space-name">
											<a href="contents.htm?spaceId=${spaceId}"><c:out value="${spaceId}" /></a>
										</div>
									
										<div class="actions" >
											<ul class="action-list">
											
											
												<li><a style="font-weight:bold" href="<c:url value="contents/add?spaceId=${spaceId}&returnTo=${currentUrl}"/>">
														<spring:message code="add.contentItem"/>
													</a>
												</li>
												
												<li><a id="removeSpaceLink" class="delete-action" href="<c:url value="removeSpace.htm">
														   		<c:param name="spaceId" value="${spaceId}"/>
														   		<c:param name="returnTo" value="${currentUrl}"/>
														    </c:url>" >
														<spring:message code="remove"/>
													</a>
												</li>
											</ul>
										</div>
									</div>
									
									<div id="metadata-div-${status.count}" class="list-item-content">
										<!--empty-->
									</div>
							</div>
						</c:forEach>
				<!-- 
					</tbody>
				</table>
				 -->

			</tiles:putAttribute>
		</tiles:insertDefinition>
	</tiles:putAttribute>
</tiles:insertDefinition>
