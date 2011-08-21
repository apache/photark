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

import org.apache.tuscany.sca.data.collection.Entry;
import org.oasisopen.sca.annotation.Remotable;

@Remotable
public interface GenericFriendFinder {


    /**
     * Train the face.com index with the given picture
     * @param imagePath  local image location
     * @param userName username under the private namespace i.e xxx@photark.com
     * @param label optional, a label to the tag
     */
    public boolean trainUrlImage(String imagePath, String userName, String label, String photarkUid) ;

    /**
     * Train the face.com index with the given picture
     * @param imagePath URL image location
     * @param userName userName username under the private namespace i.e xxx@photark.com
     * @param label optional, a label to the tag
     */
    public boolean trainLocalImage(String imagePath, String userName, String label, String photarkUid);

    /**
     * Recognize a given one or more private namespace trained friends
     * @param pathToFile  local image path
     * @param uids  private namespace user ids whom should be recognized.
     * @param photarkUid  current photark logged in user id
     * @return
     */
    public Entry<String, String[]>[] getAllMyFriendsFromPictureLocal(String pathToFile, String uids, String photarkUid);

    /**
     * Recognize a given one or more private namespace trained friends  
     * @param fileUrl URL image path
     * @param uids   private namespace user ids whom should be recognized.
     * @param photarkUid   current photark logged in user id
     * @return
     */
    public Entry<String, String[]>[] getAllMyFriendsFromPictureUrl(String fileUrl,String uids, String photarkUid);

}
