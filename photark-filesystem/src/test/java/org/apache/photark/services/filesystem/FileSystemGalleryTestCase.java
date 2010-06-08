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

package org.apache.photark.services.filesystem;

import java.io.IOException;

import junit.framework.Assert;

import org.apache.tuscany.sca.node.Contribution;
import org.apache.tuscany.sca.node.ContributionLocationHelper;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;

public class FileSystemGalleryTestCase {
    private static final String GALLERY_SERVICE_URL = "http://localhost:8085/gallery";
    
    private static Node node;

    @BeforeClass
    public static void BeforeClass() {
        try {
            String contribution = ContributionLocationHelper.getContributionLocation("gallery.composite");
            node = NodeFactory.newInstance().createNode("gallery.composite", new Contribution("gallery", contribution));
            node.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @AfterClass
    public static void AfterClass() {
        node.stop();
    }
    
    @Test
    public void testGetAlbums() throws IOException, JSONException {
        WebConversation wc = new WebConversation();
        WebRequest request = new GetMethodWebRequest(GALLERY_SERVICE_URL);
        WebResponse response = wc.getResource(request);

        JSONObject jsonResponde = new JSONObject(response.getText());
        
        Assert.assertNotNull(jsonResponde);
        
        //for debug purposes
        System.out.println(">>>" + jsonResponde.toString());    
    }
    
    
    /*
    @Test
    public void testDiscoverAlbums() {
        AlbumList albumList = gallery.getAlbums();

        Assert.assertEquals(2, albumList.getAlbums().size());
        Assert.assertTrue(albumList.getAlbums().get(0).getName().startsWith("album-"));
        Assert.assertTrue(albumList.getAlbums().get(0).getName().startsWith("album-"));
    }*/
}
