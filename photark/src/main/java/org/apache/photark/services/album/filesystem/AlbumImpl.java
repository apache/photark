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

package org.apache.photark.services.album.filesystem;

import java.io.File;
import java.io.FilenameFilter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.photark.services.album.Album;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Property;


public class AlbumImpl implements Album {
    private String gallery;
    private String name;
    private String location;
    private List<String> pictures = new ArrayList<String>();

    @Init
    public void init() {
        try {
            URL albumURL = this.getClass().getClassLoader().getResource(getLocation());
            if(albumURL == null) {
                // Accomodate for J2EE classpath that starts in WEB-INF\classes
                albumURL = this.getClass().getClassLoader().getResource("../../" + getLocation());
            }

            if(albumURL != null) {
                File album = new File(albumURL.toURI());
                if (album.isDirectory() && album.exists()) {
                    String[] listPictures = album.list(new ImageFilter(".jpg"));
                    for(String image : listPictures) {
                        image = getLocation() + image;
                        pictures.add(image);
                    }
                }
            }
        } catch (Exception e) {
            // FIXME: ignore for now
            e.printStackTrace();
        }
    }

    @Property
    public void setGallery(String gallery) {
        this.gallery = gallery;
        this.location = null;
    }
    
    public String getName() {
        return name;
    }
    
    @Property
    public void setName(String name) {
        this.name = name;
        this.location = null;
    }

    
    public String[] getPictures() {
        String[] pictureArray = new String[pictures.size()];
        pictures.toArray(pictureArray);
        return pictureArray;
    }

    public String getLocation() {
        if (location == null) {
            location = gallery + "/" + name + "/";
        }
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    /**
     * Inner fileFilter class
     */
    private class ImageFilter implements FilenameFilter {
        String afn;
        ImageFilter(String afn) { this.afn = afn; }
        public boolean accept(File dir, String name) {
          // Strip path information:
          String f = new File(name).getName();
          return f.indexOf(afn) != -1;
        }
      }

}
