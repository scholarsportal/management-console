<%@include file="/WEB-INF/jsp/include.jsp" %>
<tiles:insertDefinition name="base-service" >
	<tiles:putAttribute name="title">
		<spring:message code="services" />	
	</tiles:putAttribute>
	<tiles:putAttribute name="mainTab" value="services"/>
	<tiles:putAttribute name="menu">
		&lt;&lt;Menu Here&gt;&gt;
	</tiles:putAttribute>
	<tiles:putAttribute name="main-content">
		<tiles:insertDefinition name="base-main-content">
			<tiles:putAttribute name="header">
				<tiles:insertDefinition name="base-content-header">
					<tiles:putAttribute name="title">
						<spring:message code="services"/>
					</tiles:putAttribute>	
				</tiles:insertDefinition>
			</tiles:putAttribute>
			<tiles:putAttribute name="body">
				<table>
					<tr>
						<td style="width: 50%">
						<div class="services deployed-services">
						<h4>Deployed Services</h4>
						<c:if test="${empty deployedServices}">
							<p>No services are currently deployed.</p>
						</c:if> <c:forEach items="${deployedServices}" var="service">
							<div class="service">
							<table class="standard">
								<tr>
									<th><c:out value="${service.serviceId}" /></th>
								</tr>
								<tr>
									<td>
									<form action="unDeployService.htm" method="post">
									<table>
										<tr>
											<td>Status</td>

											<td><c:out value="${service.status}" /></td>
											<td style="text-align: right"><input type="hidden"
												name="serviceId"
												value="<c:out value="${service.serviceId}" />" /> <input
												type="submit" value="Undeploy"
												style="margin: 0; padding: 0; font-size: 0.8em" /></td>
										</tr>
									</table>
									</form>
									</td>
								</tr>
								<tr>
									<td>
										<table>
											<tr>
												<td>Configuration</td>
												<td>
													<table style="background-color:#EEE">
														<c:forEach items="${service.config}" var="configItem">
															<tr>
																<td><c:out value="${configItem.key}" />:</td>
																<td><c:out value="${configItem.value}" /></td>
															</tr>
														</c:forEach>
													</table>
												</td>
											</tr>
										</table>
									</td>
								</tr>
							</table>
							      </div>
							    </c:forEach>
							</div>					
						</td>
						<td>
						    <div class="services available-services">
							    <h4>Available Services</h4>
							    <c:if test="${empty availableServices}">
							    	<p>
							    		There are no available services.
							    	</p>
							    </c:if>
							    <c:forEach items="${availableServices}" var="service">
							      <div class="service">
							        <table class="standard">
							          <tr>
							            <th><c:out value="${service.serviceId}" /></th>
							          </tr>
							          <tr>
							            <td>
							              <form action="deployService.htm" method="post">
							                <input type="hidden" name="serviceId" value="<c:out value="${service.serviceId}" />" />
							                <table>
							                  <c:forEach items="${service.config}" var="configItem">                
							                  <tr>
							                    <td><c:out value="${configItem.key}" /></td>
							                    <td><input type="text" id="config.<c:out value="${configItem.key}" />" name="config.<c:out value="${configItem.key}" />" value="<c:out value="${configItem.value}" />" /></td>
							                  </tr>
							                  </c:forEach>
							                  <tr style="border-bottom:0px solid #ccc">
							                    <td>Deploy To</td>
							                    <td>
							                      <select name="serviceHost">                
							                        <c:forEach items="${serviceHosts}" var="serviceHost">
							                        <option value="<c:out value="${serviceHost}" />"><c:out value="${serviceHost}" /></option>
							                        </c:forEach>
							                      </select>
							                      <input type="submit" value="Deploy" />
							                    </td>
							                  </tr>
							                </table>        
							              </form>   
							            </td>       
							          </tr>
							        </table>
							      </div>
							    </c:forEach>    
						    </div>
						
						</td>
					</tr>
				</table>
			    
			</tiles:putAttribute>
		</tiles:insertDefinition>
	</tiles:putAttribute>
</tiles:insertDefinition>

