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

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.photark.Image;
import org.apache.photark.filesystem.services.ImageFilter;
import org.apache.photark.services.album.Album;
import org.apache.photark.services.jcr.JCRRepositoryManager;
import org.oasisopen.sca.annotation.Destroy;
import org.oasisopen.sca.annotation.Init;
import org.oasisopen.sca.annotation.Property;

public class JCRAlbumImpl implements Album {
    private static final Logger logger = Logger.getLogger(JCRAlbumImpl.class.getName());

    private JCRRepositoryManager repositoryManager;

    private String gallery;
    private String name;
    private String location;
    private boolean initialized;
    private static Map<String, Album> albums = new HashMap<String, Album>();

    public synchronized static Album createAlbum(JCRRepositoryManager repositoryManager, String name ) {
        if (!albums.containsKey(name)) {
            albums.put(name, new JCRAlbumImpl(repositoryManager,name ));
        }
        return albums.get(name);
    }

    public JCRAlbumImpl(JCRRepositoryManager repositoryManager, String name) {
        this.repositoryManager = repositoryManager;
        this.name = name;
    }

    /**
     * Initialize the gallery service
     *   - During initialization, check for local images and create a JCR album 
     *     which is usefull for sample gallery shiped in the sample application.
     */
    @Init
    public synchronized void init() {
        logger.info("Initializing JCR Album");
        try {
            URL albumURL = this.getClass().getClassLoader().getResource(getLocation());
            if (albumURL == null) {
                String loc = "../../" + getLocation();
                albumURL = this.getClass().getClassLoader().getResource(loc);
            }

            Session session = repositoryManager.getSession();
            if (albumURL != null) {
                try {
                    File album = new File(albumURL.toURI());
                    if (album.isDirectory() && album.exists()) {
                        String[] listPictures = album.list(new ImageFilter(".jpg"));
                        if (listPictures != null && listPictures.length > 0) {
                            Node albumNode = getAlbumNode(name);
                            for (String image : listPictures) {
                                if (!albumNode.hasNode(image)) {
                                    Node picNode = albumNode.addNode(image);
                                    String imagePath = albumURL.getPath() + image;
                                    InputStream imageContent = new FileInputStream(new File(imagePath));
                                    picNode.setProperty("imageContent", imageContent);
                                    picNode.setProperty("name", image);
                                    picNode.setProperty("location", image);
                                }
                            }
                        }
                    }

                    session.save();
                } catch (Exception e) {
                    // FIXME: ignore for now
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            // FIXME: ignore for now
            e.printStackTrace();
        } finally {
            //repositoryManager.releaseSession();
        }
        initialized = true;
    }

    @Destroy
    public void destroy() {
        //repositoryManager.releaseSession();
    }

    @Property
    public void setGallery(String gallery) {
        this.gallery = gallery;
        this.location = null;
    }

    public String getName() {
        return name;
    }

    @Property
    public void setName(String name) {
        this.name = name;
        this.location = null;
    }

    public String getDescription() {
        String description="";
        if (!initialized) {
            init();
        }
        try {
            Session session = repositoryManager.getSession();
            Node root = session.getRootNode();
            Node albumNode = root.getNode(name);
            if (albumNode.hasProperty("description")) {
                description = albumNode.getProperty("description").getString();
            } /*else {
                logger.info("description of album " + name + " not found");
            } */

        } catch (Exception e) {
            // FIXME: ignore for now
            e.printStackTrace();
        } finally {
            //repositoryManager.releaseSession();
        }
        return description;
    }

    @Property
    public void setDescription(String description) {
        try {
            Session session = repositoryManager.getSession();
            Node root = session.getRootNode();
            Node albumNode = root.getNode(name);
            albumNode.setProperty("description", description);
            session.save();
        } catch (RepositoryException e) {
            e.printStackTrace();
        }  finally {
            //repositoryManager.releaseSession();
        }

    }

    public String getLocation() {
        if (location == null) {
            location = gallery + "/" + name + "/";
        }
        return location;

    }

    public void setLocation(String location) {
        logger.info("inside setLocation:location:" + location);
        this.location = location;
    }

    public String[] getPictures() {
        if (!initialized) {
            init();
        }
        List<String> pictures = new ArrayList<String>();
        try {
            Session session = repositoryManager.getSession();
            Node root = session.getRootNode();
            Node albumNode = root.getNode(name);
            NodeIterator nodes = albumNode.getNodes();

            while (nodes.hasNext()) {
                Node node = nodes.nextNode();
                if (node.getPath().equals("/jcr:system"))
                    continue;
                pictures.add(node.getProperty("location").getString());
            }
        } catch (Exception e) {
            // FIXME: ignore for now
            e.printStackTrace();
        } finally {
            //repositoryManager.releaseSession();
        }

        String[] pictureArray = new String[pictures.size()];
        pictures.toArray(pictureArray);
        return pictureArray;
    }

    public void removeNodes() {
        try {
            Session session = repositoryManager.getSession();
            Node root = session.getRootNode();
            NodeIterator nodes = root.getNodes();
            while (nodes.hasNext()) {
                Node node = nodes.nextNode();
                if (node.getPath().equals("/jcr:system"))
                    continue;
                else
                    node.remove();
            }
            session.save();
        } catch (Exception e) {
            // FIXME: ignore for now
            e.printStackTrace();
        } finally {
            //repositoryManager.releaseSession();
        }

    }

    public void addPicture(Image picture) {
        try {
            Session session = repositoryManager.getSession();
            Node root = session.getRootNode();
            Node albumNode = root.getNode(name);
            Node picNode = albumNode.addNode(picture.getName());
            picture.getImageAsStream();
            picNode.setProperty("imageContent", picture.getImageAsStream());
            picNode.setProperty("name", picture.getName());
            picNode.setProperty("location", picture.getName());
            session.save();
        } catch (RepositoryException e) {
            e.printStackTrace();
        }  finally {
            //repositoryManager.releaseSession();
        }
    }

    public void deletePicture(Image picture) {
        try {
            Session session = repositoryManager.getSession();
            Node root = session.getRootNode();
            Node albumNode = root.getNode(name);
            Node picNode = albumNode.addNode(picture.getName());
            picNode.remove();
            session.save();
        } catch (RepositoryException e) {
            e.printStackTrace();
        }  finally {
            //repositoryManager.releaseSession();
        }
    }

    /**
     * This method deletes the picture node.
     * @param String pictureName
     * 
     */
    public void deletePicture(String pictureName) {
        try {
            Session session = repositoryManager.getSession();
            Node root = session.getRootNode();
            Node albumNode = root.getNode(name);
            if(albumNode.hasNode(pictureName)){
                Node picNode = albumNode.getNode(pictureName);
                picNode.remove();
                session.save();
            }else{
                logger.info("image " + pictureName + " not found");
            }    	            
        } catch (RepositoryException e) {
            e.printStackTrace();
        }  finally {
            //repositoryManager.releaseSession();
        }
    }

    /**
     * This method create new album node in case it does not exists in
     * repository or return older album node otherwise.
     * 
     * @param albumName
     * @return
     * @throws RepositoryException
     */
    private Node getAlbumNode(String name) throws RepositoryException {
        Session session = repositoryManager.getSession();
        Node root = session.getRootNode();
        if (root.hasNode(name)) {
            return root.getNode(name);
        } else {
            return root.addNode(name);
        }
    }
}
