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

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.apache.photark.util.ImageMetadataScanner;

/**
 * Model representing an album image
 */
public class Image implements Serializable {
    
    private static final long serialVersionUID = 2681872730283844321L;
    
    private String imageName;
    private Date datePosted;
    private transient InputStream imageStream;

    private List<ImageMetadata> imageAttributes;
    
    public Image() {
        
    }

    /**
     * Constructor
     * @param imageFile a File representing the image
     * @param datePosted Date when image is being added
     */
    public Image(File imageFile, Date datePosted) {
        this.imageName = imageFile.getName();
        this.datePosted = datePosted;
        try {
            this.imageStream = new FileInputStream(imageFile);
        } catch(Exception fnf) {
            fnf.printStackTrace();
        }
    }
    
    /**
     * Constructor
     * @param name Image name
     * @param datePosted Date when image is being added
     * @param imageStream Image stream content
     */
    public Image(String name, Date datePosted, InputStream imageStream) {
        this.imageName = name;
        this.datePosted = datePosted;
        this.imageStream = imageStream;
    }

    /**
     * Get image file name
     * @return image file name
     */
    public String getName() {
        return imageName;
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
    
    /**
     * Return image metadata retrieved from EXIF properties
     * @return list of image metadata attributes
     */
    public List<ImageMetadata> getImageMetadata() {
        if(imageAttributes == null) {
            imageAttributes = ImageMetadataScanner.scanImageMetadata(imageName, imageStream);
        }
        return imageAttributes;
    }

}
