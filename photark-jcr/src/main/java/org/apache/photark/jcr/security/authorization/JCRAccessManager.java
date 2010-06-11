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

import java.util.ArrayList;

import javax.jcr.LoginException;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.photark.jcr.JCRRepositoryManager;
import org.apache.photark.security.authorization.AccessList;
import org.apache.photark.security.authorization.User;
import org.apache.photark.security.authorization.UserInfo;
import org.apache.photark.security.authorization.services.AccessManager;
import org.oasisopen.sca.annotation.Destroy;
import org.oasisopen.sca.annotation.Init;
import org.oasisopen.sca.annotation.Reference;
import org.oasisopen.sca.annotation.Remotable;
import org.oasisopen.sca.annotation.Scope;

@Remotable
@Scope("COMPOSITE")
public class JCRAccessManager implements AccessManager {

	/** JCR Repository Manager **/
	private static JCRRepositoryManager repositoryManager;
	AccessList accessList;

	public JCRAccessManager() {

	}

	@Reference(name = "repositoryManager")
	protected void setRepositoryManager(JCRRepositoryManager repositoryManager) {
		this.repositoryManager = repositoryManager;
	}

	@Init
	public void init() {

		try {
			Session session = repositoryManager.getSession();
			Node root = session.getRootNode();

			if (!root.hasNode("userStore")) {

				Node userStore = root.addNode("userStore");
				Node roles = userStore.addNode("roles");
				Node allUsers = userStore.addNode("allUsers");
				Node registeredUserRole = roles.addNode("registeredUserRole");
				Node unRegisteredUserRole = roles
						.addNode("unRegisteredUserRole");
				session.save();
			}

		} catch (Exception e) {
			// FIXME: ignore for now
			e.printStackTrace();
		} finally {
			// repositoryManager.releaseSession();
		}
	}

	public synchronized void addUserToRole(User user, String node) {
		init();
		try {
			Session session = repositoryManager.getSession();
			Node subRoleNode = (Node) session.getItem("/userStore/roles/"
					+ node);
			Node userNode;
			UserInfo userInfo = user.getUserInfo();
			if (subRoleNode != null) {
				if (subRoleNode.hasNode(toJCRFormat(user.getUserId()))) {
					userNode = subRoleNode
							.getNode(toJCRFormat(user.getUserId()));
				} else {
					userNode = subRoleNode
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
		} catch (Exception e) {
			// FIXME: ignore for now
			e.printStackTrace();
		} finally {
			// repositoryManager.releaseSession();

		}

	}

	public synchronized User getUser(String userId) {
		init();
		User user = null;
		try {
			Session session = repositoryManager.getSession();
			Node allUsers = (Node) session.getItem("/userStore/allUsers");
			Node userNode;

			if (allUsers != null) {

				if (allUsers.hasNode(toJCRFormat(userId))) {
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
			}

		} catch (Exception e) {
			// FIXME: ignore for now
			e.printStackTrace();
		} finally {
			// repositoryManager.releaseSession();
		}
		return user;
	}

	@Destroy
	public void destroy() {
		// repositoryManager.releaseSession();
	}

	public String getCurrentUserInfo() {

		return "Works";
	}

	public boolean isUserStoredInRole(String userId, String node) {
		init();
		try {
			Session session = repositoryManager.getSession();
			Node subRoleNode = (Node) session.getItem("/userStore/roles/"
					+ node);
			if (subRoleNode != null && subRoleNode.hasNode(toJCRFormat(userId))) {
				return true;
			}
		} catch (Exception e) {
			// FIXME: ignore for now
			e.printStackTrace();
		} finally {
			// repositoryManager.releaseSession();
		}
		return false;
	}

	public synchronized AccessList creatAccessList(String userId, String email) {

		User user = new User(userId);
		user.setUserInfo(new UserInfo(email));
		if (!isUserStoredInRole(userId, "registeredUserRole")) {
			if (!isUserStoredInRole(userId, "unRegisteredUserRole")) {
				addUserToRole(user, "unRegisteredUserRole");
			}
		}
		AccessList accessList = new AccessList(userId, new ArrayList<String>());
		this.accessList = accessList;
		return accessList;
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
			Node subRoleNode = (Node) session.getItem("/userStore/roles/"
					+ node);
		
			if (subRoleNode != null) {
				if (subRoleNode.hasNode(toJCRFormat(userId))) {
				Node	userNode = subRoleNode
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

}
