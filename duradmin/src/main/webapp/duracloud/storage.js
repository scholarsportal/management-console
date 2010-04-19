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
				dojox.storage.remove(this._makeKey(spaceId,contentId));
			}catch(err){
				console.error(err);
			}
		};
		
		duracloud.storage.expireSpace = function(spaceId){
			try{
				dojox.storage.remove(this._makeKey(spaceId));
			}catch(err){
				console.error(err);
			}
		};
		
		duracloud.storage.get = function(){
			try{
				var args = new Array();
				for(var i=0; i < arguments.length;i++){
					args.push(arguments[i]);
				}
				return dojox.storage.get(this._makeKey.apply(this, args));
			}catch(err){
				console.error(err);
				return null;
			}
		};
		
		
		duracloud.storage.put= function(){
			try{
				var args = new Array();
				for(var i=0; i < arguments.length-1;i++){
					args.push(arguments[i]);
				}

				return dojox.storage.put(this._makeKey.apply(this, args), arguments[arguments.length-1]);
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
				dojox.storage.clear();
			}catch(err){
				console.error(err);
			}
		};

		
	})();
}

