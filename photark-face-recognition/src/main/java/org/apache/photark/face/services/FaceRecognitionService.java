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

import com.github.mhendred.face4j.exception.FaceClientException;
import com.github.mhendred.face4j.exception.FaceServerException;
import com.github.mhendred.face4j.model.*;
import com.github.mhendred.face4j.response.GroupResponse;
import com.github.mhendred.face4j.response.LimitsResponse;
import com.github.mhendred.face4j.response.TrainResponse;
import com.github.mhendred.face4j.response.UsersResponse;
import org.apache.photark.face.services.beans.PhotarkPhoto;
import org.oasisopen.sca.annotation.Remotable;

import java.io.File;
import java.util.List;

@Remotable
public interface FaceRecognitionService {

    /**
     * Removes old saved tags on a Photo
     *
     * @param tids Tag ids which should be removed. Can pass multiple tag ids once by comma delimiting.
     * @return java.util.List of RemovedTag instances
     * @throws FaceClientException
     * @throws FaceServerException
     */
    public List<RemovedTag> removeTags(String tids) throws FaceClientException, FaceServerException;

    /**
     * Trains the face index with a given set if uids(can be private namespace or a public namespace)
     * i.e friends@Facebook.com
     *
     * @param uids Comma separated uids which can be recognized later.
     * @return TrainResponse
     * @throws FaceClientException
     * @throws FaceServerException
     */
    public void train(String uids) throws FaceClientException, FaceServerException;

    /** Adds a manual tag for a Photo.But manual tags are not used to train the system. They are used for the purpose
     * of adding face tags for the Photos which were not detected by your program.
     *
     * @param url
     * @param x
     * @param y
     * @param width
     * @param height
     * @param uid
     * @param label
     * @param taggerId
     * @throws FaceClientException
     * @throws FaceServerException
     */

    public void addTag(String url, float x, float y, int width, int height, String uid, String label, String taggerId) throws FaceClientException, FaceServerException;

    /** Gives saved tags in one or more photos or for the specified User IDs.
     *
     * @param pids Photo ID
     * @param urls Comma delimited urls.
     * @param uids Comma delimited User IDs
     * @param order Default value is 'recent' which specifies the latest tags. Also value 'random' indicates
     *        to select tags randomly.
     * @param filter Filter results
     *
     * @param together Returns photos which are only contains all uids together (provided that you give multiple User IDs) 
     *        appears together in the photos, if you are
     * @param limit Max no of tags to be returned. Default value is 5.
     * @return Photos which are tagged in the given uids
     * @throws FaceClientException
     * @throws FaceServerException
     */
    public List<Photo> getTagsWithPIDs(String pids, String urls, String uids, String order, String filter, boolean together, int limit) throws FaceClientException, FaceServerException;


    /**
     * Same as {@getTags} except this can only pass uids for get tags unlike both pids and uids in earlier case
     * @param urls
     * @param uids
     * @param order
     * @param filter
     * @param together
     * @param limit
     * @return
     * @throws FaceClientException
     * @throws FaceServerException
     */
    public List<Photo> getTags(String urls, String uids, String order, String filter, boolean together, int limit) throws FaceClientException, FaceServerException ;
    

    /**
     * Saves tags for a given user with a label
     * @param tids Set of tag ids which are associated with the given User ID
     * @param uid User Id in to which you should add one or more tags
     * @param label For readability. Generally we use user's First name
     * @return SavedTag
     * @throws FaceClientException
     * @throws FaceServerException
     */
    public List<SavedTag> saveTags(String tids, String uid, String label) throws FaceClientException, FaceServerException;

    /**
     *  Does recognize whether the given image contains any of given users(by ID).
     * @param imageFile Image file which you gonna to identify whether your friends are there.
     * @param uids User IDs whom should be get recognized
     * @return  Photo
     * @throws FaceClientException
     * @throws FaceServerException
     */
    public PhotarkPhoto recognizeFromFile(File imageFile, String uids) throws FaceClientException, FaceServerException;

