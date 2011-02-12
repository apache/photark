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

//gallery json-rpc service
dojo.require("dojo.rpc.JsonService");
dojo.require("dojo._base.xhr");

var albumCover;
var albums;
var albumName = "New Album";
var albumIndex;
var albumDescEditOn = false;
var albumDesc = "";
var userId;
var SECURITY_TOKEN;
var permissions = "";
var currentView = "";
var userInit=false;

var gallery = new dojo.rpc.JsonService(photark.constants.GalleryServiceEndpoint);
function adminLogout() {
    window.location = "../logout";
}
function populateSelect() {
    gallery = new dojo.rpc.JsonService(photark.constants.GalleryServiceEndpoint);
    gallery.getAlbumsToUser(SECURITY_TOKEN).addCallback(function(albums, exception) {
        //  gallery.getAlbums().addCallback( function(albums, exception) {
        if (exception) {
            //            alert(exception.msg);
            //            return;
            adminLogout();
        }
        this.albums = albums;
        var selectAlbum = dojo.byId("selectAlbum");

        selectAlbum.options.length = 0;
        if (userId != "Guest") {
            selectAlbum.options[selectAlbum.options.length] = new Option("New Album", "New Album", true, false);
        }
        for (var pos = 0; pos < albums.length; pos++) {
            selectAlbum.options[selectAlbum.options.length] = new Option(albums[pos].name, albums[pos].name, false, false);
        }
        selectAlbum.options[selectAlbum.options.length] = new Option("New-Remote-Album", "New-Remote-Album", false, false);

        dojo.byId("selectAlbum").value = albumName;
        if (currentView == "AlbumOnload" || currentView == "Album") {
            manageAlbumFields();
        }

    });
}

//to set the album cover
function setAlbumCoverResponse(cover, exception) {
    if (exception) {
        //            alert(exception.msg);
        //            return;
        adminLogout();
    }
    document.getElementById('albumCoverDiv').innerHTML = "<table id='albumCover' style='width:200px;' border='0' cellspacing='0' cellpadding='1'></table>";

    var table = document.getElementById('albumCover');
    var row = table.insertRow(0);
    var column = row.insertCell(0);
    if (cover != null) {
        var img = document.createElement("img");
        img.src = (window.location.href).replace("admin/upload.html", "") + "gallery/" + albumName + "/" + cover;
        var img_html = "<img src=" + img.src + " class=\"slideImage\" width=200px ondragstart=\"return false\" onselectstart=\"return false\" oncontextmenu=\"return false\" galleryimg=\"no\" usemap=\"#imagemap\" alt=\"\"/>";
        var html = "<table border=\"0\" style=\"width:180px; text-align: center;\"><tr style=\"cellpadding:10\"><td><a>" + img_html + "</a></td></tr><tr><td>" + albumName + "</td></tr></table>";
        column.innerHTML = html;
    }
}

function initializeAdminGallery() {
    //var albumName = albums[albumIndex].name;
    var remove = false;
    if (userId == "SuperAdmin" || (albums[albumIndex].owners + "").indexOf(userId) != -1 || permissions.indexOf("|" + albumName + ".deleteImages|") != -1) {
        remove = true;
    }
    var table = document.getElementById('adminTableGallery');
    var lastRow = table.rows.length;
    for (var i = 0; i < albums[albumIndex].pictures.length;) {
        var row = table.insertRow(Math.floor(i / 4));
        for (var j = 0; j < 4; j++) {
            var column = row.insertCell(i % 4);
            if (albums[albumIndex].pictures[i] != null) {

                var img = document.createElement("img");
                img.src = (window.location.href).replace("admin/upload.html", "") + "gallery/" + albumName + "/" + albums[albumIndex].pictures[i];
                var img_html = "<img src=" + img.src + " class=\"slideImage\" width=200px ondragstart=\"return false\" onselectstart=\"return false\" oncontextmenu=\"return false\" galleryimg=\"no\" usemap=\"#imagemap\" alt=\"\"/>";
                if (remove) {
                    var html = "<table border=\"0\" style=\"width:180px; text-align: center;\"><tr style=\"cellpadding:10\"><td colspan=\"2\"><a>" + img_html + "</a></td></tr><tr><td>" + albums[albumIndex].pictures[i] + "</td><td><a href=\"javascript:confirmDelete('" + albums[albumIndex].pictures[i] + "','image');\">Remove</a></td></tr></table>";
                } else {
                    var html = "<table border=\"0\" style=\"width:180px; text-align: center;\"><tr style=\"cellpadding:10\"><td colspan=\"2\"><a>" + img_html + "</a></td></tr><tr><td>" + albums[albumIndex].pictures[i] + "</td><td></td></tr></table>";
                }
                column.innerHTML = html;

            }
            i++;
        }

    }
}

