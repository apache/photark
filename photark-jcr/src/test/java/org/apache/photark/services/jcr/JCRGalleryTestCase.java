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
import org.junit.Test;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.PostMethodWebRequest;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;

public class JCRGalleryTestCase {
    private static final String GALLERY_SERVICE_URL = "http://localhost:8085/gallery";
    private static final String ALBUM_SERVICE_URL = GALLERY_SERVICE_URL + "/albums";

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
        String[] albums = readAlbums();

        //for debug purposes
        //System.out.println(">>>" + jsonResponse.toString());

        Assert.assertNotNull(albums);
    }

    @Test
    public void testAddAlbums() throws IOException, JSONException {
        String[] albums = readAlbums();
        int albumSize = albums.length;

        //for debug purposes
        //System.out.println(">>>" + jsonResponse.toString());

        addAlbum();

        albums = readAlbums();

        //for debug purposes
        //System.out.println(">>>" + jsonResponse.toString());

        Assert.assertEquals(albumSize + 1, albums.length);
    }

    @Test
    public void testRemoveAlbums() throws IOException, JSONException {
        WebConversation wc = new WebConversation();
        WebRequest request = new GetMethodWebRequest(ALBUM_SERVICE_URL + "/" + getLastAlbumName());
        ((GetMethodWebRequest) request).setMethod("DELETE");
        WebResponse response = wc.getResource(request);

        Assert.assertEquals(200, response.getResponseCode());
    }

    private void addAlbum() throws IOException, JSONException {
        JSONObject jsonAlbum = new JSONObject();
        String albumName = getNewAlbumName();
        jsonAlbum.put("name", albumName);
        jsonAlbum.put("description", "Some description to my album : " + albumName);

        WebConversation wc = new WebConversation();
        WebRequest request   = new PostMethodWebRequest(ALBUM_SERVICE_URL, new ByteArrayInputStream(jsonAlbum.toString().getBytes("UTF-8")),"application/json");
        request.setHeaderField("Content-Type", "application/json");
        WebResponse response = wc.getResource(request);

        Assert.assertEquals(204, response.getResponseCode());
    }

    private String[] readAlbums()  throws IOException, JSONException {
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

    private String getNewAlbumName() throws IOException, JSONException {
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

    private String getLastAlbumName() throws IOException, JSONException {
        String[] albums = readAlbums();
        String album = albums[albums.length -1];

        return album;
    }

}
