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

var index_off= new Image(31,31); index_off.src = "images/index.gif";
var index_on = new Image(31,31); index_on.src = "images/index_on.gif";
var next_off = new Image(31,31); next_off.src = "images/next.gif";
var next_on  = new Image(31,31); next_on.src = "images/next_on.gif";
var prev_off = new Image(31,31); prev_off.src = "images/prev.gif";
var prev_on  = new Image(31,31); prev_on.src = "images/prev_on.gif";
var slide_before_start  = new Image(31,31); slide_before_start.src = "images/slide_before_start.gif";
var slide_pause  = new Image(31,31); slide_pause.src = "images/slide_pause.gif";
var show_slide  = new Image(31,31); show_slide.src = "images/show_slide.gif";
var show_slide_on  = new Image(31,31); show_slide_on.src = "images/show_slide_on.gif";
var show_slide_slow  = new Image(31,31); show_slide_slow.src = "images/show_slide_slow.gif";
var show_slide_slow_on  = new Image(31,31); show_slide_slow_on.src = "images/show_slide_slow_on.gif";

var gallery;
var searchService;
var galleryName;
var galleryAlbums;
var albumCovers = new Array();
var albumName;
var albumItems;
var albumPos = 0;
var pos = 0;
var slideShowSpeed=0;
var timer;
var userId;
var SECURITY_TOKEN;
var permissions = new Array();

dojo.addOnLoad(function() {
    dojo.require("dojo._base.xhr");
    dojo.require("dojo.rpc.JsonService");
    dojo.addOnLoad(getJSONAccessList);
    dojo.addOnLoad(populateUserInfo);
    dojo.addOnLoad(initServices);
    dojo.addOnLoad(initGallery);
 });

function getJSONAccessList() {
    dojo.xhrPost({
        sync: true,
        url:"security", //photark.constants.SecurityEndpoint,
        content:{request:"getJSONAccessList"},
        handleAs: "json",
        load: function(response, ioArgs) {
            userId = response.userId;
            SECURITY_TOKEN = response.token;
            permissions = response.defaultPermissions;

        },
        error: function(response, ioArgs) {
            console.error("Error in getting JSON Access List");
        }
    });
}

function initServices(){
  	searchService = new dojo.rpc.JsonService( photark.constants.SearchServiceEndpoint );
    gallery = new dojo.rpc.JsonService( photark.constants.GalleryServiceEndpoint );
}