function manageAlbumFields() {
    var selectAlbum = dojo.byId("selectAlbum");
    albumName = selectAlbum.value;
    document.getElementById('adminGallery').innerHTML = "<table id='adminTableGallery' style='width:720px;' border='0' cellspacing='0' cellpadding='1'></table>";
    if (selectAlbum.children[0].value == "New Album") {
        albumIndex = selectAlbum.selectedIndex - 1;
    } else {
        albumIndex = selectAlbum.selectedIndex;
    }
    cancelAlbumDesc();
    dojo.byId("progressBar").style.display = "none";
    dojo.byId("filesDiv").style.display = "";
    dojo.byId("btnUploader").style.display = "";
    dojo.byId("btnUpload").style.display = "";
    if (albumName == "New Album") {

        document.getElementById('albumCoverDiv').innerHTML = "<table id='albumCover' style='width:200px;' border='0' cellspacing='0' cellpadding='1'></table>";
        dojo.byId("newAlbumName").style.display = "";
        dojo.byId("newAlbumLabel").style.display = "";
        dojo.byId("btnAlbumDesc").style.display = "none";
        dojo.byId("deleteAlbum").style.display = "none";
        dojo.byId("albumDescriptionDiv").innerHTML = "<textarea cols='20' rows='5' class='textarea' name='albumDescription' id='albumDescription'></textarea>";

        dojo.byId("remoteAlb_type").style.display = "none";
        dojo.byId("remoteAlb_passwd").style.display = "none";
        dojo.byId("remoteAlb_uname").style.display = "none";
        dojo.byId("remoteAlb_url").style.display = "none";
        dojo.byId("import_submit").style.display = "none";

    } else if(albumName =="New-Remote-Album") {

         document.getElementById('albumCoverDiv').innerHTML = "<table id='albumCover' style='width:200px;' border='0' cellspacing='0' cellpadding='1'></table>";

         dojo.byId("newAlbumName").style.display = "";
         dojo.byId("remoteAlb_passwd").style.display = "";
         dojo.byId("remoteAlb_uname").style.display = "";
         dojo.byId("remoteAlb_url").style.display = "";
         dojo.byId("import_submit").style.display = "";
         dojo.byId("remoteAlb_type").style.display = "";

         dojo.byId("deleteAlbum").style.display = "none";
         dojo.byId("albumDescriptionDiv").innerHTML = "<textarea cols='20' rows='5' class='textarea' name='albumDescription' id='albumDescription'></textarea>";
         dojo.byId("btnUploader").style.display = "none";
         dojo.byId("btnUpload").style.display = "none";
         dojo.byId("btnAlbumDesc").style.display = "none";
         dojo.byId("newAlbumLabel").style.display = "";
         dojo.byId("filesDiv").style.display = "none";

    } else {

         dojo.byId("remoteAlb_type").style.display = "none";
         dojo.byId("remoteAlb_passwd").style.display = "none";
         dojo.byId("remoteAlb_uname").style.display = "none";
         dojo.byId("remoteAlb_url").style.display = "none";
         dojo.byId("import_submit").style.display = "none";

        dojo.byId("newAlbumName").style.display = "none";
        dojo.byId("newAlbumLabel").style.display = "none";
        if (userId == "SuperAdmin" || ( (albums[albumIndex].owners + "").indexOf(userId) != -1) || permissions.indexOf("|" + albumName + ".editAlbumDescription|") != -1) {
            dojo.byId("btnAlbumDesc").style.display = "";
        } else {
            dojo.byId("btnAlbumDesc").style.display = "none";
        }
        if (userId == "SuperAdmin" || ( (albums[albumIndex].owners + "").indexOf(userId) != -1) || permissions.indexOf("|" + albumName + ".deleteAlbum|") != -1) {
            dojo.byId("deleteAlbum").style.display = "";
        } else {
            dojo.byId("deleteAlbum").style.display = "none";
        }
        gallery.getAlbumsToUser(SECURITY_TOKEN).addCallback(function(albums, exception) {
            //   gallery.getAlbums().addCallback( function(albums, exception) {
            if (exception) {
                //            alert(exception.msg);
                //            return;
                adminLogout();
            }
            dojo.byId("albumDescription").value = albums[albumIndex].description;
            gallery.getAlbumCoverToUser(albums[albumIndex].name, SECURITY_TOKEN).addCallback(setAlbumCoverResponse);
            // gallery.getAlbumCover(albums[albumIndex].name).addCallback(setAlbumCoverResponse);
            initializeAdminGallery();
        });
        dojo.byId("albumDescription").value = "";
        if (!(userId == "SuperAdmin" || (albums[albumIndex].owners + "").indexOf(userId) != -1 || permissions.indexOf("|" + albumName + ".addImages|") != -1)) {
            dojo.byId("filesDiv").style.display = "none";
            dojo.byId("btnUploader").style.display = "none";
            dojo.byId("btnUpload").style.display = "none";
        }
    }
}

