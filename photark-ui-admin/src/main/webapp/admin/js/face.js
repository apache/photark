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
    genericFaceService = new dojo.rpc.JsonService(photark.constants.GenericFriendFinder);

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
        store_facebook_access_token(accesstoken);

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
     var userName = dojo.byId("train_uname_input").value;

    if (selectFaceApp.value == "General-Face-Recognition") {
        var filePath = dojo.byId("imageFilePathInput").value;
        var fileUrl = dojo.byId("imageUrlInput").value;
        var label = dojo.byId("train_label_input").value;

        if (label == "") {
            label = "photark_default";
        }

        if ((filePath == "" ) && (fileUrl != "")) {
            genericFaceService.trainUrlImage(fileUrl, userName, label).addCallback(facebook_gff_void_callback);
        } else if ((fileUrl == "" ) && (filePath != "")) {

            genericFaceService.trainLocalImage(filePath, userName, label).addCallback(facebook_gff_void_callback);
        } else {
           alert("..You should fill either image file path or url ...!!! ");
        }


    } else if (selectFaceApp.value == "FaceBook-Friend-Finder") {
        faceService.train(userName).addCallback(facebook_ff_callback);
    }

}

function store_facebook_access_token(accessToken) {
    dojo.xhrPost({
        url:"../security", //photark.constants.SecurityEndpoint,
        content:{request:"getUser"},
        handleAs: "json",
        load: function(response, ioArgs) {
            facebookService.storeFacebookAccessToken(response.user.userId, accessToken).addCallback(facebook_ff_void_callback);
        },
        error: function(response, ioArgs) {

        }
    });
}

function face_callback(items, exception) {
    if (exception) {
        alert("Error");
    }

    alert(items);
}

function facebook_ff_void_callback(items, exception) {
    if (exception) {
        alert("Error");
    } else {
        //      alert("CAME");
    }
}

function facebook_gff_void_callback(items, exception) {
    if (exception) {
        alert("Error");
    } else {
          alert("GFF CAME");
    }
}





