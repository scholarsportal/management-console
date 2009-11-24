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
			<div><input style="min-width:10em" name="name" type="text" /></div> 
			<div>
			<textarea style="min-width:15em" name="value" rows="3" cols="10"></textarea>
			</div>
			<div style="white-space:nowrap">
			<input type="submit" value="Add" />
			<input type="button" onclick="hideMiniform(event)" value="Cancel"/>					
			</div>
			
		</form>
	</tiles:putAttribute>
	<tiles:putAttribute name="body">
			<c:choose>
				<c:when test="${not empty metadata}">
						<table class="small extended-metadata">
						<c:forEach items="${metadata}" var="m" varStatus="status">
							<tr>
								<td>
											<input class="minibutton" 
											type="button" value="x" 
											onclick="removeMetadataByKey('${spaceId}', '${m.name}','${contentId}', this.parentNode.parentNode);"/>								
								</td>
								<td>
								<table>
									<tr>
										<td style="font-weight:bold">
										${m.name}
										</td>
									
									</tr>
									<tr>
										<td>
											${m.value}
										</td>
									</tr>
								</table>
								</td>
							</tr>
						</c:forEach>					
						</table>

				</c:when>
				<c:otherwise>
					No metadata defined.
				</c:otherwise>
			</c:choose>
	</tiles:putAttribute>
</tiles:insertTemplate>
