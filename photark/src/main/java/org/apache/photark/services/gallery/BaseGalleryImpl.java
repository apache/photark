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

package org.apache.photark.services.gallery;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.apache.photark.Image;
import org.apache.photark.services.album.Album;
import org.oasisopen.sca.annotation.AllowsPassByReference;
import org.oasisopen.sca.annotation.Property;
import org.oasisopen.sca.annotation.Reference;

public abstract class BaseGalleryImpl {
    private static final Logger logger = Logger.getLogger(BaseGalleryImpl.class.getName());

    @Reference(required = false)
    public GalleryListener listeners;

    protected String name;
    private String location;
    protected boolean initialized;
    protected List<Album> albums = new ArrayList<Album>();

    public BaseGalleryImpl() {

    }

    public BaseGalleryImpl(String name) {
        this.name = name;
    }

    public abstract void init();

    public String getName() {
        return name;
    }

    @Property
    public void setName(String name) {
        this.name = name;
    }

    // public void addAlbum(Album album) {
    //
    // }

    public Album[] getAlbums() {
        if (!initialized) {
            init();
        }

        Album[] albumArray = new Album[albums.size()];
        albums.toArray(albumArray);
        return albumArray;
    }

    public String getAlbumCover(String albumName) {
        Album albumLookup = getAlbum(albumName);

        if (albumLookup != null) {
            String[] pictures = albumLookup.getPictures();
            // this check is to avoid Exception
            if (pictures.length > 0) {
                return albumLookup.getPictures()[0];
            } else {
                logger.info("No Album Cover Picture found for album:" + albumName);
                return null;
            }
        } else {
            // FIXME: return proper not found exception
            return null;
        }
    }

    public String[] getAlbumPictures(String albumName) {
        Album albumLookup = getAlbum(albumName);

        if (albumLookup != null) {
            return albumLookup.getPictures();
        } else {
            // FIXME: return proper not found exception
            return new String[] {};
        }
    }

    protected Album getAlbum(String albumName) {
        Album albumLookup = null;
        for (Album album : albums) {
            if (album.getName().equalsIgnoreCase(albumName)) {
                albumLookup = album;
                break;
            }
        }

        return albumLookup;
    }

    private String getLocation() {
        return location;
    }
    
    @AllowsPassByReference
    public void imageAdded(String albumName, Image image) {
        
        if (this.listeners != null) {
            
//            for (GalleryListener listener : listeners) {
//                listener.imageAdded(image);
//            }
            listeners.imageAdded(albumName, image);
            
        }
        
    }
    
    @AllowsPassByReference
    public void imageRemoved(String albumName, Image image) {
        
        if (this.listeners != null) {
            
//            for (GalleryListener listener : listeners) {
//                listener.imageRemoved(image);
//            }
            listeners.imageRemoved(albumName, image);
            
        }
        
    }
    public Album[] getAlbumsToUser(String securityToken){
         return getAlbums();
    }

   public String getAlbumCoverToUser(String albumName, String securityToken){
        return getAlbumCover(albumName);
    }

   public String[] getAlbumPicturesToUser(String albumName, String securityToken) {
       return getAlbumPictures(albumName);
   }
}
