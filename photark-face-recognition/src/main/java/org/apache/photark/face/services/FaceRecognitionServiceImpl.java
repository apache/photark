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
import org.apache.photark.jcr.JCRRepositoryManager;
import org.oasisopen.sca.annotation.Init;
import org.oasisopen.sca.annotation.Reference;
import org.oasisopen.sca.annotation.Remotable;
import org.oasisopen.sca.annotation.Scope;

import java.io.File;
import java.util.List;

@Remotable
@Scope("COMPOSITE")
public class FaceRecognitionServiceImpl implements FaceRecognitionService {

    private DefaultFaceClient defaultFaceClient;
    private String apiKey = "";
    private String apiSecret = "";

    @Init
    public void init() {
        defaultFaceClient = new DefaultFaceClient(apiKey, apiSecret);

    }

    public List<RemovedTag> removeTags(String tids) throws FaceClientException, FaceServerException {
        return defaultFaceClient.removeTags(tids);
    }

    public TrainResponse train(String uids) throws FaceClientException, FaceServerException {
        return defaultFaceClient.train(uids);
    }

    public void addTag(String url, float x, float y, int width, int height, String uid, String label, String taggerId) throws FaceClientException, FaceServerException {
        defaultFaceClient.addTag(url, x, y, width, height, uid, label, taggerId);
    }

    public List<Photo> getTags(String urls, String uids, String order, String filter, boolean together, int limit) throws FaceClientException, FaceServerException {
        return defaultFaceClient.getTags(urls, uids, order, filter, together, limit);
    }

    public List<Photo> getTags(String pids, String urls, String uids, String order, String filter, boolean together, int limit) throws FaceClientException, FaceServerException {
        return defaultFaceClient.getTags(pids, urls, uids, order, filter, together, limit);
    }

    public List<SavedTag> saveTags(String tids, String uid, String label) throws FaceClientException, FaceServerException {
        return defaultFaceClient.saveTags(tids, uid, label);
    }

    public Photo recognize(File imageFile, String uids) throws FaceClientException, FaceServerException {
        return defaultFaceClient.recognize(imageFile, uids);
    }

    public List<Photo> recognize(String urls, String uids) throws FaceClientException, FaceServerException {
        return defaultFaceClient.recognize(urls, uids);
    }

    public Photo detect(File imageFile) throws FaceClientException, FaceServerException {
        return defaultFaceClient.detect(imageFile);
    }

    public List<Photo> detect(String urls) throws FaceClientException, FaceServerException {
        return defaultFaceClient.detect(urls);
    }

    public List<UserStatus> status(String uids) throws FaceClientException, FaceServerException {
        return defaultFaceClient.status(uids);
    }

    public List<Photo> facebookGet(String uids) throws FaceClientException, FaceServerException {
        return defaultFaceClient.facebookGet(uids);
    }

    public GroupResponse group(String urls, String uids) throws FaceClientException, FaceServerException {
        return defaultFaceClient.group(urls, uids);
    }

    public GroupResponse group(File imageFile, String uids) throws FaceClientException, FaceServerException {
        return defaultFaceClient.group(imageFile, uids);
    }

    public UsersResponse users(String namespaces) throws FaceClientException, FaceServerException {
        return defaultFaceClient.users(namespaces);
    }

    public LimitsResponse getLimits() throws FaceClientException, FaceServerException {
        return defaultFaceClient.getLimits();
    }

    public List<Namespace> namespaces() throws FaceClientException, FaceServerException {
        return defaultFaceClient.namespaces();
    }

    public Namespace getNamespace(String namespace) throws FaceClientException, FaceServerException {
        return defaultFaceClient.getNamespace(namespace);
    }

    public void setFacebookOauth2(String fbUserId, String oauthToken) {
        defaultFaceClient.setFacebookOauth2(fbUserId, oauthToken);
    }

    public void setTwitterOauth(String oauthUser, String oauthSecret, String oauthToken) {
        defaultFaceClient.setTwitterOauth(oauthUser, oauthSecret, oauthToken);
    }

    public void clearFacebookCreds() {
        defaultFaceClient.clearFacebookCreds();
    }

    public void clearTwitterCreds() {
        defaultFaceClient.clearTwitterCreds();
    }

    public void setAggressive(boolean isAggressive) {
        defaultFaceClient.setAggressive(isAggressive);
    }

    public boolean isAggressive() {
        return defaultFaceClient.isAggressive();
    }

    public void setFaceDotComAPIKeySecret(String apiKey, String apiSecret) {
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
    }

    public void createNewDefaultFaceClient(String apiKey, String apiSecret) {
        defaultFaceClient = new DefaultFaceClient(apiKey,apiSecret);
    }

}
