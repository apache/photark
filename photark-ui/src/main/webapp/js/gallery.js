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

var index_off = new Image(31, 31);
index_off.src = "images/index.gif";
var index_on = new Image(31, 31);
index_on.src = "images/index_on.gif";
var next_off = new Image(31, 31);
next_off.src = "images/next.gif";
var next_on = new Image(31, 31);
next_on.src = "images/next_on.gif";
var prev_off = new Image(31, 31);
prev_off.src = "images/prev.gif";
var prev_on = new Image(31, 31);
prev_on.src = "images/prev_on.gif";
var slide_before_start = new Image(31, 31);
slide_before_start.src = "images/slide_before_start.gif";
var slide_pause = new Image(31, 31);
slide_pause.src = "images/slide_pause.gif";
var show_slide = new Image(31, 31);
show_slide.src = "images/show_slide.gif";
var show_slide_on = new Image(31, 31);
show_slide_on.src = "images/show_slide_on.gif";
var show_slide_slow = new Image(31, 31);
show_slide_slow.src = "images/show_slide_slow.gif";
var show_slide_slow_on = new Image(31, 31);
show_slide_slow_on.src = "images/show_slide_slow_on.gif";

var gallery;
var remoteGallery;
var searchService;
var faceService;
var galleryName;

var galleryAlbums;
var remoteGalleryAlbums;

var albumTags;
var albumCovers = new Array();
var remoteAlbumCovers = new Array();

var albumName;
var albumItems;
var albumPos = 0;
var pos = 0;
var rpos = 0;
var remoteFlag = 0;

var slideShowSpeed = 0;
var timer;
var userId;
var SECURITY_TOKEN;
var permissions = new Array();
var albumImageToBeLoaded = null;

var facebook_ff;
var genericFaceService;

dojo.addOnLoad(function() {
    dojo.require("dojo._base.xhr");
    dojo.require("dojo.rpc.JsonService");
    dojo.addOnLoad(getJSONAccessList);
    dojo.addOnLoad(populateUserInfo);
    dojo.addOnLoad(initServices);
    dojo.addOnLoad(initGallery);
});
function logout() {
    window.location = "./logout";
}
function getJSONAccessList() {
    dojo.xhrPost({
        sync: true,
        url:"security", //photark.constants.SecurityEndpoint,
        content:{request:"getJSONAccessList"},
        handleAs: "json",
        load: function(response, ioArgs) {
            userId = response.userId;
            SECURITY_TOKEN = response.token;
            permissions = response.permissions;

        },
        error: function(response, ioArgs) {
            console.error("Error in getting JSON Access List");
            logout();
        }
    });
}


function initServices() {
    searchService = new dojo.rpc.JsonService(photark.constants.SearchServiceEndpoint);
    gallery = new dojo.rpc.JsonService(photark.constants.GalleryServiceEndpoint);
    remoteGallery = new dojo.rpc.JsonService(photark.constants.RemoteGalleryServiceEndpoint);
    faceService = new dojo.rpc.JsonService(photark.constants.FaceRecognitionService);
    facebook_ff = new dojo.rpc.JsonService(photark.constants.FacebookFriendFinder);
    genericFaceService = new dojo.rpc.JsonService(photark.constants.GenericFriendFinder);

}

function initGallery() {
    try {
        //following if condition is for a script error that arises in IE 7/8/9
        if (SECURITY_TOKEN = 'undefined') {
            dojo.require("dojo._base.xhr");
            dojo.require("dojo.rpc.JsonService");
            getJSONAccessList();
            populateUserInfo();
            initServices();
        }
        gallery.getAlbumsToUser(SECURITY_TOKEN).addCallback(gallery_getAlbumsResponse);
        remoteGallery.getAlbumsToUser(SECURITY_TOKEN, "remote").addCallback(remote_gallery_getAlbumsResponse);


    } catch(exception) {
        alert(exception);
        // logout();
    }
}

