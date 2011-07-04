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

import junit.framework.Assert;

import org.apache.photark.services.jcr.JCRRepositoryManager;
import org.apache.photark.social.PhotArkSocialConstants;
import org.apache.photark.social.exception.PhotArkSocialException;
import org.apache.photark.social.person.Person;
import org.apache.photark.social.person.PersonManager;
import org.apache.photark.social.person.PersonManagerImpl;
import org.apache.photark.social.person.relationship.RelationshipManager;
import org.apache.photark.social.person.relationship.RelationshipManagerImpl;
import org.junit.Test;

public class RelationshipManagerTestCase {
    @Test
    public void testRelationshipManager() throws IOException, PhotArkSocialException {
        JCRRepositoryManager repositoryManager = new JCRRepositoryManager();
        String dir = "socialtest";
        File homeDir = new File(dir);
        if (homeDir.exists()) {
            deleteDir(homeDir);
        }
        homeDir.mkdir();
        repositoryManager.setRepositoryHome(dir);
        RelationshipManager relationshipManager = new RelationshipManagerImpl();
        PersonManager pm = new PersonManagerImpl(repositoryManager);
        Person person = new Person();
        person.setId("user1");
        person.setDisplayName("TestUser1");
        person.setFirstName("test");
        person.setLastName("user1");
        pm.savePerson("user1", person);
        Person person2 = new Person();
        person2.setId("user2");
        person2.setDisplayName("TestUser2");
        person2.setFirstName("test");
        person2.setLastName("user2");
        pm.savePerson("user2", person2);
        Person person3 = new Person();
        person3.setId("user3");
        person3.setDisplayName("TestUser3");
        person3.setFirstName("test");
        person3.setLastName("user3");
        pm.savePerson("user3", person3);
        // Test acceptReationship()
        relationshipManager.requestRelationship("user2", "user1");
        // Test getPendingRelationship()
        Assert.assertEquals(1, relationshipManager.getPendingRelationshipRequests("user1").length);
        Assert.assertEquals("user2", relationshipManager.getPendingRelationshipRequests("user1")[0]);
        relationshipManager.acceptRelationshipRequest("user1", "user2");
        // Test getRealtionshipList()
        Assert.assertNotNull(relationshipManager.getRelationshipList("user1"));
        Assert.assertEquals(1, relationshipManager.getRelationshipList("user1").length);
        Assert.assertEquals("user2", relationshipManager.getRelationshipList("user1")[0]);
        // Test getRelationshipStatus()
        Assert.assertEquals(PhotArkSocialConstants.RELATIONSHIP_FRIEND,
                            relationshipManager.getRelationshipStatus("user1", "user2"));
        // Test requestRelationship()
        relationshipManager.requestRelationship("user1", "user3");
        Assert.assertEquals(PhotArkSocialConstants.RELATIONSHIP_REQUEST_PENDING,
                            relationshipManager.getRelationshipStatus("user1", "user3"));
        Assert.assertEquals(PhotArkSocialConstants.RELATIONSHIP_REQUEST_RECEIVED,
                            relationshipManager.getRelationshipStatus("user3", "user1"));
        Assert.assertEquals(PhotArkSocialConstants.RELATIONSHIP_NONE,
                            relationshipManager.getRelationshipStatus("user2", "user3"));
        // Test ignoreRelationship()
        relationshipManager.ignoreRelationship("user3", "user1");
        Assert.assertEquals(PhotArkSocialConstants.RELATIONSHIP_NONE,
                            relationshipManager.getRelationshipStatus("user3", "user1"));
        // Test removeRelationship()
        relationshipManager.removeRelationship("user1", "user2");
        Assert.assertEquals(PhotArkSocialConstants.RELATIONSHIP_NONE,
                            relationshipManager.getRelationshipStatus("user1", "user2"));

    }

    /* If the repository home directory already exists, delete it */
    private static boolean deleteDir(File dir) {

        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // The directory is now empty so now it can be smoked
        return dir.delete();
    }
}
