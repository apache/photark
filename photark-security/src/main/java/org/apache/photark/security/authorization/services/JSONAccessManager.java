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
import org.apache.photark.security.authorization.Permission;
import org.apache.photark.security.authorization.Role;
import org.apache.photark.security.authorization.User;
import org.oasisopen.sca.annotation.Remotable;

import java.util.List;

      /*
      * the Json RPC interface
      *
      * */
@Remotable
public interface JSONAccessManager {

    //role

    User[] getUsersFromList(String listName, String securityToken);

    void replaceUsersInList(String[] userIds, String listName, String securityToken);

    public Role[] getRoles();

    public void addRole(String roleName, String permissions, String securityToken);

    public void deleteRole(String roleName, String securityToken);

    public Permission[] getPermissions();

    public void addToRole(String albumName, List<String[]> RolesAndUserGroups, String securityToken);

    public Object[] getAlbumPermissionInfo(String albumName, String securityToken);


    //user

    User[] getAllUsers();


    //user Groups

    public List getGroups(String securityToken);

    public void addGroup(String groupName, String users, String securityToken);

    public void deleteGroup(String groupName, String securityToken);


}
