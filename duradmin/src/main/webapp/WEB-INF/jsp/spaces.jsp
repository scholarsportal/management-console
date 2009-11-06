<%@include file="/WEB-INF/jsp/include.jsp" %>
<tiles:insertDefinition name="base-space" >
	<tiles:putAttribute name="title">
		<spring:message code="spaces" />	
	</tiles:putAttribute>
	<tiles:putAttribute name="menu">
		<tiles:insertTemplate template="/WEB-INF/jsp/layout/box-control.jsp">
			<tiles:putAttribute name="title">
				Tags
			</tiles:putAttribute>
			<tiles:putAttribute name="miniform">
				<form>
					<input type="text" size="13"/> <input type="submit" value="Add"/>
					<input type="button" onclick="hideMiniform(event)" value="Cancel"/>					
				</form>
			</tiles:putAttribute>
			<tiles:putAttribute name="body">
				<ul class="small horizontal-list">
					<li>
						<span style="white-space:nowrap;">
							tag 1  <input  class="minibutton" type="button" value="x" />
						</span>
					</li>
					<li>
						<span  style="white-space:nowrap;" >
							tag 2  <input  class="minibutton"  type="button" value="x"  />
						</span>
					</li>
				</ul>
			</tiles:putAttribute>
		</tiles:insertTemplate>
	</tiles:putAttribute>
	<tiles:putAttribute name="main-content">
		<tiles:insertDefinition name="base-main-content">
			<tiles:putAttribute name="header">
				<tiles:insertDefinition name="base-content-header">
					<tiles:putAttribute name="title">
						<spring:message code="spaces"/>
					</tiles:putAttribute>	
					<tiles:putAttribute name="subtitle">
						<a href="<c:url value="/spaces/add"/>">Add Space</a>
					</tiles:putAttribute>
				</tiles:insertDefinition>
			</tiles:putAttribute>
			<tiles:putAttribute name="body">
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
						<c:forEach items="${spaces}" var="spaceId" varStatus="status">
							<tr id="${spaceId}" onmouseover="loadSpaceMetadata('metadata-div-${status.count}', '${spaceId}');">
								<td id="actionColumn" >
									<div>
										<b><a href="contents.htm?spaceId=${spaceId}"><c:out value="${spaceId}" /></a></b>
									</div>
								
									<div id="actionDiv" class="actions" >
										<ul>
										
										
											<li><a style="font-weight:bold" href="<c:url value="contents/add?spaceId=${spaceId}&returnTo=${currentUrl}"/>">
													<spring:message code="add.contentItem"/>
												</a>
											</li>
											
											<li><a href="<c:url value="removeSpace.htm">
													   		<c:param name="spaceId" value="${spaceId}"/>
													   		<c:param name="returnTo" value="${currentUrl}"/>
													    </c:url>" onclick="return confirmDeleteOperation();">
													<spring:message code="remove"/>
												</a>
											</li>
										</ul>
									</div>
			
									
								</td>					
								<td >
									<div id="metadata-div-${status.count}" style="min-height:2.0em; font-size:0.9em">
										<!--empty-->
									</div>
								</td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</tiles:putAttribute>
		</tiles:insertDefinition>
	</tiles:putAttribute>
</tiles:insertDefinition>
