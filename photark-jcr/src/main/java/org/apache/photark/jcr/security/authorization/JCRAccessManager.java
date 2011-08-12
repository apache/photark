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

package org.apache.photark.jcr.security.authorization;

import static org.apache.photark.security.utils.Constants.ALBUM_ADD_IMAGES_PERMISSION;
import static org.apache.photark.security.utils.Constants.ALBUM_CREATE_PERMISSION;
import static org.apache.photark.security.utils.Constants.ALBUM_DELETE_IMAGES_PERMISSION;
import static org.apache.photark.security.utils.Constants.ALBUM_EDIT_ALBUM_DESCRIPTION_PERMISSION;
import static org.apache.photark.security.utils.Constants.ALBUM_VIEW_IMAGES_PERMISSION;
import static org.apache.photark.security.utils.Constants.ALL_GROUPS;
import static org.apache.photark.security.utils.Constants.ALL_PERMISSIONS;
import static org.apache.photark.security.utils.Constants.ALL_ROLES;
import static org.apache.photark.security.utils.Constants.ALL_USERS;
import static org.apache.photark.security.utils.Constants.ALL_USERS_VIEW_ROLE;
import static org.apache.photark.security.utils.Constants.BLOCKED_USER_LIST;
import static org.apache.photark.security.utils.Constants.DEFAULT_LISTS;
import static org.apache.photark.security.utils.Constants.GROUP_OWNER;
import static org.apache.photark.security.utils.Constants.GROUP_USERS;
import static org.apache.photark.security.utils.Constants.GUEST;
import static org.apache.photark.security.utils.Constants.MUTUALLY_EXCLUSIVE_LIST;
import static org.apache.photark.security.utils.Constants.PERMISSION_DESCRIPTION;
import static org.apache.photark.security.utils.Constants.REGISTERED_USER_LIST;
import static org.apache.photark.security.utils.Constants.ROLE_PERMISSIONS;
import static org.apache.photark.security.utils.Constants.ROLE_USER_GROUPS;
import static org.apache.photark.security.utils.Constants.SUPER_ADMIN;
import static org.apache.photark.security.utils.Constants.SUPER_ADMIN_LIST;
import static org.apache.photark.security.utils.Constants.UNREGISTERED_USER_LIST;
import static org.apache.photark.security.utils.Constants.USER_DISPLAY_NAME;
import static org.apache.photark.security.utils.Constants.USER_EMAIL;
import static org.apache.photark.security.utils.Constants.USER_GROUP_CREATE_PERMISSION;
import static org.apache.photark.security.utils.Constants.USER_ID;
import static org.apache.photark.security.utils.Constants.USER_LISTS;
import static org.apache.photark.security.utils.Constants.USER_REAL_NAME;
import static org.apache.photark.security.utils.Constants.USER_STORE;
import static org.apache.photark.security.utils.Constants.USER_WEBSITE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.LoginException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.ValueFormatException;

import org.apache.photark.jcr.JCRRepositoryManager;
import org.apache.photark.jcr.services.JCRAlbumImpl;
import org.apache.photark.jcr.util.JCREncoder;
import org.apache.photark.security.authorization.AccessList;
import org.apache.photark.security.authorization.Permission;
import org.apache.photark.security.authorization.Role;
import org.apache.photark.security.authorization.User;
import org.apache.photark.security.authorization.UserInfo;
import org.apache.photark.security.authorization.services.AccessManager;
import org.apache.photark.security.authorization.services.JSONAccessManager;
import org.apache.photark.services.album.Album;
import org.oasisopen.sca.annotation.Destroy;
import org.oasisopen.sca.annotation.Init;
import org.oasisopen.sca.annotation.Reference;
import org.oasisopen.sca.annotation.Remotable;
import org.oasisopen.sca.annotation.Scope;

@Remotable
@Scope("COMPOSITE")
public class JCRAccessManager implements AccessManager, JSONAccessManager {

    /**
     * JCR Repository Manager *
     */
    private static JCRRepositoryManager repositoryManager;
    boolean initialised = false;
    private static Map<String, Object[]> accessTokenMap = new HashMap<String, Object[]>();

    // JSONRPCSecurityManager jsonSecurityManager= new JSONRPCSecurityManager();

    public JCRAccessManager() {

    }

    @Reference(name = "repositoryManager")
    protected void setRepositoryManager(JCRRepositoryManager repositoryManager) {
        JCRAccessManager.repositoryManager = repositoryManager;
    }

    /*
     * initialing jcr
     */

