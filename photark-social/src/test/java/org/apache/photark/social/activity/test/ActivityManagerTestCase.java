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

package org.apache.photark.social.activity.test;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import javax.jcr.LoginException;
import javax.jcr.RepositoryException;

import junit.framework.Assert;

import org.apache.photark.services.jcr.JCRRepositoryManager;
import org.apache.photark.social.Activity;
import org.apache.photark.social.PhotArkSocialException;
import org.apache.photark.social.services.ActivityService;
import org.apache.photark.social.services.impl.JCRActivityServiceImpl;
import org.junit.Test;

public class ActivityManagerTestCase {

    @Test
    public void testActivityManagerImpl() throws IOException, PhotArkSocialException, LoginException,
        RepositoryException {
        JCRRepositoryManager repositoryManager = new JCRRepositoryManager();
        String dir = "socialtest";
        File homeDir = File.createTempFile(dir, "");
        homeDir.delete();
        homeDir.mkdir();
        repositoryManager.setRepositoryHome(dir);
        ActivityService activityManager = new JCRActivityServiceImpl(repositoryManager);
        // Test save activity & get activity methods
        Activity activity1 = new Activity();
        activity1.setId("1");
        activity1.setBody("User has posted an album");
        activity1.setPostedTime(new Date());
        activity1.setTitle("New Album");
        activityManager.saveActivity("user1", activity1);
        Activity activity2 = activityManager.getActivity("user1", null, null, "1");
        Assert.assertNotNull(activity2);
        Assert.assertEquals(activity1.getBody(), activity2.getBody());
        Assert.assertEquals(activity1.getPostedTime(), activity2.getPostedTime());
        Activity activity3 = new Activity();
        activity3.setId("2");
        activity3.setBody("User has tagges a photo");
        activity3.setPostedTime(new Date());
        activity3.setTitle("New Phhot Tagged");
        activityManager.saveActivity("user1", activity3);
        // Test get activities method
        String[] ids = new String[] {"1", "2"};
        Activity[] activities = activityManager.getActivities("user1", null, null, null, ids);
        Assert.assertNotNull(activities);
        Assert.assertEquals(2, activities.length);
        // Test remove activity method
        activityManager.deleteActivity("user1", "1");
        activity2 = activityManager.getActivity("user1", null, null, "1");
        Assert.assertNull(activity2);
    }
}
