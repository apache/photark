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
package org.apache.photark.jcr.album.subscription;

import java.util.logging.Logger;

import org.apache.photark.Image;
import org.apache.photark.album.flickr.FlickrPhotoStream;
import org.apache.photark.album.picasa.PicasaPhotoStream;
import org.apache.photark.jcr.JCRRepositoryManager;
import org.apache.photark.jcr.services.JCRAlbumImpl;
import org.apache.photark.services.gallery.RemoteAlbumSubscription;
import org.apache.photark.services.gallery.RemoteGallery;
import org.apache.tuscany.sca.data.collection.Entry;
import org.oasisopen.sca.annotation.Reference;
import org.oasisopen.sca.annotation.Remotable;
import org.oasisopen.sca.annotation.Scope;

@Remotable
@Scope("COMPOSITE")
public class AlbumSubscriptionManager implements RemoteAlbumSubscription {

    private static FlickrPhotoStream flickerPhotoStream;
    private static PicasaPhotoStream picasaPhotoStream;
    private JCRRepositoryManager repositoryManager;
    private static final Logger logger = Logger.getLogger(AlbumSubscriptionManager.class.getName());
    private RemoteGallery remoteGallery;


    public AlbumSubscriptionManager() {

    }

    @Reference(name = "repositoryManager")
    protected void setRepositoryManager(JCRRepositoryManager repositoryManager) {
        this.repositoryManager = repositoryManager;
    }

    @Reference(name = "jcrRemoteGallery")
    protected void setGallery(RemoteGallery remoteGallery) {
        this.remoteGallery = remoteGallery;
    }


    public Entry<String, Image>[] addRemoteAlbum(String url, String alb, String albDesc, String type,String uname,String passwd) {

        if ((type != null) && (type.equals("Subscribe-Picasa"))) {
            return getPicasaAlbumSubscriptionImages(url,alb,albDesc,uname,passwd);
        } else if ((type != null) && (type.equals("Subscribe-Flicker"))) {
            return getFlickerAlbumSubscriptionImages(url, alb, albDesc);
        } else {
            return null;
        }
    }


    private Entry<String, Image>[] getFlickerAlbumSubscriptionImages(String url, String albumName, String albumDesc) {

        remoteGallery.addAlbum(albumName, "remote");
        JCRAlbumImpl jcrAlbImpl = new JCRAlbumImpl(repositoryManager, albumName, "remote");
        jcrAlbImpl.setDescription(albumDesc);
        flickerPhotoStream = new FlickrPhotoStream(url);
        Entry<String, Image>[] images = flickerPhotoStream.getAll();

        for (Entry<String, Image> image : images) {
            Image i = image.getData();
            i.setType("remote");
            System.out.println(">> ID       : " + i.getId());
            System.out.println(">> Title    : " + i.getTitle());
            System.out.println(">> Location : " + i.getLocation());
            jcrAlbImpl.addPicture(i);

        }

        return images;
    }

    private Entry<String, Image>[] getPicasaAlbumSubscriptionImages(String url, String albumName, String albumDesc,String uname,String passwd) {

        remoteGallery.addAlbum(albumName, "remote");
        JCRAlbumImpl jcrAlbImpl = new JCRAlbumImpl(repositoryManager, albumName, "remote");
        jcrAlbImpl.setDescription(albumDesc);

        if((!uname.equals("")) && (!passwd.equals(""))) {
            picasaPhotoStream = new PicasaPhotoStream(url,uname,passwd);
        } else {
            picasaPhotoStream = new PicasaPhotoStream(url);
        }

        Entry<String, Image>[] images = picasaPhotoStream.getAll();
        for (Entry<String, Image> image : images) {
            Image i = image.getData();
            i.setType("remote");
            System.out.println(">> ID       : " + i.getId());
            System.out.println(">> Title    : " + i.getTitle());
            System.out.println(">> Location : " + i.getLocation());
            jcrAlbImpl.addPicture(i);

        }

        return images;
    }


}