    @Init
    public synchronized void init() {

        try {
            Session session = repositoryManager.getSession();
            Node root = session.getRootNode();
            if (!root.hasNode(USER_STORE)) {
                Node userStore = root.addNode(USER_STORE);
                Node mutuallyExclusiveRoles = userStore.addNode(MUTUALLY_EXCLUSIVE_LIST);
                mutuallyExclusiveRoles
                    .setProperty(DEFAULT_LISTS, new String[] {BLOCKED_USER_LIST, UNREGISTERED_USER_LIST,
                                                              REGISTERED_USER_LIST, SUPER_ADMIN_LIST});

                Node allUsers = userStore.addNode(ALL_USERS);
                Node lists = userStore.addNode(USER_LISTS);
                Node roles = userStore.addNode(ALL_ROLES);
                Node allPermissions = userStore.addNode(ALL_PERMISSIONS);
                Node allUsersNode = roles.addNode(ALL_USERS_VIEW_ROLE);
                allUsersNode.setProperty(ROLE_PERMISSIONS, ALBUM_VIEW_IMAGES_PERMISSION);
                allUsersNode.addNode("boston");
                userStore.addNode(ALL_GROUPS);

                // lists
                lists.addNode(BLOCKED_USER_LIST);
                Node unRegisteredUserList = lists.addNode(UNREGISTERED_USER_LIST); // logged
                                                                                   // in
                                                                                   // but
                                                                                   // not
                                                                                   // registered
                lists.addNode(REGISTERED_USER_LIST);
                Node superAdminList = lists.addNode(SUPER_ADMIN_LIST);

                // Default users

                // super admin
                superAdminList.addNode(SUPER_ADMIN);
                Node userNode = allUsers.addNode(SUPER_ADMIN);
                userNode.setProperty(USER_DISPLAY_NAME, "SuperAdmin");
                userNode.setProperty(USER_EMAIL, "");
                userNode.setProperty(USER_REAL_NAME, "");
                userNode.setProperty(USER_WEBSITE, "");
                userNode.setProperty(USER_ID, "SuperAdmin");

                // guest
                unRegisteredUserList.addNode(GUEST);
                userNode = allUsers.addNode(GUEST);
                userNode.setProperty(USER_DISPLAY_NAME, "GuestUser");
                userNode.setProperty(USER_EMAIL, "");
                userNode.setProperty(USER_REAL_NAME, "");
                userNode.setProperty(USER_WEBSITE, "");
                userNode.setProperty(USER_ID, "GuestUser");

                // per Album permissions
                allPermissions.addNode(ALBUM_VIEW_IMAGES_PERMISSION)
                    .setProperty(PERMISSION_DESCRIPTION, "Allow the users to view the album images");
                allPermissions.addNode(ALBUM_ADD_IMAGES_PERMISSION)
                    .setProperty(PERMISSION_DESCRIPTION, "Allow the users to add new images to the album");
                allPermissions.addNode(ALBUM_DELETE_IMAGES_PERMISSION)
                    .setProperty(PERMISSION_DESCRIPTION, "Allow the users to delete images from the album");
                allPermissions.addNode(ALBUM_EDIT_ALBUM_DESCRIPTION_PERMISSION)
                    .setProperty(PERMISSION_DESCRIPTION, "Allow the users to edit Album description");

                session.save();
                initialised = true;
            }

        } catch (Exception e) {
            // FIXME: ignore for now
            e.printStackTrace();
        }
    }

    @Destroy
    public void destroy() {
        // repositoryManager.releaseSession();
    }

    // ****************************************************************************************
    // for lists

    // add user to one of the four user lists

    public synchronized void addUserToList(User user, String listName) {
        if (!initialised) {
            init();
        }
        try {
            Session session = repositoryManager.getSession();
            // deleting from other lists
            deleteMutuallyExclusiveLists(user.getUserId(), listName);

            Node users = (Node)session.getItem("/" + USER_STORE + "/" + USER_LISTS + "/" + listName);
            Node userNode;
            UserInfo userInfo = user.getUserInfo();
            if (users != null) {
                if (users.hasNode(JCREncoder.toJCRFormat(user.getUserId()))) {
                    users.getNode(JCREncoder.toJCRFormat(user.getUserId()));
                } else {
                    users.addNode(JCREncoder.toJCRFormat(user.getUserId()));
                }
            }
            session.save();
            // deleting the accessList of the users
            removeAccessList(user.getUserId());
            addToAllUsers(user);

        } catch (Exception e) {
            // FIXME: ignore for now
            e.printStackTrace();
        }

    }

    // deleting users from other mutually exclusive lists

    private synchronized void deleteMutuallyExclusiveLists(String userId, String roleName) {
        if (!initialised) {
            init();
        }

        try {
            Session session = repositoryManager.getSession();
            Node allMutuallyExclusiveRoles = (Node)session.getItem("/" + USER_STORE + "/" + MUTUALLY_EXCLUSIVE_LIST);
            for (PropertyIterator pi = allMutuallyExclusiveRoles.getProperties(); pi.hasNext();) {
                Property p = pi.nextProperty();

                if (!p.getName().equals("jcr:primaryType")) {
                    ArrayList<String> list = new ArrayList<String>();
                    for (Value v : p.getValues()) {
                        list.add(v.getString());
                    }

                    if (list.contains(roleName)) {
                        for (Object aList : list) {
                            String theList = (String)aList;
                            if (!theList.equals(roleName)) {
                                removeUserFromList(userId, theList);
                            }
                        }
                    }
                }

            }
            session.save();
        } catch (PathNotFoundException e) {
            e.printStackTrace();
        } catch (RepositoryException e) {
            e.printStackTrace();
        }
    }

    // to get all the users from the list

    public synchronized User[] getUsersFromList(String listName, String securityToken) {

        if (!initialised) {
            init();
        }
        List<User> userList = new ArrayList<User>();
        if (isPermitted(getUserIdFromSecurityToken(securityToken), null, null)) {
            try {
                Session session = repositoryManager.getSession();
                Node users = (Node)session.getItem("/" + USER_STORE + "/" + USER_LISTS + "/" + listName);
                if (users != null) {
                    for (NodeIterator ni = users.getNodes(); ni.hasNext();) {
                        Node userNode = ni.nextNode();
                        userList.add(getUser(JCREncoder.toNormalFormat(userNode.getName())));
                    }
                }
            } catch (Exception e) {
                // FIXME: ignore for now
                e.printStackTrace();
            }

        }
        User[] array = new User[userList.size()];
        return userList.toArray(array);
    }

