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

package org.apache.photark.album.flickr;

import org.apache.photark.Image;


public class FlickrAlbumImpl implements org.apache.photark.services.album.Album {

    public void addPicture(Image picture) {
        throw new UnsupportedOperationException("Can't upload image to Album subscription");
    }

    public void deletePicture(Image picture) {
        throw new UnsupportedOperationException("Can't upload image to Album subscription");
    }

    public void deletePicture(String picture) {
        throw new UnsupportedOperationException("Can't upload image to Album subscription");
    }
    
    public void addOwner(String owner){
    	
    }

    public String[] getOwners(){
    	 throw new UnsupportedOperationException("Can't upload image to Album subscription");
    }
    

    public String getDescription() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getLocation() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getName() {
        // TODO Auto-generated method stub
        return null;
    }

    public String[] getPictures() {
        // TODO Auto-generated method stub
        return null;
    }

    public void setDescription(String description) {
        // TODO Auto-generated method stub
        
    }

    public void setLocation(String location) {
        // TODO Auto-generated method stub
        
    }

    public void setName(String name) {
        // TODO Auto-generated method stub
        
    }

}
