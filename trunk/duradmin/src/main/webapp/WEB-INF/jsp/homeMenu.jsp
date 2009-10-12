<%@include file="/WEB-INF/jsp/include.jsp"%>
<div>
	<div>
		Current Provider:
	</div>
	<div>
		<form:form modelAttribute="contentStoreSelector">
			<form:select path="selectedId">
				<form:options items="${contentStoreSelector.contentStores}"
					itemLabel="storageProviderType" itemValue="storeId" 
				/>
			</form:select>
		</form:form>
	</div>
</div>