    // replace the users in the list

    public synchronized void replaceUsersInList(String[] userIds, String listName, String securityToken) {

        if (!initialised) {
            init();
        }
        if (isPermitted(getUserIdFromSecurityToken(securityToken), null, null)) {
            try {
                Session session = repositoryManager.getSession();

                Node usersNode = (Node)session.getItem("/" + USER_STORE + "/" + USER_LISTS + "/" + listName);
                if (usersNode != null) {
                    usersNode.remove();
                    session.save();
                }
                Node userListNode = (Node)session.getItem("/" + USER_STORE + "/" + USER_LISTS);
                if (userListNode != null) {
                    userListNode.addNode(listName);

                    for (String userId : userIds) {
                        addUserToList(getUser(userId), listName);
                    }
                    session.save();
                }
                // deleting the accessList of the users
                removeAllAccessList();

            } catch (Exception e) {
                // FIXME: ignore for now
                e.printStackTrace();
            }

        }

    }

    // remove users from the List

    public synchronized void removeUserFromList(String userId, String listName) {
        Session session;
        try {
            session = repositoryManager.getSession();
            Node listNodeUsers = (Node)session.getItem("/" + USER_STORE + "/" + USER_LISTS + "/" + listName);

            if (listNodeUsers != null) {
                if (listNodeUsers.hasNode(JCREncoder.toJCRFormat(userId))) {
                    Node userNode = listNodeUsers.getNode(JCREncoder.toJCRFormat(userId));
                    userNode.remove();
                    session.save();
                    // deleting the accessList of the users
                    removeAccessList(userId);
                }
            }
        } catch (LoginException e) {

            e.printStackTrace();
        } catch (RepositoryException e) {

            e.printStackTrace();
        }
    }

    // check whether the user is stored in the list

    public synchronized boolean isUserStoredInList(String userId, String listName) {
        if (userId == null || listName == null || userId.trim().equals("") || listName.trim().equals("")) {
            return false;
        }

        if (!initialised) {
            init();
        }
        try {
            Session session = repositoryManager.getSession();
            Node subRoleNodeUsers = (Node)session.getItem("/" + USER_STORE + "/" + USER_LISTS + "/" + listName);
            if (subRoleNodeUsers != null && subRoleNodeUsers.hasNode(JCREncoder.toJCRFormat(userId))) {
                return true;
            }
        } catch (Exception e) {
            // FIXME: ignore for now
            e.printStackTrace();
        }
        return false;
    }

    // add user to all users group

    private synchronized void addToAllUsers(User user) throws RepositoryException {
        if (!initialised) {
            init();
        }
        Session session = repositoryManager.getSession();
        UserInfo userInfo = user.getUserInfo();
        Node userNode;
        Node allUsers = (Node)session.getItem("/" + USER_STORE + "/" + ALL_USERS);
        if (allUsers != null) {

            if (allUsers.hasNode(JCREncoder.toJCRFormat(user.getUserId()))) {
                userNode = allUsers.getNode(JCREncoder.toJCRFormat(user.getUserId()));
            } else {
                userNode = allUsers.addNode(JCREncoder.toJCRFormat(user.getUserId()));
            }
            userNode.setProperty(USER_DISPLAY_NAME, JCREncoder.toJCRFormat(userInfo.getDisplayName()));
            userNode.setProperty(USER_EMAIL, JCREncoder.toJCRFormat(userInfo.getEmail()));
            userNode.setProperty(USER_REAL_NAME, JCREncoder.toJCRFormat(userInfo.getRealName()));
            userNode.setProperty(USER_WEBSITE, JCREncoder.toJCRFormat(userInfo.getWebsite()));
            userNode.setProperty(USER_ID, JCREncoder.toJCRFormat(user.getUserId()));
        }
        session.save();
    }

    // ***************************************************************************
    // for users

    // get user for the given user id
    // taken from all users group

    public synchronized User getUser(String userId) {
        if (userId == null) {
            userId = GUEST;
        }
        if (!initialised) {
            init();
        }
        User user = null;
        try {
            Session session = repositoryManager.getSession();
            // //to delete userStore
            // Node root = session.getRootNode();
            // Node userStoreT= root.getNode("userStore");
            // userStoreT.remove();
            // session.save();
            // //
            Node allUsers = (Node)session.getItem("/" + USER_STORE + "/" + ALL_USERS);
            Node userNode;
            if (allUsers != null && allUsers.hasNode(JCREncoder.toJCRFormat(userId))) {
                userNode = allUsers.getNode(JCREncoder.toJCRFormat(userId));
                user = new User(JCREncoder.toNormalFormat(userNode.getName()));
                UserInfo userInfo =
                    new UserInfo(JCREncoder.toNormalFormat(userNode.getProperty(USER_DISPLAY_NAME).getValue()
                        .getString()), JCREncoder.toNormalFormat(userNode.getProperty(USER_EMAIL).getValue()
                        .getString()), JCREncoder.toNormalFormat(userNode.getProperty(USER_REAL_NAME).getValue()
                        .getString()), JCREncoder.toNormalFormat(userNode.getProperty(USER_WEBSITE).getValue()
                        .getString()));
                user.setUserInfo(userInfo);
            }
        } catch (Exception e) {
            // FIXME: ignore for now
            e.printStackTrace();
        }
        return user;
    }

