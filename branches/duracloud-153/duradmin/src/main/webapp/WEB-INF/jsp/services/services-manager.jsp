<%@include file="/WEB-INF/jsp/include.jsp" %>
<tiles:insertDefinition name="app-base" >
	<tiles:putAttribute name="title">
		<spring:message code="services" />	
	</tiles:putAttribute>

	<tiles:putAttribute name="header-extensions">
	<!-- 
	<script type="text/javascript" src="${pageContext.request.contextPath}/javascript/jquery.history.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/javascript/jquery.form.wizard-2.0.1-min.js"></script>
 	<script type="text/javascript" src="http://github.com/malsup/form/raw/master/jquery.form.js?v2.43"></script>
	<script type="text/javascript" src="http://ajax.microsoft.com/ajax/jquery.validate/1.7/jquery.validate.min.js"></script>
	-->
	
		<script type="text/javascript" src="${pageContext.request.contextPath}/javascript/ui.onoffswitch.js"></script>
		<script type="text/javascript" src="${pageContext.request.contextPath}/javascript/ui.selectablelist.js"></script>
		<script type="text/javascript" src="${pageContext.request.contextPath}/javascript/ui.expandopanel.js"></script>
		<script type="text/javascript" src="${pageContext.request.contextPath}/javascript/ui.listdetailviewer.js"></script>

		<script type="text/javascript" src="${pageContext.request.contextPath}/javascript/services-manager.js"></script>
	</tiles:putAttribute>
	<tiles:putAttribute name="body">
	<tiles:insertDefinition name="app-frame">
		<tiles:putAttribute name="mainTab">services</tiles:putAttribute>

		<tiles:putAttribute name="main-content">
	   		<div id="services-list-view" class="list-browser dc-list-item-viewer">
				<div class="north header list-header clearfix">
					<div id="header-spaces-list" class="header-section clearfix">						
						<a class="flex button float-r deploy-service-button" href="javascript:void(1);">
							<span><i class="pre plus">Deploy a New Service</i></span>
						</a>
						
						<h2>Services</h2>
					</div>
				</div>
			
				<div class="center dc-item-list-wrapper">
					<div class="dc-item-list">
						<table>
							<thead>
								<tr>
									<th>&nbsp;</th>
									<th>Service</th>
									<th>Hostname</th>
									<th>Status</th>
								</tr>
							</thead>
							<tbody  id="services-list">
								<tr class="dc-item service-replicate">
									<td class="icon"><div></div></td>
									<td>Service #1 - Replicate</td>
									<td>127.0.0.1</td>
									<td>OK</td>
								</tr>
								<tr class="dc-item service-generalcompute">
									<td class="icon"><div></div></td>
									<td>Service #1 - General compute</td>
									<td>127.0.0.1</td>
									<td>OK</td>
								</tr>
								<tr class="dc-item service-bitintegrity">
									<td class="icon"><div></div></td>
									<td>Service #1</td>
									<td>127.0.0.1</td>
									<td>OK</td>
								</tr>
								<tr class="dc-item service-image">
									<td class="icon"><div></div></td>
									<td>Service #1</td>
									<td>127.0.0.1</td>
									<td>OK</td>
								</tr>
								<tr class="dc-item service-video">
									<td class="icon"><div></div></td>
									<td>Service #1</td>
									<td>127.0.0.1</td>
									<td>OK</td>
								</tr>
								<tr class="dc-item service-filetransform">
									<td class="icon"><div></div></td>
									<td>Service #1</td>
									<td>127.0.0.1</td>
									<td>OK</td>
								</tr>	
							</tbody>
						</table>
					</div>			
				</div>
			</div>

			<div id="detail-pane" class="detail-pane" style="padding-top:10px;">
				
			</div>

			<div id="service-detail-pane" style="display:none">
				<div class="north header">
					<h1>Service Detail</h1>
					<h2>
						<span class="service-name"> Name</span> 
						<span class="service-version">Version</span>
					</h2>
					<div class="toggle-control flex switch-holder">
					    <div class="r">
					    	<span>
					    		Status
					    	</span>
					    	<span class="deploy-switch">
					    		[deployed] [undeploy]
					    	</span>
					    </div>
					</div>

					<div class="button-bar">
						<button class="flex button reconfigure-button"><span><i class="pre pencil">Reconfigure</i></span></button>
					</div>

				</div>
				<div class="center">
				</div>

			</div>	
			
			<div id="available-services-dialog" class="dialog">
					<div class="ui-layout-north" > 
						<h1>Select a Service</h1>
					</div>
					<div class="ui-layout-west" style="display: none;"> 
						<div id="available-services-list" class="ui-layout-content ui-widget-content dc-item-list">
							<table>
								<thead>
									<tr>
										<th>&nbsp;</th>
										<th>Service</th>
									</tr>
								</thead>
								<tbody  id="available-services-list">
								</tbody>							
							
							</table>


						</div>
					 </div> 
				
					<div class="ui-layout-center" style="display: none;"> 
						<div id="service-detail" class="ui-layout-content ui-widget-content" style="border: 0;">
							<h1>{Service Name</h1>

							<p>{
							service description service description service description service description service description 							
							service description service description service description service description service description 							
							service description service description service description service description service description 							
							service description service description service description service description service description 							
							service description service description service description service description service description 							
							service description service description service description service description service description 							
							service description service description service description service description service description 							
							service description service description service description service description service description 							
							}</p>
						</div>
					</div> 
			</div>

			<div id="reconfigure-service-dialog" class="dialog" title="Reconfigure Service">
				<h1>Reconfigure the Service</h1>
								<form enctype="multipart/form-data">
					<div id="form-fields" class="form-fields h400">
						<fieldset>
							<ul>
								<li class="row clearfix first-of-type">
									<label for="host">Select Host</label>
									<select name="host" id="host" class="field" />
										<option value="null default">- Select one -</option>
										<option value="1">First option</option>
										<option value="2">Second option</option>
									</select>
								</li>
								<li class="row clearfix"><label for="textinput1">Text Input</label><input type="text" name="textinput1" id="dropdown1" class="field" /></li>
								<li class="row clearfix">
									<label for="dropdown1">Dropdown with a really long name that wraps</label>
									<select name="dropdown1" id="dropdown1" class="field" />
										<option value="null default">- Select one -</option>
										<option value="1">First option</option>
										<option value="2">Second option</option>
									</select>
								</li>
								<li class="row clearfix">
								<label for="checkboxes">Checkboxes</label>
									<ul class="field">
										<li><input type="checkbox" id="c1" />Checkbox 1</li>
										<li><input type="checkbox" id="c2" />Checkbox 2</li>
										<li><input type="checkbox" id="c3" />Checkbox 3</li>
										<li><input type="checkbox" id="c4" />Checkbox 4</li>
										<li><input type="checkbox" id="c1" />Checkbox 1</li>
										<li><input type="checkbox" id="c2" />Checkbox 2</li>
										<li><input type="checkbox" id="c3" />Checkbox 3</li>
										<li><input type="checkbox" id="c4" />Checkbox 4</li>
										<li><input type="checkbox" id="c1" />Checkbox 1</li>
										<li><input type="checkbox" id="c2" />Checkbox 2</li>
										<li><input type="checkbox" id="c3" />Checkbox 3</li>
										<li><input type="checkbox" id="c4" />Checkbox 4</li>
										<li><input type="checkbox" id="c1" />Checkbox 1</li>
										<li><input type="checkbox" id="c2" />Checkbox 2</li>
										<li><input type="checkbox" id="c3" />Checkbox 3</li>
										<li><input type="checkbox" id="c4" />Checkbox 4</li>
									</ul>
								</li>
							</ul>
						</fieldset>
					</div>
				</form>
			</div>

			<div id="configure-service-dialog" class="dialog" title="Configure Service">
				<h1>Configure the Service</h1>
				<p class="hint">Configure your service, then click "Deploy"</p>
				<form enctype="multipart/form-data">
					<div id="form-fields" class="form-fields h400">
						<fieldset>
							<ul>
								<li class="row clearfix first-of-type">
									<label for="host">Select Host</label>
									<select name="host" id="host" class="field" />
										<option value="null default">- Select one -</option>
										<option value="1">First option</option>
										<option value="2">Second option</option>
									</select>
								</li>
								<li class="row clearfix"><label for="textinput1">Text Input</label><input type="text" name="textinput1" id="dropdown1" class="field" /></li>
								<li class="row clearfix">
									<label for="dropdown1">Dropdown with a really long name that wraps</label>
									<select name="dropdown1" id="dropdown1" class="field" />
										<option value="null default">- Select one -</option>
										<option value="1">First option</option>
										<option value="2">Second option</option>
									</select>
								</li>
								<li class="row clearfix">
								<label for="checkboxes">Checkboxes</label>
									<ul class="field">
										<li><input type="checkbox" id="c1" />Checkbox 1</li>
										<li><input type="checkbox" id="c2" />Checkbox 2</li>
										<li><input type="checkbox" id="c3" />Checkbox 3</li>
										<li><input type="checkbox" id="c4" />Checkbox 4</li>
										<li><input type="checkbox" id="c1" />Checkbox 1</li>
										<li><input type="checkbox" id="c2" />Checkbox 2</li>
										<li><input type="checkbox" id="c3" />Checkbox 3</li>
										<li><input type="checkbox" id="c4" />Checkbox 4</li>
										<li><input type="checkbox" id="c1" />Checkbox 1</li>
										<li><input type="checkbox" id="c2" />Checkbox 2</li>
										<li><input type="checkbox" id="c3" />Checkbox 3</li>
										<li><input type="checkbox" id="c4" />Checkbox 4</li>
										<li><input type="checkbox" id="c1" />Checkbox 1</li>
										<li><input type="checkbox" id="c2" />Checkbox 2</li>
										<li><input type="checkbox" id="c3" />Checkbox 3</li>
										<li><input type="checkbox" id="c4" />Checkbox 4</li>
									</ul>
								</li>
							</ul>
						</fieldset>
					</div>
				</form>	
			</div>
			
		</tiles:putAttribute>
		
</tiles:insertDefinition>	
	</tiles:putAttribute>
	
</tiles:insertDefinition>



