<%@include file="/WEB-INF/jsp/include.jsp"%>
<tiles:insertDefinition name="base-service" >
	<tiles:putAttribute name="title">
		<spring:message code="configure.service" />	
	</tiles:putAttribute>
	<tiles:putAttribute name="menu" value=""/>
	<tiles:putAttribute name="main-content">
		<tiles:insertDefinition name="base-main-content">
			<tiles:putAttribute name="header">
				<tiles:insertDefinition name="base-content-header">
					<tiles:putAttribute name="title">
						<spring:message code="configure.service"/>
					</tiles:putAttribute>	
				</tiles:insertDefinition>
			</tiles:putAttribute>
			<tiles:putAttribute name="body">
				<div>
					<h3>
						${serviceInfo.displayName}
					</h3>
					<p>
						${serviceInfo.description}
					</p>
				</div>
				<form:form >
					<input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}"/>
					<c:forEach items="${errors}" var="err">
						<ul>
							<c:if test="${empty err.source}">
							<li>
									<span class="message-error">${err.text}</span>
							</li>
							</c:if>
							
						</ul>
						
					</c:forEach>
					<div>
						<table class="basic-form">
							<c:forEach items="${userConfigs}" var="userConfig">
								<tr>
									<td class="label">${userConfig.displayName}</td>
									<td class="input">
										<c:choose>

											<c:when test="${userConfig.inputType.name == 'SINGLESELECT' }">
												<select name="${userConfig.name}">
													<c:forEach items="${userConfig.options}" var="option">
														<option value="${option.value}" <c:if test="${option.selected}">selected="selected"</c:if>>${option.displayName}</option>
													</c:forEach>
												</select>
											</c:when>

											<c:when test="${userConfig.inputType.name == 'MULTISELECT' }">
												<ul class="vertical-list">
														<c:forEach items="${userConfig.options}" var="option" varStatus="status">
														<li>
															<input id="${userConfig.name}-checkbox-${status.count-1}" type="checkbox" name="${userConfig.name}-checkbox-${status.count-1}" <c:if test="${option.selected}">checked="checked"</c:if>/>
															<label  for="${userConfig.name}-checkbox-${status.count-1}" >
															
															${option.displayName}</label>
															</li>
	
														</c:forEach>
												</ul>
																									
											</c:when>

											<c:when test="${userConfig.inputType.name == 'TEXT' }">
												<input type="text" name="${userConfig.name}" value="${userConfig.value}"/>
											</c:when>
											
										</c:choose>										
									</td>
									<td>
										<c:forEach items="${errors}" var="err">
											<c:if test="${err.source == userConfig.name }">
												<span class="message-error">${err.text}</span>
											</c:if>
										</c:forEach>
									</td>
								</tr>
							</c:forEach>
						</table>
					</div>
					<div class="basic-form-buttons" >
						<input type="submit" name="_eventId_submit" class="blocking-action" value="<spring:message code="deploy"/>"/> 
						<input type="submit" name="_eventId_cancel" value="<spring:message code="cancel"/>"/> 
					</div>
				</form:form>
			</tiles:putAttribute>
		</tiles:insertDefinition>
	</tiles:putAttribute>
</tiles:insertDefinition>

