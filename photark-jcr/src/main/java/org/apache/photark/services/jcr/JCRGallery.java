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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jcr.LoginException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.photark.Album;
import org.apache.photark.AlbumList;
import org.apache.photark.AlbumRef;
import org.apache.photark.Image;
import org.apache.photark.ImageList;
import org.apache.photark.ImageRef;
import org.apache.photark.services.GalleryService;
import org.apache.photark.services.PhotarkRuntimeException;
import org.oasisopen.sca.annotation.Init;
import org.oasisopen.sca.annotation.Reference;
import org.oasisopen.sca.annotation.Scope;

/**
 * JCR based Gallery
 *
 * @version $Rev$ $Date$
 */
@Scope("COMPOSITE")
public class JCRGallery implements GalleryService {
    private static final String JCR_ALBUM_ROOT_NODE = "albums";

    private static final Logger logger = Logger.getLogger(JCRGallery.class.getName());

    private Map<String, Album> albums = new HashMap<String, Album>();

    private JCRRepositoryManager repositoryManager;

    public JCRGallery(@Reference(name="repositoryManager") JCRRepositoryManager repositoryManager) {
        this.repositoryManager = repositoryManager;
    }

    @Init
    public void init() {
        try {
            if(logger.isLoggable(Level.FINE)) {
                logger.fine("Initializing JCR Gallery");
            }

            //get all albums from the repository
            Node albumRootNode = getAlbumRoot(repositoryManager);
            NodeIterator albumNodes = albumRootNode.getNodes();
            while (albumNodes.hasNext()) {
                Node albumNode = albumNodes.nextNode();
                if(albumNode != null) {
                    Album album = fromAlbumNode(albumNode);
                    albums.put(album.getName(), album);
                }
            }

            if(logger.isLoggable(Level.FINE)) {
                logger.fine("Found '" + albums.size() + "' albums in the repository");
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error retrieving list of Albums :" + e.getMessage(), e);
        }
    }

    // ALBUM FUNCTIONS

    public AlbumList getAlbums() {
        AlbumList albumList = new AlbumList();
        //loop trough the entries and create a albumRef for each Album
        Iterator<Entry<String, Album>> albumIterator = albums.entrySet().iterator();
        while(albumIterator.hasNext()) {
            Album album = (Album) albumIterator.next().getValue();
            albumList.getAlbums().add(AlbumRef.createAlbumRef(album));
        }

        return albumList;
    }

    public Album getAlbum(String albumId) {
        if(albumId == null || albumId.isEmpty()) {
            throw new InvalidParameterException("Invalid/Empty album id");
        }

        if(! albums.containsKey(albumId)) {
            throw new InvalidParameterException("Album '" + albumId + "' not found");
        }

        return albums.get(albumId);
    }

    public void addAlbum(Album newAlbum) throws PhotarkRuntimeException {
        if(newAlbum.getName() == null || newAlbum.getName().isEmpty()) {
            throw new InvalidParameterException("Album has no name");
        }

        try {
            Node albumRootNode = getAlbumRoot(repositoryManager);

            if(albumRootNode.hasNode(newAlbum.getName())) {
                throw new PhotarkRuntimeException("Error creating album. Album '" + newAlbum.getName() + "' already exists !");
            }

            Node albumNode = albumRootNode.addNode(newAlbum.getName());
            if(albumNode != null) {
                albumNode.setProperty("name", newAlbum.getName());
                albumNode.setProperty("description", newAlbum.getDescription());
                albumNode.setProperty("name", newAlbum.getLocation());
                repositoryManager.getSession().save();
            }

            albums.put(newAlbum.getName(), newAlbum);

        } catch(RepositoryException e) {
            if(logger.isLoggable(Level.SEVERE)) {
                logger.log(Level.SEVERE, "Error creating new album node '" + newAlbum.getName() + "'" );
            }
            throw new PhotarkRuntimeException("Error creating new album node '" + newAlbum.getName() + "'" );
        }
    }

    public void updateAlbum(Album album) throws PhotarkRuntimeException {
        if(album.getName() == null || album.getName().isEmpty()) {
            throw new InvalidParameterException("Album has no name");
        }

        if(! albums.containsKey(album.getName())) {
            if(logger.isLoggable(Level.SEVERE)) {
                logger.log(Level.SEVERE, "Album '" + album.getName() + "' not found");
            }
            throw new InvalidParameterException("Album '" + album.getName() + "' not found");
        }

        try {
            Node albumRootNode = getAlbumRoot(repositoryManager);

            if(! albumRootNode.hasNode(album.getName())) {
                if(logger.isLoggable(Level.SEVERE)) {
                    logger.log(Level.SEVERE, "Album node '" + album.getName() + "' not found in the JCR repository");
                }

                throw new PhotarkRuntimeException("Album node '" + album.getName()  + "' not found in the JCR repository");
            }

            Node albumNode = albumRootNode.getNode(album.getName());
            albumNode.setProperty("name", album.getName());
            albumNode.setProperty("description", album.getDescription());
            albumNode.setProperty("name", album.getLocation());
            repositoryManager.getSession().save();

            albums.put(album.getName(), album);

        } catch(RepositoryException e) {
            if(logger.isLoggable(Level.SEVERE)) {
                logger.log(Level.SEVERE, "Error updating album node '" + album.getName() + "'" );
            }
            throw new PhotarkRuntimeException("Error updating album node '" + album.getName() + "'" );
        }


    }

    public void removeAlbum(String albumName) throws PhotarkRuntimeException {
        if(albumName == null || albumName.isEmpty()) {
            throw new InvalidParameterException("Invalid/Empty album name");
        }


        if(! albums.containsKey(albumName)) {
            if(logger.isLoggable(Level.SEVERE)) {
                logger.log(Level.SEVERE, "Album '" + albumName + "' not found");
            }
            throw new InvalidParameterException("Album '" + albumName + "' not found");
        }

        try {
            Node albumRootNode = getAlbumRoot(repositoryManager);

            if(! albumRootNode.hasNode(albumName)) {
                if(logger.isLoggable(Level.SEVERE)) {
                    logger.log(Level.SEVERE, "Album node '" + albumName + "' not found in the JCR repository");
                }

                throw new PhotarkRuntimeException("Album node '" + albumName  + "' not found in the JCR repository");
            }

            Node albumNode = albumRootNode.getNode(albumName);
            albumNode.remove();
            repositoryManager.getSession().save();

            albums.remove(albumName);

        } catch(RepositoryException e) {
            if(logger.isLoggable(Level.SEVERE)) {
                logger.log(Level.SEVERE, "Error deleting album node '" + albumName + "'" );
            }
            throw new PhotarkRuntimeException("Error deleting album node '" + albumName + "'" );
        }
    }

    // IMAGE FUNCTIONS

    public ImageList getImages(String albumName) {
        ImageList images = new ImageList();
        Node albumNode = null;

        try {
            albumNode = getAlbumNode(repositoryManager, albumName);
            NodeIterator imageNodes = albumNode.getNodes();

            while (imageNodes.hasNext()) {
                Node imageNode = imageNodes.nextNode();
                if(imageNode != null) {
                    Image image = fromImageNode(imageNode);
                    images.getImages().add(ImageRef.createImageRef(image));
                }
            }
        }catch(RepositoryException e) {
            if(logger.isLoggable(Level.SEVERE)) {
                logger.log(Level.SEVERE, "Error retrieving images for album '" + albumName + "'" );
            }
            throw new PhotarkRuntimeException("Error retrieving images for album '" + albumName + "'" );
        }

        return images;
    }

    public Image getImage(String albumName, String imageId) {
        if(imageId == null || imageId.isEmpty()) {
            throw new InvalidParameterException("Invalid/Empty image id");
        }

        if(! albums.containsKey(imageId)) {
            throw new InvalidParameterException("Image '" + imageId + "' not found");
        }

        return null;
    }

    public void addImage(String albumName, Image newImage) throws PhotarkRuntimeException {
        if(newImage.getName() == null || newImage.getName().isEmpty()) {
            throw new InvalidParameterException("Image has no name");
        }

        try {
            Node albumNode = getAlbumNode(repositoryManager, albumName);

            if(albumNode.hasNode(newImage.getId())) {
                throw new PhotarkRuntimeException("Error creating image. Image '" + newImage.getId() + "' already exists !");
            }

            Node imageNode = albumNode.addNode(newImage.getId());
            if(imageNode != null) {
                imageNode.setProperty("id", newImage.getId());
                imageNode.setProperty("name", newImage.getName());
                imageNode.setProperty("title", newImage.getTitle());
                imageNode.setProperty("description", newImage.getDescription());
                imageNode.setProperty("url", newImage.getLocation());
                repositoryManager.getSession().save();
            }

        } catch(RepositoryException e) {
            if(logger.isLoggable(Level.SEVERE)) {
                logger.log(Level.SEVERE, "Error creating new image node '" + newImage.getName() + "'" );
            }
            throw new PhotarkRuntimeException("Error creating new image node '" + newImage.getName() + "'" );
        }
    }

    public void updateImage(String albumName, Image image) throws PhotarkRuntimeException {
        if(image.getId() == null || image.getId().isEmpty()) {
            throw new InvalidParameterException("Image has no id");
        }
        
        if(image.getName() == null || image.getName().isEmpty()) {
            throw new InvalidParameterException("Image has no name");
        }

        if(! albums.containsKey(albumName)) {
            if(logger.isLoggable(Level.SEVERE)) {
                logger.log(Level.SEVERE, "Album '" + albumName + "' not found");
            }
            throw new InvalidParameterException("Album '" + albumName + "' not found");
        }

        try {
            Node albumRootNode = getAlbumRoot(repositoryManager);

            if(! albumRootNode.hasNode(image.getName())) {
                if(logger.isLoggable(Level.SEVERE)) {
                    logger.log(Level.SEVERE, "Image node '" + image.getName() + "' not found in the JCR repository");
                }

                throw new PhotarkRuntimeException("Image node '" + image.getName()  + "' not found in the JCR repository");
            }

            Node imageNode = albumRootNode.getNode(image.getId());
            imageNode.setProperty("name", image.getName());
            imageNode.setProperty("description", image.getDescription());
            imageNode.setProperty("name", image.getLocation());
            repositoryManager.getSession().save();

        } catch(RepositoryException e) {
            if(logger.isLoggable(Level.SEVERE)) {
                logger.log(Level.SEVERE, "Error updating image node '" + image.getName() + "'" );
            }
            throw new PhotarkRuntimeException("Error updating image node '" + image.getName() + "'" );
        }

    }

    public void removeImage(String albumName, String imageId) throws PhotarkRuntimeException {
        
        if(imageId == null || imageId.isEmpty()) {
            throw new InvalidParameterException("Invalid/Empty album name");
        }


        if(! albums.containsKey(albumName)) {
            if(logger.isLoggable(Level.SEVERE)) {
                logger.log(Level.SEVERE, "Album '" + albumName + "' not found");
            }
            throw new InvalidParameterException("Album '" + albumName + "' not found");
        }

        try {
            Node albumRootNode = getAlbumRoot(repositoryManager);

            if(! albumRootNode.hasNode(imageId)) {
                if(logger.isLoggable(Level.SEVERE)) {
                    logger.log(Level.SEVERE, "Image node '" + imageId + "' not found in the JCR repository");
                }

                throw new PhotarkRuntimeException("Image node '" + imageId  + "' not found in the JCR repository");
            }

            Node imageNode = albumRootNode.getNode(imageId);
            imageNode.remove();
            repositoryManager.getSession().save();

            albums.remove(imageId);

        } catch(RepositoryException e) {
            if(logger.isLoggable(Level.SEVERE)) {
                logger.log(Level.SEVERE, "Error deleting album node '" + imageId + "'" );
            }
            throw new PhotarkRuntimeException("Error deleting album node '" + imageId + "'" );
        }
    }

    // UTILITY FUNCTIONS

    private static Album fromAlbumNode(Node albumNode) throws RepositoryException {
        Album album = new Album();
        album.setName(albumNode.getName());
        album.setDescription(albumNode.getProperty("description").getString());

        return album;
    }


    private static Image fromImageNode(Node imageNode) throws RepositoryException {
        Image image = new Image();
        image.setName(imageNode.getName());
        image.setTitle(imageNode.getProperty("title").getString());
        image.setDescription(imageNode.getProperty("description").getString());
        image.setLocation(imageNode.getProperty("url").getString());
        image.setThumbnailLocation(imageNode.getProperty("urlThumb").getString());

        return image;
    }


    private static Node getAlbumRoot(JCRRepositoryManager repositoryManager) throws PhotarkRuntimeException {
        Session session = null;
        Node root = null;
        Node albumRoot = null;
        try {
            session = repositoryManager.getSession();
            root = session.getRootNode();
            if (root.hasNode(JCR_ALBUM_ROOT_NODE)) {
                albumRoot = root.getNode(JCR_ALBUM_ROOT_NODE);
            } else {
                albumRoot = root.addNode(JCR_ALBUM_ROOT_NODE);
                session.save();
            }
        } catch (LoginException e) {
            logger.log(Level.SEVERE, "Error loging in to photark repository :" + e.getMessage(), e);
            throw new PhotarkRuntimeException("Error loging in to photark repository  :" + e.getMessage(), e);
        } catch (RepositoryException e) {
            logger.log(Level.SEVERE, "Error retrieving albums from the repository :" + e.getMessage(), e);
            throw new PhotarkRuntimeException("Error retrieving albums from the repository :" + e.getMessage(), e);
        }

        return albumRoot;
    }

    /**
     * This method create new album node in case it does not exists in
     * repository or return older album node otherwise.
     *
     * @param
     * @return
     * @throws RepositoryException
     */
    private static Node getAlbumNode(JCRRepositoryManager repositoryManager, String name) throws PhotarkRuntimeException {
        Session session = null;
        Node root = null;
        Node albumNode = null;
        try {
            session = repositoryManager.getSession();
            root = session.getRootNode();
            if (root.hasNode(name)) {
                albumNode = root.getNode(name);
            }
        } catch (LoginException e) {
            logger.log(Level.SEVERE, "Error loging in to photark repository :" + e.getMessage(), e);
            throw new PhotarkRuntimeException("Error loging in to photark repository  :" + e.getMessage(), e);
        } catch (RepositoryException e) {
            logger.log(Level.SEVERE, "Error retrieving albums from the repository :" + e.getMessage(), e);
            throw new PhotarkRuntimeException("Error retrieving albums from the repository :" + e.getMessage(), e);
        }

        return albumNode;
    }
}
