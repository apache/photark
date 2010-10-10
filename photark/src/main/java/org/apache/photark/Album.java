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

/**
 * Model representing a gallery album
 *
 * @version $Rev$ $Date$
 */
public class Album implements Serializable {

    private static final long serialVersionUID = -6183599220015859753L;

    private String name;
    private String description;
    private String location;
    //private Date dateCreated;
    //private Date dateUpdated;
    //private int size;

    private List<Image> images = new ArrayList<Image>();

    /**
     * Default constructor
     */
    public Album() {

    }

    /**
     * Get album name
     * @return album name
     */
    public String getName() {
        return name;
    }

    /**
     * Set album name
     * @param name album name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get album description
     * @return album description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set album description
     * @param description album description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Get album location URL
     * @return the album location URL
     */
    public String getLocation() {
        return this.location;
    }

    /**
     * Set album location URL
     * @param location album location url
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * List of album images
     * @return the list of album images
     */
    public List<Image> getImages() {
        return images;
    }
}