//this method will delete the selected image from the current album
function addAlbumDesc() {
    if (albumDescEditOn == false) {
        albumDescEditOn = true;
        albumDesc = dojo.byId("albumDescription").value;
        dojo.byId("albumDescriptionDiv").innerHTML = "<textarea cols='20' rows='5' class='textarea' name='albumDescription' id='albumDescription'></textarea>";
        dojo.byId("albumDescription").value = albumDesc;
        dojo.byId("albumDescription").focus();
        dojo.byId("btnAlbumDesc").innerHTML = "Save album Description";
        dojo.byId("cancelBtnAlbumDesc").style.display = "";
    } else {
        var desc = dojo.byId("albumDescription").value;
        cancelAlbumDesc();

        dojo.xhrPost({
            url:"upload",
            content:{albumName:albumName,addAlbumDesc:desc},
            handleAs: "text",
            load: function(response, ioArgs) {
                reloadAdminGallery();
            },
            error: function(response, ioArgs) {
                console.error("Error in editing album description");
                adminLogout();
            }
        });
    }
}

//to cancel the editing of the albumDescription
function cancelAlbumDesc() {
    dojo.byId("albumDescriptionDiv").innerHTML = "<textarea cols='20' rows='5' class='textarea' name='albumDescription' id='albumDescription' readonly='readonly' style='background-color:#F4F2F2;'></textarea>";
    dojo.byId("albumDescription").value = albumDesc;
    dojo.byId("btnAlbumDesc").innerHTML = "Edit album Description";
    dojo.byId("cancelBtnAlbumDesc").style.display = "none";
    albumDescEditOn = false;
}

//this method will delete the selected image from the current album
function removeImage(imageName) {
    dojo.xhrDelete({
        url:"upload",
        content:{imageName:imageName,albumName:albumName},
        handleAs: "text",
        load: function(response, ioArgs) {
            reloadAdminGallery();
        },
        error: function(response, ioArgs) {
            console.error("Error in deleting file");
            adminLogout();
        }
    });
}

function reloadAdminGallery() {
    gallery = new dojo.rpc.JsonService(photark.constants.GalleryServiceEndpoint);
    populateSelect();
}

//confirm before deletion
function confirmDelete(item, type) {
    var r;
    if (type == "Group") {
        if (confirm("Are you sure to delete the user group " + groups[currentGroup][0] + "?")) {
            deleteGroup();
        }
    } else if (type != "Role") {
        if (item == undefined) {
            r = confirm("Are you sure to delete the album " + albumName + "?");
        } else {
            r = confirm("Are you sure to delete the image " + item + "?");
        }
        if (r) {
            removeImage(item);
        }
    }

}


function populateUserInfo() {
    dojo.xhrPost({
        url:"../security", //photark.constants.SecurityEndpoint,
        content:{request:"getUser"},
        handleAs: "json",
        load: function(response, ioArgs) {
            userInit=true;
            displayLoginLinks(response);
             switchAdminViewsTo('AlbumOnload');
        },
        error: function(response, ioArgs) {
            console.error("Error in getting user info");
            adminLogout();
        }
    });
}

function displayLoginLinks(response) {
    if (response != null && response.user.userId != "null") {
        var displayName = response.user.userInfo.displayName;

        document.getElementById("loginName").innerHTML = "<b>" + displayName + "</b>";
    } else {
        document.getElementById("loginName").innerHTML = "";
    }


}

function getJSONAccessList() {
    dojo.xhrPost({
        sync: true,
        url:"../security", //photark.constants.SecurityEndpoint,
        content:{request:"getJSONAccessList"},
        handleAs: "json",
        load: function(response, ioArgs) {
            userId = response.userId;
            SECURITY_TOKEN = response.token;
            permissions = response.permissions;
            if(  userInit==false){

               populateUserInfo();

            //  populateSelect();
            }

        },
        error: function(response, ioArgs) {
            console.error("Error in getting JSON Access List");
            adminLogout();
        }
    });
}

function switchAdminViewsTo(toView) {
    currentView = toView;
    if ('AlbumOnload' == toView) {

        populateSelect();
        dojo.byId("userMgtDiv").style.display = "none";
        dojo.byId("superAdminDiv").style.display = "none";
        dojo.byId("newAlbum").style.display = "";

    } else if ('Album' == toView) {
        getJSONAccessList();
        populateSelect();
        dojo.byId("userMgtDiv").style.display = "none";
        dojo.byId("superAdminDiv").style.display = "none";
        dojo.byId("newAlbum").style.display = "";

    } else {
        if (userId == "SuperAdmin") {
            getJSONAccessList();
            populateSuperAdminRoles();
            populateSuperAdminBlockUser();
            dojo.byId("superAdminDiv").style.display = "";
            dojo.byId("userMgtDiv").style.display = "none";
            dojo.byId("newAlbum").style.display = "none";
        } else {
            if ('Group' == toView) {

                populateGroups();
                //  populateUsers();
                dojo.byId("newRoleDetails").style.display = "none";
                dojo.byId("newGroupDetails").style.display = "";
            } else {
                getJSONAccessList();
                populateRoleAlbums();
                dojo.byId("newRoleDetails").style.display = "";
                dojo.byId("newGroupDetails").style.display = "none";
            }
            dojo.byId("userMgtDiv").style.display = "";
            dojo.byId("newAlbum").style.display = "none";
        }
    }
}

dojo.addOnLoad(function() {
   //getJSONAccessList();
     dojo.addOnLoad(getJSONAccessList);



});
