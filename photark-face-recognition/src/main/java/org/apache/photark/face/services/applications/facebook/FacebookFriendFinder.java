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

import org.apache.tuscany.sca.data.collection.Entry;
import org.oasisopen.sca.annotation.Remotable;

@Remotable
public interface FacebookFriendFinder {


    /**
     * Recognize all facebook friends who are in the give picture
     * @param pathToFile Local file location of the image which should be recognized
     * @param photarkUid Current logged in user's Photark security ID
     * @return Entry
     */
    public Entry<String, String[]>[] getAllMyFBFriendsFromPictureLocal(String pathToFile,String photarkUid);



    /**
     * Recognize all facebook friends who are in the give picture
     * @param fileUrl  URL of the file location of the image which should be recognized
     * @param photarkUid  Current logged in user's Photark security ID
     * @return Entry
     */
    public Entry<String, String[]>[] getAllMyFBFriendsFromPictureUrl(String fileUrl,String photarkUid);



    /**
     *  Authenticate Photark Face recognition Facebook  app
     * @param facebookId
     * @param fbAccessToken
     */
    public void setFacebookAuth(String facebookId, String fbAccessToken);


    /** Stores the Facebook access token in local JCR with the user's security credentials
     *
     * @param photarkUid
     * @param accessToken
     */
    public void storeFacebookAccessToken(String photarkUid, String accessToken);
}