function populateUserInfo() {
    dojo.xhrPost({
        url:"security", //photark.constants.SecurityEndpoint,
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
function displayLoginLinks(response) {
    if (response != null && response.user.userId != "null" && response.user.userId != "Guest") {
        var displayName = response.user.userInfo.displayName;

        document.getElementById("loginLinks").innerHTML = "Welcome <b>" + displayName + "</b> : <span><a href=\"./admin/upload.html\"><u>Admin page</u></a></span>&nbsp;&nbsp;<span><a href=\"./logout/\"><u>Logout</u></a></span>";
    } else {
        document.getElementById("loginLinks").innerHTML = "<span><a href=\"./admin/authenticate\"><u>Super admin</u></a></span>&nbsp;&nbsp;<span><a href=\"./home/authenticate\"><u>Login</u></a></span>";
    }


}

function remote_gallery_getAlbumsResponse(albums, exception) {

    if (exception) {
        // alert(exception.msg);
        // return;
        logout();
    }
    remoteGalleryAlbums = albums;

    for (i = 0; i < remoteGalleryAlbums.length; i++)
    {
        //gallery.getAlbumCover(galleryAlbums[i].name).addCallback(gallery_getAlbumCoverResponse);
        remoteGallery.getAlbumCoverToUser(remoteGalleryAlbums[i].name, SECURITY_TOKEN, "remote").addCallback(remote_gallery_getAlbumCoverResponse);
    }
}

function gallery_getAlbumsResponse(albums, exception) {
    if (exception) {
        // alert(exception.msg);
        // return;
        logout();
    }
    galleryAlbums = albums;

    for (i = 0; i < galleryAlbums.length; i++)
    {
        // gallery.getAlbumCover(galleryAlbums[i].name).addCallback(gallery_getAlbumCoverResponse);
        gallery.getAlbumCoverToUser(galleryAlbums[i].name, SECURITY_TOKEN).addCallback(gallery_getAlbumCoverResponse);
    }
}


function remote_gallery_getAlbumCoverResponse(cover, exception) {
    if (exception) {
        //        alert(exception.msg);
        //        return;
        logout();
    }
    remoteAlbumCovers[rpos] = cover;
    rpos += 1;
    if (remoteAlbumCovers.length == remoteGalleryAlbums.length)
    {
        initializeRemoteGallery();
        displayRemoteGallery();
    }
}

function gallery_getAlbumCoverResponse(cover, exception) {
    if (exception) {
        //        alert(exception.msg);
        //        return;
        logout();
    }
    albumCovers[pos] = cover;
    pos += 1;
    if (albumCovers.length == galleryAlbums.length)
    {
        initializeGallery();
        displayGallery();
    }
}

function searchResponse(items, exception) {

    if (exception) {
        //        alert(exception.msg);
        //        return;
        logout();
    }

    var table = document.getElementById('tableSearch');
    deleteTableRows(table);

    for (i = 0; i < items.length / 5; i++) {
        var row = table.insertRow(i * 2);
        for (j = 0; j < 5 && i * 5 + j < items.length; j++) {

            var column = row.insertCell(j);
            var aux = items[i * 5 + j].split('/', 2);
            var albumName = aux[0];
            var imageName = aux[1];
            //	        var img = document.createElement("img");
            //	        img.src = (window.location.href).replace("admin/upload.html", "") + "gallery/" +items[i*5 + j ];
            //	        img['class'] = "slideImage";
            //	        img.width=100;
            //	        //img.height=10;
            //	        img.ondragstart = function () { return false; };
            //	        img.onselectstart = function () { return false; };
            //	        img.onconstextmenu = function () { return false; };
            //	        img.alt = items[i*5 + j];
            //	        var a = document.createElement("a");
            //	        a.href = "javascript:initializeAlbum('" + albumName + "', '" + imageName + "')";
            //	        a.appendChild(img);
            column.innerHTML = "<a href=\"javascript:initializeAlbum('" + albumName + "', '" + imageName + "')\">" + albumName + "/" + imageName + "</a>";


        }

        row = table.insertRow(i * 2 + 1);
        column = row.insertCell(0);
        column.innerHTML = "<img src=\"images/space.gif\" class=\"slideImage\" width=\"10\" height=\"10\" ondragstart=\"return false\" onselectstart=\"return false\" oncontextmenu=\"return false\" galleryimg=\"no\" usemap=\"#imagemap\" alt=\"\">";

    }

    displaySearchResults();

}

function deleteTableRows(table) {
    while (table.rows.length > 0) {
        table.deleteRow(0);
    }
}

function addTag() {
    var tag = document.getElementById("addtag-input").value;
    searchService.addTag(albumName, albumItems[albumPos], tag);

    var imageID = albumName + '/' + albumItems[albumPos];
    var imageTags = albumTags[imageID];

    if (imageTags == null) {
        imageTags = new Array();
        albumTags[imageID] = imageTags;

    }

    imageTags.push(tag);
    showTags(imageTags);

}

function clearFaceTable() {
    for (var i = document.getElementById("faceTable").rows.length; i > 0; i--) {
        document.getElementById("faceTable").deleteRow(i - 1);
    }
}

function showFriends() {
     dojo.xhrPost({
        url:"security", //photark.constants.SecurityEndpoint,
        content:{request:"getUser"},
        handleAs: "json",
        load: function(response, ioArgs) {
            if(response.user.userId != "Guest") {
              _showFriends();
            } else {
                alert("Please login before using Photark Face Apps....!!!");
            }

        },
        error: function(response, ioArgs) {

        }
    });
}

function _showFriends() {
    clearFaceTable();

    var facetype = dojo.byId("faceAppType").value;
    var textField = document.getElementById('addtag-input');

    if (facetype == "facebook") {
        showFacebookFriends();
    } else if (facetype == "private") {
        checkGenericRecognition(textField.value);
//       showGenericFriends(textField.value);
    }


}

function checkGenericRecognition(userName){
       dojo.xhrPost({
        url:"security", //photark.constants.SecurityEndpoint,
        content:{request:"getUser"},
        handleAs: "json",
        load: function(response, ioArgs) {
            faceService.checkGenericRecognitionValidity(response.user.userId,userName).addCallback(face_callback);
        },
        error: function(response, ioArgs) {

        }
    });
}


function showPreLoader(){
    var img = dojo.byId("loadingImg");
                    dojo.attr(img, {
                       style: {visibility:"visible"}
                    });
}

function hidePreLoader(){
    var img = dojo.byId("loadingImg");
                    dojo.attr(img, {
                       style: {visibility:"hidden"}
                    });
}


function showGenericFriends(userName) {
//  var file_path =  document.getElementById("albumImage").src;
    var file_path = "https://lh4.googleusercontent.com/-rb_m-GQcL00/Ti8sqThvrDI/AAAAAAAAAMY/kUBurbFKJ0A/s640/friends_2.jpg";

    dojo.xhrPost({
        url:"security", //photark.constants.SecurityEndpoint,
        content:{request:"getUser"},
        handleAs: "json",
        load: function(response, ioArgs) {
            showPreLoader();
            genericFaceService.getAllMyFriendsFromPictureUrl(file_path, userName, response.user.userId).addCallback(facebook_gff_callback);
        },
        error: function(response, ioArgs) {

        }
    });

}

function showFacebookFriends() {
//  var file_path =  document.getElementById("albumImage").src;
    var file_path = "https://lh4.googleusercontent.com/-rb_m-GQcL00/Ti8sqThvrDI/AAAAAAAAAMY/kUBurbFKJ0A/s640/friends_2.jpg";

    dojo.xhrPost({
        url:"security", //photark.constants.SecurityEndpoint,
        content:{request:"getUser"},
        handleAs: "json",
        load: function(response, ioArgs) {
            showPreLoader();
            facebook_ff.getAllMyFBFriendsFromPictureUrl(file_path, response.user.userId).addCallback(facebook_ff_callback);
        },
        error: function(response, ioArgs) {

        }
    });

}

function face_callback(userName, exception) {
    if (exception) {
        alert("FB AUTH Error");

    } else {
        if(userName != "photark.not.allowed") {
            showGenericFriends(userName);
        } else {
            alert("You are only allowed to Recognize friends from your own domain...!!!");
        }

    }

}

function facebook_gff_callback(entries, exception) {
    if (exception) {
        alert("FB AUTH Error");
    } else {
        hidePreLoader();
        viewFaceResults(entries,"gff"); // gff refers to Generic Friend Finder
    }


}


function facebook_ff_callback(entries, exception) {
    if (exception) {
        alert("Error");
    } else {
        hidePreLoader();
        viewFaceResults(entries,"fff"); // fff refers to Facebook Friend Finder
    }

}

function viewFaceResults(entries,faceAppType) {

    var table = document.getElementById('faceTable');
    var lastRow = 0;
    var row = table.insertRow(lastRow++);

    column = row.insertCell(0);
    column.width = 1300;
    column.innerHTML = "<span style=\"color:#336633\">"+"Name"+"</span>";
    column = row.insertCell(1);column.width = 400;
    column.innerHTML = "<span style=\"color:#336633\">"+"Gender"+"</span>";
    column = row.insertCell(2);column.width = 400;
    column.innerHTML = "<span style=\"color:#336633\">"+"Confidence"+"</span>";

    if(faceAppType == "fff") {
    for (var i = 0; i < entries.length; i++) {
        var row = table.insertRow(i+1);
        var user_data = entries[i];
        var uname = entries[i].data[0];
        var link = entries[i].data[1];
        var gender = entries[i].data[2];
        var confidence = entries[i].data[3];

        column = row.insertCell(0);
        column.innerHTML = "<a href=\""+link+"\" style=\"color:green;\">"+uname+"</a>";

        column = row.insertCell(1);
        column.innerHTML = "<span style=\"color:green\">"+gender+"</span>";

        column = row.insertCell(2);
        column.innerHTML =  "<span style=\"color:green\">"+confidence+" %"+"</span>";
    }
    } else  if(faceAppType == "gff") {
    for (var i = 0; i < entries.length; i++) {
        var row = table.insertRow(i+1);
        var user_data = entries[i];
        var uname = entries[i].data[0];
        var gender = entries[i].data[1];
        var confidence = entries[i].data[2];

        column = row.insertCell(0);
        column.innerHTML = "<span style=\"color:green\">"+uname+"</span>";

        column = row.insertCell(1);
        column.innerHTML = "<span style=\"color:green\">"+gender+"</span>";

        column = row.insertCell(2);
        column.innerHTML =  "<span style=\"color:green\">"+confidence+" %"+"</span>";
    }
    }

}


function initializeRemoteGallery() {
    var table = document.getElementById('remoteTableGallery');
    var lastRow = table.rows.length;
    for (i = 0; i < remoteGalleryAlbums.length; i++) {
        var row = table.insertRow(lastRow++);
        var column = row.insertCell(0);

        if (remoteAlbumCovers[i] != null) {

            var albumName = remoteGalleryAlbums[i].name;
            var img = document.createElement("img");
            img.src = remoteAlbumCovers[i];
            var img_html = "<img src=" + img.src + " class=\"slideImage\" width=180px ondragstart=\"return false\" onselectstart=\"return false\" oncontextmenu=\"return false\" galleryimg=\"no\" usemap=\"#imagemap\" alt=\"\"/>";
            var html = "<a href=\"javascript:initializeRemoteAlbum('" + albumName + "', null)\">" + img_html + "</a>";
            column.innerHTML = html;

            column = row.insertCell(1);
            column.innerHTML = "<div style=\"width:500\">" + remoteGalleryAlbums[i].description + "</div>";

            row = table.insertRow(lastRow++);
            column = row.insertCell(0)
            column.innerHTML = albumName;

            row = table.insertRow(lastRow++);
            column = row.insertCell(0)
            column.innerHTML = "<img src=\"images/space.gif\" class=\"slideImage\" width=\"10\" height=\"10\" ondragstart=\"return false\" onselectstart=\"return false\" oncontextmenu=\"return false\" galleryimg=\"no\" usemap=\"#imagemap\" alt=\"\">";
        }
    }
}

function initializeGallery() {
    var table = document.getElementById('tableGallery');
    var lastRow = table.rows.length;
    for (i = 0; i < galleryAlbums.length; i++) {
        var row = table.insertRow(lastRow++);
        var column = row.insertCell(0);

        if (albumCovers[i] != null) {

            var albumName = galleryAlbums[i].name;
            var img = document.createElement("img");
            img.src = window.location.href + "gallery/" + albumName + "/" + albumCovers[i];
            var img_html = "<img src=" + img.src + " class=\"slideImage\" width=180px ondragstart=\"return false\" onselectstart=\"return false\" oncontextmenu=\"return false\" galleryimg=\"no\" usemap=\"#imagemap\" alt=\"\"/>";
            var html = "<a href=\"javascript:initializeAlbum('" + albumName + "', null)\">" + img_html + "</a>";
            column.innerHTML = html;

            column = row.insertCell(1);
            column.innerHTML = "<div style=\"width:500\">" + galleryAlbums[i].description + "</div>";

            row = table.insertRow(lastRow++);
            column = row.insertCell(0)
            column.innerHTML = albumName;

            row = table.insertRow(lastRow++);
            column = row.insertCell(0)
            column.innerHTML = "<img src=\"images/space.gif\" class=\"slideImage\" width=\"10\" height=\"10\" ondragstart=\"return false\" onselectstart=\"return false\" oncontextmenu=\"return false\" galleryimg=\"no\" usemap=\"#imagemap\" alt=\"\">";
        }
    }
}

function displayRemoteGallery() {
    setVisibility('gallery', true);
    setVisibility('album', false);
    setVisibility('search', false);
}


function displayGallery() {
    setVisibility('gallery', true);
    setVisibility('album', false);
    setVisibility('search', false);
}

function displaySearchResults() {
    setVisibility('search', true);
    setVisibility('gallery', false);
    setVisibility('album', false);
}

function initializeAlbum(albumName, imageName) {
    try {
        this.albumName = albumName;
        albumImageToBeLoaded = imageName;
        albumTags = new Array();
        gallery.getAlbumPicturesToUser(albumName, SECURITY_TOKEN).addCallback(gallery_getAlbumPicturesResponse);

    } catch(exception) {
        alert(e);
    }
}

function initializeRemoteAlbum(albumName, imageName) {
    try {
        this.albumName = albumName;
        albumImageToBeLoaded = imageName;
        albumTags = new Array();
        remoteGallery.getAlbumPicturesToUser(albumName, SECURITY_TOKEN, "remote").addCallback(gallery_getRemoteAlbumPicturesResponse);

    } catch(exception) {
        alert(e);
    }
}

function loadTags(albumPos) {
    var imageTags = albumTags[albumName + '/' + albumItems[albumPos]];

    if (imageTags == null) {
        searchService.getTags(albumName, albumItems[albumPos]).addCallback(getTagsResponse);

    } else {
        showTags(imageTags);
    }

}

function getTagsResponse(items, exception) {
    if (exception) {
        alert(exception.msg);
        // logout();
        return;
    }

    albumTags[items.imageID] = items.tags;

    if (albumName + '/' + albumItems[albumPos] == items.imageID) {
        showTags(items.tags);
    }

}

function showTags(tags) {
    var table = document.getElementById('tableTags');
    var textField = document.getElementById('addtag-input');
    textField.value = "";
    deleteTableRows(table);

    var lastRow = 0;
    for (i = 0; i < tags.length; i++) {
        var row = table.insertRow(lastRow++);
        var column = row.insertCell(0);

        var divElement = document.createElement("div");
        var tagElement = document.createElement("a");
        tagElement.href = "javascript:executeSearch('tag:" + tags[i] + "')";
        tagElement.innerHTML = tags[i];
        divElement.id = tags[i];
        divElement.style.display = 'inline';
        divElement.onmouseover = function (evt) {
            this.removeButton.style.display = 'inline';
            this.removeButton.style.visibility = 'visible';
        };
        divElement.onmouseout = function (evt) {
            this.removeButton.style.display = 'none';
            this.removeButton.style.visibility = 'hidden';
        };

        divElement.appendChild(tagElement);

        var removeElement = document.createElement("a");
        removeElement['class'] = "removeTag";
        removeElement.href = "javascript:removeTag('" + tags[i] + "')";
        removeElement.id = "removeTag_" + tags[i];
        removeElement.innerHTML = 'remove';
        removeElement.style.display = 'none';
        removeElement.style.visibility = 'hidden';
        divElement.removeButton = removeElement;
        divElement.appendChild(removeElement);

        column.appendChild(divElement);

    }

    setVisibility('tableTags', true);

}

function removeTag(tag) {
    searchService.removeTag(albumName, albumItems[albumPos], tag);
    var table = document.getElementById('tableTags');

    for (i = 0; i < table.rows.length; i++) {

        if (table.rows[i].cells[0].firstChild.id == tag) {
            table.deleteRow(i);
            break;
        }

    }


}

function gallery_getAlbumPicturesResponse(items, exception) {
    if (exception) {
        // alert(exception.msg);
        displayGallery();
        logout();
        //  return;
    }
    albumItems = items;
    albumPos = 0;

    if (albumImageToBeLoaded != null) {

        for (i = 0; i < items.length; i++) {

            if (items[i] == albumImageToBeLoaded) {
                albumPos = i;
                albumImageToBeLoaded = null;

            }

        }

    }

    showAlbum();
}

function gallery_getRemoteAlbumPicturesResponse(items, exception) {
    if (exception) {
        // alert(exception.msg);
        displayGallery();
        logout();
        //  return;
    }
    albumItems = items;
    albumPos = 0;

    if (albumImageToBeLoaded != null) {

        for (i = 0; i < items.length; i++) {

            if (items[i] == albumImageToBeLoaded) {
                albumPos = i;
                albumImageToBeLoaded = null;

            }

        }

    }

    showRemoteAlbum();
}

function showAlbum() {
    if (albumItems.length > 0) {
        showImage(albumPos);
    }
    displayAlbum();
}

function showRemoteAlbum() {
    if (albumItems.length > 0) {
        showRemoteImage(albumPos);
    }
    displayAlbum();
}

function displayAlbum() {
    setVisibility('gallery', false);
    setVisibility('album', true);
    setVisibility('search', false);
}

function showImage(albumPos) {
    var img = document.createElement("img");
    img.onload = function(evt) {
        document.getElementById("albumImage").src = this.src;
        document.getElementById("albumImage").width = this.width;
        document.getElementById("albumImage").height = this.height;
    }
    img.src = window.location.href + "gallery/" + this.albumName + "/" + albumItems[albumPos];
    loadTags(albumPos);
    remoteFlag = 0;
    return false;
}

function showRemoteImage(albumPos) {
    var img = document.createElement("img");
    img.onload = function(evt) {
        document.getElementById("albumImage").src = this.src;
        document.getElementById("albumImage").width = this.width;
        document.getElementById("albumImage").height = this.height;
    }
    img.src = albumItems[albumPos];
    remoteFlag = 1;
    loadTags(albumPos);
    return false;
}


function goNext() {
    if (albumPos < albumItems.length - 1) {
        albumPos++;
        if (remoteFlag == 1) {
            showRemoteImage(albumPos);
        } else {
            showImage(albumPos);
        }
    }
}

function goPrevious() {
    if (albumPos > 0) {
        albumPos--;
        if (remoteFlag == 1) {
            showRemoteImage(albumPos);
        } else {
            showImage(albumPos);
        }

    }
}

function setVisibility(divId, visible) {
    //valid values { visible, hidden }
    if (document.getElementById) {
        var element = document.getElementById(divId)
        if (visible) {
            element.style.display = 'block';
            element.style.visibility = 'visible';
        } else {
            element.style.display = 'none';
            element.style.visibility = 'hidden';
        }
    }
}

function onGoPreviousMouseOver() {
    if (albumPos == 0) {
        document.previous.src = prev_off.src;
    } else {
        document.previous.src = prev_on.src;
    }
}

function onGoNextMouseOver() {
    if (albumPos == albumItems.length - 1) {
        document.next.src = next_off.src;
    } else {
        document.next.src = next_on.src;
    }
}

function goSlideShow() {
    if (slideShowSpeed == 0) {
        slideShowSpeed = 1;
        clearTimeout(timer);
        startTimer(5000);
    } else if (slideShowSpeed == 1) {
        slideShowSpeed = 2;
        clearTimeout(timer);
        startTimer(2000);
    } else {
        slideShowSpeed = 0;
        clearTimeout(timer);
    }
}

function beforeClick() {
    clearTimeout(timer);
    slideShowSpeed = 0;
    document.show.src = slide_before_start.src;
}

function search() {
    var query = document.getElementById("search-input").value;
    executeSearch(query);
}

function executeSearch(query) {
    //searchService.search(query).addCallback(searchResponse);
    searchService.searchToUser(query, SECURITY_TOKEN).addCallback(searchResponse);
}

function onSlideShow() {
    if (slideShowSpeed == 0) {
        document.show.src = show_slide_slow_on.src;
    } else if (slideShowSpeed == 1) {
        document.show.src = show_slide_on.src;
    } else {
        document.show.src = slide_pause.src;
    }
}

function offSlideShow() {
    if (slideShowSpeed == 0) {
        document.show.src = slide_before_start.src;
    } else if (slideShowSpeed == 1) {
        document.show.src = show_slide_slow.src;
    } else {
        document.show.src = show_slide.src;
    }
}

function startTimer(time) {
    if (albumPos < albumItems.length - 1) {
        albumPos++;
    } else {
        albumPos = 1;
    }
    if (remoteFlag == 1) {
        showRemoteImage(albumPos);
    } else {
        showImage(albumPos);
    }
    timer = setTimeout("startTimer(" + time + ")", time);
}
