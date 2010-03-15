<%@include file="/WEB-INF/jsp/include.jsp" %>
<tiles:insertDefinition name="base-service" >
	<tiles:putAttribute name="title">
		<spring:message code="services" />	
	</tiles:putAttribute>
	<tiles:putAttribute name="mainTab" value="services"/>
	<tiles:putAttribute name="menu">

	</tiles:putAttribute>
	<tiles:putAttribute name="main-content">
		<tiles:insertDefinition name="base-main-content">
			<tiles:putAttribute name="header">
				<tiles:insertDefinition name="base-content-header">
					<tiles:putAttribute name="title">
						Services
					</tiles:putAttribute>	
					<tiles:putAttribute name="subtitle">
						<ul class="action-list">
							<li>
								<a href="#" class="deploy-new">Deploy New Service</a>
							<li>
						</ul>			
					</tiles:putAttribute>

				</tiles:insertDefinition>
			</tiles:putAttribute>
			<tiles:putAttribute name="body">
				<script type="text/javascript">
				    dojo.require("dijit.layout.TabContainer");
				    dojo.require("dijit.layout.ContentPane");
					dojo.require("dijit.Dialog");
					dojo.require("dijit.form.DropDownButton");
					dojo.require("dijit.form.Button");
					
				    dojo.addOnLoad(function(){
				    	dojo.query(".deploy-new").forEach(function(e){
						    dojo.connect(e,"onclick", function(){
						    	//onclick select available tab
						    	var available = dijit.byId("available");
						    	var tabs = dijit.byId("tabs");
						    	tabs.selectChild(available);
						    });
				    	});
				    	
				    	dojo.query(".deployment-tabs").forEach(function(e){
					    	dojo.subscribe(e.id + "-selectChild", function(node){
					    		loadConfigDetails(node.domNode);
			    			});
			    			
			    			var tabs = dijit.byId(e.id);
			    			loadConfigDetails(tabs.getChildren()[0].domNode);
				    	});

				    	
				    });
				    
					function loadConfigDetails(containerNode){
						var idArray = containerNode.id.split("-");
	    				var serviceId = idArray[1];
	    				var deploymentId = idArray[2];
	    				dojo.query(".configDetails",containerNode).forEach(function (configNode) {
		    				loadConfigurationDetails(configNode,serviceId,deploymentId);
		    			});
					}				    
				</script>			
				

				<div dojoType="dijit.layout.TabContainer" style="width: 100%; height: 100%;" id="tabs" doLayout="false">
					<div dojoType="dijit.layout.ContentPane" id="deployed" title="Deployed"  style="min-height:400px" >
							<c:if test="${empty serviceInfos}">
								<p>
									There are no services deployed at this time.
								</p>
								<p>
									<a href="#" class="deploy-new">Deploy a service</a>
								</p>
								
							</c:if>
			                <div>
								<table class="standard">
									<tbody>
									<c:forEach items="${serviceInfos}" var="serviceInfo">

									<tr>
										<td style="width:400px">
											${serviceInfo.displayName}
										    
										</td>
										
										<td style="text-align:right">
											<div dojoType="dijit.form.DropDownButton">
											    <span>Deployments</span>
											    <div dojoType="dijit.TooltipDialog" closable = "true" style="display:none">
													<div dojoType="dijit.layout.TabContainer" class="deployment-tabs" style="width:100%; height:100%" doLayout="false">
												       <c:forEach items="${serviceInfo.deployments}" var="deployment" varStatus="status">
	 													  <div dojoType="dijit.layout.ContentPane"  title="${deployment.hostname}" selected="true" id="dt-${serviceInfo.id}-${deployment.id}"  >
	 													  	 <table class="tooltipDialog-header">
	 													  	 	<tr>
	 													  	 		<td>
																		<span>${serviceInfo.displayName}</span>
	 													  	 		</td>
	 													  	 		<td>
	 													  	 			<ul class="action-list">
																			<c:if test="${not empty serviceInfo.userConfigs}">
																				<li>
																					<a href="<c:url value="/services/deploy">
																						<c:param name="serviceId" value="${serviceInfo.id}"/>
																						<c:param name="deploymentId" value="${deployment.id}"/>
																						<c:param name="returnTo" value="${currentUrl}"/>
																						</c:url>"><spring:message code="reconfigure"/></a> 
																				</li>
																			</c:if>
																			<li>
																				<a href="#" onclick="undeployService('${serviceInfo.id}','${deployment.id}')">Undeploy</a>
																			</li>
																		</ul>
	 													  	 		
	 													  	 		</td>
	 													  	 	</tr>
	 													  	 	
	 													  	 </table>
																
																
																<div  class="details">
																		<c:if test="${not empty deployment.userConfigs}">
																			<table class="standard">
																				<tr>
																					<th colspan="2">User Configuration</th>
																				</tr>
																				<c:forEach items="${deployment.userConfigs}" var="uc">
																				<tr>
																					<td>${uc.displayName}</td>
																					<td>${uc.displayValue}</td>
																				</tr>												
																				</c:forEach>
																			</table>
																		</c:if>
																		
																		<div class="configDetails">
																		
																		</div>
																</div>
																<div>
														</div>

																			
													      </div>
												       </c:forEach>
												    </div>
												 </div>

										    </div>
										
										
											<c:if test="${serviceInfo.newDeploymentAllowed}">
												<a href="<c:url value="/services/deploy">
												<c:param name="serviceId" value="${serviceInfo.id}"/>
												<c:param name="returnTo" value="${currentUrl}"/>
												</c:url>"><spring:message code="deploy.new"/></a>
											</c:if>
										</td>
									</tr>
									</c:forEach>

									</tbody>
								</table>
							</div>			
							
						</div>
				        <div dojoType="dijit.layout.ContentPane" title="Available" id="available" >
								<table class="standard available-services">
									<tbody>
						            <c:forEach items="${availableServiceInfos}" var="serviceInfo">

									<tr>
										<td>
											${serviceInfo.displayName}

										</td>
										<td>
											${serviceInfo.description}
										
										</td>
										<td>
										<a href="<c:url value="/services/deploy">
											<c:param name="serviceId" value="${serviceInfo.id}"/>
											<c:param name="returnTo" value="${currentUrl}"/>
											</c:url>"><spring:message code="deploy.new"/></a>							
										</td>
									</tr>
									</c:forEach>

									</tbody>
									
								</table>
				        </div>
					</div>

			    
			</tiles:putAttribute>
		</tiles:insertDefinition>
	</tiles:putAttribute>
</tiles:insertDefinition>

