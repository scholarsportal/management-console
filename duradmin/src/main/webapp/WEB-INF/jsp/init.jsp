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
					<tiles:putAttribute name="title" value="DurAdmin Initialization"/>
				</tiles:insertDefinition>
			</tiles:putAttribute>
			<tiles:putAttribute name="body">

                <form method="post" action="init.htm">
                  <spring:bind path="initBean.duraStoreHost">
                     DuraStore Host: <input type="text" name="duraStoreHost" value="${status.value}" />
                  </spring:bind>

                  <spring:bind path="initBean.duraStorePort">
                     DuraStore Port: <input type="text" name="duraStorePort" value="${status.value}" />
                  </spring:bind>

                  <spring:bind path="initBean.duraStoreContext">
                     DuraStore Context: <input type="text" name="duraStoreContext" value="${status.value}" />
                  </spring:bind>
                  <br />
                  <spring:bind path="initBean.duraServiceHost">
                     DuraService Host: <input type="text" name="duraServiceHost" value="${status.value}" />
                  </spring:bind>

                  <spring:bind path="initBean.duraServicePort">
                     DuraService Port: <input type="text" name="duraServicePort" value="${status.value}" />
                  </spring:bind>

                  <spring:bind path="initBean.duraServiceContext">
                     DuraService Context: <input type="text" name="duraServiceContext" value="${status.value}" />
                  </spring:bind>

                  <input type="submit" value="Initialize DurAdmin" />
                </form>

			</tiles:putAttribute>
		</tiles:insertDefinition>
	</tiles:putAttribute>
</tiles:insertDefinition>