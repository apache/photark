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

package org.apache.photark.services.jcr;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.PostMethodWebRequest;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;

public class JCRBaseTest {
    protected static final String GALLERY_SERVICE_URL = "http://localhost:8085/gallery";
    protected static final String GALLERY_UPLOAD_SERVICE_URL = "http://localhost:8085/upload";
    protected static final String ALBUM_SERVICE_URL = GALLERY_SERVICE_URL + "/albums";

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

    protected void addAlbum() throws IOException, JSONException {
        String albumName = getNewAlbumName();
        addAlbum(albumName);
    }

    protected void addAlbum(String albumName) throws IOException, JSONException {
        JSONObject jsonAlbum = new JSONObject();
        jsonAlbum.put("name", albumName);
        jsonAlbum.put("description", "Some description to my album : " + albumName);

        WebConversation wc = new WebConversation();
        WebRequest request   = new PostMethodWebRequest(ALBUM_SERVICE_URL, new ByteArrayInputStream(jsonAlbum.toString().getBytes("UTF-8")),"application/json");
        request.setHeaderField("Content-Type", "application/json");
        WebResponse response = wc.getResource(request);

        Assert.assertEquals(204, response.getResponseCode());        
    }
    
    protected String[] readAlbums()  throws IOException, JSONException {
        WebConversation wc = new WebConversation();
        WebRequest request = new GetMethodWebRequest(ALBUM_SERVICE_URL);
        WebResponse response = wc.getResource(request);

        JSONObject jsonResponse = new JSONObject(response.getText());

        //for debug purposes
        System.out.println(">>>" + jsonResponse.toString());

        JSONArray albums = (org.json.JSONArray) jsonResponse.get("albums");
        List<String> albumNames = new ArrayList<String>();
        for(int pos=0; pos<albums.length(); pos++) {
            JSONObject album = (JSONObject) albums.get(pos);
            albumNames.add( album.getString("name"));
        }

        String[] albumNameArray = new String[albumNames.size()];
        albumNames.toArray(albumNameArray);
        Arrays.sort(albumNameArray, String.CASE_INSENSITIVE_ORDER);

        return albumNameArray;
    }

    protected String getNewAlbumName() throws IOException, JSONException {
        String[] albums = readAlbums();
        String album = null;
        if (albums.length == 0) {
            album = "album-0";
        } else {
            album = albums[albums.length -1 ];
            String[] tokens = album.split("-");
            album = tokens[0] + "-" + (Integer.parseInt(tokens[1]) + 1);
        }
        return album;
    }

    protected String getLastAlbumName() throws IOException, JSONException {
        String[] albums = readAlbums();
        String album = albums[albums.length -1];

        return album;
    }

}
