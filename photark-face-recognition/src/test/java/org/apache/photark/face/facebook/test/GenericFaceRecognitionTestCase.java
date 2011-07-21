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

package org.apache.photark.face.facebook.test;

import org.apache.photark.face.services.FaceRecognitionService;
import org.apache.tuscany.sca.node.Contribution;
import org.apache.tuscany.sca.node.ContributionLocationHelper;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.github.mhendred.face4j.DefaultFaceClient;
import com.github.mhendred.face4j.exception.FaceClientException;
import com.github.mhendred.face4j.exception.FaceServerException;
import com.github.mhendred.face4j.model.Face;
import com.github.mhendred.face4j.model.Photo;

@Ignore
public class GenericFaceRecognitionTestCase {

    private DefaultFaceClient defaultFaceClient;
    private String apiKey = "";
    private String apiSecret = "";
    private static Node node;


    @BeforeClass
    public static void BeforeClass() {
        try {
            String contribution = ContributionLocationHelper.getContributionLocation("face-recognition-test.composite");
            node =
                NodeFactory.newInstance().createNode("face-recognition-test.composite",
                                                     new Contribution("c1", contribution));
            node.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @AfterClass
    public static void AfterClass() {
        if(node != null) {
            node.stop();
        }
    }
    
    /*
     * This test case shows a generic face recognition app. This includes very
     * same methods we used in FaceRecognitionService SCA component This trains
     * two images of Jenifer Lopez and clearly identifies her among Shakira and
     * Marc Anthony
     */
    @Test
    public void testFaceRecognition() throws FaceServerException, FaceClientException {
        FaceRecognitionService defaultFaceClient =
            (FaceRecognitionService)node.getService(FaceRecognitionService.class, "FaceRecognitionService");
        Photo p1 =
            defaultFaceClient
                .detectFromUrls("https://lh3.googleusercontent.com/-z13PTuGA9mg/Thi6cKAiJVI/AAAAAAAAABs/lTEMvH9in1s/s128/Jennifer-Lopez0045.jpg").get(0);
        Photo p2 =
            defaultFaceClient
                .detectFromUrls("https://lh5.googleusercontent.com/-K6Jpe-1liwc/Thk0cEGT9cI/AAAAAAAAAB4/9a_84-oMqL8/s128/jennifer-lopez.jpg").get(0);

        defaultFaceClient.saveTags(p1.getFace().getTID(), "jenifer@photark.com", "jenifer");
        defaultFaceClient.saveTags(p2.getFace().getTID(), "jenifer@photark.com", "jenifer");

        defaultFaceClient.train("jenifer@photark.com");
        Photo p =
            defaultFaceClient
                .recognizeFromUrls("https://lh3.googleusercontent.com/-4I_Yn56XwAw/Thi6LIZSutI/AAAAAAAAABo/jOjx2cGgHao/s128/110306_latinjlo_400X400.jpg",
                                   "jenifer@photark.com").get(0);

        for (Face f : p.getFaces()) {
            if (f.getGuess() == null) {
                System.out.println(" > Cannot identify Jenifer Lopez :: " + f.toString());
            } else {
                System.out.println(" > Identified Jenifer Lopez :: " + f.toString());
            }
        }

    }


}
