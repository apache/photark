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

package org.apache.photark.filesystem.services;

import org.apache.photark.services.album.Album;
import org.apache.photark.services.gallery.BaseGalleryImpl;
import org.apache.photark.services.gallery.Gallery;

public class FileSystemGalleryImpl extends BaseGalleryImpl implements Gallery {
    private static final java.util.logging.Logger logger =
        java.util.logging.Logger.getLogger(FileSystemGalleryImpl.class.getName());

    public FileSystemGalleryImpl() {

    }

    public FileSystemGalleryImpl(String name) {
        super(name);
    }


    @org.oasisopen.sca.annotation.Init
    public void init() {
        logger.info("Initializing fileSystem Gallery");
        try {
            java.net.URL galleryURL = this.getClass().getClassLoader().getResource(name);
            if(galleryURL == null) {
                // Accomodate for J2EE classpath that starts in WEB-INF\classes
                galleryURL = this.getClass().getClassLoader().getResource("../../" + name);
            }
            if(galleryURL == null) {
                // Workaroud for Google apps Engine
                String galleryDir = System.getProperty("user.dir") + "/"  + name;
                galleryURL = new java.net.URL("file://" + galleryDir);
            }

            if(galleryURL != null) {
                java.io.File album = new java.io.File(galleryURL.toURI());
                if (album.isDirectory() && album.exists()) {
                    java.io.File[] albums = album.listFiles();
                    for(java.io.File albumFile : albums) {
                        if(! albumFile.getName().startsWith(".")) {
                            if(albumFile.isDirectory() && albumFile.exists()) {
                                org.apache.photark.services.album.Album newAlbum = new FileSystemAlbumImpl();
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

    public void addAlbum(String albumName) {

    }

    public boolean hasAlbum(String albumName) {
        return false;
    }

    public void deleteAlbum(String albumName) {

	}

    public Album[] getAlbumsToSetPermission(String securityToken) {
        return new Album[0];
    }
}