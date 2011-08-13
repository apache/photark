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
package org.apache.photark.face.services.applications.facebook;

import com.github.mhendred.face4j.DefaultFaceClient;
import com.github.mhendred.face4j.exception.FaceClientException;
import com.github.mhendred.face4j.exception.FaceServerException;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.types.User;
import org.apache.photark.face.services.FaceRecognitionService;
import org.apache.photark.face.services.beans.PhotArkFace;
import org.apache.photark.face.services.beans.PhotarkPhoto;
import org.apache.photark.security.authorization.services.AccessManager;
import org.apache.photark.security.authorization.services.SecurityServiceImpl;
import org.apache.photark.security.utils.Constants;
import org.apache.tuscany.sca.data.collection.Entry;
import org.oasisopen.sca.annotation.Init;
import org.oasisopen.sca.annotation.Reference;
import org.oasisopen.sca.annotation.Scope;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Scope("COMPOSITE")
public class FacebookFriendFinderImpl implements FacebookFriendFinder {

    private FaceRecognitionService faceRecognitionService;
    private final String adamFBUserId = "";
    private final String adamAccessToken = "";
    private AccessManager accessManager;

    @Init
    public void init() {
        System.out.println("# ... Initializing FacebookFriendFinder Service ...");
    }

    @Reference(name = "faceRecognitionService")
    protected void setFaceRecognitionService(FaceRecognitionService faceRecognitionService) {
        this.faceRecognitionService = faceRecognitionService;
    }


    @Reference(name = "accessmanager")
    protected void setAccessService(AccessManager accessManager) {
        this.accessManager = accessManager;
    }

    public Entry<String, String[]>[] check() {
        List<Entry<String, String[]>> detectedFriends = new ArrayList<Entry<String, String[]>>();
        detectedFriends.add(new Entry<String, String[]>("uid", new String[]{"AAAA", "BBBB"}));
        Entry<String, String[]>[] imageArray = new Entry[detectedFriends.size()];
        return detectedFriends.toArray(imageArray);

    }

    public Entry<String, String[]>[] getAllMyFBFriendsFromPictureLocal(String pathToFile, String photarkUid) {

        return processFBFriends(pathToFile, true, photarkUid);
    }

    public Entry<String, String[]>[] getAllMyFBFriendsFromPictureUrl(String fileUrl, String photarkUid) {

        return processFBFriends(fileUrl, false, photarkUid);
    }

    public void storeFacebookAccessToken(String photarkUid, String accessToken) {
        faceRecognitionService.setFacebookOauth2(getMyFacebookUserId(accessToken), accessToken);
        accessManager.setFacebookAccessTokenToUser(photarkUid, Constants.REGISTERED_USER_LIST, accessToken);
    }

    public void setFacebookAuth(String facebookId, String fbAccessToken) {
        faceRecognitionService.setFacebookOauth2(facebookId, fbAccessToken);
    }


    private Entry<String, String[]>[] processFBFriends(String fileLocation, boolean isLocal, String photarkUid) {

        PhotarkPhoto photo = null;
        List<Entry<String, String[]>> detectedFriends = new ArrayList<Entry<String, String[]>>();
        String accessToken = accessManager.getUserFacebookAccessToken(photarkUid, Constants.REGISTERED_USER_LIST);
        try {
            faceRecognitionService.setFacebookOauth2(getMyFacebookUserId(accessToken), accessToken);
            if (isLocal) {
                photo = faceRecognitionService.recognizeFromFile(new File(fileLocation), "friends@facebook.com");
            } else {
                photo = faceRecognitionService.recognizeFromUrl(fileLocation, "friends@facebook.com");
            }

//          output tuple = [name, link, gender, confidence,]

            for (PhotArkFace face : photo.getPhotArkFaces()) {

                String uid = "";
                String confidence = "";
                String gender = "";
                if (face.getGuess() != null) {
                    System.out.println("***Identified*** " + face.getGuess().toString());
                    uid = face.getGuess().getGuessID();
                    confidence = face.getGuess().getConfidence();
                    gender = face.getGender();
                    detectedFriends.add(new Entry<String, String[]>(uid, getFacebookUserDataTuple(accessToken, uid, gender, confidence)));
                } else {
                    System.out.println("??? Unidentified ..");
                }
            }

        } catch (FaceClientException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (FaceServerException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        //TODO If want can validate and remove duplicates from the "detectedFriends".

        Entry<String, String[]>[] dataArray = new Entry[detectedFriends.size()];
        return detectedFriends.toArray(dataArray);

    }

    private String getMyFacebookUserId(String accessToken) {
        FacebookClient facebookClient = new DefaultFacebookClient(accessToken);
        User user = facebookClient.fetchObject("me", User.class);
        return user.getId();
    }

    private String[] getFacebookUserDataTuple(String accessToken, String uid, String gender, String confidence) {
        String name = "";
        String link = "";
        String userId = uid.trim().split("@")[0];

        FacebookClient facebookClient = new DefaultFacebookClient(accessToken);
        User user = facebookClient.fetchObject(userId, User.class);
        name = user.getName();
        link = user.getLink();
        return new String[]{name, link, gender, confidence};
    }

}
