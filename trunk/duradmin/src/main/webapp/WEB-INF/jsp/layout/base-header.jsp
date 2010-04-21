<%@include file="/WEB-INF/jsp/include.jsp" %>
	<tiles:importAttribute name="customLogoUrl" />

	<div id="top-header" >
		<img alt="<spring:message code="application.title"/>" src="${pageContext.request.contextPath}/images/duracloud_transparent.png" height="50"/>
		<c:if test="${not empty customLogoUrl}">
			<img src="${pageContext.request.contextPath}/${customLogoUrl}" height="50"/>
		</c:if>

		<c:if test="${pageContext.request.userPrincipal != null}">
			<ul class="global-actions horizontal-list">
				<li>
					<span>Welcome ${pageContext.request.userPrincipal.name}</span>
				</li>
	            <li>
		             | <a href='<c:url value="/logout"/>' class="logout">Logout</a>
	            </li>
            </ul>
		</c:if>
	</div>
	<div>
	<tiles:importAttribute name="mainMenu" />
	<table id="main-menu" >
		<tr>
			<td id="main-menu-left">
				<ul id="menu">
					<c:forEach items="${mainMenu}" var="mi">
						<li>
							<a  id="${mi.name}MenuItem" href='<c:url value="${mi.href}" />' class="<c:if test="${mi.name == mainTab}">current</c:if>" >
								<spring:message code="${mi.messageKey}"/>
							</a>
						</li>
					</c:forEach>
				</ul>
			</td>		
			<td id="main-menu-center">
				<div id="flashMessageDiv">
				<c:if test="${not empty flashMessage}">
					<span class="message-${fn:toLowerCase(flashMessage.severity)}">${flashMessage.text}</span>
				</c:if>
				</div>
				
			</td>
			
			
			<td id="main-menu-right">
	            <ul id="menu">
	            <li>
				<tiles:importAttribute name="contentStoreProvider" ignore="true"/>
	
				<tiles:importAttribute name="currentUrl" />
				<c:if test="${not empty contentStoreProvider}">
	
					<form  action="<c:url value="/changeProvider"/>" method="POST">
						<input type="hidden" name="returnTo" value="<c:url value="/spaces.htm"/>"/>
						<spring:message code="storageProviders"/>:
						<select name="storageProviderId" onchange="submit();">
							<c:forEach var="storeOption" items="${contentStoreProvider.contentStores}">
								<option value="${storeOption.storeId}" <c:if test="${contentStoreProvider.selectedContentStoreId == storeOption.storeId}">selected</c:if> >
									<spring:message code="${fn:toLowerCase(storeOption.storageProviderType)}"/>
								</option>
							</c:forEach>
						</select>
					</form>
				</c:if>
	            </li>
				</ul>
			</td>
		</tr>
	</table>
</div>	