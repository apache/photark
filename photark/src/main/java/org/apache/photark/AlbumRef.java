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

package org.apache.photark;

/**
 * A Album Rerefence model object
 * Useful to provide summary set of album 
 * information over the wire
 * 
 * @version $Rev$ $Date$
 */
public class AlbumRef {
    private String albumName;
    private String coverImageRef;
    private String albumRef;

    /**
     * Default constructor
     */
    public AlbumRef() {

    }

    /**
     * Get Album name
     * @return the album name
     */
    public String getName() {
        return albumName;
    }

    /**
     * Set Album name
     * @param name the album name
     */
    public void setName(String name) {
        this.albumName = name;
    }

    /**
     * Get cover image reference
     * @return the cover image url reference
     */
    public String getCoverImageRef() {
        return coverImageRef;
    }

    /**
     * Set cover image reference
     * @param coverImageRef the cover image url reference
     */
    public void setCoverImageRef(String coverImageRef) {
        this.coverImageRef = coverImageRef;
    }

    /**
     * Get album ref
     * @return album ref
     */
    public String getRef() {
        return albumRef;
    }

    /**
     * Set album ref
     * @param albumRef album ref
     */
    public void setRef(String albumRef) {
        this.albumRef = albumRef;
    }

    /**
     * Utility method to create a AlbumRef from a Album
     * @param album
     * @return
     */
    public static AlbumRef createAlbumRef(Album album) {
        AlbumRef albumRef = new AlbumRef();

        albumRef.setName(album.getName());
        if(! album.getImages().isEmpty()) {
            albumRef.setCoverImageRef(album.getImages().get(0).getLocation());
        }
        albumRef.setRef(album.getLocation());

        return albumRef;
    }
}
