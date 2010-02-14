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

import org.apache.photark.services.album.Album;
import org.oasisopen.sca.annotation.Property;

public abstract class AbsGalleryImpl implements Gallery {

    protected String name;
    private String location;
    protected boolean initialized;
    protected List<Album> albums = new ArrayList<Album>();

    public AbsGalleryImpl() {

    }

    public AbsGalleryImpl(String name) {
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

    public void addAlbum(Album album) {

    }

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
                System.out.println("No Album Cover Picture found for album:" + albumName);
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

    private Album getAlbum(String albumName) {
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
}
