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
var facebookUidService;

dojo.addOnLoad(function() {
    dojo.require("dojo._base.xhr");
    dojo.require("dojo.rpc.JsonService");
    dojo.addOnLoad(checkAccessTokenRedirect);
    dojo.addOnLoad(initView);

});


function initView(){
   var selectFaceApp = dojo.byId("selectFaceApp");
   selectFaceApp.value = "FaceBook-Friend-Finder";
  dojo.byId("or").style.display = "none";
  dojo.byId("imageFilePath").style.display = "none";
  dojo.byId("imageUrl").style.display = "none";
  dojo.byId("train_label").style.display = "none";
  dojo.byId("authFBLabel").style.display = "";
  dojo.byId("train_uname").style.display = "";
  dojo.byId("import_submit").style.display = "";

}

function manageFields(){
   var selectFaceApp = dojo.byId("selectFaceApp");
   var selectedApp = selectFaceApp.value;


  if(selectedApp == "General-Face-Recognition") {
  dojo.byId("imageFilePath").style.display = "";
  dojo.byId("imageUrl").style.display = "";
  dojo.byId("train_uname").style.display = "";
  dojo.byId("import_submit").style.display = "";
  dojo.byId("train_label").style.display = "";
  dojo.byId("authFBLabel").style.display = "none";

  }else if(selectedApp == "FaceBook-Friend-Finder") {
  dojo.byId("or").style.display = "none";
  dojo.byId("imageFilePath").style.display = "none";
  dojo.byId("imageUrl").style.display = "none";
  dojo.byId("train_label").style.display = "none";
  dojo.byId("authFBLabel").style.display = "";
  dojo.byId("train_uname").style.display = "";
  dojo.byId("import_submit").style.display = "";
  }


}


function checkAccessTokenRedirect(){
  var url = window.location.href;
 if(url != "http://localhost:8080/photark/admin/face.html") {
  var accesstoken = url.split("&")[0].split("=")[1];

  alert(accesstoken);
//var s = "https://graph.facebook.com/me?access_token="+accesstoken;
//var s="http://graph.facebook.com/100000016449363";
  }
 }

function logout() {
    window.location = "./logout";
}

function facebookAuth() {
var url = "https://graph.facebook.com/oauth/authorize?type=user_agent&client_id=224265757599119&redirect_uri=http://localhost:8080/photark/admin/face.html&  scope=user_photos,email,user_birthday,user_online_presence,offline_access";
window.location = url;
}


