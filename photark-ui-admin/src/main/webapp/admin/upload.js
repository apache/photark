/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

dojo.require("dojox.form.FileUploader");
dojo.require("dijit.form.Button");
dojo.require("dijit.ProgressBar");

//using this early for the forceNoFlash test:
dojo.require("dojox.embed.Flash");

var passthrough = function(msg){
	//for catching messages from Flash
	if(window.console){
		console.log(msg);	
	}
};


dojo.addOnLoad( function(){

	var fileUploaderConfig = {
		isDebug:false,
		hoverClass:"uploadHover",
		activeClass:"uploadPress",
		disabledClass:"uploadDisabled",
		uploadUrl:"upload",
		fileMask:[
			["Jpeg File", 	       "*.jpg;*.jpeg"],
			["GIF File", 	       "*.gif"],
			["PNG File", 	       "*.png"],
			["All Images", 	       "*.jpg;*.jpeg;*.gif;*.png"],
			["Image Archive Files","*.zip;*.tar"]
		]
	};
	
	if(dojo.byId("btnUploader")){
		dojo.byId("files").value = "";
		
		//instantiate uploader passing config properties
		var uploader = new dojox.form.FileUploader(dojo.mixin({
			button:dojo.byId("btnUploader"),
			fileListId:"files",
			selectMultipleFiles:true,
			deferredUploading:false
		},fileUploaderConfig), "btnUploader");
		
		
		doUpload = function(){
			console.log("doUpload");
			var selectAlbum = dojo.byId("selectAlbum");
			var selected = selectAlbum.value;
			console.log("selected:"+selected);
			if(selected == null || (selected != null && selected == "" && selected.length == 0)) {
				alert("Photo Upload can not be started.Select Album before upload");
			} else if(selected == "New Album") {
				var albumName = dojo.byId("newAlbumName").value;
				//add new album to list of albums
				selectAlbum.options[selectAlbum.options.length] =  new Option(albumName, albumName, false, false);
				//upload the files
				uploader.upload({albumName:albumName});
			} else {
				//upload files to existent album
				uploader.upload({albumName:selected});
			}
			dojo.byId("newAlbumName").value ="";
			
		}
	}

});