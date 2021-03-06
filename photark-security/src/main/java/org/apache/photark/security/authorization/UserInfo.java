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

package org.apache.photark.security.authorization;

import java.io.Serializable;

import org.oasisopen.sca.annotation.Remotable;

/**
 * Model representing Information of an User of Gallery
 */

public class UserInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3735573328644358405L;
	private String displayName = "";
	private String email = "";
	private String realName = "";
	private String website = "";

	public UserInfo(String email) {
		this.email = email;
	}

	public UserInfo(String displayName, String email, String realName,
			String website) {
		super();
		this.displayName = displayName;
		this.email = email;
		this.realName = realName;
		this.website = website;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getEmail() {
		return email;
	}

	public String getRealName() {
		return realName;
	}

	public String getWebsite() {
		return website;
	}
}
