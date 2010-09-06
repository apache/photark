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

package org.apache.photark.security.authorization.services;

import org.apache.photark.security.authorization.AccessList;
import org.apache.photark.security.authorization.User;
import org.oasisopen.sca.annotation.Remotable;

/**
 * the interface for the local classes
 */
@Remotable
public interface AccessManager {


    /**
     * creating access list for the users
     *
     * @param userId The userID of the user
     * @param email  E-mail of the user
     * @return AccessList of the user
     */
    AccessList createAccessList(String userId, String email);

    /**
     * update existing active access list of the users
     *
     * @param userId The userID of the user
     * @return AccessList of the user
     */
    AccessList updateAccessList(String userId);


    /**
     * add user to one of the four user lists
     *
     * @param user     The User
     * @param listName The name of the list  ("blockedUserList","unRegisteredUserList""registeredUserList","superAdminList")
     */
    void addUserToList(User user, String listName);

    /**
     * remove users from the List
     *
     * @param userId   The userID of the user
     * @param listName The name of the list  ("blockedUserList","unRegisteredUserList""registeredUserList","superAdminList")
     */
    void removeUserFromList(String userId, String listName);


    /**
     * check whether the user is stored in the list
     *
     * @param userId   The userID of the user
     * @param listName listName The name of the list  ("blockedUserList","unRegisteredUserList""registeredUserList","superAdminList")
     * @return true if the user is stored, and false if not
     */
    boolean isUserStoredInList(String userId, String listName);

    /**
     * get user for the given user id
     * these users are taken from allUsersGroup
     *
     * @param userId The userID of the user
     * @return The user
     */
    User getUser(String userId);


    /**
     * check whether the user is permitted to the album
     * Super admin > owner > other users with the given permission
     * are allowed
     *
     * @param userId          The userID of the user
     * @param albumName       The Name of the Album
     * @param permissionNames The Names of the Allowed Permissions separated by comma (,)
     * @return true if permitted, else false
     */
    boolean isPermitted(String userId, String albumName, String[] permissionNames);


    /**
     * get AccessList From UserId
     *
     * @param userId The userID of the user
     * @return AccessList of the user
     */
    AccessList getAccessListFromUserId(String userId);


    /**
     * check whether the user in the access token map
     *
     * @param userId The userID of the user
     * @return true if the user was provided a Security Token, and the user not logged out yet, else false
     */
    boolean isUserActive(String userId);


    /**
     * get Security Token From UserId
     *
     * @param userId The userID of the user
     * @return The Security Token of the user
     */
    String getSecurityTokenFromUserId(String userId);


    /**
     * save access list and token in the access token map
     *
     * @param accessList Access list of the user
     * @param token      Security Token of the user
     */
    void putAccessListAndToken(AccessList accessList, String token);


    /**
     * remove access list and token from the access token map
     *
     * @param userId The userID of the user
     */
    void removeAccessListAndToken(String userId);


    /**
     * get AccessList From SecurityToken
     *
     * @param token Security Token of the user
     * @return Access list of the user
     */
    AccessList getAccessListFromSecurityToken(String token);


    /**
     * get UserId From SecurityToken
     *
     * @param token Security Token of the user
     * @return The userID of the user
     */
    String getUserIdFromSecurityToken(String token);
}
