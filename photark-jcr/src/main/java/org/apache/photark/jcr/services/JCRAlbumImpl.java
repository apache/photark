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
import java.util.*;
import java.util.logging.Logger;

import javax.jcr.*;

import org.apache.photark.Image;
import org.apache.photark.jcr.JCRRepositoryManager;
import org.apache.photark.security.authorization.Permission;
import org.apache.photark.services.album.Album;
import org.apache.photark.services.album.ImageFilter;
import org.oasisopen.sca.annotation.Destroy;
import org.oasisopen.sca.annotation.Init;
import org.oasisopen.sca.annotation.Property;

public class JCRAlbumImpl implements Album {
    private static final Logger logger = Logger.getLogger(JCRAlbumImpl.class.getName());

    private JCRRepositoryManager repositoryManager;

    private String gallery;
    private String name;
    private String type;
    private String location;
    private boolean initialized;
    private static Map<String, Album> albums = new HashMap<String, Album>();

    public synchronized static Album createAlbum(JCRRepositoryManager repositoryManager, String name) {
        if (!albums.containsKey(name)) {
            albums.put(name, new JCRAlbumImpl(repositoryManager, name));
        }
        return albums.get(name);
    }

    public synchronized static Album createAlbum(JCRRepositoryManager repositoryManager, String name, String type) { // Adhere to new structure

        if (!albums.containsKey(name)) {
            albums.put(name, new JCRAlbumImpl(repositoryManager, name, type));
        }
        return albums.get(name);
    }

    public JCRAlbumImpl(JCRRepositoryManager repositoryManager, String name) {
        this.repositoryManager = repositoryManager;
        this.name = name;
        this.type = "local";   // temporary
    }

    public JCRAlbumImpl(JCRRepositoryManager repositoryManager, String name, String type) {

        this.repositoryManager = repositoryManager;
        this.name = name;
        this.type = type;
    }

    /**
     * Initialize the gallery service
     * - During initialization, check for local images and create a JCR album
     * which is usefull for sample gallery shiped in the sample application.
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
//                            Node albumNode = getAlbumNode(name);
                            Node albumNode = getAlbumNode();              // this line added to adhere to new structure
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

    public String getType() {
        return type;
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
        String description = "";
        if (!initialized) {
            init();
        }
        try {
            Node albumNode = getAlbumNode();
            if (albumNode.hasProperty("description")) {
                description = albumNode.getProperty("description").getString();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //repositoryManager.releaseSession();
        }
        return description;
    }

    @Property
    public void setDescription(String description) {
        try {
            Node albumNode = getAlbumNode();
            albumNode.setProperty("description", description);
        } catch (RepositoryException e) {
            e.printStackTrace();
        } finally {
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
            NodeIterator nodes = getAlbumNode().getNodes();
            while (nodes.hasNext()) {
                Node node = nodes.nextNode();
                pictures.add(node.getProperty("location").getString());
            }
        } catch (RepositoryException e) {

        }

        String[] pictureArray = new String[pictures.size()];
        pictures.toArray(pictureArray);
        return pictureArray;
    }

    private Node getAlbumNode() {       

        Node albumNode = null;

        try {
            Session session = repositoryManager.getSession();
            Node root = session.getRootNode().getNode("albums");

            if (this.type.equals("local")) {                        // returns a local album node or if not exists create a new album node
                if (root.getNode("local").hasNode(name)) {
                    albumNode = root.getNode("local").getNode(name);
                } else {
                    albumNode = root.getNode("local").addNode(name);
                }

            } else if (this.type.equals("remote")) {                   // returns a remote album node or if not exists create a new album node
                if (root.getNode("remote").hasNode(name)) {
                    albumNode = root.getNode("remote").getNode(name);
                } else {
                    albumNode = root.getNode("remote").addNode(name);
                }

            }
            session.save();
        } catch (Exception e) {

        }
        return albumNode;
    }

    public void removeNodes() {
        try {
            Session session = repositoryManager.getSession();
            Node root = session.getRootNode().getNode("albums").getNode("local");
            NodeIterator nodes = root.getNodes();
            while (nodes.hasNext()) {
                Node node = nodes.nextNode();
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

    public void addPicture(Image picture) {    // Adhered to new structure

        try {

            Session session = repositoryManager.getSession();
            String location = "";
            if (this.type.equals("local")) {
                location = picture.getName();
            } else if (this.type.equals("remote")) {
                location = picture.getLocation();
            }
            Node picNode = getAlbumNode().addNode(picture.getName());
            picNode.setProperty("imageContent", picture.getImageAsStream());
            picNode.setProperty("name", picture.getName());
            picNode.setProperty("location", location);
            session.save();


        } catch (RepositoryException e) {
            e.printStackTrace();
        } finally {
            //repositoryManager.releaseSession();
        }
    }

    public void deletePicture(Image picture) {
        try {
            Session session = repositoryManager.getSession();

            Node albumNode = getAlbumNode();
            Node picNode = albumNode.getNode(picture.getName());
            picNode.remove();
            session.save();
        } catch (RepositoryException e) {
            e.printStackTrace();
        } finally {
            //repositoryManager.releaseSession();
        }
    }

    @Property
    public void addOwner(String owner) {
        List<String> ownerList = new ArrayList<String>();
        ownerList.add(owner);
        try {
            Session session = repositoryManager.getSession();
            Node root = session.getRootNode().getNode("albums").getNode(type);
            Node albumNode = root.getNode(name);
            if (albumNode.hasProperty("owners")) {
                for (Value ownerValue : albumNode.getProperty("owners").getValues()) {
                    if (!ownerList.contains(ownerValue.getString())) {
                        ownerList.add(ownerValue.getString());
                    }

                }
            }
            String[] owners = new String[ownerList.size()];
            for (int i = 0; i < ownerList.size(); i++) {
                owners[i] = ownerList.get(i);
            }
            albumNode.setProperty("owners", owners);
            session.save();
        } catch (RepositoryException e) {
            e.printStackTrace();
        } finally {
            //repositoryManager.releaseSession();
        }
    }

    public String[] getOwners() {
        if (!initialized) {
            init();
        }
        try {
            Session session = repositoryManager.getSession();
            Node root = session.getRootNode().getNode("albums").getNode(type);
            if (root.hasNode(name)) {
                Node albumNode = root.getNode(name);
                if (albumNode.hasProperty("owners")) {
                    Value[] values = albumNode.getProperty("owners").getValues();
                    String[] owners = new String[values.length];
                    for (int i = 0; i < values.length; i++) {
                        owners[i] = values[i].getString();
                    }
                    return owners;
                }
            }


        } catch (Exception e) {
            // FIXME: ignore for now
            e.printStackTrace();
        } finally {
            //repositoryManager.releaseSession();
        }
        return new String[]{};
    }

    /**
     * This method deletes the picture node.
     *
     * @param pictureName
     */
    public void deletePicture(String pictureName) {
        try {
            Session session = repositoryManager.getSession();

            Node albumNode = getAlbumNode();
            if (albumNode.hasNode(pictureName)) {
                Node picNode = albumNode.getNode(pictureName);
                picNode.remove();
                session.save();
            } else {
                logger.info("image " + pictureName + " not found");
            }
        } catch (RepositoryException e) {
            e.printStackTrace();
        } finally {
            //repositoryManager.releaseSession();
        }
    }

    /**
     * This method create new album node in case it does not exists in
     * repository or return older album node otherwise.
     *
     * @param
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
