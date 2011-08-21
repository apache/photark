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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.mhendred.face4j.model.*;
import org.apache.photark.face.services.beans.BeanGeneratorUtil;
import org.apache.photark.face.services.beans.PhotArkFace;
import org.apache.photark.face.services.beans.PhotarkPhoto;
import org.apache.photark.jcr.JCRRepositoryManager;
import org.apache.photark.jcr.util.JCREncoder;
import org.apache.photark.security.utils.Constants;
import org.oasisopen.sca.annotation.Init;
import org.oasisopen.sca.annotation.Reference;
import org.oasisopen.sca.annotation.Scope;
import org.oasisopen.sca.annotation.Service;

import com.github.mhendred.face4j.DefaultFaceClient;
import com.github.mhendred.face4j.exception.FaceClientException;
import com.github.mhendred.face4j.exception.FaceServerException;
import com.github.mhendred.face4j.response.GroupResponse;
import com.github.mhendred.face4j.response.LimitsResponse;
import com.github.mhendred.face4j.response.UsersResponse;

import javax.jcr.*;

@Service(FaceRecognitionService.class)
@Scope("COMPOSITE")
public class FaceRecognitionServiceImpl implements FaceRecognitionService {

    private DefaultFaceClient defaultFaceClient;
    private final String API_KEY = "";
    private final String API_SECRET = "";
    private JCRRepositoryManager repositoryManager;
    private Map<String, String> friendMap;

    @Init
    public void init() {
        System.out.println("# ... Initializing FaceRecognitionService ...");
        defaultFaceClient = new DefaultFaceClient(API_KEY, API_SECRET);
        friendMap = new HashMap<String, String>();
        loadFriendMap();
    }

    private void loadFriendMap() {

        Session session;
        try {
            session = repositoryManager.getSession();
            Node listNodeUsers = (Node) session.getItem("/" + Constants.USER_STORE + "/" + Constants.USER_LISTS + "/" + Constants.REGISTERED_USER_LIST);

            NodeIterator nit = listNodeUsers.getNodes();
            while (nit.hasNext()) {
                Node propertyNode;
                Node userNode = nit.nextNode();
                if (userNode.hasNode("face.friends.names")) {
                    propertyNode = userNode.getNode("face.friends.names");
                } else {
                    propertyNode = userNode.addNode("face.friends.names");
                }

                PropertyIterator propertyIterator = propertyNode.getProperties();
                while (propertyIterator.hasNext()) {
                    Property property = propertyIterator.nextProperty();
                    if ((property != null) && (property.getValue() != null)) {
                        String userName = property.getValue().getString();
                        friendMap.put(userName, "True");
                    }
                }

            }

        } catch (LoginException e) {
            e.printStackTrace();
        } catch (RepositoryException e) {
            e.printStackTrace();
        }
    }


