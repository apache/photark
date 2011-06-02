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
package org.apache.photark.face.services;

import com.github.mhendred.face4j.DefaultFaceClient;
import com.github.mhendred.face4j.exception.FaceClientException;
import com.github.mhendred.face4j.exception.FaceServerException;
import com.github.mhendred.face4j.model.*;
import com.github.mhendred.face4j.response.GroupResponse;
import com.github.mhendred.face4j.response.LimitsResponse;
import com.github.mhendred.face4j.response.TrainResponse;
import com.github.mhendred.face4j.response.UsersResponse;

import java.io.File;
import java.util.List;

public interface FaceRecognitionService {


       public List<RemovedTag> removeTags(String tids) throws FaceClientException, FaceServerException;

       public TrainResponse train(String uids) throws FaceClientException, FaceServerException ;

       public void addTag(String url, float x, float y, int width, int height, String uid, String label, String taggerId) throws FaceClientException, FaceServerException ;

       public List<Photo> getTags(String urls, String uids, String order, String filter, boolean together, int limit) throws FaceClientException, FaceServerException ;

       public List<Photo> getTags(String pids, String urls, String uids, String order, String filter, boolean together, int limit) throws FaceClientException, FaceServerException ;

       public List<SavedTag> saveTags(String tids, String uid, String label) throws FaceClientException, FaceServerException;

       public Photo recognize(File imageFile, String uids) throws FaceClientException, FaceServerException;

       public List<Photo> recognize(String urls, String uids) throws FaceClientException, FaceServerException ;

       public Photo detect(File imageFile) throws FaceClientException, FaceServerException ;

       public List<Photo> detect(String urls) throws FaceClientException, FaceServerException;

       public List<UserStatus> status(String uids) throws FaceClientException, FaceServerException ;

       public List<Photo> facebookGet(String uids) throws FaceClientException, FaceServerException ;

       public GroupResponse group(String urls, String uids) throws FaceClientException, FaceServerException ;

       public GroupResponse group(File imageFile, String uids) throws FaceClientException, FaceServerException ;

       public UsersResponse users(String namespaces) throws FaceClientException, FaceServerException;

       public LimitsResponse getLimits() throws FaceClientException, FaceServerException ;

       public List<Namespace> namespaces() throws FaceClientException, FaceServerException ;

       public Namespace getNamespace(String namespace) throws FaceClientException, FaceServerException ;

       public void setFacebookOauth2(String fbUserId, String oauthToken) ;

       public void setTwitterOauth(String oauthUser, String oauthSecret, String oauthToken) ;

       public void clearFacebookCreds();

       public void clearTwitterCreds() ;

       public void setAggressive(boolean isAggressive) ;

       public boolean isAggressive() ;

       public void setFaceDotComAPIKeySecret(String apiKey,String apiSecret);

       public void createNewDefaultFaceClient(String apiKey,String apiSecret);

    }
