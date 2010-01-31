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
dojo.require("dojo.parser");

//using this early for the forceNoFlash test:
dojo.require("dojox.embed.Flash");

var passthrough = function(msg){
	//for catching messages from Flash
	if(window.console){
		console.log(msg);	
	}
}

forceNoFlash = false;
selectMultipleFiles = false;

var qs = window.location.href.split("?");

if(qs.length>1){
	qs = qs[1];
	if(qs.indexOf("forceNoFlash")>-1){
		forceNoFlash = true;
	}
	if(qs.indexOf("multiMode")>-1){
		selectMultipleFiles = true;
	}
}
 
var setLoc = function(href){
	window.location.href = window.location.href.split("?")[0] + href;
}

var uploadUrl = "Upload";
var rmFiles = "";
var fileMask = [
	["Jpeg File", 	"*.jpg;*.jpeg"],
	["GIF File", 	"*.gif"],
	["PNG File", 	"*.png"],
	["Archive File","*.zip;*.tar"],
	["All Images", 	"*.jpg;*.jpeg;*.gif;*.png;*.zip;*.tar"]
];


function activateUploader(){

	if(dojo.isArray(fileMask[0])){
		dojo.byId("fTypes").innerHTML+=fileMask[fileMask.length-1][1];
	}else{
		dojo.byId("fTypes").innerHTML+=fileMask[1];
	}
	
	dojo.byId("fileToUpload").value = "";
	
	console.log("LOC:", window.location)
	console.log("UPLOAD URL:",uploadUrl);
	
	var f0 = new dojox.form.FileUploader({
		button:dijit.byId("btn0"), 
		degradable:true,
		uploadUrl:uploadUrl, 
		uploadOnChange:false, 
		selectMultipleFiles:selectMultipleFiles,
		fileMask:fileMask,
		isDebug:true
	});
	
	doUpload = function(){
		console.log("doUpload");
		var selected = dojo.byId("selectalbum").value;
		if(selected == null || (selected != null && selected == "" && selected.length == 0))
			alert("Photo Upload can not be started.Select Album before upload");
		else
			f0.upload({albumname:selected});
	}
	
	dojo.connect(f0, "onChange", function(data){
		console.log("DATA:", data);
		dojo.forEach(data, function(d){
			//file.type no workie from flash selection (Mac?)
			if(selectMultipleFiles){
				dojo.byId("fileToUpload").value += d.name+" "+Math.ceil(d.size*.001)+"kb \n";
			}else{
				dojo.byId("fileToUpload").value = d.name+" "+Math.ceil(d.size*.001)+"kb \n";
			}
		});
	});

	dojo.connect(f0, "onProgress", function(data){
		console.warn("onProgress", data);
		dojo.byId("fileToUpload").value = "";
		dojo.forEach(data, function(d){
			dojo.byId("fileToUpload").value += "("+d.percent+"%) "+d.name+" \n";
			
		});
	});

	dojo.connect(f0, "onComplete", function(data){
		console.warn("onComplete", data);
	});
	
	Destroy = function(){
		f0.destroyAll();
	}
	
}

var cleanUp = function(){
	dojo.byId("fileToUpload").value = "";
	dojo.xhrGet({
		url:uploadUrl,
		handleAs:"text",
		content:{
			rmFiles:rmFiles
		}
	});
	rmFiles = "";
}