package org.apache.photark.services.jcr;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import junit.framework.Assert;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import org.apache.tuscany.sca.node.Contribution;
import org.apache.tuscany.sca.node.ContributionLocationHelper;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;
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

	private static Node node;

	@BeforeClass
	public static void BeforeClass() {
		try {
			String contribution = ContributionLocationHelper
					.getContributionLocation("gallery.composite");
			node = NodeFactory.newInstance().createNode("gallery.composite",
					new Contribution("gallery", contribution));
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
	        int albumSize = albums.size();
	        
	        //for debug purposes
	        //System.out.println(">>>" + jsonResponse.toString());    
	        
	        addAlbum();

	        albums = readAlbums();

	        //for debug purposes
	        //System.out.println(">>>" + jsonResponse.toString());    
	        
	        Assert.assertEquals(albumSize + 1, albums.size());
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
	        JSONObject jsonResponse = new JSONObject();
	        //TODO initialize josonResponse
	        JSONArray albums = (JSONArray) jsonResponse.get("albums");
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
	        JSONObject album = (JSONObject) albums.get(albums.size() -1);
	        String albumName = album.getString("name");
	        String[] tokens = albumName.split("-");
	        albumName = tokens[0] + "-" + (Integer.parseInt(tokens[1]) + 1);
	        
	        return albumName;
	    }
	    
	    private String getLastAlbumName() throws IOException, JSONException {
	        JSONArray albums = readAlbums();
	        JSONObject album = (JSONObject) albums.get(albums.size() -1);
	        String albumName = album.getString("name");
	        
	        return albumName;
	    }
}
