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

package org.apache.photark.services.jcr;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.photark.Album;
import org.apache.tuscany.sca.data.collection.Entry;
import org.apache.tuscany.sca.data.collection.NotFoundException;
import org.oasisopen.sca.annotation.Reference;

/**
 * Collection helper to handle JCR Albums
 * 
 * @version $Rev$ $Date$
 */
public class JCRAlbumCollection implements AlbumCollection {
    private static final Logger logger = Logger.getLogger(JCRAlbumCollection.class.getName());

    private JCRRepositoryManager repositoryManager;

    public JCRAlbumCollection(@Reference(name="repositoryManager") JCRRepositoryManager repositoryManager) {
        this.repositoryManager = repositoryManager;
    }

    public Entry<String, Album>[] getAll() {
        List<Entry<String, Album>> entries = new ArrayList<Entry<String, Album>>();
        
        try {
            Session session = repositoryManager.getSession();
            Node rootNode = session.getRootNode();
            NodeIterator albumNodes = rootNode.getNodes();
            while (albumNodes.hasNext()) {
                Node albumNode = albumNodes.nextNode();
                if(albumNode != null) {
                    if (albumNode.getPath().equals("/jcr:system")) {
                        continue;
                    }
            
                    Entry<String, Album> entry = new Entry<String, Album>(albumNode.getName(), fromNode(albumNode));
                    entries.add(entry);                    
                }
            }
        } catch (RepositoryException e) {
            logger.log(Level.WARNING, "Error retrieving list of Albums :" + e.getMessage(), e);
        } 
        
        Entry<String, Album>[] entriesArray = new Entry[entries.size()];
        entries.toArray(entriesArray);
        
        return entriesArray;
    }
    
    public Album get(String albumId) throws NotFoundException {
        Album album = null;
        
        try {
            Session session = repositoryManager.getSession();
            Node root = session.getRootNode();
            Node albumNode = root.getNode(albumId);
            if(albumNode != null) {
                album = fromNode(albumNode);
            }
            
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error retrieving album '" + albumId + "' :" + e.getMessage(), e);        
        }
        
        return album;
    }


    public String post(String arg0, Album arg1) {
        // TODO Auto-generated method stub
        return null;
    }

    public void put(String arg0, Album arg1) throws NotFoundException {
        // TODO Auto-generated method stub

    }

    public void delete(String arg0) throws NotFoundException {
        // TODO Auto-generated method stub

    }
    
    public Entry<String, Album>[] query(String arg0) {
        throw new UnsupportedOperationException("Operation not implemented");
    }
    
    private Album fromNode(Node albumNode) throws RepositoryException {
        Album album = new Album();
        album.setName(albumNode.getName());
        album.setDescription(albumNode.getProperty("description").getString());
        
        return album;
    }

}