    // get all the users
    // registered, blocked, and unregistered (logged in but not registered)

    public synchronized User[] getAllUsers() {
        List<User> userList = new ArrayList<User>();
        if (!initialised) {
            init();
        }
        User user = null;
        try {
            Session session = repositoryManager.getSession();
            Node allUsers = (Node)session.getItem("/" + USER_STORE + "/" + ALL_USERS);

            NodeIterator userNodes = allUsers.getNodes();
            while (userNodes.hasNext()) {
                Node userNode = userNodes.nextNode();
                userList.add(getUser(JCREncoder.toNormalFormat(userNode.getName())));
            }

        } catch (Exception e) {
            // FIXME: ignore for now
            e.printStackTrace();
        }
        User[] userArray = new User[userList.size()];
        userList.toArray(userArray);
        return userArray;

    }

    // **********************************************************
    // for permissions

    // assign Album User Groups to the Role

    public synchronized void addToRole(String albumName, List<String[]> rolesAndUserGroups, String securityToken) {

        if (isPermitted(getUserIdFromSecurityToken(securityToken), albumName, null)) {

            try {
                Node role;
                Session session = repositoryManager.getSession();
                Node roles = (Node)session.getItem("/" + USER_STORE + "/" + ALL_ROLES);
                for (NodeIterator ni = roles.getNodes(); ni.hasNext();) {
                    Node roleNode = ni.nextNode();

                    if (roleNode.hasNode(albumName)) {
                        roleNode.getNode(albumName).remove();
                    }
                }
                for (String[] rolesAndUserGroupsArray : rolesAndUserGroups) {

                    if (roles.hasNode(rolesAndUserGroupsArray[0])) {
                        role = roles.getNode(rolesAndUserGroupsArray[0]);
                        Node album;
                        album = role.addNode(albumName);
                        if (!rolesAndUserGroupsArray[0].contains(ALL_USERS_VIEW_ROLE)) {
                            album.setProperty(ROLE_USER_GROUPS, JCREncoder.toJCRFormat(rolesAndUserGroupsArray[1]));
                        }
                    }
                }
                session.save();
                // deleting the accessList of the users
                removeAllAccessList();
            } catch (LoginException e) {
                e.printStackTrace();
            } catch (PathNotFoundException e) {
                e.printStackTrace();
            } catch (RepositoryException e) {
                e.printStackTrace();
            }

        }
    }

    // checks whether the user is the owner of the album

    private synchronized boolean isUserTheOwner(String userId, String albumName) {
        if (userId == null || albumName == null || userId.trim().equals("") || albumName.trim().equals("")) {
            return false;
        }
        Album album = new JCRAlbumImpl(repositoryManager, albumName);
        return Arrays.asList(album.getOwners()).contains(userId);
    }

    private boolean isNoOwnerForAlbum(String albumName) {
        if (albumName == null || albumName.trim().equals("")) {
            return false;
        }
        Album album = new JCRAlbumImpl(repositoryManager, albumName);
        String[] owners = album.getOwners();
        if (owners.length == 0) {
            return true;
        } else {
            return false;
        }
    }

    // get all permissions

    public synchronized Permission[] getPermissions() {
        List<Permission> list = new ArrayList<Permission>();
        try {
            Session session = repositoryManager.getSession();
            Node allPermissions = (Node)session.getItem("/" + USER_STORE + "/" + ALL_PERMISSIONS);
            NodeIterator permissionNodes = allPermissions.getNodes();
            while (permissionNodes.hasNext()) {
                Node permissionNode = permissionNodes.nextNode();
                list.add(new Permission(permissionNode.getName(), permissionNode.getProperty(PERMISSION_DESCRIPTION)
                    .getString()));
            }
        } catch (ValueFormatException e) {
            e.printStackTrace();
        } catch (LoginException e) {
            e.printStackTrace();
        } catch (PathNotFoundException e) {
            e.printStackTrace();
        } catch (RepositoryException e) {
            e.printStackTrace();
        }
        Permission[] array = new Permission[list.size()];
        return list.toArray(array);

    }

    // get the permission information for the Album
    // Object[] == String [n][1] [<roleName>[1]][<userGroupNames String>[1]]

    public synchronized Object[] getAlbumPermissionInfo(String albumName, String securityToken) {
        List<Object[]> list = new ArrayList<Object[]>();
        if (isPermitted(getUserIdFromSecurityToken(securityToken), albumName, null)) {
            try {

                Session session = repositoryManager.getSession();
                Node roles = (Node)session.getItem("/" + USER_STORE + "/" + ALL_ROLES);
                NodeIterator roleNodes = roles.getNodes();
                while (roleNodes.hasNext()) {
                    Node roleNode = roleNodes.nextNode();
                    NodeIterator albumNodes = roleNode.getNodes();
                    while (albumNodes.hasNext()) {
                        Node albumNode = albumNodes.nextNode();
                        if (albumNode.getName().equals(albumName)) {
                            String userGroups;
                            if (!roleNode.getName().equals(ALL_USERS_VIEW_ROLE)) {
                                userGroups = albumNode.getProperty(ROLE_USER_GROUPS).getString();
                            } else {
                                userGroups = "";
                            }
                            list.add(new String[] {roleNode.getName(), userGroups});
                            break;
                        }

                    }
                }

            } catch (LoginException e) {
                e.printStackTrace();
            } catch (PathNotFoundException e) {
                e.printStackTrace();
            } catch (RepositoryException e) {
                e.printStackTrace();
            }
        }
        Object[] array = new Object[list.size()];
        return list.toArray(array);

    }

