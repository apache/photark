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
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Model representing an album image
 *
 * @version $Rev$ $Date$
 */
public class Image implements Serializable {

    private static final long serialVersionUID = -2607872190168102433L;

    private String id;
    private String name;
    private String title;
    private Date datePosted;

    private String url;
    private String urlThumb;

    private List<ImageMetadata> imageAttributes = new ArrayList<ImageMetadata>();
    
    private Set<String> tags = new HashSet<String>();

    /**
     * Constructor
     *
     * @param imageFile a File representing the image
     * @param datePosted Date when image is being added
     */
    public Image() {

    }

    /**
     * Constructor
     *
     * @param name Image name
     * @param datePosted Date when image is being added
     * @param imageStream Image stream content
     */
    public Image(String id, String name, String title, Date datePosted) {
        this.id = id;
        this.name = name;
        this.title = title;
        this.datePosted = datePosted;
    }
    
    public Set<String> getTags() {
    	return this.tags;
    }

    /**
     * Get image Id
     *
     * @return image id
     */
    public String getId() {
        return this.id;
    }

    /**
     * Set image id
     *
     * @param id image id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Get image file name
     *
     * @return image file name
     */
    public String getName() {
        return name;
    }

    /**
     * Set image file name
     *
     * @param name image file name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get image title
     *
     * @return image title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Set image title
     *
     * @param title image title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Get date when image was posted
     *
     * @return date posted
     */
    public Date getDatePosted() {
        return datePosted;
    }

    /**
     * Get Image URL
     *
     * @return the image URL
     */
    public String getLocation() {
        return url;
    }

    /**
     * Set Image URL
     *
     * @param url Image URL
     */
    public void setLocation(String location) {
        this.url = location;
    }

    /**
     * Get URL for the thumbnail version of the image
     *
     * @return URL for the thumbnail
     */
    public String getThumbnailLocation() {
        return urlThumb;
    }

    /**
     * Set URL for the thumbnail version of the image
     *
     * @param urlThumb URL for the thumbnail
     */
    public void setThumbnailLocation(String thumbnailLocation) {
        this.urlThumb = thumbnailLocation;
    }

    /**
     * Return image metadata retrieved from EXIF properties
     *
     * @return list of image metadata attributes
     */
    public List<ImageMetadata> getImageMetadata() {
        return imageAttributes;
    }

}
