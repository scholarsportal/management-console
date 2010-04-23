<%@include file="/WEB-INF/jsp/include.jsp" %>
<tiles:insertDefinition name="base" >
	<tiles:putAttribute name="title">
		<spring:message code="home" />
	</tiles:putAttribute>
	<tiles:putAttribute name="menu" value=""/>

	<tiles:putAttribute name="mainTab" value="home" />
	<tiles:putAttribute name="main-content">
		<tiles:insertDefinition name="base-main-content">
			<tiles:putAttribute name="header">
				<tiles:insertDefinition name="base-content-header">
					<tiles:putAttribute name="title" value="DurAdmin User Management"/>
				</tiles:insertDefinition>
			</tiles:putAttribute>
			<tiles:putAttribute name="body">

                <form method="post" action="users.htm">
                    <c:forEach items="${userBeans.users}" var="user">
                        username:<c:out value="${user.username}" />
                        <input type="button" name="verb" value="Modify"/>
                        <input type="submit" name="verb" value="Remove"/>
                        <hr/>
                    </c:forEach>
                    Username:<input type="text" name="username">
                    Password:<input type="password" name="password">
                    <input type="submit" name="verb" value="Add"/>
                </form>

			</tiles:putAttribute>
		</tiles:insertDefinition>
	</tiles:putAttribute>
</tiles:insertDefinition>