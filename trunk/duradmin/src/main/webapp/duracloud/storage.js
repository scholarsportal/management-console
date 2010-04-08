if(!dojo._hasResource["duracloud.storage"]){
	dojo._hasResource["duracloud.storage"]=true;
	dojo.provide("duracloud.storage");
	dojo.require("dojox.storage");
	dojo.require("dojo.cookie");
	dojo.require("duracloud._base");
	
	(function(){
		/**
		 * Init method checks if the storage is stale and clears it.
		 */
		duracloud.storage.init = function(){
			var cookieName = "jsessionid";
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
		};
		
		duracloud.storage.expireContentItem = function(spaceId, contentId){
			duracloud.storage.expireSpace(spaceId);
			dojox.storage.remove(this._makeKey(spaceId,contentId));
		};
		
		duracloud.storage.expireSpace = function(spaceId){
			dojox.storage.remove(this._makeKey(spaceId));
		};
		
		duracloud.storage.get = function(){
			var args = new Array();
			for(var i=0; i < arguments.length;i++){
				args.push(arguments[i]);
			}
			return dojox.storage.get(this._makeKey.apply(this, args));
		};
		
		duracloud.storage.put= function(){
			var args = new Array();
			for(var i=0; i < arguments.length-1;i++){
				args.push(arguments[i]);
			}

			return dojox.storage.put(this._makeKey.apply(this, args), arguments[arguments.length-1]);
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

	})();
}

