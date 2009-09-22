<%@include file="/WEB-INF/jsp/include.jsp"%>
    <c:if test="${not empty error}">
      <div id="error"><c:out value="${error}" /></div>
    </c:if>
    
    <h2>Services</h2>
    
    <div class="service">
    <h4>Deployed Services</h4>
    <c:forEach items="${deployedServices}" var="service">
      <div class="service">
        <table class="space">
          <tr>
            <th>ID</th>
            <td><c:out value="${service.serviceId}" /></td>
          </tr>
          <tr>
            <th>Status</th>
            <td><c:out value="${service.status}" /></td>
          </tr>          
          <tr>
            <th>Configuration</th>
            <td>
              <table>
              <c:forEach items="${service.config}" var="configItem">                
                <tr>
                  <td><c:out value="${configItem.key}" />:</td>
                  <td><c:out value="${configItem.value}" /></td>
                </tr>
              </c:forEach>
              </table>
            </td>
          </tr>
          <tr>
            <td colspan="2">
              <form action="unDeployService.htm" method="post">
                <input type="hidden" name="serviceId" value="<c:out value="${service.serviceId}" />" />
                <input type="submit" value="UnDeploy <c:out value="${service.serviceId}" />" />
              </form>          
            </td>
          </tr>
        </table>
      </div>
    </c:forEach>
    </div>
    
    <div class="service">
    <h4>Available Services</h4>
    <c:forEach items="${availableServices}" var="service">
      <div class="service">
        <table class="space">
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
                    <th><c:out value="${configItem.key}" /></th>
                    <td><input type="text" id="config.<c:out value="${configItem.key}" />" name="config.<c:out value="${configItem.key}" />" value="<c:out value="${configItem.value}" />" /></td>
                  </tr>
                  </c:forEach>
                  <tr>
                    <th>Deploy To</th>
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