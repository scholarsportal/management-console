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
				<form:form >
					Configure Service
					<input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}"/>
					<form:errors element="div" cssClass="message-error" />
					<div>
						<table class="basic-form">
							<c:forEach items="${service.userConfigs}" var="userConfig">
								<tr>
									<td class="label">${userConfig.displayName}</td>
									<td class="input">
										<c:choose>
											<c:when test="${userConfig.inputType.name == 'TEXT' }">
												Text field
											</c:when>
											<c:when test="${userConfig.inputType.name == 'SINGLESELECT' }">
												<select name="${userConfig.name}">
													<c:forEach items="${userConfig.options}" var="option">
														<option value="${option.value}">${option.displayName}</option>
													</c:forEach>
												</select>
											</c:when>

											<c:when test="${userConfig.inputType.name == 'MULTISELECT' }">
												<ul>
														<c:forEach items="${userConfig.options}" var="option" varStatus="status">
														<li>

															<input type="checkbox" name="${userconfig.name}-checkbox-${status.count}" <c:if test="${option.selected}">checked</c:if>/>
															<label for="${userconfig.name}-checkbox-${status.count}">${option.displayName}</label>
															</li>
	
														</c:forEach>
												</ul>
																									
											</c:when>

											<c:when test="${userConfig.inputType.name == 'TEXT' }">
												<input type="text" name="${userConfig.name}" value="${userConfig.value}"/>
											</c:when>
											
										</c:choose>										
									</td>
									<td class="field-help">help</td>
								</tr>
							</c:forEach>
						</table>
					</div>
					<div class="basic-form-buttons" >
						<input type="submit" name="_eventId_submit" value="<spring:message code="deploy"/>"/> 
						<input type="submit" name="_eventId_cancel" value="<spring:message code="cancel"/>"/> 
					</div>
				</form:form>
			</tiles:putAttribute>
		</tiles:insertDefinition>
	</tiles:putAttribute>
</tiles:insertDefinition>

