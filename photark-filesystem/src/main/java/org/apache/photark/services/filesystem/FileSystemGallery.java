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

package org.apache.photark.services.filesystem;

import java.io.File;
import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.ws.rs.PathParam;

import org.apache.photark.Album;
import org.apache.photark.AlbumList;
import org.apache.photark.AlbumRef;
import org.apache.photark.Image;
import org.apache.photark.services.GalleryService;
import org.oasisopen.sca.annotation.Init;
import org.oasisopen.sca.annotation.Property;

/**
 * File system based gallery
 */
public class FileSystemGallery implements GalleryService {    
    private String galleryRoot; 
    private Map<String, Album> albums = new HashMap<String, Album>();


    public FileSystemGallery(@Property(name="galleryRoot") String galleryRoot) {
        this.galleryRoot = galleryRoot;
    }
    
    @Init
    public void init() {
        try {
            
            java.net.URL galleryURL = this.getClass().getClassLoader().getResource(galleryRoot);
            if(galleryURL == null) {
                // Accomodate for J2EE classpath that starts in WEB-INF\classes
                galleryURL = this.getClass().getClassLoader().getResource("../../" + galleryRoot);
            }
            if(galleryURL == null) {
                // Workaroud for Google apps Engine
                String galleryDir = System.getProperty("user.dir") + "/"  + galleryRoot;
                galleryURL = new java.net.URL("file://" + galleryDir);
            }

            
            if(galleryRoot != null) {
                java.io.File galleryDirectory = new java.io.File(galleryURL.toURI());
                if (galleryDirectory.isDirectory() && galleryDirectory.exists()) {
                    java.io.File[] albumDirectoryList = galleryDirectory.listFiles();
                    for(java.io.File albumDirectory : albumDirectoryList) {
                        if(! albumDirectory.getName().startsWith(".")) {
                            if(albumDirectory.isDirectory() && albumDirectory.exists()) {
                                Album album = createAlbum(albumDirectory);
                                albums.put(album.getName(), album);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            // FIXME: ignore for now
            e.printStackTrace();
        }
    }


    public AlbumList getAlbums() {
        AlbumList albumList = new AlbumList();

        Iterator<Entry<String, Album>> albumIterator = albums.entrySet().iterator();  
        while(albumIterator.hasNext()) {
            Album album = (Album) albumIterator.next().getValue();
            albumList.getAlbums().add(AlbumRef.createAlbumRef(album));
        }

        return albumList;
    }

    public Album getAlbum(@PathParam("id") String albumId) {
        if(albumId == null || albumId.isEmpty()) {
            throw new InvalidParameterException("Invalid/Empty album id");
        }

        if(! albums.containsKey(albumId)) {
            throw new InvalidParameterException("Album '" + albumId + "' not found");
        }

        return albums.get(albumId);
    }

    public void addAlbum(Album newAlbum) {
        if(newAlbum.getName() == null || newAlbum.getName().isEmpty()) {
            throw new InvalidParameterException("Album has no name");
        }
        this.albums.put(newAlbum.getName(), newAlbum);
    }

    public void updateAlbum(Album album) {
        if(album.getName() == null || album.getName().isEmpty()) {
            throw new InvalidParameterException("Album has no name");
        }

        if(! albums.containsKey(album.getName())) {
            throw new InvalidParameterException("Album '" + album.getName() + "' not found");
        }

        albums.put(album.getName(), album);
    }

    public void removeAlbum(String albumId) {
        if(albumId == null || albumId.isEmpty()) {
            throw new InvalidParameterException("Invalid/Empty album id");
        }

        if(! albums.containsKey(albumId)) {
            throw new InvalidParameterException("Album '" + albumId + "' not found");
        }

        albums.remove(albumId);
    }
    
    /**
     * Process a Album folder and return a Album model 
     * @return the album model representing the album folder
     */
    private Album createAlbum(File albumDirectory) {

        Album album = new Album();
        album.setName(albumDirectory.getName());

        String[] listPictures = albumDirectory.list(new FileSystemImageFilter(".jpg"));
        for(String image : listPictures) {
            String imageLocation = "http://localhost:8085/gallery/" + album.getName() + "/" + image;

            Image albumImage = new Image();
            albumImage.setLocation(imageLocation);

            album.getImages().add(albumImage);
        }

        return album;   
    }
}
