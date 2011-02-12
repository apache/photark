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

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.ws.http.HTTPException;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Document;
import org.apache.abdera.parser.Parser;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.photark.Image;
import org.apache.tuscany.sca.data.collection.Collection;
import org.apache.tuscany.sca.data.collection.Entry;
import org.apache.tuscany.sca.data.collection.NotFoundException;

public class FlickrPhotoStream implements Collection<String, Image> {
    private static final Logger logger = Logger.getLogger(FlickrPhotoStream.class.getName());

    private static final Parser abderaParser = Abdera.getNewParser();

    private final String feedURI;

    private HttpClient httpClient;

    public FlickrPhotoStream(String feedURI) {
        this.feedURI = feedURI;

        // Create an HTTP client
        HttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();
        connectionManager.getParams().setDefaultMaxConnectionsPerHost(10);
        connectionManager.getParams().setConnectionTimeout(60000);
        httpClient = new HttpClient(connectionManager);
    }

    @SuppressWarnings("unchecked")
    public Entry<String, Image>[] getAll() {
        List<Entry<String, Image>> images = null;
        GetMethod getMethod = new GetMethod(feedURI);

        try {
            httpClient.executeMethod(getMethod);
            int status = getMethod.getStatusCode();

            // Read the Atom entry
            if (status == 200) {
                Document<org.apache.abdera.model.Feed> doc = abderaParser.parse(new InputStreamReader(getMethod.getResponseBodyAsStream()));
                org.apache.abdera.model.Feed feed = null;
                try {
                    feed = doc.getRoot();
                 } catch(Exception e) {
                     throw new IllegalArgumentException("Error parsing feed: " + e.getMessage());
                 }

                 images = new ArrayList<Entry<String, Image>>();
                 for (org.apache.abdera.model.Entry feedEntry : feed.getEntries()) {
                     Image image = fromEntry(feedEntry);
                     images.add(new Entry<String, Image>(image.getName(), image));
                }

            } else {
                throw new IllegalArgumentException("Invalid photo stream uri:" + feedURI);
            }
        } catch (IOException ioe) {
            logger.log(Level.WARNING, "Error retrieving photo stream from '" + feedURI + "' :" +  ioe.getMessage(), ioe);
        } catch (HTTPException he) {
            logger.log(Level.WARNING, "Error retrieving photo stream from '" + feedURI + "' :" +  he.getMessage(), he);
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

    private static Image fromEntry(org.apache.abdera.model.Entry entry) {

        if(logger.isLoggable(Level.FINEST)) {
            logger.log(Level.FINEST, ">>>>>>>>>>>>>");
            logger.log(Level.FINEST, ">> ID : " + entry.getId().toString());
            logger.log(Level.FINEST, ">> Title : " + entry.getTitle());
            logger.log(Level.FINEST, ">> Link : " + entry.getEnclosureLinkResolvedHref().toString());
        }

        Image image = new Image();
        String id = entry.getId().toString();
        image.setId(id.substring(id.lastIndexOf("/") + 1));
        image.setTitle(entry.getTitle());
        image.setLocation(entry.getEnclosureLinkResolvedHref().toString());
        image.setName(id.substring(id.lastIndexOf("/") + 1));

        return image;
    }


}
