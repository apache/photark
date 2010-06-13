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
var albumName="New Album";
var albumIndex;
var albumDescEditOn=false;
var albumDesc="";

var gallery = new dojo.rpc.JsonService( photark.constants.GalleryServiceEndpoint );

function populateSelect(){
    gallery = new dojo.rpc.JsonService( photark.constants.GalleryServiceEndpoint );
    gallery.getAlbums().addCallback( function(albums, exception) {
        if(exception) {
            alert(exception.msg);
            return;
        }
        this.albums=albums;
        var selectAlbum = dojo.byId("selectAlbum");

        selectAlbum.options.length=0;
        selectAlbum.options[selectAlbum.options.length] =  new Option("New Album", "New Album", true, false);
        for(var pos = 0; pos<albums.length; pos++) {
            selectAlbum.options[selectAlbum.options.length] =  new Option(albums[pos].name, albums[pos].name, false, false);
        }
        dojo.byId("selectAlbum").value=albumName;
        manageAlbumFields();

    });
}

//to set the album cover
function setAlbumCoverResponse(cover, exception) {
    if(exception){
        alert(exception.msg);
        return;
    }
    document.getElementById('albumCoverDiv').innerHTML = "<table id='albumCover' style='width:200px;' border='0' cellspacing='0' cellpadding='1'></table>";

    var table=document.getElementById('albumCover');
    var row = table.insertRow(0);
    var column = row.insertCell(0);
    if (cover != null) {
        var img = document.createElement("img");
        img.src = (window.location.href).replace("admin/upload.html","") + "gallery/"+ albumName +"/" + cover;
        var img_html = "<img src=" + img.src + " class=\"slideImage\" width=200px ondragstart=\"return false\" onselectstart=\"return false\" oncontextmenu=\"return false\" galleryimg=\"no\" usemap=\"#imagemap\" alt=\"\"/>";
        var html = "<table border=\"0\" style=\"width:180px; text-align: center;\"><tr style=\"cellpadding:10\"><td><a>" + img_html + "</a></td></tr><tr><td>"+albumName+"</td></tr></table>";
        column.innerHTML = html;
    }
}

function initializeAdminGallery() {
    var table=document.getElementById('adminTableGallery');
    var lastRow = table.rows.length;
    for (var i = 0; i < albums[albumIndex].pictures.length;) {
        var row = table.insertRow( Math.floor(i/4));
    for (var j = 0; j < 4;j++) {
            var column = row.insertCell(i%4);
            if (albums[albumIndex].pictures[i] != null) {
                var albumName = albums[albumIndex].name;
                var img = document.createElement("img");
                img.src = (window.location.href).replace("admin/upload.html","") + "gallery/"+ albumName +"/" + albums[albumIndex].pictures[i];
                var img_html = "<img src=" + img.src + " class=\"slideImage\" width=200px ondragstart=\"return false\" onselectstart=\"return false\" oncontextmenu=\"return false\" galleryimg=\"no\" usemap=\"#imagemap\" alt=\"\"/>";
                var html = "<table border=\"0\" style=\"width:180px; text-align: center;\"><tr style=\"cellpadding:10\"><td colspan=\"2\"><a>" + img_html + "</a></td></tr><tr><td>"+albums[albumIndex].pictures[i]+"</td><td><a href=\"javascript:confirmDelete('"+albums[albumIndex].pictures[i]+"');\">Remove</a></td></tr></table>";
                column.innerHTML = html;
            }
        i++;
        }

    }
}

