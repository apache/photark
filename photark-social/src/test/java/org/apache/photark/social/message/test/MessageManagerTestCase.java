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

package org.apache.photark.social.message.test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.jcr.LoginException;
import javax.jcr.RepositoryException;

import junit.framework.Assert;

import org.apache.photark.services.jcr.JCRRepositoryManager;
import org.apache.photark.social.Message;
import org.apache.photark.social.MessageCollection;
import org.apache.photark.social.PhotArkSocialException;
import org.apache.photark.social.services.MessageService;
import org.apache.photark.social.services.impl.JCRMessageServiceImpl;
import org.junit.Test;

public class MessageManagerTestCase {
    @Test
    public void testMessageManagerImpl() throws IOException, PhotArkSocialException, LoginException,
        RepositoryException {
        JCRRepositoryManager repositoryManager = new JCRRepositoryManager();
        String dir = "socialtest";
        File homeDir = File.createTempFile(dir, "");
        if (homeDir.exists()) {
            homeDir.delete();
        }
        homeDir.mkdir();
        repositoryManager.setRepositoryHome(dir);
        MessageService msgManager = new JCRMessageServiceImpl(repositoryManager);
        // Test creating message collection
        MessageCollection msgColl1 = new MessageCollection();
        msgColl1.setId("INBOX");
        msgColl1.setTitle("INBOX");
        msgColl1.setTotalCount(10);
        msgColl1.setLastUpdated(new Date());
        msgManager.createMessageCollection("userm", msgColl1);
        MessageCollection msgColl3 = new MessageCollection();
        msgColl3.setId("OUTBOX");
        msgColl3.setTitle("OUTBOX");
        msgColl3.setTotalCount(2);
        msgColl3.setLastUpdated(new Date());
        msgManager.createMessageCollection("userm", msgColl3);
        // Test retrieving message collection
        List<MessageCollection> msgCollList = msgManager.getMessageCollections("userm", null, null);
        Assert.assertNotNull(msgCollList);
        Assert.assertEquals(2, msgCollList.size());
        MessageCollection msgColl2 = msgCollList.get(0);
        Assert.assertNotNull(msgColl2);
        Assert.assertEquals(10, msgColl2.getTotalCount());
        Assert.assertEquals("INBOX", msgColl2.getId());
        msgColl2 = msgCollList.get(1);
        Assert.assertNotNull(msgColl2);
        Assert.assertEquals("OUTBOX", msgColl2.getId());
        Assert.assertEquals(msgColl3.getLastUpdated(), msgColl2.getLastUpdated());
        // Test deleting message collection
        msgManager.deleteMessageCollection("userm", "OUTBOX");
        msgCollList = msgManager.getMessageCollections("userm", null, null);
        Assert.assertNotNull(msgCollList);
        Assert.assertEquals(1, msgCollList.size());
        msgColl2 = msgCollList.get(0);
        Assert.assertNotNull(msgColl2);
        Assert.assertEquals("INBOX", msgColl2.getId());

        // Test creating & retrieving messages
        Message msg1 = new Message();
        msg1.setBody("test message 1");
        msg1.setTitle("Title");
        msg1.setStatus("pending");
        msgManager.createMessage("userm", "INBOX", msg1);
        List<String> msgIds = new ArrayList<String>();
        msgIds.add("0");
        List<Message> msgList = msgManager.getMessages("userm", "INBOX", null, msgIds, null);
        Assert.assertNotNull(msgList);
        Assert.assertEquals(msgIds.size(), msgList.size());
        Message msg2 = msgList.get(0);
        Assert.assertNotNull(msg2);
        Assert.assertEquals(msg1.getTitle(), msg2.getTitle());
        Assert.assertEquals(msg1.getBody(), msg2.getBody());
        // Test deleting messages
        msgManager.deleteMessages("userm", "INBOX", msgIds);
        msgList = msgManager.getMessages("userm", "INBOX", null, msgIds, null);
        Assert.assertNotNull(msgList);
        Assert.assertEquals(0, msgList.size());

    }
}
