<%@include file="/WEB-INF/jsp/include.jsp"%>
<tiles:insertDefinition name="base-space">
	<tiles:importAttribute name="contentStoreProvider" />
	<c:set var="contentStore" value="${contentStoreProvider.contentStore}" />

	<tiles:putAttribute name="title">
		<spring:message code="space" /> :: ${contentItem.spaceId} :: ${contentItem.contentId}
	</tiles:putAttribute>

	<tiles:putAttribute name="main-content">
		<script type="text/javascript">
			dojo.require("duracloud.storage");
			dojo.addOnLoad(function(){
					
				dojo.query("#refresh").forEach(function(element){
					dojo.connect(element, "onclick", function(e){
						var id = e.target.id;
						duracloud.storage.expireContentItem(dojo.attr(id,"spaceId"),dojo.attr(id,"contentId"));
					});
				});

			});


				
		</script>
		<div  dojoType="dijit.layout.ContentPane" region="left" splitter="false" id="menu-div">
			<script type="text/javascript">
				dojo.require("duracloud.durastore");
			</script>
	
			<div><tiles:insertTemplate
				template="/WEB-INF/jsp/layout/metadata-control.jsp">
				<tiles:putAttribute name="spaceId" value="${contentItem.spaceId}" />
				<tiles:putAttribute name="contentId" value="${contentItem.contentId}" />
				<tiles:putAttribute name="metadata"
					value="${contentItem.extendedMetadata}" />
			</tiles:insertTemplate></div>
	
			<div><tiles:insertTemplate
				template="/WEB-INF/jsp/layout/tag-control.jsp">
				<tiles:putAttribute name="spaceId" value="${contentItem.spaceId}" />
				<tiles:putAttribute name="contentId" value="${contentItem.contentId}" />
				<tiles:putAttribute name="tags" value="${contentItem.metadata.tags}" />
			</tiles:insertTemplate></div>
	
	
	
	
			<div class="sidebar-actions">
			<h4><spring:message code="form.contentItem.modifyProperties" /></h4>
			<c:url value="content.htm" var="modifyPropertiesUrl">
	            <c:param name="spaceId" value="${contentItem.spaceId}"/>
	            <c:param name="contentId" value="${contentItem.contentId}"/>
	        </c:url>
	        <form:form commandName="contentItem"
				action="${modifyPropertiesUrl}"
				method="post">
				<input type="hidden" name="action" value="update"/>
				<p><label for="mimetype"><spring:message code="mimetype" /></label>
				<form:input id="mimetype" path="contentMimetype" /> <form:errors
					path="contentMimetype" /></p>
				<p><input type='submit' class="update-content-item" contentId="${contentItem.contentId}" spaceId="${contentItem.spaceId}" value="<spring:message code="update"/>" />
				</p>
			</form:form></div>
		</div>	
		<div  dojoType="dijit.layout.ContentPane" region="top" splitter="false" class="main-content-header">
				<tiles:insertDefinition name="base-content-header">
					<tiles:putAttribute name="title">
						${contentItem.contentId}
					</tiles:putAttribute>
					<tiles:putAttribute name="subtitle">
						<table style="margin: 0; padding: 0;">
							<tr>
								<td>
								<ul class="breadcrumb">
									<li><a href="<c:url value="/spaces.htm"/>"><spring:message
										code="spaces" /></a> </li>
									<li>
										<a href="contents.htm?spaceId=${contentItem.spaceId}">${contentItem.spaceId}</a>
									</li>
								</ul>

								</td>
								<td style="text-align: right">
								<ul class="action-list" style="float: right">
									<li><a
										href="<c:url value="/contents/add" >
				   		<c:param name="spaceId" value="${contentItem.spaceId}"/>
				   		<c:param name="returnTo" value="${currentUrl}"/>
				    </c:url>"><spring:message
										code="add.contentItem" /></a></li>
									
									<c:if test="${contentItem.viewerURL != null}">
										<li>
											<a target = "viewer" href="${contentItem.viewerURL}"><spring:message code="view" /></a>
										</li>
									</c:if>
									
									<li>
										<a href="${contentItem.downloadURL}"><spring:message code="download" /></a>
									</li>

									<li><a class="delete-action remove-content-item" spaceId="${contentItem.spaceId}"
										href="<c:url value="removeContent.htm" >
										   		<c:param name="spaceId" value="${contentItem.spaceId}"/>
										   		<c:param name="contentId" value="${contentItem.contentId}"/>
										   		<c:param name="returnTo" value="${pageContext.request.contextPath}/contents.htm?spaceId=${contentItem.spaceId}"/>
										    </c:url>"><spring:message code="remove" /></a></li>

									<li><a  id="refresh" spaceId="${contentItem.spaceId}" contentId="${contentItem.contentId}" href="">
											<spring:message code="refresh"/>
										</a>
									</li>
								</ul>


								</td>
							</tr>
						</table>



					</tiles:putAttribute>
				</tiles:insertDefinition>
		
		</div>		
		<div  dojoType="dijit.layout.ContentPane" region="center" splitter="true">
				<table>
					<tr>
						<td style="width: 75%">
						<table class="property-list">
							<tr>
								<td class="label"><spring:message code="size" /></td>
								<td class="value"><c:out
									value="${contentItem.metadata.size}" /> bytes</td>
							</tr>
							<tr>
								<td class="label"><spring:message code="mimetype" /></td>
								<td class="value"><c:out
									value="${contentItem.metadata.mimetype}" /></td>
							</tr>
							<tr>
								<td class="label"><spring:message code="checksum" /></td>
								<td class="value"><c:out
									value="${contentItem.metadata.checksum}" /></td>
							</tr>
							<tr>
								<td class="label"><spring:message code="modified" /></td>
								<td class="value"><c:out
									value="${contentItem.metadata.modified}" /></td>
							</tr>
						</table>

						</td>
						<td>
						<div class="content-preview">
							<c:choose>
								<c:when test="${not empty contentItem.thumbnailURL}">
									<a target="viewer" href="${contentItem.viewerURL}">
										<img src="${contentItem.thumbnailURL}"/>
									</a>
								</c:when>
								<c:otherwise>
									<p>Preview not available.</p>
								</c:otherwise>
							</c:choose>
						</div>
						</td>

					</tr>

				</table>

			</div>
	</tiles:putAttribute>
</tiles:insertDefinition>

