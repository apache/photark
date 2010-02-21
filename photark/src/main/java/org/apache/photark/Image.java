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

import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * Model representing an album image
 */
public class Image {
    private String name;
    private Date datePosted;
    private InputStream imageStream;

    private List<Properties> imageProperties;

    /**
     * Constructor
     * @param name Image name
     * @param datePosted Date when image is being added
     */
    public Image(String name, Date datePosted) {
        this.name = name;
        this.datePosted = datePosted;
    }

    /**
     * Constructor
     * @param name Image name
     * @param datePosted Date when image is being added
     * @param imageStream Image stream content
     */
    public Image(String name, Date datePosted, InputStream imageStream) {
        this(name, datePosted);
        this.imageStream = imageStream;
    }

    /**
     * Get image file name
     * @return image file name
     */
    public String getName() {
        return name;
    }

    /**
     * Get date when image was posted
     * @return date posted
     */
    public Date getDatePosted() {
        return datePosted;
    }

    /**
     * Return image content as stream
     * @return image stream
     */
    public InputStream getImageAsStream() {
        return imageStream;
    }

}
