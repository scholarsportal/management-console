<%@include file="/WEB-INF/jsp/include.jsp" %>


<div id="top-header">
	<img alt="<spring:message code="application.title"/>" height="25px" src="images/duraspace-logo.jpg">
	<strong>
		<spring:message code="application.title"/>
	</strong>
</div>

<tiles:importAttribute name="mainMenu" />

<table id="main-menu" >
	<tr>
		<td id="main-menu-left">
			<ul id="menu">
				<c:forEach items="${mainMenu}" var="mi">
					<li>
						<a href='<c:url value="${mi.href}" />' class="<c:if test="${mi.name == mainTab}">current</c:if>" >
							<spring:message code="${mi.messageKey}"/>
						</a>
					</li>
				</c:forEach>
			</ul>
		</td>		
		<td id="main-menu-center">
			<c:if test="${not empty flashMessage}">
				<span class="message-${fn:toLowerCase(flashMessage.severity)}">${flashMessage.text}</span>
			</c:if>
		</td>
		
		
		<td id="main-menu-right">
			<tiles:importAttribute name="contentStores" ignore="true"/>
			<tiles:importAttribute name="selectedStoreId" ignore="true" />

			<tiles:importAttribute name="currentUrl" />
			<c:if test="${not empty contentStores}">
				<form  action="<c:url value="/changeProvider"/>" method="put">
					<input type="hidden" name="returnTo" value="${currentUrl}"/>
					<spring:message code="storageProviders"/>:
					<select name="storageProviderId" onchange="submit();">
						<c:forEach var="store" items="${contentStores}">
							<option value="${store.storeId}" <c:if test="${store.storeId == selectedStoreId}">selected</c:if> >
								<spring:message code="${fn:toLowerCase(store.storageProviderType)}"/> (#${store.storeId})
							</option>
						</c:forEach>
					</select>
				</form>
			</c:if>
		</td>
	</tr>
</table>
	