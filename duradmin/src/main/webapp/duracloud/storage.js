if(!dojo._hasResource["duracloud.storage"]){
	dojo._hasResource["duracloud.storage"]=true;
	dojo.provide("duracloud.storage");
	dojo.require("dojox.storage");
	dojo.require("dojo.cookie");
	dojo.require("duracloud._base");
	
	(function(){
		
		var globalNamespace = "duracloud";
		var spacesNamespace = globalNamespace + ".spaces";

		/**
		 * Init method checks if the storage is stale and clears it.
		 */
		duracloud.storage.init = function(){
			try{
				var cookieName = "duracloudSessionId";
				var sessionId = dojo.cookie(cookieName);
				if(sessionId === undefined){
					sessionId = new Date().getMilliseconds().toString();
				}
				
				var storedId =  dojox.storage.get(cookieName);
				if(storedId != sessionId){
					dojox.storage.clear();
					dojox.storage.put(cookieName, sessionId);	
					dojo.cookie(cookieName, sessionId);	
				}
			}catch(err){
				console.error(err);
			}
		};
		
		duracloud.storage.expireContentItem = function(spaceId, contentId){
			duracloud.storage.expireSpace(spaceId);
			try{
				dojox.storage.remove(this._makeKey(contentId), this._makeKey(spacesNamespace,spaceId));
			}catch(err){
				console.error(err);
			}
		};
		
		duracloud.storage.expireSpace = function(spaceId){
			try{
				dojox.storage.clear(this._makeKey(spacesNamespace,spaceId));
				dojox.storage.remove(this._makeKey(spaceId), this._makeKey(spacesNamespace));
			}catch(err){
				console.error(err);
			}
		};

		
		duracloud.storage.getSpace = function(spaceId){
			try{
				return dojox.storage.get(this._makeKey(spaceId), this._makeKey(spacesNamespace));
			}catch(err){
				console.error(err);
				return null;
			}
		};
		
		duracloud.storage.getContentItem = function(spaceId, contentId){
			try{
				return dojox.storage.get(this._makeKey(contentId), this._makeKey(spacesNamespace,spaceId));
			}catch(err){
				console.error(err);
				return null;
			}
		};

		
		duracloud.storage.putSpace = function(spaceId, space){
			try{
				return dojox.storage.put(this._makeKey(spaceId),space, null, this._makeKey(spacesNamespace));
			}catch(err){
				console.error(err);
				return null;
			}
		};

		duracloud.storage.putContentItem = function(spaceId, contentId,contentItem){
			try{
				return dojox.storage.put(this._makeKey(contentId),contentItem, null, this._makeKey(spacesNamespace, spaceId));
			}catch(err){
				console.error(err);
				return null;
			}
		};

		duracloud.storage._makeKey = function (){
			var key = "";
			for(var j=0; j<arguments.length;j++){
				var value = arguments[j];
			    for (var i=0; i<value.length; i++){
			        key += (value.charCodeAt(i).toString(16));
			     }
			}
		    return key;
		};

		duracloud.storage.clear = function (){
			try{
				dojox.storage.clear(this._makeKey(spacesNamespace));
				var spaceKeys = dojox.storage.getNamespaces();
				for(s in spaceKeys){
					dojox.storage.clear(spaceKeys[s]);
				}
			}catch(err){
				console.error(err);
			}
		};

		
	})();
}

