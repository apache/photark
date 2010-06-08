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
 * Summary info for Images
 * @version $Rev$ $Date$
 */
public class ImageRef {
    private String name;
    private String title;

    private String url;
    private String urlThumb;

    private String imageRef;

    /**
     * Get image name
     * @return image name
     */
    public String getName() {
        return name;
    }

    /**
     * Set image name
     * @param name image name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get image title
     * @return image title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Set image title
     * @param title image title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Get image location
     * @return image location
     */
    public String getLocation() {
        return url;
    }

    /**
     * Set image location
     * @param location image location
     */
    public void setLocation(String location) {
        this.url = location;
    }

    /**
     * Get thumbnail image location
     * @return thumbnail image location
     */
    public String getThumbnailLocation() {
        return urlThumb;
    }

    /**
     * Set thumbnail image location
     * @param thumbnailLocation thumbnail image location
     */
    public void setThumbnailLocation(String thumbnailLocation) {
        this.urlThumb = thumbnailLocation;
    }

    /**
     * Get image reference
     * @return image reference
     */
    public String getImageRef() {
        return imageRef;
    }

    /**
     * Set image reference
     * @param imageRef image reference
     */
    public void setImageRef(String imageRef) {
        this.imageRef = imageRef;
    }
}
