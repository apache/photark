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
import org.apache.photark.social.Person;
import org.apache.photark.social.PhotArkSocialException;
import org.apache.photark.social.services.PersonService;
import org.apache.photark.social.services.impl.JCRPersonServiceImpl;
import org.apache.tuscany.sca.node.Contribution;
import org.apache.tuscany.sca.node.ContributionLocationHelper;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class PersonManagerTestCase {
    private static Node node;
    private static PersonService personService;

    @BeforeClass
    public static void BeforeClass() {
        try {
            String contribution = ContributionLocationHelper.getContributionLocation("person-test.composite");
            node = NodeFactory.newInstance().createNode("person-test.composite", new Contribution("gallery", contribution));
            node.start();
            
            personService = node.getService(PersonService.class, "PersonServiceComponent");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterClass
    public static void AfterClass() {
        node.stop();
    }


    @Test
    public void testServiceInjection() {
        Assert.assertNotNull(personService);
    }
    
    @Test
    public void testPersonManagerImpl() throws IOException, PhotArkSocialException, LoginException, RepositoryException {
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
        personService.savePerson("testuser1", person);
        Person person2 = personService.getPerson("testuser1");
        Assert.assertNotNull(person2);
        Assert.assertEquals("testuser1", person2.getId());
        Assert.assertEquals(person.getDisplayName(), person2.getDisplayName());
        Assert.assertNotNull(person2.getBirthday());
        Assert.assertEquals(calendar.getTime(), person2.getBirthday());
        Assert.assertNotNull(person2.getActivities());
        Assert.assertEquals("Movies", person2.getActivities().get(0));
        personService.removePerson("testuser1");
        person2 = personService.getPerson("testuser1");
        Assert.assertNull(person2);

    }
}
