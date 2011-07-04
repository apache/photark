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

package org.apache.photark.social.person.test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.jcr.LoginException;
import javax.jcr.RepositoryException;

import org.apache.photark.services.jcr.JCRRepositoryManager;
import org.apache.photark.social.exception.PhotArkSocialException;
import org.apache.photark.social.person.Person;
import org.apache.photark.social.person.PersonManager;
import org.apache.photark.social.person.PersonManagerImpl;
import org.junit.Assert;
import org.junit.Test;

public class PersonManagerTestCase {
    @Test
    public void testPersonManagerImpl() throws IOException, PhotArkSocialException, LoginException, RepositoryException {
        JCRRepositoryManager repositoryManager = new JCRRepositoryManager();
        String dir = "socialtest";
        File homeDir = File.createTempFile(dir, "");
        homeDir.delete();
        homeDir.mkdir();
        repositoryManager.setRepositoryHome(dir);
        PersonManager pm = new PersonManagerImpl(repositoryManager);
        Person person = new Person();
        person.setId("testuser1");
        person.setDisplayName("TestUser1");
        person.setFirstName("test");
        person.setLastName("user1");
        Calendar calendar = new GregorianCalendar(10, 9, 10);
        person.setBirthday(calendar.getTime());
        List<String> activities = new ArrayList<String>();
        activities.add("Movies");
        activities.add("Cricket");
        person.setActivities(activities);
        pm.savePerson("testuser1", person);
        Person person2 = pm.getPerson("testuser1");
        Assert.assertNotNull(person2);
        Assert.assertEquals("testuser1", person2.getId());
        Assert.assertEquals(person.getDisplayName(), person2.getDisplayName());
        Assert.assertNotNull(person2.getBirthday());
        Assert.assertEquals(calendar.getTime(), person2.getBirthday());
        Assert.assertNotNull(person2.getActivities());
        Assert.assertEquals("Movies", person2.getActivities().get(0));
        pm.removePerson("testuser1");
        person2 = pm.getPerson("testuser1");
        Assert.assertNull(person2);

    }
}
