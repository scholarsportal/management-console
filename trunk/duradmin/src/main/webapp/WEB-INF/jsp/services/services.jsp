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
				<div style="width:100%; height:100%">
					<div dojoType="dijit.layout.TabContainer" style="width: 800px; height: 400px" id="tabs" >
						<div dojoType="dijit.layout.ContentPane" id="deployed" title="Deployed"  style="min-height:400px" >
						</div>
				        <div dojoType="dijit.layout.ContentPane" title="Available" id="available" style="min-height:400px">
				        </div>
					</div>
				</div>
				
			</tiles:putAttribute>
		</tiles:insertDefinition>
	</tiles:putAttribute>
</tiles:insertDefinition>

