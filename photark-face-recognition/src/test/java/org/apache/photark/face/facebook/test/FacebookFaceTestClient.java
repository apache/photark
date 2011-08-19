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

import com.github.mhendred.face4j.DefaultFaceClient;
import com.github.mhendred.face4j.exception.FaceClientException;
import com.github.mhendred.face4j.exception.FaceServerException;
import org.apache.photark.face.services.FaceRecognitionService;
import org.apache.photark.face.services.beans.PhotArkFace;
import org.apache.photark.face.services.beans.PhotarkPhoto;
import org.apache.tuscany.sca.node.Contribution;
import org.apache.tuscany.sca.node.ContributionLocationHelper;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class FacebookFaceTestClient {

    private DefaultFaceClient defaultFaceClient;
        private String apiKey = "";
        private String apiSecret = "";
        private static Node node;
        private final String adamFBUserId = "100002467744208";
        private final String adamAccessToken = "";
        private String imageUrl="https://lh4.googleusercontent.com/-rb_m-GQcL00/Ti8sqThvrDI/AAAAAAAAAMY/kUBurbFKJ0A/s640/friends_2.jpg";
        private String myFBFriend = "1271543184@facebook.com";


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

            defaultFaceClient.setFacebookOauth2(adamFBUserId,adamAccessToken);

            defaultFaceClient.train(myFBFriend);
            PhotarkPhoto p =
                defaultFaceClient
                    .recognizeFromUrl(imageUrl,
                                       "friends@photark.com");

            for (PhotArkFace f : p.getPhotArkFaces()) {
                if (f.getGuess() == null) {
                    System.out.println(" > Cannot identify " + f.toString());
                } else {
                    System.out.println(" > Identified " + f.getGuess().getGuessID());
                }
            }

        }


}
