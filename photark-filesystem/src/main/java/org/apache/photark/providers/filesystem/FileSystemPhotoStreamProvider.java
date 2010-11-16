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

package org.apache.photark.providers.filesystem;

import java.io.File;

import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.photark.Image;
import org.apache.photark.providers.PhotoStreamProvider;
import org.apache.photark.services.PhotarkRuntimeException;
import org.apache.photark.subscription.SubscriptionConfig;
import org.apache.photark.subscription.SubscriptionConfig;
import org.oasisopen.sca.annotation.Property;

public class FileSystemPhotoStreamProvider implements PhotoStreamProvider {
    private static final String PROVIDER_ID = "urn:album:file";

    private static final Logger logger = Logger.getLogger(FileSystemPhotoStreamProvider.class.getName());

    private String galleryRoot;

    public FileSystemPhotoStreamProvider(@Property(name="galleryRoot") String galleryRoot) {
        this.galleryRoot = galleryRoot;
    }

    public String getProviderType() {
        return PROVIDER_ID;
    }

    public List<Image> getImages(SubscriptionConfig config) throws PhotarkRuntimeException {
        List<Image> images = new ArrayList<Image>();

        File albumFolder = null;
        try {
            albumFolder = getAlbumFolder(config);
        } catch (IOException e) {
            if(logger.isLoggable(Level.WARNING)) {
                logger.log(Level.WARNING, "Error retrieving photo stream from '" + config.getUrl() + "' :" +  e.getMessage(), e);
            }
            throw new PhotarkRuntimeException("Error retrieving photo stream from '" + config.getUrl() + "' :" +  e.getMessage(), e);
        }

        String[] listPictures = albumFolder.list(new ImageFilter(".jpg"));
        for(String pictureName : listPictures) {
            Image image = new Image();
            image.setId(pictureName);
            image.setName(pictureName);
            image.setLocation("http://localhost:8085/gallery/" + config.getName() + "/" + pictureName);

            images.add(image);
        }

        return images;
    }

    public Image getImage(SubscriptionConfig config, String Id) throws PhotarkRuntimeException {
        Image image = null;

        File albumFolder = null;
        try {
            albumFolder = getAlbumFolder(config);
        } catch (IOException e) {
            if(logger.isLoggable(Level.WARNING)) {
                logger.log(Level.WARNING, "Error retrieving photo stream from '" + config.getUrl() + "' :" +  e.getMessage(), e);
            }
            throw new PhotarkRuntimeException("Error retrieving photo stream from '" + config.getUrl() + "' :" +  e.getMessage(), e);
        }

        String[] listPictures = albumFolder.list(new ImageFilter(".jpg"));
        for(String pictureName : listPictures) {
            if(pictureName.equalsIgnoreCase(Id)) {
                image = new Image();
                image.setId(pictureName);
                image.setName(pictureName);
                image.setLocation("http://localhost:8085/gallery/" + config.getName() + "/" + pictureName);
            }
        }

        if(image == null) {
            if(logger.isLoggable(Level.WARNING)) {
                logger.log(Level.WARNING, "Error retrieving photo stream from '" + config.getUrl() + "'");
            }
            throw new PhotarkRuntimeException("Error retrieving photo stream from '" + config.getUrl() + "'");
        }

        return image;
    }

    public String addImage(SubscriptionConfig album, Image image) throws PhotarkRuntimeException {
        throw new java.lang.UnsupportedOperationException("Operation not supported in album subscriptions");
    }

    public void updateImage(SubscriptionConfig album, Image image)  throws PhotarkRuntimeException {
        throw new java.lang.UnsupportedOperationException("Operation not supported in album subscriptions");
    }

    public void deleteImage(SubscriptionConfig album, String Id) throws PhotarkRuntimeException {
        throw new java.lang.UnsupportedOperationException("Operation not supported in album subscriptions");
    }

    private File getAlbumFolder(SubscriptionConfig config) throws IOException {

        String albumFolderName = galleryRoot + File.pathSeparator + config.getName();
        try {
            URL albumFolderURL = this.getClass().getClassLoader().getResource(config.getName());
            if(albumFolderURL == null) {
                // Accomodate for J2EE classpath that starts in WEB-INF\classes
                albumFolderURL = this.getClass().getClassLoader().getResource("../../" + albumFolderName);
            }
            if(albumFolderURL == null) {
                // Workaroud for Google apps Engine
                String albumDir = System.getProperty("user.dir") + "/"  + albumFolderName;
                albumFolderURL = new java.net.URL("file://" + albumDir);
            }

            if(albumFolderURL != null) {
                File albumFolder = new File(albumFolderURL.toURI());
                if (albumFolder.isDirectory() && albumFolder.exists()) {
                    java.io.File[] albums = albumFolder.listFiles();
                    for(File albumFile : albums) {
                        if(! albumFile.getName().startsWith(".")) {
                            if(albumFile.isDirectory() && albumFile.exists()) {
                                return albumFolder;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new IOException("Error retrieving photo stream from '" + config.getUrl() + "' :" +  e.getMessage(), e);
        }

        return null;
    }


    class ImageFilter implements FilenameFilter {
        String afn;
        public ImageFilter(String afn) { this.afn = afn; }
        public boolean accept(File dir, String name) {
            // Strip path information:
            String f = new File(name).getName();
            return f.indexOf(afn) != -1;
        }
    }

}
