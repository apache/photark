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
var facebookService;
var genericFaceService;
var selectFaceApp
var faceService;

dojo.addOnLoad(function() {
    dojo.require("dojo._base.xhr");
    dojo.require("dojo.rpc.JsonService");
    dojo.addOnLoad(checkAccessTokenRedirect);
    dojo.addOnLoad(initView);
    dojo.addOnLoad(initServices);

});

function initServices() {
    faceService = new dojo.rpc.JsonService(photark.constants.FaceRecognitionService);
    facebookService = new dojo.rpc.JsonService(photark.constants.FacebookFriendFinder);
}


function initView() {
    selectFaceApp = dojo.byId("selectFaceApp");
    selectFaceApp.value = "FaceBook-Friend-Finder";
    dojo.byId("or").style.display = "none";
    dojo.byId("imageFilePath").style.display = "none";
    dojo.byId("imageUrl").style.display = "none";
    dojo.byId("train_label").style.display = "none";
    dojo.byId("authFBLabel").style.display = "";
    dojo.byId("train_uname").style.display = "";
    dojo.byId("import_submit").style.display = "";

}

function manageFields() {
    selectFaceApp = dojo.byId("selectFaceApp");
    var selectedApp = selectFaceApp.value;


    if (selectedApp == "General-Face-Recognition") {
        dojo.byId("imageFilePath").style.display = "";
        dojo.byId("imageUrl").style.display = "";
        dojo.byId("train_uname").style.display = "";
        dojo.byId("import_submit").style.display = "";
        dojo.byId("train_label").style.display = "";
        dojo.byId("authFBLabel").style.display = "none";

    } else if (selectedApp == "FaceBook-Friend-Finder") {
        dojo.byId("or").style.display = "none";
        dojo.byId("imageFilePath").style.display = "none";
        dojo.byId("imageUrl").style.display = "none";
        dojo.byId("train_label").style.display = "none";
        dojo.byId("authFBLabel").style.display = "";
        dojo.byId("train_uname").style.display = "";
        dojo.byId("import_submit").style.display = "";
    }


}


function checkAccessTokenRedirect() {
    var url = window.location.href;
    if (url != "http://localhost:8080/photark/admin/face.html") {
        var accesstoken = url.split("&")[0].split("=")[1];

        alert(accesstoken);
     
    }
}

function logout() {
    window.location = "./logout";
}

function facebookAuth() {
    var url = "https://graph.facebook.com/oauth/authorize?type=user_agent&client_id=151116644958708&redirect_uri=http://localhost:8080/photark/admin/face.html&  scope=user_photos,email,user_birthday,user_online_presence,offline_access";
    window.location = url;
}

function trainUser() {
    if (selectFaceApp.value == "General-Face-Recognition") {
        //TODO call train method in generic face app

    } else if (selectFaceApp.value == "FaceBook-Friend-Finder") {
        faceService.train("1271543184@facebook.com").addCallback(facebook_ff_callback);
    }

}

function face_callback(items, exception) {
    if (exception) {
        alert("Error");
    }
}

function facebook_ff_callback(items, exception) {
    if (exception) {
        alert("Error");
    }
    alert("AA");
}