    @Reference(name = "repositoryManager")
    protected void setRepositoryManager(JCRRepositoryManager repositoryManager) {
        this.repositoryManager = repositoryManager;
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
    public void train(String uids) {
        try {
            defaultFaceClient.train(uids);
        } catch (FaceClientException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (FaceServerException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
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
    public void saveTags(String tids, String uid, String label) {
        try {
            defaultFaceClient.saveTags(tids, uid, label);
        } catch (FaceClientException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (FaceServerException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    /**
     * @see {@link FaceRecognitionService#recognizeFromFile(File,String)}
     */
    public PhotarkPhoto recognizeFromFile(File imageFile, String uids) {
        Photo photo = null;
        try {
            photo = defaultFaceClient.recognize(imageFile, uids);
        } catch (FaceClientException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (FaceServerException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return BeanGeneratorUtil.createPhotarkPhoto(photo);
    }

    /**
     * @see {@link FaceRecognitionService#recognizeFromUrl(String,String)}
     */
    public PhotarkPhoto recognizeFromUrl(String url, String uid) {
        Photo p = null;
        try {
            p = defaultFaceClient.recognize(url, uid).get(0);
        } catch (FaceClientException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (FaceServerException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return BeanGeneratorUtil.createPhotarkPhoto(p);

    }

    /**
     * @see {@link FaceRecognitionService#detectFromFile(File)}
     */
    public PhotarkPhoto detectFromFile(File imageFile) {
        Photo photo = null;
        try {
            photo = defaultFaceClient.detect(imageFile);
        } catch (FaceClientException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (FaceServerException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return BeanGeneratorUtil.createPhotarkPhoto(photo);
    }

    /**
     * @see {@link FaceRecognitionService#detectFromUrl(String)}
     */
    public PhotarkPhoto detectFromUrl(String url) {
        Photo photo = null;
        try {
            photo = defaultFaceClient.detect(url).get(0);
        } catch (FaceClientException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (FaceServerException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return BeanGeneratorUtil.createPhotarkPhoto(photo);
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


    public boolean isUserAllowedToTrain(String photarkUid, String userName) {
        boolean isUserAllowedToTrain = false;

        if ((isUserNameContainsInPrivateNameSpace(photarkUid, userName))
                || ((!isUserNameContainsInGlobalNameSpace(userName))) && (!isUserNameContainsInPrivateNameSpace(photarkUid, userName))) {
            isUserAllowedToTrain = true;
        }
        return isUserAllowedToTrain;
    }

    public boolean isUserNameContainsInPrivateNameSpace(String photarkUid, String userName) {
        boolean isUserNameContainsInPrivateNameSpace = false;
        Session session;
        try {
            session = repositoryManager.getSession();
            Node listNodeUsers = (Node) session.getItem("/" + Constants.USER_STORE + "/" + Constants.USER_LISTS + "/" + Constants.REGISTERED_USER_LIST);
            Node userNode = listNodeUsers.getNode(JCREncoder.toJCRFormat(photarkUid));
            Node propertyNode;
            if (userNode.hasNode("face.friends.names")) {
                propertyNode = userNode.getNode("face.friends.names");
            } else {
                propertyNode = userNode.addNode("face.friends.names");
            }

            if (propertyNode.hasProperty(userName)) {
                isUserNameContainsInPrivateNameSpace = true;
            }

            session.save();

        } catch (LoginException e) {

            e.printStackTrace();
        } catch (RepositoryException e) {

            e.printStackTrace();
        }
        return isUserNameContainsInPrivateNameSpace;
    }

    public String checkGenericRecognitionValidity(String photarkUid, String userName) {
        if (isUserNameContainsInPrivateNameSpace(photarkUid, userName)) {
            return userName;
        } else {
            return "photark.not.allowed";
        }
    }

    public boolean isUserNameContainsInGlobalNameSpace(String userName) {
        boolean isUserNameContainsInGlobalNameSpace = false;
        if (friendMap.containsKey(userName)) {
            isUserNameContainsInGlobalNameSpace = true;
        }
        return isUserNameContainsInGlobalNameSpace;
    }

    public boolean addFaceUserNameToUserProfile(String photarkUid, String userName) {
        boolean userNameExists = true;
        Session session;
        try {
            session = repositoryManager.getSession();
            Node listNodeUsers = (Node) session.getItem("/" + Constants.USER_STORE + "/" + Constants.USER_LISTS + "/" + Constants.REGISTERED_USER_LIST);
            Node userNode = listNodeUsers.getNode(JCREncoder.toJCRFormat(photarkUid));
            Node propertyNode;
            if (userNode.hasNode("face.friends.names")) {
                propertyNode = userNode.getNode("face.friends.names");
            } else {
                propertyNode = userNode.addNode("face.friends.names");
            }

            if (!propertyNode.hasProperty(userName)) {
                propertyNode.setProperty(userName, userName);
                userNameExists = false;
                friendMap.put(userName, "True");
            }

            session.save();

        } catch (LoginException e) {

            e.printStackTrace();
        } catch (RepositoryException e) {

            e.printStackTrace();
        }
        return userNameExists;
    }

}