    // get the permissions to create accessList permissions
    // Map<Album name, List<the user's permissions for that album>>

    private synchronized Map<String, List<Permission>> getPermissions(String userId) {
        if (!initialised) {
            init();
        }
        Map<String, List<Permission>> permissions = new HashMap<String, List<Permission>>();
        try {
            Session session = repositoryManager.getSession();
            Node RolesNode = (Node)session.getItem("/" + USER_STORE + "/" + ALL_ROLES);

            for (NodeIterator ni = RolesNode.getNodes(); ni.hasNext();) {
                Node roleNode = ni.nextNode();
                List<Permission> rolePermissionList = null;
                for (NodeIterator ni1 = roleNode.getNodes(); ni1.hasNext();) {
                    Node albumNode = ni1.nextNode();
                    if (roleNode.getName().equals(ALL_USERS_VIEW_ROLE)) {
                        rolePermissionList = concatPermissions(permissions, roleNode, rolePermissionList, albumNode);
                        continue;
                    }
                    Property userGroups = albumNode.getProperty(ROLE_USER_GROUPS);
                    for (String userGroup : userGroups.getString().split(",")) {
                        if (isUserInGroup(userId, userGroup)) {
                            rolePermissionList =
                                concatPermissions(permissions, roleNode, rolePermissionList, albumNode);
                        }
                    }
                }
            }
            return permissions;

        } catch (LoginException e) {
            e.printStackTrace();
        } catch (PathNotFoundException e) {
            e.printStackTrace();
        } catch (RepositoryException e) {
            e.printStackTrace();
        }
        return permissions;
    }

    // adding two permission lists together

    private synchronized List<Permission> concatPermissions(Map<String, List<Permission>> permissions,
                                                            Node roleNode,
                                                            List<Permission> rolePermissionList,
                                                            Node albumNode) throws RepositoryException {
        List<Permission> list;
        if (rolePermissionList == null) {
            rolePermissionList = new ArrayList<Permission>();
            for (String rolePermissionsValue : roleNode.getProperty(ROLE_PERMISSIONS).getString().split(",")) {
                rolePermissionList.add(getPermissionObject(rolePermissionsValue));
            }
        }
        if (permissions.containsKey(JCREncoder.toNormalFormat(albumNode.getName()))) {
            list = permissions.get(JCREncoder.toNormalFormat(albumNode.getName()));
        } else {
            list = new ArrayList<Permission>();
        }
        for (Permission p : rolePermissionList) {
            if (!list.contains(p)) {
                list.add(p);
            }
        }
        permissions.put(JCREncoder.toNormalFormat(albumNode.getName()), list);
        return rolePermissionList;
    }

    // creating the permission Object from the permission name

    private synchronized Permission getPermissionObject(String permissionName) {
        if (!initialised) {
            init();
        }
        Session session;
        try {
            session = repositoryManager.getSession();
            Node allPermissions = (Node)session.getItem("/" + USER_STORE + "/" + ALL_PERMISSIONS);
            if (allPermissions.hasNode(permissionName)) {
                return new Permission(allPermissions.getNode(permissionName).getName(), allPermissions
                    .getNode(permissionName).getProperty(PERMISSION_DESCRIPTION).getString());
            }
        } catch (ValueFormatException e) {
            e.printStackTrace();
        } catch (LoginException e) {
            e.printStackTrace();
        } catch (PathNotFoundException e) {
            e.printStackTrace();
        } catch (RepositoryException e) {
            e.printStackTrace();
        }
        return null;
    }

    // check the user is permitted to the album
    // Super admin > owner > other users with the given permission
    // are allowed

    public synchronized boolean isPermitted(String userId, String resourceName, String[] permissionNames) {
        if (userId == null || userId.trim().equals("")) {
            return false;
        }
        AccessList accessList = getAccessListFromUserId(userId);
        if (!initialised) {
            init();
        }

        if (accessList == null) {
            return false;
        }
        // Super admin have all the rights
        if (accessList.getUserId().equals(SUPER_ADMIN)) {
            return true;
        }
        if (resourceName == null || resourceName.trim().equals("")) {
            return false;
        }
        Map<String, List<Permission>> userPermissionMap = accessList.getPermissions();
        List<String> allowedPermissions;
        if (permissionNames != null) {
            allowedPermissions = Arrays.asList(permissionNames);
        } else {
            allowedPermissions = new ArrayList<String>();
        }
        // all albums with no owners are viewable by everyone
        if (allowedPermissions.contains(ALBUM_VIEW_IMAGES_PERMISSION) && isNoOwnerForAlbum(resourceName)) {
           return true;
        }
        List<Permission> permissions = new ArrayList<Permission>();
        // if the user in Registered User List or in the Supper Admin List
        // the user is allowed to create Albums and create User Groups
        if ((allowedPermissions.contains(ALBUM_CREATE_PERMISSION) || allowedPermissions
            .contains(USER_GROUP_CREATE_PERMISSION)) && (isUserStoredInList(accessList.getUserId(),
                                                                            REGISTERED_USER_LIST) || isUserStoredInList(accessList
                                                                                                                            .getUserId(),
                                                                                                                        SUPER_ADMIN_LIST))) {
            return true;
        }
        // owner have rights for his album
        if (isUserTheOwner(accessList.getUserId(), resourceName)) {
            return true;
        }
        if (userPermissionMap.containsKey(resourceName)) {
            permissions = (userPermissionMap.get(resourceName));
        }
        for (Permission permission : permissions) {
            if (allowedPermissions.contains(permission.getPermission())) {
                return true;
            }
        }
        return false;

    }

