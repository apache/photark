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
import java.net.URL;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.logging.Logger;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.photark.Image;
import org.apache.photark.jcr.JCRRepositoryManager;
import org.apache.photark.security.authorization.services.AccessManager;
import org.apache.photark.services.album.Album;
import org.apache.photark.services.gallery.BaseGalleryImpl;
import org.apache.photark.services.gallery.Gallery;
import org.oasisopen.sca.annotation.Init;
import org.oasisopen.sca.annotation.Reference;
import org.oasisopen.sca.annotation.Scope;
import static  org.apache.photark.security.utils.Constants.*;

@Scope("COMPOSITE")
public class JCRGalleryImpl extends BaseGalleryImpl implements Gallery {
    private static final Logger logger = Logger.getLogger(JCRGalleryImpl.class.getName());

    private JCRRepositoryManager repositoryManager;
    private AccessManager accessManager;


    public JCRGalleryImpl() {

    }

    @Reference(name = "repositoryManager")
    protected void setRepositoryManager(JCRRepositoryManager repositoryManager) {
        this.repositoryManager = repositoryManager;
        initBaseJCRStructure();
    }


    @Reference(name = "accessmanager")
    protected void setAccessService(AccessManager accessManager) {
        this.accessManager = accessManager;
    }

    public JCRGalleryImpl(String name) {
        super(name);
    }

           private void initBaseJCRStructure() {
           try {
               Session session = repositoryManager.getSession();
               Node baseRoot = session.getRootNode();
               Node rootNode;

               if (!baseRoot.hasNode("albums")) {
                   rootNode = baseRoot.addNode("albums");
               } else {
                   rootNode = baseRoot.getNode("albums");
               }

               if (!rootNode.hasNode("remote")) {
                  rootNode.addNode("remote");
               }

               if (!rootNode.hasNode("local")) {
                   rootNode.addNode("local");
               }

           } catch (RepositoryException e) {
               e.printStackTrace();
           } finally {

           }

       }

    @Init
    public void init() {
        logger.info("Initializing JCR Gallery");
        try {
            URL galleryURL = this.getClass().getClassLoader().getResource(name);
            if (galleryURL == null) {
                // Accomodate for J2EE classpath that starts in WEB-INF\classes
                galleryURL = this.getClass().getClassLoader().getResource("../../" + name);
            }

            if (galleryURL != null) {
                File album = new File(galleryURL.toURI());
                if (album.isDirectory() && album.exists()) {
                    File[] albums = album.listFiles();
                    for (File albumFile : albums) {
                        if (!albumFile.getName().startsWith(".")) {
                            if (albumFile.isDirectory() && albumFile.exists()) {
                                Album newAlbum = JCRAlbumImpl.createAlbum(repositoryManager, albumFile.getName());
                                newAlbum.setName(albumFile.getName());
                                ((JCRAlbumImpl) newAlbum).setGallery(name);
                                this.albums.add(newAlbum);
                            }
                        }
                    }
                }
            }
            getAlbumsFromJcrRepository();
        } catch (Exception e) {
            // FIXME: ignore for now
            e.printStackTrace();
        }

        initialized = true;
        Album[] albums = getAlbumsToUser(accessManager.getSecurityTokenFromUserId(SUPER_ADMIN));

        for (Album album : albums) {
            String[] pictures = album.getPictures();


            for (String picture : pictures) {
                imageAdded(album.getName(), new Image(picture, new GregorianCalendar().getTime(), null));
            }

        }

    }

    private void getAlbumsFromJcrRepository() {
        try {
            Session session = repositoryManager.getSession();
            Node rootNode = session.getRootNode().getNode("albums").getNode("local");
            NodeIterator albumNodes = rootNode.getNodes();
            while (albumNodes.hasNext()) {
                Node albumNode = albumNodes.nextNode();
                if (albumNode.getPath().equals("/jcr:system") || albumNode.getPath().equals("/"+USER_STORE)) {
                    continue;
                }
                String albumName = albumNode.getName();
                Album album = JCRAlbumImpl.createAlbum(repositoryManager, albumName);
                if (!albums.contains(album)) {
                    albums.add(album);
                }
            }
        } catch (RepositoryException e) {
            e.printStackTrace();
        } finally {
            //repositoryManager.releaseSession();
        }
    }

