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

import java.io.File;
import java.io.IOException;
import java.net.URL;

import junit.framework.Assert;

import org.json.JSONException;
import org.junit.Ignore;
import org.junit.Test;
import org.xml.sax.SAXException;

import com.meterware.httpunit.PostMethodWebRequest;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebResponse;
import com.meterware.httpunit.protocol.UploadFileSpec;

@Ignore
public class JCRImageServiceTestCase extends JCRBaseTest {

    @Test
    //@Ignore
    public void testUploadImage() throws IOException, JSONException, SAXException {
        //addAlbum("uploads");
        
        WebConversation wc = new WebConversation();
        //PostMethodWebRequest request = new PostMethodWebRequest(ALBUM_SERVICE_URL + "/" + getLastAlbumName() + "/images", true);
        PostMethodWebRequest request = new PostMethodWebRequest(GALLERY_UPLOAD_SERVICE_URL+ "/albums/uploads/images", true);

        
        URL imageURL = JCRImageServiceTestCase.class.getResource("/gallery-home/album-1/IMG_0735.jpg");
        File image = new File(imageURL.getPath());
        final UploadFileSpec ufs[] = {new UploadFileSpec(image,"image/jpeg")};
        request.setParameter("image", ufs);
        request.selectFile("image",image);

        WebResponse response = wc.getResponse(request);
        
        System.out.println(">>" + response.getResponseCode());
        
        Assert.assertTrue(response.getResponseCode() == 204);
    }
}
