package org.apache.photark.services.jcr;

import java.io.IOException;

import junit.framework.Assert;

import org.json.JSONException;
import org.junit.Test;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;

public class JCRGalleryTestCase extends JCRBaseTest {

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

}
