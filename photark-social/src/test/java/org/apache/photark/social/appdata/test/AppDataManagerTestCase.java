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

package org.apache.photark.social.appdata.test;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.jcr.LoginException;
import javax.jcr.RepositoryException;

import junit.framework.Assert;

import org.apache.photark.services.jcr.JCRRepositoryManager;
import org.apache.photark.social.appdata.AppDataManager;
import org.apache.photark.social.appdata.AppDataManagerImpl;
import org.apache.photark.social.exception.PhotArkSocialException;
import org.junit.Test;

public class AppDataManagerTestCase {
	@Test
	public void testAppDataManagerImpl() throws IOException,
			PhotArkSocialException, LoginException, RepositoryException {
		JCRRepositoryManager repositoryManager = new JCRRepositoryManager();
		String dir = "socialtest";
		File homeDir = File.createTempFile(dir, "");
		homeDir.delete();
		homeDir.mkdir();
		repositoryManager.setRepositoryHome(dir);
		AppDataManager appDataManager = new AppDataManagerImpl(
				repositoryManager);
		Map<String, String> data1 = new HashMap<String, String>();
		data1.put("albumName", "USTrip");
		data1.put("a", "1");
		// Test saveData() and getData()
		appDataManager.savePersonData("user1", data1);
		Map<String, String> data2 = null;
		data2 = appDataManager.getPersonData("user1", null);
		Assert.assertNotNull(data2);
		Assert.assertEquals(data1.size(), data2.size());
		Assert.assertTrue(data2.containsKey("albumName"));
		Assert.assertTrue(data2.containsValue("USTrip"));
		Assert.assertNotNull(data2.get("albumName"));
		Assert.assertEquals("1", data2.get("a"));
		Assert.assertTrue(data2.containsKey("a"));
		Assert.assertTrue(data2.containsValue("1"));
		Assert.assertNotNull(data2.get("a"));
		Assert.assertEquals("1", data2.get("a"));
		Set<String> fields = new HashSet<String>();
		fields.add("a");
		data1.put("a", "2");
		// Test updateData()
		appDataManager.updatePersonData("user1", null, null, fields, data1);
		data2 = appDataManager.getPersonData("user1", null);
		Assert.assertNotNull(data2);
		Assert.assertEquals(data1.size(), data2.size());
		Assert.assertTrue(data2.containsKey("albumName"));
		Assert.assertTrue(data2.containsValue("USTrip"));
		Assert.assertNotNull(data2.get("albumName"));
		Assert.assertEquals("2", data2.get("a"));
		Assert.assertTrue(data2.containsKey("a"));
		Assert.assertTrue(data2.containsValue("2"));
		Assert.assertFalse(data2.containsValue("1"));
		// Test deleteData()
		appDataManager.deletePersonData("user1", null, fields);
		data2 = appDataManager.getPersonData("user1", null);
		Assert.assertFalse(data2.containsKey("a"));
		Assert.assertFalse(data2.containsValue("2"));
		Assert.assertEquals(1, data2.size());

	}
}
