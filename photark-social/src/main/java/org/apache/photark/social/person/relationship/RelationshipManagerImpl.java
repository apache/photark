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

package org.apache.photark.social.person.relationship;

import org.apache.photark.social.exception.PhotArkSocialException;

public class RelationshipManagerImpl implements RelationshipManager{

	public String getRelationshipStatus(String veiwer, String owner)
			throws PhotArkSocialException {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean requestRelationship(String viewer, String owner)
			throws PhotArkSocialException {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean acceptRelationshipRequest(String viewer, String owner)
			throws PhotArkSocialException {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean ignoreRelationship(String viewer, String owner)
			throws PhotArkSocialException {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean removeRelationship(String owner, String viewer)
			throws PhotArkSocialException {
		// TODO Auto-generated method stub
		return false;
	}

	public String[] getRelationshipList(String loggedUser)
			throws PhotArkSocialException {
		// TODO Auto-generated method stub
		return null;
	}

	public String[] getPendingRelationshipRequests(String owner)
			throws PhotArkSocialException {
		// TODO Auto-generated method stub
		return null;
	}

}
