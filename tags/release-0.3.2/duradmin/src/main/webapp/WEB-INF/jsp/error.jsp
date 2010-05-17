<%@include file="/WEB-INF/jsp/include.jsp" %>
<tiles:insertDefinition name="base" >
	<tiles:putAttribute name="title">
		<spring:message code="unexpectedError" /> 	
	</tiles:putAttribute>
	<tiles:putAttribute name="menu">
		
	</tiles:putAttribute>
	<tiles:putAttribute name="main-content">
		<tiles:insertDefinition name="base-main-content">
			<tiles:putAttribute name="header">
				<tiles:insertDefinition name="base-content-header">
					<tiles:putAttribute name="title">
						<spring:message code="unexpectedError.mainContent.title"/>
					</tiles:putAttribute>	
				</tiles:insertDefinition>
			</tiles:putAttribute>
			<tiles:putAttribute name="body">
				<div id="error-div" class="message-error">
					<c:out value="${error}"/>
					<c:out value="${message}"/>
				</div>
				
				<c:if test="${not empty stack}">
					<div id="stack-div" class="message-error">
						<c:out value="${stack}"/>
					</div>
				</c:if>				
				
			</tiles:putAttribute>
		</tiles:insertDefinition>
	</tiles:putAttribute>
</tiles:insertDefinition>