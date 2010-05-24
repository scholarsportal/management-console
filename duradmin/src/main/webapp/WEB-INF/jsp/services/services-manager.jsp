<%@include file="/WEB-INF/jsp/include.jsp" %>
<tiles:insertDefinition name="app-base" >
	<tiles:putAttribute name="title">
		<spring:message code="services" />	
	</tiles:putAttribute>

	<tiles:putAttribute name="header-extensions">
		<script type="text/javascript" src="${pageContext.request.contextPath}/javascript/ui.selectablelist.js"></script>
		<script type="text/javascript" src="${pageContext.request.contextPath}/javascript/ui.expandopanel.js"></script>
		<script type="text/javascript" src="${pageContext.request.contextPath}/javascript/services-manager.js"></script>
	</tiles:putAttribute>
	<tiles:putAttribute name="body">
	<tiles:insertDefinition name="app-frame">
		<tiles:putAttribute name="mainTab">services</tiles:putAttribute>

		<tiles:putAttribute name="main-content">
	   		<div id="services-list-view">
				<div class="north header list-header clearfix">
					<div id="header-spaces-list" class="header-section clearfix">						
						<a class="flex button float-r deploy-service-button" href="javascript:void(1);"><span><i class="pre plus">Deploy a New Service</i></span></a>
						<h2>Services</h2>
					</div>
				</div>
			
				<div class="center">
					<table>
						<thead>
							<tr>
								<th></th>
								<th>Service</th>
								<th>Hostname</th>
								<th>Status</th>
							</tr>
						</thead>
						<tbody>
							<tr>
								<td>Icon Here</td>
								<td>Service #1</td>
								<td>127.0.0.1</td>
								<td>OK</td>
							</tr>
							<tr>
								<td>Icon Here</td>
								<td>Service #1</td>
								<td>127.0.0.1</td>
								<td>OK</td>
							</tr>
							<tr>
								<td>Icon Here</td>
								<td>Service #1</td>
								<td>127.0.0.1</td>
								<td>OK</td>
							</tr>
							<tr>
								<td>Icon Here</td>
								<td>Service #1</td>
								<td>127.0.0.1</td>
								<td>OK</td>
							</tr>
							<tr>
								<td>Icon Here</td>
								<td>Service #1</td>
								<td>127.0.0.1</td>
								<td>OK</td>
							</tr>
							<tr>
								<td>Icon Here</td>
								<td>Service #1</td>
								<td>127.0.0.1</td>
								<td>OK</td>
							</tr>
							<tr>
								<td>Icon Here</td>
								<td>Service #1</td>
								<td>127.0.0.1</td>
								<td>OK</td>
							</tr>
							<tr>
								<td>Icon Here</td>
								<td>Service #1</td>
								<td>127.0.0.1</td>
								<td>OK</td>
							</tr>
							<tr>
								<td>Icon Here</td>
								<td>Service #1</td>
								<td>127.0.0.1</td>
								<td>OK</td>
							</tr>

						</tbody>
					</table>					
				</div>
			</div>
			<div id="detail-pane" class="detail-pane">
				<div class="north header">
					<h1>Service Detail</h1>
					<h2 class="object-name">Service Name Here</h2>
					<div class="toggle-control flex switch-holder">
					    <div class="r">
					    	<span>
					    		Status
					    	</span>
					    	<span>
					    		[deployed] [undeploy]
					    	</span>
					    </div>
					</div>
		
				</div>
				<div class="center">
				</div>
			</div>	
		</tiles:putAttribute>
		
</tiles:insertDefinition>	
	</tiles:putAttribute>
	
</tiles:insertDefinition>



