<%@include file="/WEB-INF/jsp/include.jsp"%>
<tiles:importAttribute name="title"/>
<tiles:importAttribute name="subtitle" ignore="true"/>

<div class="main-content-header">
	<h1>${title}</h1>
	
	<c:if test="${not empty subtitle}">
		<h2>${subtitle}</h2>
	</c:if>	
</div>
