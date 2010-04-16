
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

				</tiles:insertDefinition>
			</tiles:putAttribute>
			<tiles:putAttribute name="body">
				<script type="text/javascript">
					dojo.require("duracloud.duraservice");

				    dojo.addOnLoad(function(){
				    	var available = dijit.byId("available");
						duracloud.duraservice.loadDeployedPanel(dijit.byId("deployed"), available);
						duracloud.duraservice.loadAvailablePanel(available);
				    });
				    
				</script>			
				<div dojoType="dijit.layout.TabContainer" style="width: 100%; height: 100%" id="tabs" >
					<div dojoType="dijit.layout.BorderContainer" id="deployed" title="Deployed" gutters="true" >
						<div dojoType="dijit.layout.ContentPane" id="deployedList" style="width:250px" region="left" splitter="true">
						</div>
						<div dojoType="dijit.layout.ContentPane" region="center" gutter="false" id="details">
						Click on a service to the right for details.					
						</div>

					</div>
			        <div dojoType="dijit.layout.ContentPane" title="Available" id="available" >
			        </div>
				</div>
				
			</tiles:putAttribute>
		</tiles:insertDefinition>
	</tiles:putAttribute>
</tiles:insertDefinition>

