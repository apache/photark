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

import javax.xml.ws.http.HTTPException;

import org.apache.abdera.Abdera;
import org.apache.abdera.factory.Factory;
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
    private static final Factory abderaFactory = Abdera.getNewFactory();
    private static final Parser abderaParser = Abdera.getNewParser();

    private HttpClient httpClient;
    private String feedURI;

    public FlickrPhotoStream(String feedURI) {
        this.feedURI = feedURI;
        
        // Create an HTTP client
        HttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();
        connectionManager.getParams().setDefaultMaxConnectionsPerHost(10);
        connectionManager.getParams().setConnectionTimeout(60000);
        httpClient = new HttpClient(connectionManager);

    }

    public Entry<String, Image>[] getAll() {
        GetMethod getMethod = new GetMethod(feedURI);
        
        try {
            httpClient.executeMethod(getMethod);
            int status = getMethod.getStatusCode();

            // Read the Atom entry
            if (status == 200) {
                Document<org.apache.abdera.model.Entry> doc = abderaParser.parse(new InputStreamReader(getMethod.getResponseBodyAsStream()));
                org.apache.abdera.model.Entry feedEntry = doc.getRoot();
            } else if (status == 404) {
                // handle not found
            } else {
                // handle error
            }            
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (HTTPException he) {
            he.printStackTrace();
        }

        return null;
    }
    
    public Image get(String key) throws NotFoundException {
        return null;
    }


    public String post(String arg0, Image arg1) {
        throw new java.lang.UnsupportedOperationException("Operation not supported in Remote Subscriptions");
    }

    public void put(String arg0, Image arg1) throws NotFoundException {
        throw new java.lang.UnsupportedOperationException("Operation not supported in Remote Subscriptions");
    }

    public void delete(String arg0) throws NotFoundException {
        throw new java.lang.UnsupportedOperationException("Operation not supported in Remote Subscriptions");
    }
    
    public Entry<String, Image>[] query(String arg0) {
        throw new java.lang.UnsupportedOperationException("Operation not supported in Remote Subscriptions");
    }

}