function manageAlbumFields(){
    var selectAlbum = dojo.byId("selectAlbum");
    albumName = selectAlbum.value;
    document.getElementById('adminGallery').innerHTML = "<table id='adminTableGallery' style='width:720px;' border='0' cellspacing='0' cellpadding='1'></table>";
    albumIndex=selectAlbum.selectedIndex-1;
    cancelAlbumDesc();
    dojo.byId("progressBar").style.display="none";

    if(albumName == "New Album") {
        document.getElementById('albumCoverDiv').innerHTML = "<table id='albumCover' style='width:200px;' border='0' cellspacing='0' cellpadding='1'></table>";
        dojo.byId("newAlbumName").style.display = "";
        dojo.byId("newAlbumLabel").style.display = "";
        dojo.byId("btnAlbumDesc").style.display = "none";
        dojo.byId("deleteAlbum").style.display = "none";
        dojo.byId("albumDescriptionDiv").innerHTML="<textarea cols='20' rows='5' class='textarea' name='albumDescription' id='albumDescription'></textarea>";
    }else{
        dojo.byId("newAlbumName").style.display = "none";
        dojo.byId("newAlbumLabel").style.display = "none";
        dojo.byId("btnAlbumDesc").style.display = "";
        dojo.byId("deleteAlbum").style.display = "";
        gallery.getAlbums().addCallback( function(albums, exception) {
            if(exception) {
                alert(exception.msg);
                return;
            }
            dojo.byId("albumDescription").value=albums[selectAlbum.selectedIndex-1].description;
            gallery.getAlbumCover(albums[albumIndex].name).addCallback(setAlbumCoverResponse);
            initializeAdminGallery();
        });
        dojo.byId("albumDescription").value="";
    }
}

//this method will delete the selected image from the current album
function addAlbumDesc(){
    if(albumDescEditOn==false){
        albumDescEditOn=true;
        albumDesc=dojo.byId("albumDescription").value;
        dojo.byId("albumDescriptionDiv").innerHTML="<textarea cols='20' rows='5' class='textarea' name='albumDescription' id='albumDescription'></textarea>";
        dojo.byId("albumDescription").value=albumDesc;
        dojo.byId("albumDescription").focus();
        dojo.byId("btnAlbumDesc").innerHTML="Save album Description";
        dojo.byId("cancelBtnAlbumDesc").style.display="";
    }else{
        var desc=dojo.byId("albumDescription").value;
        cancelAlbumDesc();

        dojo.xhrPost({
            url:"upload",
            content:{albumName:albumName,addAlbumDesc:desc},
            handleAs: "text",
            load: function(response, ioArgs){
                reloadAdminGallery();
            },
            error: function(response, ioArgs){
            console.error("Error in editing album description");
            }
        });
    }
}

//to cancel the editing of the albumDescription
function cancelAlbumDesc(){
    dojo.byId("albumDescriptionDiv").innerHTML="<textarea cols='20' rows='5' class='textarea' name='albumDescription' id='albumDescription' readonly='readonly' style='background-color:#F4F2F2;'></textarea>";
    dojo.byId("albumDescription").value=albumDesc;
    dojo.byId("btnAlbumDesc").innerHTML="Edit album Description";
    dojo.byId("cancelBtnAlbumDesc").style.display="none";
    albumDescEditOn=false;
}

//this method will delete the selected image from the current album
function removeImage(imageName){
    dojo.xhrDelete({
        url:"upload",
        content:{imageName:imageName,albumName:albumName},
        handleAs: "text",
        load: function(response, ioArgs){
            reloadAdminGallery();
        },
        error: function(response, ioArgs){
        console.error("Error in deleting file");
        }
    });
}

function reloadAdminGallery(){
    gallery = new dojo.rpc.JsonService( photark.constants.GalleryServiceEndpoint );
    populateSelect();
}

//confirm before deletion
function confirmDelete(item ){
var r;
    if(item==undefined){
        r=confirm("Are you sure to delete the album "+albumName+"?");
    }else{
        r=confirm("Are you sure to delete the image "+item+"?");
    }
    if (r==true){
        removeImage(item);
    }
}


function populateUserInfo() {
            dojo.xhrPost({
                url:"../security", //photark.constants.SecurityEndpoint,
                content:{request:"getUser"},
                handleAs: "json",
                load: function(response, ioArgs) {
                    displayLoginLinks(response);
                },
                error: function(response, ioArgs) {
                    console.error("Error in getting user info");
                }
            });
        }

function displayLoginLinks  (response) {
    if(response!=null&&response.user.userId!="null"){
        var displayName = response.user.userInfo.displayName;

        document.getElementById("loginName").innerHTML="<b>"+displayName+"</b>" ;
    } else {
        document.getElementById("loginName").innerHTML="";
    }


}
dojo.addOnLoad( function() {
    dojo.addOnLoad(populateUserInfo);
    populateSelect();


});
