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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.photark.Image;
import org.apache.tuscany.sca.data.collection.Collection;
import org.apache.tuscany.sca.data.collection.Entry;
import org.apache.tuscany.sca.data.collection.NotFoundException;

import com.google.gdata.client.photos.PicasawebService;
import com.google.gdata.data.photos.AlbumFeed;
import com.google.gdata.data.photos.PhotoEntry;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;

public class PicasaPhotoStream implements Collection<String, Image> {
    private static final Logger logger = Logger.getLogger(PicasaPhotoStream.class.getName());

    private static final String APPLICATION_NAME = "photark";
    private static final String PICASA_SERVICE_TYPE = "lh2";

    private final String feedURI;
    private final String username;
    private final String password;

    private boolean initialized = false;

    private PicasawebService picasaService;

    public PicasaPhotoStream(String feedURI) {
        this.feedURI = feedURI;
        this.username = null;
        this.password= null;
    }

    public PicasaPhotoStream(String feedURI, String username, String password) {
        this.feedURI = feedURI;
        this.username = username;
        this.password = password;

    }

    private void initialize() {
        logger.log(Level.FINE, "Initializing Google services to retrieve Picasa photo stream");
        picasaService = new PicasawebService(APPLICATION_NAME);
        if(this.username != null & this.password != null) {
            logger.log(Level.FINE, "Authenticating user");
            try {
                picasaService.setUserCredentials(username, password);
            } catch (AuthenticationException e) {
                throw new IllegalArgumentException(
                "Illegal username/password combination.");
            }
        }
        initialized = true;
    }


    @SuppressWarnings({"rawtypes", "unchecked"})
    public Entry<String, Image>[] getAll() {

        List<Entry<String, Image>> images = new ArrayList<Entry<String, Image>>();

        if(!initialized) {
            try {
                initialize();
            } catch(Exception ae) {
                logger.log(Level.FINE, "Error initializing service: " + ae.getMessage(), ae);
                return new Entry[0];
            }
        }

        // retrieve list of photos


        try {
            AlbumFeed feed = picasaService.getFeed(new URL(feedURI), AlbumFeed.class);
            for(PhotoEntry photo : feed.getPhotoEntries()) {
                Image image = fromEntry(photo);
                images.add(new Entry(image.getId(), image));
            }
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ServiceException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Entry<String, Image>[] imageArray = new Entry[images.size()];
        images.toArray(imageArray);
        return imageArray;
    }

    public Image get(String key) throws NotFoundException {
        throw new java.lang.UnsupportedOperationException("Operation not supported in album subscriptions");
    }

    public String post(String key, Image value) {
        throw new java.lang.UnsupportedOperationException("Operation not supported in album subscriptions");
    }

    public void put(String key, Image value) throws NotFoundException {
        throw new java.lang.UnsupportedOperationException("Operation not supported in album subscriptions");
    }

    public void delete(String key) throws NotFoundException {
        throw new java.lang.UnsupportedOperationException("Operation not supported in album subscriptions");
    }

    public Entry<String, Image>[] query(String query) {
        throw new java.lang.UnsupportedOperationException("Operation not supported in album subscriptions");
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
        image.setName(photo.getGphotoId());
        image.setTitle(photo.getTitle().getPlainText());
        image.setLocation(photo.getMediaContents().get(0).getUrl());

        return image;
    }

}
