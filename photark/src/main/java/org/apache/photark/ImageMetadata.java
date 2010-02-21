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

import org.apache.sanselan.formats.tiff.constants.ExifTagConstants;

/**
 * Represent a EXIF Metatada attribute/value from 
 * It implements the ExifTagConstants from Sanselan to leverage it's sxit field contants
 */
public class ImageMetadata implements ExifTagConstants {
    private String key;
    private String value;
    
    public ImageMetadata() {
        
    }
    
    public ImageMetadata(String key, String value) {
        this.key = key;
        this.value = value;
    }
    
    /**
     * Return EXIF Metadata attribute name
     * @return
     */
    public String getKey() {
        return key;
    }
    
    /**
     * Set EXIF Metadata attribute name
     * @param key
     */
    public void setKey(String key) {
        this.key = key;
    }
    
    /**
     * Get EXIF Metadata attribute value
     * @return
     */
    public String getValue() {
        return value;
    }
    
    /**
     * Set EXIF Metadata attribute value
     * @param value
     */
    public void setValue(String value) {
        this.value = value;
    }
}
