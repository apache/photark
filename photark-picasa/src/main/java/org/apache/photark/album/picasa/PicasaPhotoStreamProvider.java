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

package org.apache.photark.album.picasa;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.photark.AlbumConfig;
import org.apache.photark.Image;
import org.apache.photark.providers.PhotoStreamProvider;
import org.apache.photark.services.PhotarkRuntimeException;

import com.google.gdata.client.photos.PicasawebService;
import com.google.gdata.data.photos.AlbumFeed;
import com.google.gdata.data.photos.PhotoEntry;
import com.google.gdata.util.AuthenticationException;

public class PicasaPhotoStreamProvider implements PhotoStreamProvider {
    private static final String PROVIDER_ID = "urn::picasa";

    private static final Logger logger = Logger.getLogger(PicasaPhotoStreamProvider.class.getName());

    private static final String APPLICATION_NAME = "photark";
    //private static final String PICASA_SERVICE_TYPE = "lh2";

    public PicasaPhotoStreamProvider() {

    }

    public String getProviderType() {
        return PROVIDER_ID;
    }

    public List<Image> getImages(AlbumConfig album) throws PhotarkRuntimeException {
        PicasawebService picasaService = null;
        List<Image> images = new ArrayList<Image>();

        try {
            picasaService = createPicasaService(album);
        } catch(Exception ae) {
            if(logger.isLoggable(Level.FINE)) {
                logger.log(Level.FINE, "Error initializing service: " + ae.getMessage(), ae);
            }
            throw new PhotarkRuntimeException("Error initializing service: " + ae.getMessage(), ae);
        }

        // retrieve list of photos
        try {
            AlbumFeed feed = picasaService.getFeed(new URL(album.getUrl()), AlbumFeed.class);
            for(PhotoEntry photo : feed.getPhotoEntries()) {
                Image image = fromEntry(photo);
                images.add(image);
            }
        } catch (Exception e) {
            if(logger.isLoggable(Level.WARNING)) {
                logger.log(Level.WARNING, "Error retrieving photo stream from '" + album.getUrl() + "' :" +  e.getMessage(), e);
            }
            throw new PhotarkRuntimeException("Error retrieving photo stream from '" + album.getUrl() + "' :" +  e.getMessage(), e);
        }

        return images;
    }

    public Image getImage(AlbumConfig album, String Id) throws PhotarkRuntimeException {
        PicasawebService picasaService = null;
        Image image = null;

        try {
            picasaService = createPicasaService(album);
        } catch(Exception ae) {
            if(logger.isLoggable(Level.FINE)) {
                logger.log(Level.FINE, "Error initializing service: " + ae.getMessage(), ae);
            }
            throw new PhotarkRuntimeException("Error initializing service: " + ae.getMessage(), ae);
        }

        // retrieve list of photos
        try {
            AlbumFeed feed = picasaService.getFeed(new URL(album.getUrl() + "/" + Id), AlbumFeed.class);
            for(PhotoEntry photo : feed.getPhotoEntries()) {
                image = fromEntry(photo);
            }
        } catch (Exception e) {
            if(logger.isLoggable(Level.WARNING)) {
                logger.log(Level.WARNING, "Error retrieving photo stream from '" + album.getUrl() + "' :" +  e.getMessage(), e);
            }
            throw new PhotarkRuntimeException("Error retrieving photo stream from '" + album.getUrl() + "' :" +  e.getMessage(), e);
        }

        return image;
    }

    public String addImage(AlbumConfig album, Image image) throws PhotarkRuntimeException {
        throw new java.lang.UnsupportedOperationException("Operation not supported in album subscriptions");
    }

    public void updateImage(AlbumConfig album, Image image)  throws PhotarkRuntimeException {
        throw new java.lang.UnsupportedOperationException("Operation not supported in album subscriptions");
    }

    public void deleteImage(AlbumConfig album, String Id) throws PhotarkRuntimeException {
        throw new java.lang.UnsupportedOperationException("Operation not supported in album subscriptions");
    }


    private static PicasawebService createPicasaService(AlbumConfig album) {
        if(logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, "Initializing Google services to retrieve Picasa photo stream");
        }

        PicasawebService picasaService = new PicasawebService(APPLICATION_NAME);
        if(album.getUsername() != null & album.getPassword() != null) {
            if(logger.isLoggable(Level.FINE)) {
                logger.log(Level.FINE, "Authenticating user");
            }

            try {
                picasaService.setUserCredentials(album.getUsername(), album.getPassword());
            } catch (AuthenticationException e) {
                throw new IllegalArgumentException("Illegal username/password combination.");
            }
        }
        return picasaService;
    }


    private static Image fromEntry(PhotoEntry photo) {

        if(logger.isLoggable(Level.FINEST)) {
            logger.log(Level.FINEST, ">>>>>>>>>>>>>");
            logger.log(Level.FINEST, ">> ID : " + photo.getId());
            logger.log(Level.FINEST, ">> Title : " + photo.getTitle().getPlainText());
            logger.log(Level.FINEST, ">> Link : " + photo.getLinks().get(0).getHref());
        }

        Image image = new Image();

        // further documentation available at
        // http://code.google.com/apis/picasaweb/docs/2.0/developers_guide_java.html

        image.setId(photo.getGphotoId());
        image.setName(photo.getTitle().getPlainText());
        image.setTitle(photo.getTitle().getPlainText());
        image.setLocation(photo.getMediaContents().get(0).getUrl());

        return image;
    }
}
