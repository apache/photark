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
import java.net.URL;
import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.photark.Album;
import org.apache.photark.AlbumList;
import org.apache.photark.AlbumRef;
import org.apache.photark.Image;
import org.apache.photark.services.GalleryService;
import org.apache.photark.services.PhotarkRuntimeException;
import org.oasisopen.sca.annotation.Init;
import org.oasisopen.sca.annotation.Property;
import org.oasisopen.sca.annotation.Scope;

/**
 * File system based gallery
 *
 * @version $Rev$ $Date$
 */
@Scope("COMPOSITE")
public class FileSystemGallery implements GalleryService {
    private static final Logger logger = Logger.getLogger(FileSystemGallery.class.getName());

    private String galleryLocation;
    private String galleryRoot;
    private File   galleryRootDirectory;

    private Map<String, Album> albums = new HashMap<String, Album>();


    public FileSystemGallery(@Property(name="galleryRoot") String galleryRoot) {
        this.galleryRoot = galleryRoot;
    }

    @Init
    public void init() {
        try {
            if(logger.isLoggable(Level.FINE)) {
                logger.fine("Initializing FileSystem Gallery");
            }

            URL galleryRootURL = this.getClass().getClassLoader().getResource(galleryRoot);
            if(galleryRootURL == null) {
                // Accomodate for J2EE classpath that starts in WEB-INF\classes
                galleryRootURL = this.getClass().getClassLoader().getResource("../../" + galleryRoot);
            }
            if(galleryRootURL == null) {
                // Workaroud for Google apps Engine
                String galleryDir = System.getProperty("user.dir") + "/"  + galleryRoot;
                galleryRootURL = new java.net.URL("file://" + galleryDir);
            }

            if(logger.isLoggable(Level.FINE)) {
                logger.fine("FileSystem Gallery root " + galleryRootURL);
            }


            if(galleryRootURL != null) {
                galleryRootDirectory = new java.io.File(galleryRootURL.toURI());
                if (galleryRootDirectory.isDirectory() && galleryRootDirectory.exists()) {
                    java.io.File[] albumDirectoryList = galleryRootDirectory.listFiles();
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

            if(logger.isLoggable(Level.FINE)) {
                logger.fine("Found '" + albums.size() + "' albums in the repository");
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error initializing FileSystem gallery: " + e.getMessage(), e);
        }
    }

    public String getLocation() {
        return this.galleryLocation;
    }

    @Property(name="galleryURL", required=false)
    public void setLocation(String location) {
        this.galleryLocation = location;
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

    public Album getAlbum(String albumId) {
        if(albumId == null || albumId.isEmpty()) {
            throw new InvalidParameterException("Invalid/Empty album id");
        }

        if(! albums.containsKey(albumId)) {
            throw new InvalidParameterException("Album '" + albumId + "' not found");
        }

        return albums.get(albumId);
    }

    public void addAlbum(Album newAlbum) throws PhotarkRuntimeException {
        if(newAlbum.getName() == null || newAlbum.getName().isEmpty()) {
            throw new InvalidParameterException("Album has no name");
        }

        if (galleryRootDirectory != null) {
           File newAlbumDirectory = new File(galleryRootDirectory.getPath() + File.separator + newAlbum.getName());
           if ( ! newAlbumDirectory.mkdir()) {
               if(logger.isLoggable(Level.SEVERE)) {
                   logger.log(Level.SEVERE, "Error creating new album directory '" + newAlbumDirectory.getPath() + "'" );
               }
               throw new PhotarkRuntimeException("Error creating new album directory '" + newAlbumDirectory.getPath() + "'" );
           }
        }
        this.albums.put(newAlbum.getName(), newAlbum);
    }

    public void updateAlbum(String albumId, Album album)  throws PhotarkRuntimeException {
        if(album.getName() == null || album.getName().isEmpty()) {
            throw new InvalidParameterException("Album has no name");
        }

        if(! albums.containsKey(album.getName())) {
            if(logger.isLoggable(Level.SEVERE)) {
                logger.log(Level.SEVERE, "Album '" + album.getName() + "' not found");
            }

            throw new InvalidParameterException("Album '" + album.getName() + "' not found");
        }

        albums.put(album.getName(), album);
    }

    public void removeAlbum(String albumName)  throws PhotarkRuntimeException {
        if(albumName == null || albumName.isEmpty()) {
            throw new InvalidParameterException("Invalid/Empty album id");
        }

        if(! albums.containsKey(albumName)) {
            throw new InvalidParameterException("Album '" + albumName + "' not found");
        }

        if (galleryRootDirectory != null) {
            File newAlbumDirectory = new File(galleryRootDirectory.getPath() + File.separator + albumName);
            if ( ! newAlbumDirectory.delete()) {
                throw new PhotarkRuntimeException("Error removing album directory '" + albumName + "'" );
            }
         }

        albums.remove(albumName);
    }

    /**
     * Process a Album folder and return a Album model
     * @return the album model representing the album folder
     */
    private Album createAlbum(File albumDirectory) {

        Album album = new Album();
        album.setName(albumDirectory.getName());
        album.setLocation("http://localhost:8085/gallery/" + album.getName() );

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
