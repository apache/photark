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

import java.io.File;
import java.util.List;

import com.github.mhendred.face4j.model.*;
import org.apache.photark.face.services.beans.BeanGeneratorUtil;
import org.apache.photark.face.services.beans.PhotArkFace;
import org.apache.photark.face.services.beans.PhotarkPhoto;
import org.oasisopen.sca.annotation.Init;
import org.oasisopen.sca.annotation.Scope;
import org.oasisopen.sca.annotation.Service;

import com.github.mhendred.face4j.DefaultFaceClient;
import com.github.mhendred.face4j.exception.FaceClientException;
import com.github.mhendred.face4j.exception.FaceServerException;
import com.github.mhendred.face4j.response.GroupResponse;
import com.github.mhendred.face4j.response.LimitsResponse;
import com.github.mhendred.face4j.response.TrainResponse;
import com.github.mhendred.face4j.response.UsersResponse;
 
@Service(FaceRecognitionService.class)
@Scope("COMPOSITE")
public class FaceRecognitionServiceImpl implements FaceRecognitionService {

    private DefaultFaceClient defaultFaceClient;
    private String API_KEY = "";
    private String API_SECRET = "";


    @Init
    public void init() {
        defaultFaceClient = new DefaultFaceClient(API_KEY,API_SECRET);
    }

    /**
     * @see {@link FaceRecognitionService#removeTags(String)}
     */
    public List<RemovedTag> removeTags(String tids) throws FaceClientException, FaceServerException {
        return defaultFaceClient.removeTags(tids);
    }

    /**
     * @see {@link FaceRecognitionService#train(String)}
     */
    public void train(String uids) throws FaceClientException, FaceServerException {
       defaultFaceClient.train(uids);
    }

    /**
     * @see {@link FaceRecognitionService#addTag(String,float,float,int,int,String,String,String)}
     */
    public void addTag(String url, float x, float y, int width, int height, String uid, String label, String taggerId) throws FaceClientException, FaceServerException {
        defaultFaceClient.addTag(url, x, y, width, height, uid, label, taggerId);
    }

    /**
     * @see {@link FaceRecognitionService#getTags(String,String,String,String,boolean,int)}
     */
    public List<Photo> getTags(String urls, String uids, String order, String filter, boolean together, int limit) throws FaceClientException, FaceServerException {
        return defaultFaceClient.getTags(urls, uids, order, filter, together, limit);
    }

    /**
     * @see {@link FaceRecognitionService#getTagsWithPIDs(String,String,String,String,String,boolean,int)}
     */
    public List<Photo> getTagsWithPIDs(String pids, String urls, String uids, String order, String filter, boolean together, int limit) throws FaceClientException, FaceServerException {
        return defaultFaceClient.getTags(pids, urls, uids, order, filter, together, limit);
    }

    /**
     * @see {@link FaceRecognitionService#saveTags(String,String,String)}
     */
    public List<SavedTag> saveTags(String tids, String uid, String label) throws FaceClientException, FaceServerException {
        return defaultFaceClient.saveTags(tids, uid, label);
    }

    /**
     * @see {@link FaceRecognitionService#recognizeFromFile(File,String)}
     */
    public PhotarkPhoto recognizeFromFile(File imageFile, String uids) throws FaceClientException, FaceServerException {
       Photo photo = defaultFaceClient.recognize(imageFile, uids);
      return BeanGeneratorUtil.createPhotarkPhoto(photo);
    }

    /**
     * @see {@link FaceRecognitionService#recognizeFromUrl(String,String)}
     */
    public PhotarkPhoto recognizeFromUrl(String url, String uid) throws FaceClientException, FaceServerException {
        Photo p = defaultFaceClient.recognize(url, uid).get(0);
        return BeanGeneratorUtil.createPhotarkPhoto(p);

    }

    /**
     * @see {@link FaceRecognitionService#detectFromFile(File)}
     */
    public Photo detectFromFile(File imageFile) throws FaceClientException, FaceServerException {
        return defaultFaceClient.detect(imageFile);
    }

    /**
     * @see {@link FaceRecognitionService#detectFromUrls(String)}
     */
    public List<Photo> detectFromUrls(String urls) throws FaceClientException, FaceServerException {
        return defaultFaceClient.detect(urls);
    }

    /**
     * @see {@link FaceRecognitionService#status(String)}
     */
    public List<UserStatus> status(String uids) throws FaceClientException, FaceServerException {
        return defaultFaceClient.status(uids);
    }

    /**
     * @see {@link FaceRecognitionService#facebookGet(String)}
     */
    public List<Photo> facebookGet(String uids) throws FaceClientException, FaceServerException {
        return defaultFaceClient.facebookGet(uids);
    }

    /**
     * @see {@link FaceRecognitionService#groupFromUrls(String,String)}
     */
    public GroupResponse groupFromUrls(String urls, String uids) throws FaceClientException, FaceServerException {
        return defaultFaceClient.group(urls, uids);
    }

    /**
     * @see {@link FaceRecognitionService#groupFromFile(File,String)}
     */
    public GroupResponse groupFromFile(File imageFile, String uids) throws FaceClientException, FaceServerException {
        return defaultFaceClient.group(imageFile, uids);
    }

    /**
     * @see {@link FaceRecognitionService#users(String)}
     */
    public UsersResponse users(String namespaces) throws FaceClientException, FaceServerException {
        return defaultFaceClient.users(namespaces);
    }

    /**
     * @see {@link FaceRecognitionService#getLimits()}
     */
    public LimitsResponse getLimits() throws FaceClientException, FaceServerException {
        return defaultFaceClient.getLimits();
    }

    /**
     * @see {@link FaceRecognitionService#getAllNamespaces()}
     */
    public List<Namespace> getAllNamespaces() throws FaceClientException, FaceServerException {
        return defaultFaceClient.namespaces();
    }

    /**
     * @see {@link FaceRecognitionService#getNamespace(String)}
     */
    public Namespace getNamespace(String namespace) throws FaceClientException, FaceServerException {
        return defaultFaceClient.getNamespace(namespace);
    }

    /**
     * @see {@link FaceRecognitionService#setFacebookOauth2(String,String)}
     */
    public void setFacebookOauth2(String fbUserId, String oauthToken) {
        defaultFaceClient.setFacebookOauth2(fbUserId, oauthToken);
    }

    /**
     * @see {@link FaceRecognitionService#setTwitterOauth(String,String,String)}
     */
    public void setTwitterOauth(String oauthUser, String oauthSecret, String oauthToken) {
        defaultFaceClient.setTwitterOauth(oauthUser, oauthSecret, oauthToken);
    }

    /**
     * @see {@link FaceRecognitionService#clearFacebookCreds()}
     */
    public void clearFacebookCreds() {
        defaultFaceClient.clearFacebookCreds();
    }

    /**
     * @see {@link FaceRecognitionService#clearTwitterCreds()}
     */
    public void clearTwitterCreds() {
        defaultFaceClient.clearTwitterCreds();
    }

    /**
     * @see {@link FaceRecognitionService#setAggressive(boolean)}
     */
    public void setAggressive(boolean isAggressive) {
        defaultFaceClient.setAggressive(isAggressive);
    }

    /**
     * @see {@link FaceRecognitionService#isAggressive()}
     */
    public boolean isAggressive() {
        return defaultFaceClient.isAggressive();
    }

    /**
     * @see {@link FaceRecognitionService#createNewDefaultFaceClient(String,String)}
     */
    public void createNewDefaultFaceClient(String apiKey, String apiSecret) {
        defaultFaceClient = new DefaultFaceClient(apiKey, apiSecret);
    }


}
