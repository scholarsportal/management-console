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
			    <script type="text/javascript">
		        dojo.require("dijit.form.DropDownButton");
		        dojo.require("dijit.TooltipDialog");
		    </script>

				
			<table class="standard" style="width:400px">
				<tr>
					<th>Users</th>
					<th class="ar">
						<div dojoType="dijit.form.DropDownButton" style="font-size:0.7em">
						    <span >
						       Add
						    </span>
						    <div dojoType="dijit.TooltipDialog" style="display:none">
								<form method="post" action="users.htm">
									<table>
										<tr>
											<td>Username:</td>
											<td><input type="text" name="username"></td>
											<td>Password:</td>
											<td><input type="password" name="password"></td>
										</tr>
									</table>
				                    <input type="submit" name="verb" value="Add"/>
				                </form>
						    </div>
						</div>
					</th>
				</tr>
				<c:forEach items="${userBeans.users}" var="user">
					<tr>
						<td style="vertical-align:middle" >
							${user.username}									
						</td>
						<td class="ar" style="horizontal-align:right;padding:0">
						
							<table style="width:100px" align="right">
								<tr>
									<td>
										<form method="post" action="users.htm">
						                   <input type="submit" class="delete-action" name="verb" value="Remove" >
						                   <input type="hidden" name="username" value="${user.username}"/>
						                </form>
									</td>
									<td>
						                <div dojoType="dijit.form.DropDownButton" style="font-size:0.7em">
										    <span >
										       Modify
										    </span>
										    <div dojoType="dijit.TooltipDialog" style="display:none">
												<form method="post" action="users.htm">
													<table>
														<tr>
															<td>Reset Password:</td>
															<td><input type="password" name="password"></td>
														</tr>
													</table>
								                    <input type="submit" name="verb" value="Modify"/>
									                    <input type="hidden" name="username" value="${user.username}"/>
								                </form>
										    </div>
										</div>
									</td>
								</tr>
							</table>
						</td>
					</tr>							
				</c:forEach>						
			</table>						
	
			</tiles:putAttribute>
		</tiles:insertDefinition>
	</tiles:putAttribute>
</tiles:insertDefinition>