    // **********************************************************************************
    // for roles

    // add roles

    public synchronized void addRole(String roleName, String permissions, String securityToken) {
        if (roleName.equals(ALL_USERS_VIEW_ROLE)) {
            return;
        }
        if (permissions.startsWith(",")) {
            permissions = permissions.substring(1);
        }
        if (permissions.endsWith(",")) {
            permissions = permissions.substring(0, permissions.length() - 1);
        }
        // only super admin is allowed
        if (isPermitted(getUserIdFromSecurityToken(securityToken), null, null)) {
            try {
                Node role;
                Session session = repositoryManager.getSession();
                Node roles = (Node)session.getItem("/" + USER_STORE + "/" + ALL_ROLES);
                if (!roles.hasNode(roleName)) {

                    role = roles.addNode(roleName);
                } else {
                    role = roles.getNode(roleName);

                }
                role.setProperty(ROLE_PERMISSIONS, permissions);
                session.save();
                // deleting the accessList of the users
                removeAllAccessList();

            } catch (LoginException e) {
                e.printStackTrace();
            } catch (PathNotFoundException e) {
                e.printStackTrace();
            } catch (RepositoryException e) {
                e.printStackTrace();
            }

        }
    }

    // delete roles

    public synchronized void deleteRole(String roleName, String securityToken) {

        if (roleName.equals(ALL_USERS_VIEW_ROLE)) {
            return;
        }
        if (isPermitted(getUserIdFromSecurityToken(securityToken), null, null)) {
            try {
                Node role;
                Session session = repositoryManager.getSession();
                Node roles = (Node)session.getItem("/" + USER_STORE + "/" + ALL_ROLES);
                if (roles.hasNode(roleName)) {
                    role = roles.getNode(roleName);
                    role.remove();
                    session.save();
                    // deleting the accessList of the users
                    removeAllAccessList();
                }

            } catch (LoginException e) {
                e.printStackTrace();
            } catch (PathNotFoundException e) {
                e.printStackTrace();
            } catch (RepositoryException e) {
                e.printStackTrace();
            }
        }
    }

    // get all roles

    public synchronized Role[] getRoles() {
        List<Role> roleList = new ArrayList<Role>();
        try {

            Session session = repositoryManager.getSession();
            Node roles = (Node)session.getItem("/" + USER_STORE + "/" + ALL_ROLES);
            NodeIterator groupNodes = roles.getNodes();
            while (groupNodes.hasNext()) {
                Node roleNode = groupNodes.nextNode();
                // if user is SUPER ADMIN or the user this the owner of the
                // group he will get the group
                // if (accessList.getUserId().equals(SUPER_ADMIN) ||
                // groupNode.hasProperty(GROUP_OWNER) &&
                // accessList.getUserId().equals(JCREncoder.toNormalFormat(groupNode.getProperty(GROUP_OWNER).getString())))
                // {
                List<Permission> permissionList = new ArrayList<Permission>();
                for (String permissionName : roleNode.getProperty(ROLE_PERMISSIONS).getString().split(",")) {

                    permissionList.add(getPermissionObject(permissionName));
                }
                Role role = new Role(roleNode.getName());
                role.setPermission(permissionList);
                roleList.add(role);

            }

        } catch (LoginException e) {
            e.printStackTrace();
        } catch (PathNotFoundException e) {
            e.printStackTrace();
        } catch (RepositoryException e) {
            e.printStackTrace();
        }
        Role[] roleArray = new Role[roleList.size()];
        return roleList.toArray(roleArray);

    }

    // **********************************************************************************
    // for user groups

    // add user group

    public synchronized void addGroup(String groupName, String userIds, String securityToken) {

        try {
            Node group;
            Session session = repositoryManager.getSession();
            Node roles = (Node)session.getItem("/" + USER_STORE + "/" + ALL_GROUPS);
            AccessList accessList = getAccessListFromSecurityToken(securityToken);

            if (!roles.hasNode(groupName)) {
                // super admin not allowed to create groups
                if (!accessList.getUserId().equals(SUPER_ADMIN) && isPermitted(accessList.getUserId(),
                                                                               groupName,
                                                                               new String[] {USER_GROUP_CREATE_PERMISSION})) {
                    group = roles.addNode(groupName);
                    group.setProperty(GROUP_USERS, JCREncoder.toJCRFormat(userIds));
                    group.setProperty(GROUP_OWNER, JCREncoder.toJCRFormat(accessList.getUserId()));

                }
            } else {

                group = roles.getNode(groupName);
                if (group.getProperty(GROUP_OWNER).getString().contains(JCREncoder.toJCRFormat(accessList.getUserId()))) {
                    // deleting the accessList of the users
                    for (String userId : group.getProperty(GROUP_USERS).getString().split(",")) {
                        removeAccessList(JCREncoder.toNormalFormat(userId));
                    }
                    group.setProperty(GROUP_USERS, JCREncoder.toJCRFormat(userIds));
                }

            }
            // deleting the accessList of the users
            for (String userId : userIds.split(",")) {
                removeAccessList(JCREncoder.toNormalFormat(userId));
            }
            session.save();
        } catch (LoginException e) {
            e.printStackTrace();
        } catch (PathNotFoundException e) {
            e.printStackTrace();
        } catch (RepositoryException e) {
            e.printStackTrace();
        }

    }

