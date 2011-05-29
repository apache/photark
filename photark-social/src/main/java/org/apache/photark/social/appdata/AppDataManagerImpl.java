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

package org.apache.photark.social.appdata;

import java.util.Map;
import java.util.Set;

import org.apache.photark.social.exception.PhotArkSocialException;

public class AppDataManagerImpl implements AppDataManager{

	public Map<String, String> getPersonData(String userId, String groupId)
			throws PhotArkSocialException {
		// TODO Auto-generated method stub
		return null;
	}

	public Map<String, Map<String, String>> getPeopleData(String[] userIds,
			String groupId) throws PhotArkSocialException {
		// TODO Auto-generated method stub
		return null;
	}

	public void savePersonData(String userId, Map<String, String> values)
			throws PhotArkSocialException {
		// TODO Auto-generated method stub
		
	}

	public void deletePersonData(String userId, String groupId,
			Set<String> fields) throws PhotArkSocialException {
		// TODO Auto-generated method stub
		
	}

	public void updatePersonData(String userId, String groupId, String appId,
			Set<String> fields, Map<String, String> values)
			throws PhotArkSocialException {
		// TODO Auto-generated method stub
		
	}

	

}
