<%@include file="/WEB-INF/jsp/include.jsp" %>
<tiles:insertDefinition name="base" >
	<tiles:putAttribute name="title">
		<spring:message code="home" />	
	</tiles:putAttribute>
	<tiles:putAttribute name="mainTab" value="home" />
	<tiles:putAttribute name="main-content">
		<tiles:insertDefinition name="base-main-content">
			<tiles:putAttribute name="header">
				<tiles:insertDefinition name="base-content-header">
					<tiles:putAttribute name="title" value="Welcome to Duracloud"/>
				</tiles:insertDefinition>
			</tiles:putAttribute>
			<tiles:putAttribute name="body">
				<table>
					<tr>
						<td style="width:50%">
							<h3>Getting Started with Duracloud</h3>
							<p>
								The best way to get started with Duracloud is by 
								<a href="<c:url value="/spaces/add"/>">adding a new space</a>.
							</p>							
						</td>
						<td>
							<h3>My Duracould</h3>
							<p>
								We might display some useful information about 
								the current size, scope, activity of this 
								installation.  
							</p>							
						
						</td>
					</tr>
				</table>
			</tiles:putAttribute>
		</tiles:insertDefinition>
	</tiles:putAttribute>
</tiles:insertDefinition>
