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

package org.apache.photark.album.flickr;

import org.apache.photark.Image;
import org.apache.tuscany.sca.data.collection.Entry;
import org.junit.BeforeClass;
import org.junit.Test;

public class FlickrSubscriptionTestCase {
    private static final String SUBSCRIPTION_URL = "http://api.flickr.com/services/feeds/photos_public.gne?id=24662369@N07&lang=en-us&format=atom";
    private static FlickrPhotoStream photoStream;

    @BeforeClass
    public static void BeforeClass() {
        photoStream = new FlickrPhotoStream(SUBSCRIPTION_URL);
    }

    @Test
    public void testDiscoverAlbums() {
        Entry<String, Image>[] images = photoStream.getAll();

        for(Entry<String, Image> image : images) {
            Image i = image.getData();

            System.out.println(">>>>>>>>>>>>>");
            System.out.println(">> ID       : " + i.getId());
            System.out.println(">> Title    : " + i.getTitle());
            System.out.println(">> Location : " + i.getLocation());
        }
    }
}
