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

import java.io.ByteArrayInputStream;
import java.io.IOException;

import junit.framework.Assert;

import org.apache.tuscany.sca.node.Contribution;
import org.apache.tuscany.sca.node.ContributionLocationHelper;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.PostMethodWebRequest;
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
        JSONArray albums = readAlbums();

        //for debug purposes
        //System.out.println(">>>" + jsonResponse.toString());    
        
        Assert.assertNotNull(albums);
        
    }

    @Test
    public void testAddAlbums() throws IOException, JSONException {
        JSONArray albums = readAlbums();
        int albumSize = albums.length();
        
        //for debug purposes
        //System.out.println(">>>" + jsonResponse.toString());    
        
        addAlbum();

        albums = readAlbums();

        //for debug purposes
        //System.out.println(">>>" + jsonResponse.toString());    
        
        Assert.assertEquals(albumSize + 1, albums.length());
    }
    
    @Test
    public void testRemoveAlbums() throws IOException, JSONException {
        WebConversation wc = new WebConversation();
        WebRequest request = new GetMethodWebRequest(GALLERY_SERVICE_URL + "/" + getLastAlbumName());
        ((GetMethodWebRequest) request).setMethod("DELETE");
        WebResponse response = wc.getResource(request);
        
        Assert.assertEquals(200, response.getResponseCode());
    }


    private JSONArray readAlbums()  throws IOException, JSONException {
        WebConversation wc = new WebConversation();
        WebRequest request = new GetMethodWebRequest(GALLERY_SERVICE_URL);
        WebResponse response = wc.getResource(request);

        JSONObject jsonResponse = new JSONObject(response.getText());
        JSONArray albums = (org.json.JSONArray) jsonResponse.get("albums");

        //for debug purposes
        //System.out.println(">>>" + jsonResponse.toString());    
        
        return albums;
    }
    
    private void addAlbum() throws IOException, JSONException {
        JSONObject jsonAlbum = new JSONObject();
        jsonAlbum.put("name", getNewAlbumName());
        
        WebConversation wc = new WebConversation();
        WebRequest request   = new PostMethodWebRequest(GALLERY_SERVICE_URL, new ByteArrayInputStream(jsonAlbum.toString().getBytes("UTF-8")),"application/json");
        request.setHeaderField("Content-Type", "application/json");
        WebResponse response = wc.getResource(request);
        
        Assert.assertEquals(204, response.getResponseCode());
    }
    
    private String getNewAlbumName() throws IOException, JSONException {
        JSONArray albums = readAlbums();
        JSONObject album = (JSONObject) albums.get(albums.length() -1);
        String albumName = album.getString("name");
        String[] tokens = albumName.split("-");
        albumName = tokens[0] + "-" + (Integer.parseInt(tokens[1]) + 1);
        
        return albumName;
    }
    
    private String getLastAlbumName() throws IOException, JSONException {
        JSONArray albums = readAlbums();
        JSONObject album = (JSONObject) albums.get(albums.length() -1);
        String albumName = album.getString("name");
        
        return albumName;
    }
}
