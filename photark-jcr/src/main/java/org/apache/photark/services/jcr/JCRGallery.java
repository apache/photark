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

import java.security.InvalidParameterException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.photark.Album;
import org.apache.photark.AlbumList;
import org.apache.photark.AlbumRef;
import org.apache.photark.services.GalleryService;
import org.apache.photark.services.PhotarkRuntimeException;
import org.apache.tuscany.sca.data.collection.Entry;
import org.oasisopen.sca.annotation.Reference;
import org.oasisopen.sca.annotation.Scope;

/**
 * JCR based Gallery
 * 
 * @version $Rev$ $Date$
 */
@Scope("COMPOSITE")
public class JCRGallery implements GalleryService {
    private static final Logger logger = Logger.getLogger(JCRGallery.class.getName());

    private JCRRepositoryManager repositoryManager;
    
    public JCRAlbumCollection albumCollection;
    
    public JCRGallery(@Reference(name="repositoryManager") JCRRepositoryManager repositoryManager) {
        this.repositoryManager = repositoryManager;
    }
    
    public AlbumList getAlbums() {
        //get all albums from the repository
        Entry<String, Album>[] albumEntries = albumCollection.getAll();
        
        AlbumList albums = new AlbumList();
        //loop trough the entries and create a albumRef for each Album
        for(Entry<String, Album> albumEntry : albumEntries) {
            albums.getAlbums().add(AlbumRef.createAlbumRef(albumEntry.getData()));
        }
        return null;
    }

    public Album getAlbum(String albumId) {
        if(albumId == null || albumId.isEmpty()) {
            throw new InvalidParameterException("Invalid/Empty album id");
        }
        
        Album album = null;
        try {
            //find the album based on the albumId
            Session session = repositoryManager.getSession();
            Node root = session.getRootNode();
            Node albumNode = root.getNode(albumId);
            
            //convert albumNode to a Album object
            album = fromNode(albumNode);
        } catch (Exception e) {
            logger.log(Level.WARNING,"Error retrieving album '" + albumId + "' : " + e.getMessage(), e);
        } 

        return album;
    }

    public void addAlbum(Album newAlbum) throws PhotarkRuntimeException {
        if(newAlbum.getName() == null || newAlbum.getName().isEmpty()) {
            throw new InvalidParameterException("Album has no name");
        }
    }

    public void updateAlbum(Album album) throws PhotarkRuntimeException {
        if(album.getName() == null || album.getName().isEmpty()) {
            throw new InvalidParameterException("Album has no name");
        }
    }

    public void removeAlbum(String albumName) throws PhotarkRuntimeException {
        if(albumName == null || albumName.isEmpty()) {
            throw new InvalidParameterException("Invalid/Empty album id");
        }
    }
        
    private Album fromNode(Node albumNode) throws RepositoryException {
        Album album = new Album();
        album.setName(albumNode.getName());
        album.setDescription(albumNode.getProperty("description").getString());
        
        return album;
    }
}
