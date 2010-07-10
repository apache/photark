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

import org.apache.photark.jcr.JCRRepositoryManager;
import org.apache.photark.jcr.services.JCRAlbumImpl;
import org.apache.photark.security.authorization.AccessList;
import org.apache.photark.security.authorization.Permission;
import org.apache.photark.security.authorization.User;
import org.apache.photark.security.authorization.UserInfo;
import org.apache.photark.security.authorization.services.AccessManager;
import org.apache.photark.security.authorization.services.JSONRPCSecurityManager;
import org.apache.photark.services.album.Album;
import org.oasisopen.sca.annotation.*;

import javax.jcr.*;
import javax.jcr.Property;
import java.util.*;

@Remotable
@Scope("COMPOSITE")
public class JCRAccessManager implements AccessManager {

	/** JCR Repository Manager **/
	private static JCRRepositoryManager repositoryManager;
	//AccessList accessList;

	public JCRAccessManager() {

	}

	@Reference(name = "repositoryManager")
	protected void setRepositoryManager(JCRRepositoryManager repositoryManager) {
		JCRAccessManager.repositoryManager = repositoryManager;
	}

	@Init
	public synchronized void init() {

		try {
			Session session = repositoryManager.getSession();
			Node root = session.getRootNode();

           // List<Set<String>> mutuallyExclusiveList = new ArrayList<Set<String>>();

            Set<String> Default = new HashSet<String>();
            Default.add("superAdminRole");
            Default.add("registeredUserRole");
            Default.add("unRegisteredUserRole");
            Default.add("blockedUserRole");

           // mutuallyExclusiveList.add(Default);

            if (!root.hasNode("userStore")) {

                Node userStore = root.addNode("userStore");

                Node mutuallyExclusiveRoles = userStore.addNode("mutuallyExclusiveRoles");
                mutuallyExclusiveRoles.setProperty("_default", new String[]{"superAdminRole", "registeredUserRole", "unRegisteredUserRole", "blockedUserRole"}) ;

                userStore.addNode("allUsers");

                Node roles = userStore.addNode("roles");

                Node blockedUserRole = roles.addNode("blockedUserRole");
                blockedUserRole.addNode("users");
                blockedUserRole.addNode("permissions");
                blockedUserRole.setProperty("parents", new String[]{});

                Node unRegisteredUserRole = roles.addNode("unRegisteredUserRole");
                unRegisteredUserRole.addNode("users");
                Node unRegisteredUserRolePermissions = unRegisteredUserRole.addNode("permissions");
                unRegisteredUserRole.setProperty("parents", new String[]{"blockedUserRole"});

                Node registeredUserRole = roles.addNode("registeredUserRole");
                registeredUserRole.addNode("users");
                Node registeredUserRolePermissions = registeredUserRole.addNode("permissions");
                registeredUserRole.setProperty("parents", new String[]{"unRegisteredUserRole"});

                Node superAdminRole = roles.addNode("superAdminRole");
                Node superAdminRoleUsers = superAdminRole.addNode("users");
                Node superAdminRolePermissions = superAdminRole.addNode("permissions");
                superAdminRole.setProperty("parents", new String[]{"registeredUserRole"});

                Node userNode=   superAdminRoleUsers.addNode("SuperAdmin");
				userNode.setProperty("displayName","SuperAdmin");
				userNode.setProperty("email", "");
				userNode.setProperty("realName", "");
				userNode.setProperty("webSite", "");
				userNode.setProperty("userId","SuperAdmin");

                 userNode=   unRegisteredUserRole.addNode("UnRegisteredUser");
				userNode.setProperty("displayName","UnRegisteredUser");
				userNode.setProperty("email", "");
				userNode.setProperty("realName", "");
				userNode.setProperty("webSite", "");
				userNode.setProperty("userId","UnRegisteredUser");

                unRegisteredUserRolePermissions.addNode("boston").setProperty("permissions", new String[]{"viewImages"});
                // unRegisteredUserRolePermissions.addNode("vegas").setProperty("permissions",new String[]{"view"});

                //registeredUserRolePermissions.addNode("boston").setProperty("permissions", new String[]{"view"});
                registeredUserRolePermissions.addNode("vegas").setProperty("permissions", new String[]{"viewImages"});

//                superAdminRolePermissions.addNode("boston").setProperty("permissions", new String[]{"viewImages", "addImages", "deleteImages", "deleteAlbum", "editAlbumDescription"});
//                superAdminRolePermissions.addNode("vegas").setProperty("permissions", new String[]{"viewImages", "addImages", "deleteImages", "deleteAlbum", "editAlbumDescription"});

                registeredUserRolePermissions.setProperty("permissions", new String[]{"createAlbum", "deleteAlbum.own"
                        , "createGroupRole", "deleteGroupRole.own", "manageGroupRole.own"
                        , "viewImagesOnAlbum.own", "addImagesToAlbum.own", "deleteImagesFromAlbum.own", "editAlbumDescription.own"});

                superAdminRolePermissions.setProperty("permissions", new String[]{"createAlbum", "deleteAlbum.own", "deleteAlbum.others"
                        , "createGroupRole", "deleteGroupRole.own", "deleteGroupRole.others", "manageGroupRole.own", "manageGroupRole.others", "manageMainRoles"
                        , "viewImagesOnAlbum.own", "addImagesToAlbum.own", "deleteImagesFromAlbum.own", "editAlbumDescription.own"
                        , "viewImagesOnAlbum.others", "addImagesToAlbum.others", "deleteImagesFromAlbum.others", "editAlbumDescription.others"});

                Node allPermissions = userStore.addNode("allPermissions");

                allPermissions.addNode("createAlbum").setProperty("desc", "Allow the users to crete a new Albums");

                allPermissions.addNode("deleteAlbum.own").setProperty("desc", "Allow the users to delete the Albums they own");
                allPermissions.addNode("deleteAlbum.others").setProperty("desc", "Allow the users to delete the Albums they dont own");

                allPermissions.addNode("createGroupRole").setProperty("desc", "Allow the users to create Groups");
                allPermissions.addNode("deleteGroupRole.own").setProperty("desc", "Allow the users to delete the Groups they own");
                allPermissions.addNode("deleteGroupRole.others").setProperty("desc", "Allow the users to delete the Groups they dont own");
                allPermissions.addNode("manageGroupRole.own").setProperty("desc", "Allow the users to change the users and permissions of the Groups they own");
                allPermissions.addNode("manageGroupRole.others").setProperty("desc", "Allow the users to change the users and permissions of the Groups they dont own");

                allPermissions.addNode("manageMainRoles").setProperty("desc", "Allow the users to change the users and permissions of the Main roles (superAdminRole, registeredUserRole, unRegisteredUserRole, blockedUserRole)");

                allPermissions.addNode("viewImagesOnAlbum.own").setProperty("desc", "Allow the users to view their album images");
                allPermissions.addNode("addImagesToAlbum.own").setProperty("desc", "Allow the users to add new images to their album");
                allPermissions.addNode("deleteImagesFromAlbum.own").setProperty("desc", "Allow the users to delete images from their album");
                allPermissions.addNode("editAlbumDescription.own").setProperty("desc", "Allow the users to edit their Album description");

                allPermissions.addNode("viewImagesOnAlbum.others").setProperty("desc", "Allow the users to view Others album images");
                allPermissions.addNode("addImagesToAlbum.others").setProperty("desc", "Allow the users to add new images to Others album");
                allPermissions.addNode("deleteImagesFromAlbum.others").setProperty("desc", "Allow the users to delete images from Others album");
                allPermissions.addNode("editAlbumDescription.others").setProperty("desc", "Allow the users to edit Others Album description");

                //per Album permissions
                allPermissions.addNode("viewImages").setProperty("desc", "Allow the users to view the album images");
                allPermissions.addNode("addImages").setProperty("desc", "Allow the users to add new images to the album");
                allPermissions.addNode("deleteImages").setProperty("desc", "Allow the users to delete images from the album");
                allPermissions.addNode("editAlbumDescription").setProperty("desc", "Allow the users to edit Album description");

                session.save();
			}

		} catch (Exception e) {
			// FIXME: ignore for now
			e.printStackTrace();
		}
    }

