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

package org.apache.photark.services.gallery.jcr;

import java.io.File;
import java.net.URL;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.photark.services.album.Album;
import org.apache.photark.services.album.jcr.AlbumImpl;
import org.apache.photark.services.gallery.AbsGalleryImpl;
import org.apache.photark.services.gallery.Gallery;
import org.oasisopen.sca.annotation.Init;

public class GalleryImpl extends AbsGalleryImpl implements Gallery {

    public GalleryImpl() {

    }

    public GalleryImpl(String name) {
        super(name);
    }

    @Init
    public void init() {
        System.out.println(">>> Initializing JCR Gallery");
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
                                Album newAlbum = AlbumImpl.createAlbum(albumFile.getName());
                                newAlbum.setName(albumFile.getName());
                                ((AlbumImpl)newAlbum).setGallery(name);
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
    }

    private void getAlbumsFromJcrRepository() {
        try {
            Session session = JCRSession.getSession();
            Node rootNode = session.getRootNode();
            NodeIterator albumNodes = rootNode.getNodes();
            while (albumNodes.hasNext()) {
                Node albumNode = albumNodes.nextNode();
                if (albumNode.getPath().equals("/jcr:system")) {
                    continue;
                }
                String albumName = albumNode.getName();
                Album album = AlbumImpl.createAlbum(albumName);
                if (!albums.contains(album)) {
                    albums.add(album);
                }
            }
        } catch (RepositoryException e) {
            e.printStackTrace();
        }
    }

    public void addAlbum(String albumName) {
        Session session = JCRSession.getSession();
        try {
            Node rootNode = session.getRootNode();
            if (rootNode.hasNode(albumName)) {
                System.out.println("This album is already in gallery");
                return;
            }
            // add album to the root
            rootNode.addNode(albumName);
            session.save();
        } catch (RepositoryException e) {
            e.printStackTrace();
        }
    }
}
