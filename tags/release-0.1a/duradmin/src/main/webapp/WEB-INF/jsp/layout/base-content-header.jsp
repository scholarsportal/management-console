<%@include file="/WEB-INF/jsp/include.jsp"%>
<tiles:importAttribute name="title"/>
<tiles:importAttribute name="subtitle" ignore="true"/>
<h1>${title}</h1>

<div class="subtitle">
	<c:if test="${not empty subtitle}">
		${subtitle}
	</c:if>	
</div>