	public synchronized void addUserToRole(User user, String roleName) {
		init();
		try {
			Session session = repositoryManager.getSession();
            deleteMutuallyExclusiveRoles(user.getUserId(), roleName);

			Node subRoleNodeUsers = (Node) session.getItem("/userStore/roles/" + roleName+"/users");
			Node userNode;
			UserInfo userInfo = user.getUserInfo();
			if (subRoleNodeUsers != null) {
				if (subRoleNodeUsers.hasNode(toJCRFormat(user.getUserId()))) {
					userNode = subRoleNodeUsers
							.getNode(toJCRFormat(user.getUserId()));
				} else {
					userNode = subRoleNodeUsers
							.addNode(toJCRFormat(user.getUserId()));
				}
				userNode.setProperty("displayName", toJCRFormat(userInfo
						.getDisplayName()));
				userNode.setProperty("email", toJCRFormat(userInfo.getEmail()));
				userNode.setProperty("realName", toJCRFormat(userInfo
						.getRealName()));
				userNode.setProperty("webSite", toJCRFormat(userInfo
						.getWebsite()));
				userNode.setProperty("userId", toJCRFormat(user.getUserId()));
			}
           session.save();
            addToAllUsers(user);

		} catch (Exception e) {
			// FIXME: ignore for now
			e.printStackTrace();
		}

    }

