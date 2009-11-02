<%@include file="/WEB-INF/jsp/include.jsp" %>
<tiles:insertDefinition name="base-space" >
	<tiles:importAttribute name="contentStoreProvider"/>
	<c:set var="contentStore" value="${contentStoreProvider.contentStore}"/>

	<tiles:putAttribute name="title">
		<spring:message code="space"/> :: ${contentItem.spaceId} :: ${contentItem.contentId}
	</tiles:putAttribute>
	<tiles:putAttribute name="menu">
	
		<div class="sidebar-actions">
			<ul>
			    <li>
				    <a href="<c:url value="/contents/add" >
				   		<c:param name="spaceId" value="${contentItem.spaceId}"/>
				   		<c:param name="returnTo" value="${currentUrl}"/>
				    </c:url>"><spring:message code="add.contentItem"/></a> 
				</li>
				<li><a href="${contentStore.baseURL}/${contentItem.spaceId}/${contentItem.contentId}?storeID=${contentStore.storeId}"><spring:message code="download"/></a> 
				</li>
			</ul>
		</div>
		
		<div class="sidebar-actions">
			<h4><spring:message code="form.contentItem.modifyProperties"/></h4>
		    <form:form commandName="contentItem"  action="content.htm?spaceId=${contentItem.spaceId}&contentId=${contentItem.contentId}" method="post">
		      <input type="hidden" name="action" value="update" />
		      <p>
		        <label for="mimetype"><spring:message code="mimetype"/></label>
		        <form:input id="mimetype" path="contentMimetype"/>  
		        <form:errors path="contentMimetype"/>
		      </p>
		      <p>                        
		        <input type='submit' value="<spring:message code="update"/>"/>
		      </p>
		    </form:form>    
		</div>

	</tiles:putAttribute>
	<tiles:putAttribute name="main-content">
		<tiles:insertDefinition name="base-main-content">
			<tiles:putAttribute name="header">
				<tiles:insertDefinition name="base-content-header">
					<tiles:putAttribute name="title">
						${contentItem.contentId}
					</tiles:putAttribute>
					<tiles:putAttribute name="subtitle">
						<ul>
							<li>
								<a href="<c:url value="/spaces.htm"/>"><spring:message code="spaces"/></a> <c:out value="::"/>
	
							</li>
							<li>
							</li>
							<li>
						    	<a href="contents.htm?spaceId=${contentItem.spaceId}">${contentItem.spaceId}</a> <c:out value="::"/>
							</li>
						</ul>
					</tiles:putAttribute>
				</tiles:insertDefinition>
			</tiles:putAttribute>
			<tiles:putAttribute name="body">
			
				<table>
					<tr>
						<td style="width:75%">
						    <table  class="property-list" >
						        <tr>
						          <td class="label"><spring:message code="contentItem.id"/></td>
						          <td class="value"><a href="${contentStore.baseURL}/${contentItem.spaceId}/${contentItem.contentId}?storeID=${contentStore.storeId}">${contentItem.contentId}</a></td>
						        </tr>
						        <tr>
						          <td class="label"><spring:message code="size"/></td>
						          <td class="value"><c:out value="${contentItem.metadata.size}"/> bytes</td>
						        </tr>
						        <tr>
						          <td class="label"><spring:message code="mimetype"/></td>
						          <td class="value"><c:out value="${contentItem.metadata.mimetype}"/></td>
						        </tr>
						        <tr>
						          <td class="label"><spring:message code="checksum"/></td>
						          <td class="value"><c:out value="${contentItem.metadata.checksum}"/></td>
						        </tr>
						        <tr>
						          <td class="label"><spring:message code="modified"/></td>
						          <td class="value"><c:out value="${contentItem.metadata.modified}"/></td>
						        </tr>
						    </table>
						
						</td>					
						<td >
							<div class="content-preview-div" style="border:1px dashed #999; min-height:10.5em; padding:0.25em">
								<p>Content Item Preview/Thumbnail</p>
							</div>							
						</td>					

					</tr>
					
				</table>


			</tiles:putAttribute>
		</tiles:insertDefinition>
	</tiles:putAttribute>
</tiles:insertDefinition>

