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

package org.apache.photark.services;

import java.io.InputStream;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.commons.lang.StringUtils;
import org.apache.photark.services.gallery.jcr.JCRSession;
import org.apache.tuscany.sca.data.collection.Entry;
import org.apache.tuscany.sca.data.collection.NotFoundException;
import org.oasisopen.sca.annotation.Init;

public class ImageDisplayerImpl implements ImageDisplayer {
    private Session session = JCRSession.getSession();

    @Init
    public void init() {
    }

    public ImageDisplayerImpl() {
    }

    public InputStream get(String key) throws NotFoundException {
        String sub = StringUtils.substringAfter(key, "splayer/");
        String stringArray[] = StringUtils.split(sub, "/");
        String albumName = stringArray[0];
        InputStream inStream = null;
        try {
            Node root = session.getRootNode();
            Node albumNode = root.getNode(albumName);
            String image = stringArray[1];
            Node picNode = albumNode.getNode(image);
            inStream = picNode.getProperty("imageContent").getStream();

        } catch (PathNotFoundException e) {
            e.printStackTrace();
        } catch (RepositoryException e) {
            e.printStackTrace();
        }
        return inStream;
    }

    public Entry<String, InputStream>[] getAll() {
        return null;
    }

    public void delete(String key) {

    }

    public void put(String key, InputStream inputStream) {

    }

    public String post(String key, InputStream inputStream) {
        System.out.println("insdie post:" + key);

        return key;
    }

    public Entry<String, InputStream>[] query(String queryString) {
        return null;
    }
}
