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
import java.util.GregorianCalendar;
import java.util.logging.Logger;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.photark.Image;
import org.apache.photark.jcr.JCRRepositoryManager;
import org.apache.photark.services.album.Album;
import org.apache.photark.services.gallery.BaseGalleryImpl;
import org.apache.photark.services.gallery.Gallery;
import org.oasisopen.sca.annotation.Init;
import org.oasisopen.sca.annotation.Reference;
import org.oasisopen.sca.annotation.Scope;

@Scope("COMPOSITE")
public class JCRGalleryImpl extends BaseGalleryImpl implements Gallery {
    private static final Logger logger = Logger.getLogger(JCRGalleryImpl.class.getName());
    
    private JCRRepositoryManager repositoryManager;

    public JCRGalleryImpl() {

    }
    
    @Reference(name="repositoryManager")
    protected void setRepositoryManager(JCRRepositoryManager repositoryManager) {
        this.repositoryManager = repositoryManager;
    }

    public JCRGalleryImpl(String name) {
        super(name);
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
                                ((JCRAlbumImpl)newAlbum).setGallery(name);
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
        Album[] albums = getAlbums();
        
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
            Node rootNode = session.getRootNode();
            NodeIterator albumNodes = rootNode.getNodes();
            while (albumNodes.hasNext()) {
                Node albumNode = albumNodes.nextNode();
                if (albumNode.getPath().equals("/jcr:system")||albumNode.getPath().equals("/userStore")) {
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
            Node rootNode = session.getRootNode();
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

	public void deleteAlbum(String albumName) {
		try {
			Session session = repositoryManager.getSession();
			Node root = session.getRootNode();
			if(root.hasNode(albumName)){
				Node albumNode = root.getNode(albumName);
				 Album album = JCRAlbumImpl.createAlbum(repositoryManager, albumName);
				if (albums.contains(album)) {
                    albums.remove(album);
                }
				albumNode.remove();
				session.save();
				//init();
				logger.info("album " + albumName + " deleted");
			}else{
				logger.info("album " + albumName + " not found");
			}    	            
		} catch (RepositoryException e) {
			e.printStackTrace();
		}  finally {
			//repositoryManager.releaseSession();
		}
		
	}
}
