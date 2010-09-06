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

import java.util.List;

import org.apache.photark.security.authorization.Permission;
import org.apache.photark.security.authorization.Role;
import org.apache.photark.security.authorization.User;
import org.oasisopen.sca.annotation.Remotable;

/**
 * the Json RPC interface
 */
@Remotable
public interface JSONAccessManager {

    /**
     * to get all the users from the list
     *
     * @param listName      Name of the list ("blockedUserList","unRegisteredUserList""registeredUserList","superAdminList")
     * @param securityToken Security token
     * @return Array of Users
     */
    User[] getUsersFromList(String listName, String securityToken);

    /**
     * replace the users in the list
     *
     * @param userIds       The new users' userIDs
     * @param listName      Name of the list ("blockedUserList","unRegisteredUserList""registeredUserList","superAdminList")
     * @param securityToken Security token
     */
    void replaceUsersInList(String[] userIds, String listName, String securityToken);

    //

    /**
     * get all available roles
     *
     * @return Array of all Roles
     */
    public Role[] getRoles();


    /**
     * add or update  roles
     *
     * @param roleName      The new Role name
     * @param permissions   The permissions to the role Separated by comma (,)
     * @param securityToken Security token
     */
    public void addRole(String roleName, String permissions, String securityToken);


    /**
     * delete roles
     *
     * @param roleName      The Role to be deleted
     * @param securityToken Security token
     */
    public void deleteRole(String roleName, String securityToken);


    /**
     * get all available permissions
     *
     * @return Array of Permissions
     */
    public Permission[] getPermissions();


    /**
     * assign Album, User Groups to the Role
     *
     * @param albumName          Name of the Album that need to be assigned to the Role
     * @param RolesAndUserGroups A list that contains a String array of size=2, [Role name, UserGroup name separated by comma]
     * @param securityToken      Security Token
     */
    public void addToRole(String albumName, List<String[]> RolesAndUserGroups, String securityToken);


    /**
     * get the permission information for the Album
     *
     * @param albumName     Album Name
     * @param securityToken Security Token
     * @return An Object array, here each Object contains a String array of Size=2 where String [Role name][UserGroup name separated by comma]
     */
    public Object[] getAlbumPermissionInfo(String albumName, String securityToken);


    // get all the users; registered, blocked, and unregistered (logged in but not registered)

    /**
     * get all the users; registered, blocked, and unregistered (unregistered mean logged in, but not registered in the system)
     *
     * @return Array of users
     */
    User[] getAllUsers();


    // get user groups owned by the user. For super admin all groups

    /**
     * get user groups owned by the user. For super admin all groups
     *
     * @param securityToken Security Token
     * @return List which contains Objects, here each Object contains a String array of Size=2 where String [Role name][UserGroup name separated by comma]
     */
    public List getGroups(String securityToken);


    /**
     * add or update user group
     *
     * @param groupName     Name of the Group
     * @param users         Name of the users Separated by comma
     * @param securityToken Security Token
     */
    public void addGroup(String groupName, String users, String securityToken);


    /**
     * delete the user group
     *
     * @param groupName     Name of the group
     * @param securityToken Security Token
     */

    public void deleteGroup(String groupName, String securityToken);

}
