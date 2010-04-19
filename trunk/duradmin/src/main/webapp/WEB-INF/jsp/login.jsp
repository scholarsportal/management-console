<%@include file="/WEB-INF/jsp/include.jsp" %>
<tiles:insertDefinition name="base" >
	<tiles:putAttribute name="title">
		Duradmin: Login
	</tiles:putAttribute>
	<tiles:putAttribute name="menu">
	</tiles:putAttribute>
	<tiles:putAttribute name="main-content">
		<tiles:insertDefinition name="base-main-content">
			<tiles:putAttribute name="header">
				<tiles:insertDefinition name="base-content-header">
					<tiles:putAttribute name="title">
						Please sign in
					</tiles:putAttribute>	
				</tiles:insertDefinition>
			</tiles:putAttribute>
			<tiles:putAttribute name="body">
				<form id="loginForm" action="${pageContext.request.contextPath}/j_spring_security_check" method="post" >
					<div>
						<table class="basic-form">
							<tr>
								<td class="label">
									<label for="j_username">Username</label>
								</td>
								<td class="input">
									<input type="text" name="j_username"/>								
								</td>
							</tr>
							<tr>
								<td class="label">
									<label for="j_password">Password</label>
								</td>
								<td class="input">
									<input type="password" name="j_password"/>
								</td>
							</tr>
						</table>
					</div>
					<div class="basic-form-buttons" >
						<input type="submit" value="Login" />					
					</div>
				</form>
			</tiles:putAttribute>
		</tiles:insertDefinition>
	</tiles:putAttribute>
</tiles:insertDefinition>