    // delete the user groups

    public synchronized void deleteGroup(String groupName, String securityToken) {
        AccessList accessList = getAccessListFromSecurityToken(securityToken);
        Map<String, List<String>> groupMap = new HashMap<String, List<String>>();
        try {

            Session session = repositoryManager.getSession();
            Node groups = (Node)session.getItem("/" + USER_STORE + "/" + ALL_GROUPS);

            if (groups.hasNode(groupName)) {
                Node groupNode = groups.getNode(groupName);
                if (accessList.getUserId().equals(SUPER_ADMIN) || groupNode.hasProperty(GROUP_OWNER)
                    && accessList.getUserId().equals(JCREncoder.toNormalFormat(groupNode.getProperty(GROUP_OWNER)
                        .getString()))) {
                    // deleting the accessList of the users
                    for (String userId : groupNode.getProperty(GROUP_USERS).getString().split(",")) {
                        removeAccessList(JCREncoder.toNormalFormat(userId));
                    }
                    groupNode.remove();
                    Node roles = (Node)session.getItem("/" + USER_STORE + "/" + ALL_ROLES);
                    NodeIterator roleNodes = roles.getNodes();
                    while (roleNodes.hasNext()) {
                        Node roleNode = roleNodes.nextNode();
                        NodeIterator albumNodes = roleNode.getNodes();
                        while (albumNodes.hasNext()) {
                            Node albumNode = albumNodes.nextNode();
                            if (isUserTheOwner(accessList.getUserId(), albumNode.getName())) {
                                String userGroups;
                                if (!roleNode.getName().equals(ALL_USERS_VIEW_ROLE)) {
                                    userGroups = albumNode.getProperty(ROLE_USER_GROUPS).getString();
                                    userGroups = (userGroups + ",").replace(groupName + ",", "");
                                    if (userGroups.trim().equals("")) {
                                        albumNode.setProperty(ROLE_USER_GROUPS, "");
                                    } else {
                                        albumNode.setProperty(ROLE_USER_GROUPS,
                                                              userGroups.substring(0, userGroups.length() - 1));
                                    }
                                }

                            }
                        }
                    }

                    session.save();
                }

            }

        } catch (LoginException e) {
            e.printStackTrace();
        } catch (PathNotFoundException e) {
            e.printStackTrace();
        } catch (RepositoryException e) {
            e.printStackTrace();
        }

    }

    // get user groups owned by the user. For super admin all groups

    public synchronized List getGroups(String securityToken) {
        AccessList accessList = getAccessListFromSecurityToken(securityToken);
        List<Object[]> groupList = new ArrayList<Object[]>();
        try {
            Session session = repositoryManager.getSession();
            Node roles = (Node)session.getItem("/" + USER_STORE + "/" + ALL_GROUPS);
            NodeIterator groupNodes = roles.getNodes();
            while (groupNodes.hasNext()) {
                Node groupNode = groupNodes.nextNode();
                // if user is super admin, or the owner of the group he will get
                // the group
                if (accessList.getUserId().equals(SUPER_ADMIN) || groupNode.hasProperty(GROUP_OWNER)
                    && accessList.getUserId().equals(JCREncoder.toNormalFormat(groupNode.getProperty(GROUP_OWNER)
                        .getString()))) {
                    groupList.add(new Object[] {
                                                groupNode.getName(),
                                                Arrays.asList(JCREncoder.toNormalFormat(groupNode
                                                    .getProperty(GROUP_USERS).getString()).split(","))});
                    // groupMap.put(groupNode.getName(),
                    // Arrays.asList(JCREncoder.toNormalFormat(groupNode.getProperty(GROUP_USERS).getString()).split(",")));
                }
            }

        } catch (LoginException e) {
            e.printStackTrace();
        } catch (PathNotFoundException e) {
            e.printStackTrace();
        } catch (RepositoryException e) {
            e.printStackTrace();
        }

        return groupList;

    }

    // check user is in the groups

