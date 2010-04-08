<%@include file="/WEB-INF/jsp/include.jsp"%>
<tiles:insertDefinition name="base-space">
	<tiles:putAttribute name="title">
		<spring:message code="space" />
	</tiles:putAttribute>
	<tiles:putAttribute name="menu">
		<div><tiles:insertTemplate
			template="/WEB-INF/jsp/layout/box-control.jsp">
			<tiles:putAttribute name="title">
				<spring:message code="space.details"/>
			</tiles:putAttribute>
			<tiles:putAttribute name="miniform" value="" />
			<tiles:putAttribute name="body">
				<script type="text/javascript">
					dojo.require("duracloud.durastore");
				</script>
			
				<table class="small extended-metadata">
					<tr>
						<td><spring:message code="access" /></td>
						<td><spring:message
							code="access.${fn:toLowerCase(space.metadata.access)}" /></td>
					</tr>
					<tr>
						<td><spring:message code="created" /></td>
						<td>${space.metadata.created}</td>
					</tr>
					<tr>
						<td><spring:message code="contentItem.count" /></td>
						<td>${space.metadata.count}</td>
					</tr>

				</table>
			</tiles:putAttribute>
		</tiles:insertTemplate></div>
		<!-- extended metadata -->
		<div><tiles:insertTemplate
			template="/WEB-INF/jsp/layout/metadata-control.jsp">
			<tiles:putAttribute name="spaceId" value="${space.spaceId}" />
			<tiles:putAttribute name="metadata" value="${space.extendedMetadata}" />
		</tiles:insertTemplate></div>
		<!-- tags -->
		<div><tiles:insertTemplate
			template="/WEB-INF/jsp/layout/tag-control.jsp">
			<tiles:putAttribute name="spaceId" value="${space.spaceId}" />
			<tiles:putAttribute name="tags" value="${space.metadata.tags}" />
		</tiles:insertTemplate></div>
	</tiles:putAttribute>
	<tiles:putAttribute name="main-content">


	
		<tiles:insertDefinition name="base-main-content">
			<tiles:putAttribute name="header">
				<tiles:insertDefinition name="base-content-header">
					<tiles:putAttribute name="title">
						${space.spaceId}
					</tiles:putAttribute>
					<tiles:putAttribute name="subtitle">
						<table style="margin: 0; padding: 0;">
							<tr>
								<td>

								<ul class="breadcrumb">
									<li><a href="<c:url value="/spaces.htm"/>"><spring:message
										code="spaces" /></a> </li>
								</ul>


								</td>
								<td style="text-align: right">
								<ul class="action-list">
									<li><a
										href="<c:url value="/contents/add?spaceId=${space.spaceId}"/>"><spring:message
										code="add.contentItem" /></a></li>
									<li><a spaceId="${space.spaceId}" class="update-space"
										href="<c:url value="/space/changeAccess">
											<c:param name="spaceId" value="${space.spaceId}"/>
											<c:param name="returnTo" value="${currentUrl}"/>
										</c:url>">
										<spring:message
											code="${space.metadata.access == 'OPEN' ?'close.space' : 'open.space'}" />
									</a></li>
									<li><a  spaceId="${space.spaceId}" class="remove-space delete-action" href="<c:url value="removeSpace.htm">
											   		<c:param name="spaceId" value="${space.spaceId}"/>
											   		<c:param name="returnTo" value="${pageContext.request.contextPath}/spaces.htm"/>
											    </c:url>">
											<spring:message code="remove"/>
										</a>
									</li>

								</ul>

								</td>
							</tr>
						</table>
					</tiles:putAttribute>
				</tiles:insertDefinition>
			</tiles:putAttribute>
			<tiles:putAttribute name="body">
				<tiles:importAttribute name="contentStoreProvider" />
				<c:set var="contentStore"
					value="${contentStoreProvider.contentStore}" />
				<c:choose>
				<c:when test="${space.metadata.count == 0}">
					<p>This space is empty. <a
						href="contents/add?spaceId=${space.spaceId}"><spring:message
						code="add.contentItem" /> >></a></p>
				</c:when>
				<c:otherwise>
					<table class="small" >
						<tr>
							<td>
								<form action="contents.htm?spaceId=${space.spaceId}" onchange="submit();"
									method="post">
									<input type="text" name="viewFilter"  value="${contentItemList.viewFilter}"/> 
									<spring:message code="filterById" />
								</form>
							</td>
							<td style="text-align:right">
							<!-- ugly: should be cleaned up  -->
							<form action="contents.htm?spaceId=${space.spaceId}"
								method="post"><select id="mpp" name="mpp"
								onchange="submit()">
								<option <c:if test="${contentItemList.maxResultsPerPage == 5 }">selected</c:if>>5</option>
								<option <c:if test="${contentItemList.maxResultsPerPage == 10 }">selected</c:if>>10</option>
								<option <c:if test="${contentItemList.maxResultsPerPage == 25 }">selected</c:if>>25</option>
								<option <c:if test="${contentItemList.maxResultsPerPage == 50 }">selected</c:if>>50</option>
								<option <c:if test="${contentItemList.maxResultsPerPage == 100 }">selected</c:if>>100</option>
								<option <c:if test="${contentItemList.maxResultsPerPage == 200 }">selected</c:if>>200</option>

							</select> <label for="mpp">items per page</label></form>

							</td>
							<td style="text-align:right; vertical-align:middle">
								<c:if test="${contentItemList.previousAvailable or contentItemList.nextAvailable}">
									<ul class="horizontal-list">
		
										<c:choose>
											<c:when test="${contentItemList.previousAvailable}">
												<a title="first page"
													href="contents.htm?action=f&spaceId=${space.spaceId}">[first]</a>
			
												<a title="previous page"
													href="contents.htm?action=p&spaceId=${space.spaceId}">[previous]</a>
											</c:when>
											<c:otherwise>
												<span class="disabled">
													[first] [previous]
												</span>
											</c:otherwise>
										</c:choose>
		
		
										<c:choose>
											<c:when test="${contentItemList.nextAvailable}">
												<a title="next"
													href="contents.htm?action=n&spaceId=${space.spaceId}">[next]</a>
											</c:when>
											<c:otherwise>
												<span class="disabled">
													[next]
												</span>
		
											</c:otherwise>
										</c:choose>
										
									</ul>
								</c:if>
							</td>
						</tr>
					</table>
					
					<script type="text/javascript">
						dojo.require("duracloud.durastore");
	
						dojo.addOnLoad(function(){
						
							/*
							ds = duracloud.durastore;
							dojo.query(".actionable-item").forEach(
								function(item){
							    	dojo.connect(item, 'onmouseover', function() {
										var nodeId;
										dojo.query("[id*='metadata-div-']",item).forEach(function(div){
											nodeId = div.id;
										});
										ds.loadContentItem(nodeId,'${space.spaceId}',item.id);
							    	});
								});
								
							*/
							

							dojo.query(".content-item-details").forEach(function(div){
								duracloud.durastore.loadContentItem(div, dojo.attr(div.id, "spaceId"), dojo.attr(div.id, "contentId"));
							});
						});
					</script>
					<c:forEach items="${contentItemList.contentItemList}" var="content"
						varStatus="status">
						<div  id="${content.encodedContentId}" class="actionable-item">
						<table>
									<tr class="list-item-header">
										<td colspan="2">
											<a href="<c:url value="content.htm">
										              <c:param name="spaceId" value="${space.spaceId}"/>
												   	  <c:param name="contentId" value="${content.contentId}"/>
											      </c:url>">${content.contentId}</a>
										
										</td>
									</tr>

									<tr>
										<td  width="70%" id="details">
											<div class="tiny-thumb">
												<c:if test="${not empty content.tinyThumbnailURL}">
													<img src="${content.tinyThumbnailURL}" />
												</c:if>
											</div>
											<div id="metadata-div-${status.count}" contentId="${content.contentId}" spaceId="${content.spaceId}"  class="content-item-details">
												<!--empty-->
											</div>
										</td>

										<td>
											<div  class="actions highlight">
												<ul class="action-list">
													<li>
														<a
													href="<c:url value="content.htm">
												              <c:param name="spaceId" value="${space.spaceId}"/>
														   	  <c:param name="contentId" value="${content.contentId}"/>
													      </c:url>"><spring:message code="details" /></a>
											        </li>
													<li>
														<a href="<c:url value="${content.downloadURL}"></c:url>"><spring:message code="download" /></a>
													</li>
													<li><a  class="delete-action remove-content-item" spaceId="${content.spaceId}" contentId="${content.contentId}"
														href="<c:url value="removeContent.htm"  >
														   		<c:param name="spaceId" value="${space.spaceId}"/>
														   		<c:param name="contentId" value="${content.contentId}"/>
														   		<c:param name="returnTo" value="${currentUrl}"/>
														    </c:url>" ><spring:message code="remove" /></a></li>
												</ul>
											</div>
										</td>
									</tr>
								</table>
								</div>
							</c:forEach>
					</c:otherwise>
				</c:choose>
			</tiles:putAttribute>
		</tiles:insertDefinition>
	</tiles:putAttribute>
</tiles:insertDefinition>