function initGallery() {
    try {
     //   gallery.getAlbums().addCallback(gallery_getAlbumsResponse); getAlbumsToUser
        gallery.getAlbumsToUser(SECURITY_TOKEN).addCallback(gallery_getAlbumsResponse);
    } catch(exception) {
        alert(exception);
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
function displayLoginLinks  (response) {
    if(response!=null&&response.user.userId!="null"&&response.user.userId!="UnRegisteredUser"){
        var displayName = response.user.userInfo.displayName;

        document.getElementById("loginLinks").innerHTML="Welcome <b>"+displayName+"</b> : <span><a href=\"./admin/upload.html\"><u>Admin page</u></a></span>&nbsp;&nbsp;<span><a href=\"./logout/\"><u>Logout</u></a></span>" ;
    } else {
        document.getElementById("loginLinks").innerHTML="<span><a href=\"./admin/authenticate\"><u>Super admin</u></a></span>&nbsp;&nbsp;<span><a href=\"./home/authenticate\"><u>Login</u></a></span>";
    }


}

function gallery_getAlbumsResponse(albums, exception) {
    if(exception) {
        alert(exception.msg);
        return;
    }
    galleryAlbums = albums;

    for(i=0; i< galleryAlbums.length; i++)
    {
       // gallery.getAlbumCover(galleryAlbums[i].name).addCallback(gallery_getAlbumCoverResponse);
          gallery.getAlbumCoverToUser(galleryAlbums[i].name,SECURITY_TOKEN).addCallback(gallery_getAlbumCoverResponse);
    }
}


function gallery_getAlbumCoverResponse(cover, exception) {
    if(exception){
        alert(exception.msg);
        return;
    }
    albumCovers[pos] = cover;
    pos += 1;
    if(albumCovers.length == galleryAlbums.length)
    {
        initializeGallery();
        displayGallery();
    }
}

function searchResponse(items, exception){

 	if(exception) {
        alert(exception.msg);
        return;
    }

	var table=document.getElementById('tableSearch');
	deleteTableRows(table);
    var lastRow = 0;//table.rows.length;
    for (i = 0; i < items.length; i++) {
        var row = table.insertRow(lastRow++);
        var column = row.insertCell(0);

        var img = document.createElement("img");
        img.src = "";
        //img.class = "slideImage";
        img.alt = items[i];
        var a = document.createElement("a");
        a.href = window.location.href + "gallery/" + items[i];
        a.appendChild(img);
        column.appendChild(a);
        row = table.insertRow(lastRow++);
        column = row.insertCell(0)
        column.innerHTML = "<img src=\"images/space.gif\" class=\"slideImage\" width=\"10\" height=\"10\" ondragstart=\"return false\" onselectstart=\"return false\" oncontextmenu=\"return false\" galleryimg=\"no\" usemap=\"#imagemap\" alt=\"\">";
   }

   displaySearchResults();

}

function deleteTableRows(table) {
	while (table.rows.length > 0) {
		table.deleteRow(0);
	}
}

function initializeGallery() {
    var table=document.getElementById('tableGallery');
    var lastRow = table.rows.length;
    for (i = 0; i < galleryAlbums.length; i++) {
        var row = table.insertRow(lastRow++);
        var column = row.insertCell(0);

        if (albumCovers[i] != null) {

            var albumName = galleryAlbums[i].name;
            var img = document.createElement("img");
            img.src = window.location.href + "gallery/"+ albumName +"/" + albumCovers[i];
            var img_html = "<img src=" + img.src + " class=\"slideImage\" width=180px ondragstart=\"return false\" onselectstart=\"return false\" oncontextmenu=\"return false\" galleryimg=\"no\" usemap=\"#imagemap\" alt=\"\"/>";
            var html = "<a href=\"javascript:initializeAlbum('" + albumName + "')\">" + img_html + "</a>";
            column.innerHTML = html;

            column = row.insertCell(1);
            column.innerHTML = "<div style=\"width:500\">"+galleryAlbums[i].description+"</div>";

            row = table.insertRow(lastRow++);
            column = row.insertCell(0)
            column.innerHTML = albumName;

            row = table.insertRow(lastRow++);
            column = row.insertCell(0)
            column.innerHTML = "<img src=\"images/space.gif\" class=\"slideImage\" width=\"10\" height=\"10\" ondragstart=\"return false\" onselectstart=\"return false\" oncontextmenu=\"return false\" galleryimg=\"no\" usemap=\"#imagemap\" alt=\"\">";
        }
   }
}

function displayGallery() {
    setVisibility('gallery',true);
    setVisibility('album',false);
    setVisibility('search',false);
}

function displaySearchResults() {
    setVisibility('search',true);
    setVisibility('gallery',false);
    setVisibility('album',false);
}

function initializeAlbum(albumName) {
    try {
        this.albumName = albumName;
       // gallery.getAlbumPictures(albumName).addCallback(gallery_getAlbumPicturesResponse);
          gallery.getAlbumPicturesToUser(albumName,SECURITY_TOKEN).addCallback(gallery_getAlbumPicturesResponse);
    } catch(exception) {
        alert(e);
    }
}

function gallery_getAlbumPicturesResponse(items, exception) {
    if(exception) {
        alert(exception.msg);
        displayGallery();
        return;
    }
    albumItems = items;
    albumPos = 0;
    showAlbum();
}

function showAlbum() {
    if(albumItems.length > 0) {
        showImage(albumPos);
    }
    displayAlbum();
}

function displayAlbum() {
    setVisibility('gallery',false);
    setVisibility('album',true);
    setVisibility('search',false);
}

function showImage(albumPos) {
    var img = document.createElement("img");
    img.onload = function(evt) {
        document.getElementById("albumImage").src = this.src;
        document.getElementById("albumImage").width=this.width;
        document.getElementById("albumImage").height=this.height;
    }
    img.src = window.location.href + "gallery/"+ this.albumName +"/" + albumItems[albumPos];
    return false;
}

function goNext() {
    if(albumPos < albumItems.length - 1) {
        albumPos++;
        showImage(albumPos);
    }
}

function goPrevious() {
    if(albumPos > 0) {
        albumPos--;
        showImage(albumPos);
    }
}

function setVisibility(divId, visible) {
    //valid values { visible, hidden }
    if (document.getElementById) {
        var element = document.getElementById(divId)
        if(visible) {
            element.style.display = 'block';
            element.style.visibility = 'visible';
        } else {
            element.style.display = 'none';
            element.style.visibility = 'hidden';
        }
    }
}

function onGoPreviousMouseOver(){
    if(albumPos == 0){
        document.previous.src=prev_off.src;
    }else{
        document.previous.src=prev_on.src;
    }
}

function onGoNextMouseOver(){
    if(albumPos == albumItems.length - 1){
        document.next.src=next_off.src;
    }else{
        document.next.src=next_on.src;
    }
}

function goSlideShow(){
    if(slideShowSpeed==0){
        slideShowSpeed=1;
        clearTimeout(timer);
        startTimer(5000);
    }else if(slideShowSpeed==1) {
        slideShowSpeed=2;
        clearTimeout(timer);
        startTimer(2000);
    }else{
        slideShowSpeed=0;
        clearTimeout(timer);
    }
}

function beforeClick(){
        clearTimeout(timer);
        slideShowSpeed=0;
        document.show.src=slide_before_start.src;
}

function search(){
		var query = document.getElementById("search-input").value;
		searchService.search(query).addCallback(searchResponse);
}

function onSlideShow(){
    if(slideShowSpeed==0){
        document.show.src=show_slide_slow_on.src;
    }else if(slideShowSpeed==1){
        document.show.src=show_slide_on.src;
    }else{
        document.show.src=slide_pause.src;
    }
}

function offSlideShow(){
    if(slideShowSpeed==0){
        document.show.src=slide_before_start.src;
    }else if(slideShowSpeed==1){
        document.show.src=show_slide_slow.src;
    }else{
        document.show.src=show_slide.src;
    }
}

function startTimer(time){
    if(albumPos < albumItems.length - 1) {
        albumPos++;
    }else{
        albumPos=1;
    }
    showImage(albumPos);
    timer=setTimeout("startTimer("+time+")",time);
}
