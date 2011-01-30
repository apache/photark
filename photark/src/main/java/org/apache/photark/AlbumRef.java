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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

/**
 * A Album Reference model object
 * Useful to provide summary set of album
 * information over the wire
 *
 * @version $Rev$ $Date$
 */
public class AlbumRef implements Serializable {

    private static final long serialVersionUID = 5346908464708654247L;

    private String albumName;
    private List<Link> links = new ArrayList<Link>();

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
    @XmlElement(name="link", namespace="http://www.w3.org/2005/Atom")
    public List<Link> getLinks() {
        return this.links;
    }


    @Override
    public String toString() {
        return "AlbumRef [albumName=" + albumName + ", links=" + links + "]";
    }

    /**
     * Utility method to create a AlbumRef from a Album
     * @param album
     * @return
     */
    public static AlbumRef createAlbumRef(Album album) {
        AlbumRef albumRef = new AlbumRef();

        albumRef.setName(album.getName());
        albumRef.getLinks().add(Link.to("album", album.getLocation()));
        if(! album.getImages().isEmpty()) {
            albumRef.getLinks().add(Link.to("cover", album.getImages().get(0).getLocation()));
        }


        return albumRef;
    }

}
