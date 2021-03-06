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

package org.apache.photark.jcr.services;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.commons.lang.StringUtils;
import org.apache.photark.jcr.JCRRepositoryManager;
import org.apache.photark.services.ImageCollection;
import org.apache.tuscany.sca.data.collection.Entry;
import org.apache.tuscany.sca.data.collection.NotFoundException;
import org.oasisopen.sca.annotation.Destroy;
import org.oasisopen.sca.annotation.Init;
import org.oasisopen.sca.annotation.Reference;
import org.oasisopen.sca.annotation.Scope;
import org.oasisopen.sca.annotation.Service;

/**
 * JCR based implementation of the Image collection component 
 * Used to retrieve image files from the JCR repository
 */
@Scope("COMPOSITE")
@Service(ImageCollection.class)
public class JCRImageCollectionImpl implements ImageCollection {
    private static final Logger logger = Logger.getLogger(JCRImageCollectionImpl.class.getName());
    
    private JCRRepositoryManager repositoryManager;

    public JCRImageCollectionImpl() {
        
    }
    
    @Reference(name="repositoryManager")
    protected void setRepositoryManager(JCRRepositoryManager repositoryManager) {
        this.repositoryManager = repositoryManager;
    }

    
    @Init
    public void init() {
        try {
            repositoryManager = new JCRRepositoryManager();
        } catch (IOException e) {
            // FIXME: ignore for now
            e.printStackTrace();
        }
    }
    
    @Destroy
    public void destroy() {
        //repositoryManager.releaseSession();
    }

    public InputStream get(String key) throws NotFoundException {
        String sub = StringUtils.substringAfter(key, "gallery/");
        String stringArray[] = StringUtils.split(sub, "/");
        String albumName = stringArray[0];
        InputStream inStream = null;
        try {
            Session session = repositoryManager.getSession();
            Node root = session.getRootNode().getNode("albums").getNode("local");
            Node albumNode = root.getNode(albumName);
            String image = stringArray[1];
            Node picNode = albumNode.getNode(image);
            inStream = picNode.getProperty("imageContent").getStream();

        } catch (PathNotFoundException e) {
            e.printStackTrace();
        } catch (RepositoryException e) {
            e.printStackTrace();
        } finally {
            //repositoryManager.releaseSession();
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
        logger.info("insdie post:" + key);

        return key;
    }

    public Entry<String, InputStream>[] query(String queryString) {
        return null;
    }
}