    /**
     * Recognizes same as {@recognizeFromFile} except, this time the domain can be given as a url itself.
     * @param urls
     * @param uids
     * @return
     * @throws FaceClientException
     * @throws FaceServerException
     */
    public PhotarkPhoto recognizeFromUrl(String urls, String uids) throws FaceClientException, FaceServerException ;

    /**  Gives tags of the detected faces of the given photo with multiple details of the Photo
     *
     * @param imageFile   Image to be detected
     * @return Photo
     * @throws FaceClientException
     * @throws FaceServerException
     */
    public Photo detectFromFile(File imageFile) throws FaceClientException, FaceServerException;

    /**
     * Detection happens same as {@detectFromFile} except this time its from urls
     * @param urls
     * @return
     * @throws FaceClientException
     * @throws FaceServerException
     */
    public List<Photo> detectFromUrls(String urls) throws FaceClientException, FaceServerException;

    /**
     * Gives the status of the given User IDs from the training set.
     * @param uids
     * @return
     * @throws FaceClientException
     * @throws FaceServerException
     */
    public List<UserStatus> status(String uids) throws FaceClientException, FaceServerException;

    /**
     *  Gives Facebook tags for one or more specified USer IDs
     * @param uids
     * @return
     * @throws FaceClientException
     * @throws FaceServerException
     */
    public List<Photo> facebookGet(String uids) throws FaceClientException, FaceServerException;

    /** Detects and group and optionally tries to recognize faces of the given uids in the image
     *
     * @param imageFile
     * @param uids
     * @return
     * @throws FaceClientException
     * @throws FaceServerException
     */
    public GroupResponse groupFromFile(File imageFile, String uids) throws FaceClientException, FaceServerException;

    /**
     *  Grouping happens same as {@groupFromFile} except this time its from urls
     * @param urls
     * @param uids
     * @return
     * @throws FaceClientException
     * @throws FaceServerException
     */
    public GroupResponse groupFromUrls(String urls, String uids) throws FaceClientException, FaceServerException ;

    /**
     * Gives a list of users of the given namespace
     * @param namespaces
     * @return
     * @throws FaceClientException
     * @throws FaceServerException
     */
    public UsersResponse users(String namespaces) throws FaceClientException, FaceServerException;

    /**
     *  Gives usage stats
     * @return
     * @throws FaceClientException
     * @throws FaceServerException
     */
    public LimitsResponse getLimits() throws FaceClientException, FaceServerException;

    /**
     *  Gives the list of all namspaces you have registered in your face.com API key
     * @return
     * @throws FaceClientException
     * @throws FaceServerException
     */
    public List<Namespace> getAllNamespaces() throws FaceClientException, FaceServerException;

    /**
     *  Gives stats of the given namespace by {@link Namespace}
     * @param namespace
     * @return
     * @throws FaceClientException
     * @throws FaceServerException
     */
    public Namespace getNamespace(String namespace) throws FaceClientException, FaceServerException;

    /**
     * Sets the Facebook credentials for a particular by specifying his FB uid and his access token.
     * @param fbUserId
     * @param oauthToken
     */
    public void setFacebookOauth2(String fbUserId, String oauthToken);

    /**
     * Sets the Twitter credentials for a particular by specifying his TW uid and his access token and secret.
     * @param oauthUser
     * @param oauthSecret
     * @param oauthToken
     */
    public void setTwitterOauth(String oauthUser, String oauthSecret, String oauthToken);

    /**
     *  Clears the existing Facebook credentials.
     */
    public void clearFacebookCreds();

    /**
     *   Clears existing Twitter credentials.
     */
    public void clearTwitterCreds();

    /**
     *  Changes the current face detector's work mode from default position(Normal) to Aggressive.
     * @param isAggressive
     */
    public void setAggressive(boolean isAggressive);

    /**
     * Check whether face detector of your application is in the mode "Aggressive"
     * @return
     */
    public boolean isAggressive();

    /**
     *  create a new DafaultFaceClient by the specified face.com registered api key and secret.
     * @param apiKey
     * @param apiSecret
     */
    public void createNewDefaultFaceClient(String apiKey, String apiSecret);

}
