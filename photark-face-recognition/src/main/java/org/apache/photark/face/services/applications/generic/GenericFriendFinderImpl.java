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

package org.apache.photark.face.services.applications.generic;

import com.github.mhendred.face4j.exception.FaceClientException;
import com.github.mhendred.face4j.exception.FaceServerException;
import com.github.mhendred.face4j.model.Face;
import com.github.mhendred.face4j.model.Photo;
import org.apache.photark.face.services.FaceRecognitionService;
import org.apache.photark.face.services.beans.PhotArkFace;
import org.apache.photark.face.services.beans.PhotarkPhoto;
import org.apache.photark.security.authorization.services.AccessManager;
import org.apache.tuscany.sca.data.collection.Entry;
import org.oasisopen.sca.annotation.Init;
import org.oasisopen.sca.annotation.Reference;
import org.oasisopen.sca.annotation.Scope;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Scope("COMPOSITE")
public class GenericFriendFinderImpl implements GenericFriendFinder {
    private FaceRecognitionService faceRecognitionService;
    private AccessManager accessManager;

    @Init
    public void init() {
        System.out.println("# ... Initializing GenericFriendFinder Service ...");
    }

    @Reference(name = "faceRecognitionService")
    protected void setFaceRecognitionService(FaceRecognitionService faceRecognitionService) {
        this.faceRecognitionService = faceRecognitionService;
    }


    @Reference(name = "accessmanager")
    protected void setAccessService(AccessManager accessManager) {
        this.accessManager = accessManager;
    }

    public Entry<String, String[]>[] getAllMyFriendsFromPictureLocal(String pathToFile, String uids, String photarkUid) {
        return processMyFriends(pathToFile, uids, true, photarkUid);
    }

    public Entry<String, String[]>[] getAllMyFriendsFromPictureUrl(String fileUrl, String uids, String photarkUid) {
        return processMyFriends(fileUrl, uids, false, photarkUid);
    }


    private Entry<String, String[]>[] processMyFriends(String pathToFile, String uid, boolean isLocal, String photarkUid) {
        PhotarkPhoto photo;
        List<Entry<String, String[]>> detectedFriends = new ArrayList<Entry<String, String[]>>();

        try {
            if (isLocal) {
                photo = faceRecognitionService.recognizeFromFile(new File(pathToFile), uid);
            } else {
                photo = faceRecognitionService.recognizeFromUrl(pathToFile, uid);

            }
            // user data tuple [uid,confidence,gender]
            for (PhotArkFace face : photo.getPhotArkFaces()) {
                String userName = "";
                String confidence = "";
                String gender = "";
                if (face.getGuess() != null) {
//                    System.out.println("***Identified*** " + face.getGuess().toString());
                    userName = face.getGuess().getGuessID();
                    confidence = face.getGuess().getConfidence();
                    gender = face.getGender();
                    System.out.println("***Identified*** " + userName+" : "+" : "+gender+" : "+confidence);
                    if(userName != null) {
                    detectedFriends.add(new Entry<String, String[]>(userName, new String[]{userName, gender, confidence}));
                    }
                    } else {
                    System.out.println("??? Unidentified ..");
                }
            }

        } catch (FaceClientException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (FaceServerException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        Entry<String, String[]>[] dataArray = new Entry[detectedFriends.size()];
        return detectedFriends.toArray(dataArray);
    }

    public void trainLocalImage(String imagePath, String userName, String label) {
        try {
            PhotarkPhoto photo = faceRecognitionService.detectFromFile(new File(imagePath));
            for (PhotArkFace face : photo.getPhotArkFaces()) {
                if (face.getGuess() != null) {
                    userName = face.getGuess().getGuessID();

                    faceRecognitionService.saveTags(face.getTid(), userName, label);
                } else {
                    faceRecognitionService.saveTags(face.getTid(), userName, label);
                }

                faceRecognitionService.train(userName);
            }
        } catch (FaceClientException e) {
            e.printStackTrace();
        } catch (FaceServerException e) {
            e.printStackTrace();
        }

    }

    public void trainUrlImage(String imagePath, String userName, String label) {
        try {
            PhotarkPhoto photo = faceRecognitionService.detectFromUrl(imagePath);
            for (PhotArkFace face : photo.getPhotArkFaces()) {
                if (face.getGuess() != null) {
                    userName = face.getGuess().getGuessID();

                    faceRecognitionService.saveTags(face.getTid(), userName, label);
                } else {
                    faceRecognitionService.saveTags(face.getTid(), userName, label);
                }

                faceRecognitionService.train(userName);
            }
        } catch (FaceClientException e) {
            e.printStackTrace();
        } catch (FaceServerException e) {
            e.printStackTrace();
        }

    }
}
