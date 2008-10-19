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

package services;

import java.util.ArrayList;
import java.util.List;

import org.apache.abdera.model.Entry;
import org.apache.tuscany.sca.binding.atom.collection.Collection;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Reference;

public class AlbumAgregator implements Album {
    private List<String> pictures = new ArrayList<String>();
    
    @Reference(required=false)
    protected Album album;
    
    @Reference(required=false)
    protected Collection albumFeed;

    @Init
    public void init() {
        if(album != null) {
            for(String picture : album.getPictures()) {
                pictures.add(picture);
            }            
        }
        
        List<Entry> entries = new ArrayList<Entry>();
        if (albumFeed != null) {
            for(Entry feedPicture : albumFeed.getFeed().getEntries()) {
                String feedImageLink = feedPicture.getEnclosureLinkResolvedHref().toString();
                pictures.add(feedImageLink);
            }
        }
    }
    public String[] getPictures() {
        String[] pictureArray = new String[pictures.size()];
        pictures.toArray(pictureArray);
        return pictureArray;
    }

}