    public void addAlbum(String albumName) {
        try {
            Session session = repositoryManager.getSession();
            Node rootNode = session.getRootNode().getNode("albums").getNode("local");
            if (rootNode.hasNode(albumName)) {
                logger.info("This album is already in gallery");
                return;
            }
            // add album to the root
            rootNode.addNode(albumName);
            session.save();
            // add album to the list of albums
            Album album = JCRAlbumImpl.createAlbum(repositoryManager, albumName);
            if (!albums.contains(album)) {
                albums.add(album);
            }
        } catch (RepositoryException e) {
            e.printStackTrace();
        } finally {
            //repositoryManager.releaseSession();
        }
    }

    public boolean hasAlbum(String albumName) {
        try {
            Session session = repositoryManager.getSession();
            Node rootNode = session.getRootNode().getNode("albums").getNode("local");
            if (rootNode.hasNode(albumName)) {
                //   logger.info("This album is already in gallery");
                return true;
            }
        } catch (RepositoryException e) {
            e.printStackTrace();
        } finally {
            //repositoryManager.releaseSession();
        }
        return false;
    }

    public void deleteAlbum(String albumName) {
        try {
            Session session = repositoryManager.getSession();
            Node root = session.getRootNode().getNode("albums").getNode("local");;
            if (root.hasNode(albumName)) {
                Node albumNode = root.getNode(albumName);
                Album album = JCRAlbumImpl.createAlbum(repositoryManager, albumName);
                if (albums.contains(album)) {
                    albums.remove(album);
                }
                albumNode.remove();
                session.save();
                //init();
                logger.info("album " + albumName + " deleted");
            } else {
                logger.info("album " + albumName + " not found");
            }
        } catch (RepositoryException e) {
            e.printStackTrace();
        } finally {
            //repositoryManager.releaseSession();
        }

    }


    public String[] getAlbumPictures(String albumName) {
        return getAlbumPicturesToUser(albumName, accessManager.getSecurityTokenFromUserId(GUEST));
    }

    public String[] getAlbumPicturesToUser(String albumName, String securityToken) {
        String[] permissions = new String[]{ALBUM_VIEW_IMAGES_PERMISSION};
        if (accessManager.isPermitted(accessManager.getUserIdFromSecurityToken(securityToken), albumName, permissions)) {
            Album albumLookup = getAlbum(albumName);
            if (albumLookup != null) {
                return albumLookup.getPictures();
            } else {
                // FIXME: return proper not found exception
                return new String[]{};
            }
        } else {
            return new String[]{};
        }
    }

    public String getAlbumCover(String albumName) {
        return getAlbumCoverToUser(albumName, accessManager.getSecurityTokenFromUserId(GUEST));
    }

    public String getAlbumCoverToUser(String albumName, String securityToken) {
              String[] permissions = new String[]{ALBUM_VIEW_IMAGES_PERMISSION};
        if (accessManager.isPermitted(accessManager.getUserIdFromSecurityToken(securityToken), albumName, permissions)) {
            Album albumLookup = getAlbum(albumName);

            if (albumLookup != null) {
                String[] pictures = albumLookup.getPictures();
                // this check is to avoid Exception
                if (pictures.length > 0) {
                    return albumLookup.getPictures()[0];
                } else {
                    logger.info("No Album Cover Picture found for album:" + albumName);
                    return null;
                }
            } else {
                // FIXME: return proper not found exception
                return null;
            }
        } else {
            return null;
        }
    }

    public Album[] getAlbums() {
        return getAlbumsToUser(accessManager.getSecurityTokenFromUserId(GUEST));
    }

    public Album[] getAlbumsToUser(String securityToken) {
        if (!initialized) {
            init();
        }
        List<Album> userAlbums = new ArrayList<Album>();
        for (Album album : albums) {
            String[] permissions = new String[]{ALBUM_VIEW_IMAGES_PERMISSION};
            if (accessManager.isPermitted(accessManager.getUserIdFromSecurityToken(securityToken), album.getName(), permissions)) {
                userAlbums.add(album);
            }
        }
        Album[] albumArray = new Album[userAlbums.size()];
        userAlbums.toArray(albumArray);
        return albumArray;
    }

    /*this method is used to get the albums, that the user can add to various roles*/
    public Album[] getAlbumsToSetPermission(String securityToken) {
        if (!initialized) {
            init();
        }
        List<Album> userAlbums = new ArrayList<Album>();
        for (Album album : albums) {
            // only the super admin and the album owner is allowed
            if (accessManager.isPermitted(accessManager.getUserIdFromSecurityToken(securityToken), album.getName(), null)) {
                userAlbums.add(album);
            }
        }
        Album[] albumArray = new Album[userAlbums.size()];
        userAlbums.toArray(albumArray);
        return albumArray;
    }


}
