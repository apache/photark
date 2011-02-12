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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.photark.jcr.JCRRepositoryManager;
import org.apache.photark.services.album.Album;
import org.apache.photark.services.album.AlbumAgregator;

public class JCRAlbumAggregator extends AlbumAgregator {

    private JCRRepositoryManager repositoryManager;
    private Map<String, Album> localAlbums = new HashMap<String, Album>();
    private Map<String, Album> remoteAlbums = new HashMap<String, Album>();
    private boolean isUpdated = false;
    private List<Album> localAlbumList = new ArrayList<Album>();
    private List<Album> remoteAlbumList = new ArrayList<Album>();
    private static final Logger logger = Logger.getLogger(JCRAlbumAggregator.class.getName());


    public JCRAlbumAggregator(JCRRepositoryManager repositoryManager) {

        this.repositoryManager = repositoryManager;
        initJCRAlbumAggregator();
    }

    private void initJCRAlbumAggregator() {  // should init each time gallery loads
        loadLocalAlbums();
        loadRemoteAlbums();
    }

    public void put(Album album, String type) {

    String albumName = album.getName();

       if((type != null) && (type.equals("local"))) {
          if(!localAlbums.containsKey(albumName)) {
            localAlbums.put(albumName,album);
            localAlbumList.add(album);
          }

       } else if ((type != null) && ((type.equals("remote")))) {
           if(!remoteAlbums.containsKey(albumName)) {
            remoteAlbums.put(albumName,album);
            remoteAlbumList.add(album);
          }
       }

    }

    public void remove(Album album,String type) {

          String albumName = album.getName();

       if((type != null) && (type.equals("local"))) {
          if(localAlbums.containsKey(albumName)) {
            localAlbums.remove(album);
            localAlbumList.remove(album);
          }

       } else if ((type != null) && (type.equals("remote"))) {
           if(remoteAlbums.containsKey(albumName)) {
            remoteAlbums.remove(album);
            remoteAlbumList.remove(album);
          }
       }


    }

    public void refreshAlbums() {
       initJCRAlbumAggregator();
    }

    public Map<String, Album> getAllLocalAlbums() {

        return localAlbums;
    }

    public Map<String, Album> getAllRemoteAlbums() {

        return remoteAlbums;
    }

    public List<Album> getLocalAlbumsAsList() {

        return localAlbumList;
    }

    public List<Album> getRemoteAlbumsAsList() {

        return remoteAlbumList;
    }

    public List<Album> getAllAlbumList() {
        List<Album> list = new ArrayList<Album>();
        list.addAll(localAlbumList);
        list.addAll(remoteAlbumList);
        return list;
    }

    private void loadLocalAlbums() {

        try {

            Session session = repositoryManager.getSession();
            Node rootNode = session.getRootNode().getNode("albums");

            NodeIterator nit = rootNode.getNode("local").getNodes();

            while (nit.hasNext()) {

                Node albumNode = nit.nextNode();
                String albumName = albumNode.getName();

                Album album = new JCRAlbumImpl(repositoryManager, albumName, "local");
                if (!localAlbums.containsKey(albumName)) {
                    localAlbums.put(albumName, album);
                    localAlbumList.add(album);
                }
            }

        } catch (RepositoryException e) {

        }
    }

    private void loadRemoteAlbums() {
        try {

            Session session = repositoryManager.getSession();
            Node rootNode = session.getRootNode().getNode("albums");

            NodeIterator nit = rootNode.getNode("remote").getNodes();

            while (nit.hasNext()) {
                    Node albumNode = nit.nextNode();
                    String albumName = albumNode.getName();
                    Album album = new JCRAlbumImpl(repositoryManager, albumName, "remote");
                    if (!remoteAlbums.containsKey(albumName)) {
                        remoteAlbums.put(albumName, album);
                        remoteAlbumList.add(album);
                    }

            }

        } catch (RepositoryException e) {

        }

    }

}
