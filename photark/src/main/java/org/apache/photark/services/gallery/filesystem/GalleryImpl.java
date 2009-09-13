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

package org.apache.photark.services.gallery.filesystem;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.photark.services.album.Album;
import org.apache.photark.services.gallery.Gallery;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Property;

public class GalleryImpl implements Gallery {
    private String name;
    private String location;

    private boolean initialized;
    private List<Album> albums = new ArrayList<Album>();
    
    public GalleryImpl() {
        
    }
    
    public GalleryImpl(String name) {
        this.name = name;
    }
    
    @Init
    public void init() {
        try {
            URL galleryURL = this.getClass().getClassLoader().getResource(name);
            if(galleryURL == null) {
                // Accomodate for J2EE classpath that starts in WEB-INF\classes
                galleryURL = this.getClass().getClassLoader().getResource("../../" + name);
            }

            if(galleryURL != null) {
                File album = new File(galleryURL.toURI());
                if (album.isDirectory() && album.exists()) {
                    File[] albums = album.listFiles();
                    for(File albumFile : albums) {
                        if(! albumFile.getName().startsWith(".")) {
                            if(albumFile.isDirectory() && albumFile.exists()) {
                                Album newAlbum = new org.apache.photark.services.album.filesystem.AlbumImpl();
                                newAlbum.setName(albumFile.getName());
                                newAlbum.setLocation(albumFile.getPath());
                                this.albums.add(newAlbum);
                            }                                
                        }
                    }
                }
            }
        } catch (Exception e) {
            // FIXME: ignore for now
            e.printStackTrace();
        }
        
        initialized = true;
    }
    
    public String getName() {
        return name;
    }
    
    @Property
    public void setName(String name) {
        this.name = name;
    }

    public void addAlbum(Album album) {
        
    }

    public Album[] getAlbums() {
        if(! initialized) {
            init();
        }
        
        Album[] albumArray = new Album[albums.size()];
        albums.toArray(albumArray);
        return albumArray;
    }
    
    public String getAlbumCover(String albumName) {
        Album albumLookup = getAlbum(albumName);
        
        if (albumLookup != null) {
            return albumLookup.getPictures()[0];
        } else {
            //FIXME: return proper not found exception
            return null;             
        }
    }
    
    public String[] getAlbumPictures(String albumName) {
        Album albumLookup = getAlbum(albumName);
        
        if (albumLookup != null) {
            return albumLookup.getPictures();
        } else {
            //FIXME: return proper not found exception
            return new String[]{};             
        }
    }
    
    private Album getAlbum(String albumName) {
        Album albumLookup = null;
        for(Album album : albums) {
            if(album.getName().equalsIgnoreCase(albumName)) {
                albumLookup = album;
                break;
            }
        }
        
        return albumLookup;
    }

    private String getLocation() {
        return location;
    }
}
