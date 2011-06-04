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

package org.apache.photark.album.filesystem;

import java.io.File;
import java.net.URL;
import java.util.List;

import org.apache.photark.Image;
import org.apache.photark.providers.filesystem.FileSystemPhotoStreamProvider;
import org.apache.photark.subscription.SubscriptionConfig;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Verify various operations for the FileSystem Gallery
 *
 * @version $Rev$ $Date$
 */
public class FileSystemProviderTestCase {
    private static final String GALLERY_ROOT = "gallery-root";
    private static final String ALBUM_1 = GALLERY_ROOT + File.separatorChar + "album-1";
    private static final String ALBUM_2 = GALLERY_ROOT + File.separatorChar + "album-2";

    private static FileSystemPhotoStreamProvider fileSystemPhotoStreamProvider;

    @BeforeClass
    public static void BeforeClass() {
        fileSystemPhotoStreamProvider = new FileSystemPhotoStreamProvider();
    }


    @Test
    public void testDiscoverAlbums() throws Exception {
        List<Image> images = fileSystemPhotoStreamProvider.getImages(createSubcriptionConfig(ALBUM_1));

        for(Image image : images) {
            System.out.println(">>>>>>>>>>>>>");
            System.out.println(">> ID       : " + image.getId());
            System.out.println(">> Title    : " + image.getTitle());
            System.out.println(">> Location : " + image.getLocation());
        }
    }

    private static SubscriptionConfig createSubcriptionConfig(String albumName) {
        URL albumURL = FileSystemProviderTestCase.class.getClassLoader().getResource(albumName);

        SubscriptionConfig album = new SubscriptionConfig();
        album.setId("24662369");
        album.setName("Album 1");
        album.setType(fileSystemPhotoStreamProvider.getProviderType());
        album.setUrl(albumURL.toString());

        return album;
    }


}
