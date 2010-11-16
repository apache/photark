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

package org.apache.photark.album.picasa;

import java.util.List;

import org.apache.photark.Image;
import org.apache.photark.subscription.SubscriptionConfig;
import org.junit.BeforeClass;
import org.junit.Test;

public class PicasaAlbumProviderTestCase {
    private static final String SUBSCRIPTION_URL = "http://picasaweb.google.com/data/feed/api/featured";
    private static PicasaPhotoStreamProvider picasaAlbumProvider;

    @BeforeClass
    public static void BeforeClass() {
        picasaAlbumProvider = new PicasaPhotoStreamProvider();
    }

    @Test
    public void testDiscoverAlbums() throws Exception {
        List<Image> images = picasaAlbumProvider.getImages(createAlbumConfig());

        for(Image image : images) {
            System.out.println(">>>>>>>>>>>>>");
            System.out.println(">> ID       : " + image.getId());
            System.out.println(">> Title    : " + image.getTitle());
            System.out.println(">> Location : " + image.getLocation());
        }
    }


    private static SubscriptionConfig createAlbumConfig() {
        SubscriptionConfig album = new SubscriptionConfig();
        album.setId("24662369");
        album.setName("NASA Goddard");
        album.setType(picasaAlbumProvider.getProviderType());
        album.setUrl(SUBSCRIPTION_URL);

        return album;
    }
}
