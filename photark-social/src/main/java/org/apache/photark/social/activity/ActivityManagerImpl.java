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

package org.apache.photark.social.activity;

import java.util.Set;

import org.apache.photark.social.exception.PhotArkSocialException;
import org.apache.photark.social.util.FilterOptions;

public class ActivityManagerImpl implements ActivityManager {

    public Activity createActivity(String userId, String groupId, Set<String> fields) throws PhotArkSocialException {
        // TODO Auto-generated method stub
        return null;
    }

    public void saveActivity(String userId, Activity activity) throws PhotArkSocialException {
        // TODO Auto-generated method stub

    }

    public void updateActivity(String userId, Activity activity) throws PhotArkSocialException {
        // TODO Auto-generated method stub

    }

    public void deleteActivity(String userId, String activityId) throws PhotArkSocialException {
        // TODO Auto-generated method stub

    }

    public void deleteActivities(String userId, String groupId, Set<String> activityIds) throws PhotArkSocialException {
        // TODO Auto-generated method stub

    }

    public Activity[] getActivities(String[] userIds, String groupId, Set<String> fields, FilterOptions filters) throws PhotArkSocialException {
        // TODO Auto-generated method stub
        return null;
    }

    public Activity[] getActivities(String userId, String groupId, Set<String> fields, FilterOptions filters, String[] activityIds)
        throws PhotArkSocialException {
        // TODO Auto-generated method stub
        return null;
    }

    public Activity getActivity(String userId, String groupId, Set<String> fields, String activityId) throws PhotArkSocialException {
        // TODO Auto-generated method stub
        return null;
    }

}