    private synchronized boolean isUserInGroup(String userId, String groupName) {
        if (userId == null || groupName == null || userId.trim().equals("") || groupName.trim().equals("")) {
            return false;
        }
        try {

            Session session = repositoryManager.getSession();
            Node groups = (Node)session.getItem("/" + USER_STORE + "/" + ALL_GROUPS);

            if (groups.hasNode(groupName)) {
                Node groupNode = groups.getNode(groupName);
                return groupNode.getProperty(GROUP_USERS).getString().contains(JCREncoder.toJCRFormat(userId));
            }

        } catch (LoginException e) {
            e.printStackTrace();
        } catch (PathNotFoundException e) {
            e.printStackTrace();
        } catch (RepositoryException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ******************************************************************
    // for access list

    // creating access list for the users

    public synchronized AccessList createAccessList(String userId, String email) {
        if (!initialised) {
            init();
        }
        boolean newUser = true;

        Session session;
        try {
            session = repositoryManager.getSession();
            if (userId.equals(GUEST) || userId.equals(SUPER_ADMIN)) {
                newUser = false;
            } else {
                for (Value mutuallyExclusiveList : ((Node)session.getItem("/" + USER_STORE
                    + "/"
                    + MUTUALLY_EXCLUSIVE_LIST)).getProperty(DEFAULT_LISTS).getValues()) {
                    if ((!UNREGISTERED_USER_LIST.equals(mutuallyExclusiveList.getString())) && (isUserStoredInList(userId,
                                                                                                                   mutuallyExclusiveList
                                                                                                                       .getString()))) {
                        newUser = false;
                        break;
                    }
                }
            }
            if (newUser) {
                User user = getUser(userId);
                if (user == null) {
                    user = new User(userId);
                    user.setUserInfo(new UserInfo(email));
                }
                addUserToList(user, UNREGISTERED_USER_LIST);
            }
        } catch (RepositoryException e) {
            e.printStackTrace();
        }
        Map<String, List<Permission>> permissions = getPermissions(userId);
        return new AccessList(userId, permissions);
    }

    // update the access list of the users

    public synchronized AccessList updateAccessList(String userId) {
        if (!initialised) {
            init();
        }

        Map<String, List<Permission>> permissions = getPermissions(userId);
        return new AccessList(userId, permissions);
    }

    // get SecurityToken From UserId

    public synchronized String getSecurityTokenFromUserId(String userId) {
        if (accessTokenMap.containsKey(userId)) {
            Object[] accessListAndToken = accessTokenMap.get(userId);
            return (String)accessListAndToken[1];
        } else {
            return null;
        }

    }

    // get AccessList From SecurityToken

    public synchronized AccessList getAccessListFromSecurityToken(String token) {
        String userId = getUserIdFromSecurityToken(token);
        if (accessTokenMap.containsKey(userId)) {
            Object[] accessListAndToken = accessTokenMap.get(userId);
            if (((String)accessListAndToken[1]).equals(token)) {
                if (accessListAndToken[0] != null) {
                    return (AccessList)accessListAndToken[0];
                } else {
                    AccessList accessList = updateAccessList(userId);
                    putAccessListAndToken(accessList, (String)accessListAndToken[1]);
                    return accessList;
                }
            }
        }
        return createAccessList(GUEST, "");
    }

    // get AccessList From UserId

    public synchronized AccessList getAccessListFromUserId(String userId) {
        if (accessTokenMap.containsKey(userId)) {
            Object[] accessListAndToken = accessTokenMap.get(userId);
            if (accessListAndToken[0] != null) {
                return (AccessList)accessListAndToken[0];
            } else {
                AccessList accessList = updateAccessList(userId);
                putAccessListAndToken(accessList, (String)accessListAndToken[1]);
                return accessList;
            }

        } else {
            return createAccessList(GUEST, "");
        }
    }

    // get UserId From SecurityToken

    public String getUserIdFromSecurityToken(String token) {
        String userId = token.substring(0, token.length() - 25); // don't use
                                                                 // this
                                                                 // anywhere
                                                                 // else
        // getSecurityToken(userId);
        if (token.equals(getSecurityTokenFromUserId(userId))) {
            return userId;
        }
        return GUEST;

    }

    public void setFacebookAccessTokenToUser(String userId, String listName, String accesstoken) {

           Session session;
        try {

            if(isUserStoredInList(userId,listName)) {

                session = repositoryManager.getSession();

                Node listNodeUsers = (Node)session.getItem("/" + USER_STORE + "/" + USER_LISTS + "/" + listName);
                Node userNode = listNodeUsers.getNode(JCREncoder.toJCRFormat(userId));

                if(!userNode.hasProperty("photark.facebook.accesstoken")) {
                   userNode.setProperty("photark.facebook.accesstoken",accesstoken);
                }
                session.save();

            }
        } catch (LoginException e) {

            e.printStackTrace();
        } catch (RepositoryException e) {

            e.printStackTrace();
        }

    }

    public String getUserFacebookAccessToken(String userId, String listName) {

        Session session;
        String accessToken = "";
     try {

         if(isUserStoredInList(userId,listName)) {

             session = repositoryManager.getSession();

             Node listNodeUsers = (Node)session.getItem("/" + USER_STORE + "/" + USER_LISTS + "/" + listName);
             Node userNode = listNodeUsers.getNode(JCREncoder.toJCRFormat(userId));

             if(userNode.hasProperty("photark.facebook.accesstoken")) {
              accessToken = userNode.getProperty("photark.facebook.accesstoken").getValue().getString();
             }
             session.save();

         }
     } catch (LoginException e) {

         e.printStackTrace();
     } catch (RepositoryException e) {

         e.printStackTrace();
     }


        return accessToken;
    }

    // save access list and token in the access token map

    public synchronized void putAccessListAndToken(AccessList accessList, String token) {
        accessTokenMap.put(accessList.getUserId(), new Object[] {accessList, token});

    }

    // remove access list and token from the access token map

    public synchronized void removeAccessListAndToken(String userId) {
        if (accessTokenMap.containsKey(userId)) {
            accessTokenMap.remove(userId);
        }

    }

    // remove access list from the access token map

    private synchronized void removeAccessList(String userId) {
        if (accessTokenMap.containsKey(userId)) {
            accessTokenMap.put(userId, new Object[] {null, getSecurityTokenFromUserId(userId)});
            // putAccessListAndToken(null, );

        }

    }

    // remove all access list from the access token map

    private synchronized void removeAllAccessList() {
        for (String userId : accessTokenMap.keySet()) {
            removeAccessList(userId);
        }

    }

    // check whether the user in the access token map

    public boolean isUserActive(String userId) {
        return accessTokenMap.containsKey(userId);
    }

}
