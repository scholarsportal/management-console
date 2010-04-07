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
				<script type="text/javascript">
					dojo.require("duracloud.durastore");

					dojo.addOnLoad(function(){
						dojo.query(".space-list-item").forEach(
							function(item){
						    	dojo.connect(item, 'onmouseover', function() {
									var nodeId;
									dojo.query("[id*='metadata-div-']",item).forEach(function(div){
										nodeId = div.id;
									});
									duracloud.durastore.loadSpaceMetadata(nodeId,item.id);
						    	});
							});
						});
				</script>
				<c:forEach items="${spaces}" var="spaceId" varStatus="status">
				
					<div  id="${spaceId}" class="space-list-item actionable-item" >
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
			</tiles:putAttribute>
		</tiles:insertDefinition>
	</tiles:putAttribute>
</tiles:insertDefinition>