    private synchronized void addToAllUsers(User user) throws RepositoryException {
        Session session =repositoryManager.getSession();
        UserInfo userInfo =user.getUserInfo() ;
        Node userNode;
        Node allUsers = (Node) session.getItem("/userStore/allUsers");
        if (allUsers != null) {

            if (allUsers.hasNode(toJCRFormat(user.getUserId()))) {
                userNode = allUsers.getNode(toJCRFormat(user.getUserId()));
            } else {
                userNode = allUsers.addNode(toJCRFormat(user.getUserId()));
            }
            userNode.setProperty("displayName", toJCRFormat(userInfo
                    .getDisplayName()));
            userNode.setProperty("email", toJCRFormat(userInfo.getEmail()));
            userNode.setProperty("realName", toJCRFormat(userInfo
                    .getRealName()));
            userNode.setProperty("webSite", toJCRFormat(userInfo
                    .getWebsite()));
            userNode.setProperty("userId", toJCRFormat(user.getUserId()));
        }
        session.save();
    }

    private synchronized void deleteMutuallyExclusiveRoles(String userId, String roleName) {


        try {
            Session session = repositoryManager.getSession();
            Node allMutuallyExclusiveRoles = (Node) session.getItem("/userStore/mutuallyExclusiveRoles");
            for (PropertyIterator pi = allMutuallyExclusiveRoles.getProperties(); pi.hasNext();) {
                Property p = pi.nextProperty();

                if (!p.getName().equals("jcr:primaryType")) {
                    ArrayList<String> list =new ArrayList<String>();
                    for(Value v :p.getValues()){
                            list.add(v.getString());
                    }

                    if (list.contains(roleName)) {
                        for (Object aList : list) {
                            String role = (String) aList;
                            if (!role.equals(roleName)) {
                                removeUserFromRole(userId, role);
                            }
                        }
                    }
                }


            }
            session.save();
        } catch (PathNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (RepositoryException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
        public synchronized User getUser(String userId) {
		init();
		User user = null;
		try {
			Session session = repositoryManager.getSession();
//            //to delete userStore
//            Node root =  session.getRootNode();
//            Node userStoreT= root.getNode("userStore");
//            userStoreT.remove();
//            session.save();
//            //
			Node allUsers = (Node) session.getItem("/userStore/allUsers");
			Node userNode;
				if (allUsers != null&& allUsers.hasNode(toJCRFormat(userId))) {
					userNode = allUsers.getNode(toJCRFormat(userId));
					user = new User(userId);
					UserInfo userInfo = new UserInfo(
							toNormalFormat(userNode.getProperty("displayName").getValue().getString()),
							toNormalFormat(userNode.getProperty("email").getValue().getString()),
							toNormalFormat(userNode.getProperty("realName").getValue().getString()),
							toNormalFormat(userNode.getProperty("webSite").getValue().getString())
							);
					user.setUserInfo(userInfo);
				}
		} catch (Exception e) {
			// FIXME: ignore for now
			e.printStackTrace();
		}
            return user;
	}

	@Destroy
	public void destroy() {
		// repositoryManager.releaseSession();
	}

	public synchronized boolean isUserStoredInRole(String userId, String roleName) {
		init();
		try {
			Session session = repositoryManager.getSession();
			Node subRoleNodeUsers = (Node) session.getItem("/userStore/roles/"
					+ roleName+"/users");
			if (subRoleNodeUsers != null && subRoleNodeUsers.hasNode(toJCRFormat(userId))) {
				return true;
			}
		} catch (Exception e) {
			// FIXME: ignore for now
			e.printStackTrace();
		}
        return false;
	}

    public synchronized AccessList createAccessList(String userId, String email) {
        boolean newUser = true;
        User user = new User(userId);
        user.setUserInfo(new UserInfo(email));
        Session session = null;
        try {
            session = repositoryManager.getSession();
            for (Value mutuallyExclusiveRole : ((Node) session.getItem("/userStore/mutuallyExclusiveRoles")).getProperty("_default").getValues()) {
                if ((!"unRegisteredUserRole".equals(mutuallyExclusiveRole.getString())) && (isUserStoredInRole(userId, mutuallyExclusiveRole.getString()))) {
                    newUser = false;
                    break;
                }
            }
            if (newUser) {

                addUserToRole(user, "unRegisteredUserRole");

            }

        } catch (RepositoryException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

       Map<String, List<Permission>>  permissions= getPermissions(userId);
//		AccessList accessList = new AccessList(userId, permissions);
//		//this.accessList = accessList;
//		return accessList;

	return new AccessList(userId, permissions);
	}

    private synchronized Map<String, List<Permission>>  getPermissions(String userId) {

        List<String> list = new ArrayList<String>();
        try {
            Session session = repositoryManager.getSession();
            Node RolesNode = (Node) session.getItem("/userStore/roles");

            for (NodeIterator ni = RolesNode.getNodes(); ni.hasNext();) {
                Node n = ni.nextNode();
                if (isUserStoredInRole(userId, n.getName())) {
                    list.add(n.getName());
                }
            }
            return getPermissionsForUserInRoles(list);

        } catch (LoginException e) {
            e.printStackTrace();
        } catch (PathNotFoundException e) {
            e.printStackTrace();
        } catch (RepositoryException e) {
            e.printStackTrace();
        }
         return getPermissionsForUserInRoles(list);
    }

    private synchronized Map<String, List<Permission>> getPermissionsForUserInRoles(List<String> roles) {
        Map<String, List<Permission>> permissions = new HashMap<String,  List<Permission>>();
        List<String> addedRoles = new ArrayList<String>();
        Session session;
        try {
            session = repositoryManager.getSession();
            for (int i=0 ; i<roles.size();i++ ) {
          //  for (String role : roles) {
                String role=roles.get(i);
                if (!addedRoles.contains(role)) {
                    Node aRolePermissions = (Node) session.getItem("/userStore/roles/" + role + "/permissions");
                    Node aRole = (Node) session.getItem("/userStore/roles/" + role);
                    //get the role based  permissions
                    if (aRolePermissions.hasProperty("permissions")) {
                        if (!permissions.containsKey("_default")) {
                            // Value[] permissionValues = aRolePermissions.getProperty("permissions").getValues();
                            Set<Permission> permissionSet = new HashSet<Permission>();
                            for (Value permissionValue : aRolePermissions.getProperty("permissions").getValues()) {
                                permissionSet.add(getPermissionObject(permissionValue.getString()));
                            }
                            permissions.put("_default", new ArrayList<Permission>(permissionSet));
                        } else {
                            Set<Permission> permissionSet = new HashSet<Permission>();
                            for (Value permissionValue : aRolePermissions.getProperty("permissions").getValues()) {
                                permissionSet.add(getPermissionObject(permissionValue.getString()));
                            }
                            permissionSet.addAll(permissions.get("_default"));
                            permissions.remove("_default");
                            permissions.put("_default", new ArrayList<Permission>(permissionSet));

                        }
                    }
                           //get the resource based  permissions
                    for (NodeIterator ni = aRolePermissions.getNodes(); ni.hasNext();) {
                        Node n = ni.nextNode();
                       // for (PropertyIterator pi = n.getProperties(); pi.hasNext();) {
                            Property p = n.getProperty("permissions");
                           // if (p.getName().startsWith("_")) {
                                if (!permissions.containsKey(n.getName())) {
                                    Set<Permission> permissionSet = new HashSet<Permission>();

                                    for (Value permissionValue : p.getValues()) {
                                        permissionSet.add(getPermissionObject(permissionValue.getString()));
                                    }
                                    permissions.put(n.getName(), new ArrayList<Permission>(permissionSet));
                                    //   permissions.put(p.getName(), new HashSet<Value>(Arrays.asList(p.getValues())));
                                } else {
                                    Set<Permission> permissionSet = new HashSet<Permission>();
                                    for (Value permissionValue : p.getValues()) {
                                        permissionSet.add(getPermissionObject(permissionValue.getString()));
                                    }
                                    permissionSet.addAll(permissions.get(n.getName()));
                                    permissions.remove(n.getName());
                                    permissions.put(n.getName(), new ArrayList<Permission>(permissionSet));
                                    // permissions.get(p.getName()).addAll(Arrays.asList(p.getValues()));
                                }
//                            }
//                        }
                    }



                    addedRoles.add(aRole.getName());
                    if (aRole.hasProperty("parents")) {
                        Value[] values = aRole.getProperty("parents").getValues();
                        for (Value value : values) {
                            if (!addedRoles.contains(value.getString())) {
                                roles.add(value.getString());
                            }

                        }

                    }
                }
            }



        } catch (LoginException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (RepositoryException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

          return permissions;
    }

    private synchronized Permission getPermissionObject(String permissionName) {
        Session session;
        try {
            session = repositoryManager.getSession();
            Node allPermissions = (Node) session.getItem("/userStore/allPermissions");
            if (allPermissions.hasNode(permissionName)) {
                return new Permission(allPermissions.getNode(permissionName).getName(), allPermissions.getNode(permissionName).getProperty("desc").getString());
            }
        } catch (ValueFormatException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (LoginException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (PathNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (RepositoryException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return null;  //To change body of created methods use File | Settings | File Templates.
    }

    private String toJCRFormat(String string) {
		if (string != null) {
			string = string.replaceAll("/", "#1");
			string = string.replaceAll(":", "#2");
		}
		return string;

	}

	private String toNormalFormat(String string) {
		if (string != null) {
			string = string.replaceAll("\\#1", "/");
			string = string.replaceAll("\\#2", ":");
		}
		return string;

	}

	public synchronized void removeUserFromRole(String userId, String node) {
		Session session;
		try {
			session = repositoryManager.getSession();
			Node subRoleNodeUsers = (Node) session.getItem("/userStore/roles/"
					+ node+"/users");

			if (subRoleNodeUsers != null) {
				if (subRoleNodeUsers.hasNode(toJCRFormat(userId))) {
				Node	userNode = subRoleNodeUsers
							.getNode(toJCRFormat(userId));
				userNode.remove();
				session.save();
				}
			}
		} catch (LoginException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

    public boolean isPermitted(AccessList accessList, String resourceName, String[] permissionNames) {
        if (accessList == null) {
            return false;
        }
        Map<String, List<Permission>> userPermissions = accessList.getPermissions();
        List allowedPermissions = Arrays.asList(permissionNames);
        List<Permission> permissions = new ArrayList<Permission>();
        if (userPermissions.containsKey(resourceName)) {
            permissions = (userPermissions.get(resourceName));
        }
        for (Permission permission : permissions) {
            if (allowedPermissions.contains(permission.getPermission())) {
                if (!permission.getPermission().endsWith(".others") || !permission.getPermission().endsWith(".own")) {
                    return true;
                }
            }
        }

        if (userPermissions.containsKey("_default")) {
            permissions = (userPermissions.get("_default"));
        }
        for (Permission permission : permissions) {
            if (allowedPermissions.contains(permission.getPermission())) {

                // System.out.println(resourceName+ " added");
                if (permission.getPermission().endsWith(".own") && isUserTheOwner(accessList.getUserId(), resourceName)) {
                    return true;

                } else if (permission.getPermission().endsWith(".others") && !isUserTheOwner(accessList.getUserId(), resourceName)) {
                    return true;

                } else if ((!permission.getPermission().endsWith(".others")) && (!permission.getPermission().endsWith(".own"))) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isUserTheOwner(String userId, String albumName) {
        Album album = new JCRAlbumImpl(repositoryManager, albumName);
        return Arrays.asList(album.getOwners()).contains(userId);
    }

}
