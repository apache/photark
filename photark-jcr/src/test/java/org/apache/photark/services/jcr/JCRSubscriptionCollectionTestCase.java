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

package org.apache.photark.services.jcr;

import junit.framework.Assert;

import org.apache.photark.services.SubscriptionCollection;
import org.apache.photark.subscription.SubscriptionConfig;
import org.apache.tuscany.sca.data.collection.NotFoundException;
import org.apache.tuscany.sca.node.Contribution;
import org.apache.tuscany.sca.node.ContributionLocationHelper;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class JCRSubscriptionCollectionTestCase {
    private static Node node;
    private static SubscriptionCollection subscriptions;

    @BeforeClass
    public static void BeforeClass() {
        try {
            String contribution = ContributionLocationHelper.getContributionLocation("gallery.composite");
            node = NodeFactory.newInstance().createNode("gallery.composite", new Contribution("gallery", contribution));
            node.start();

            subscriptions = node.getService(SubscriptionCollection.class, "SubscriptionComponent");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterClass
    public static void AfterClass() {
        node.stop();
    }

    @Test
    public void testAddSubscription() throws Exception {
        SubscriptionConfig subscription = new SubscriptionConfig();
        subscription.setTitle("Test 01");
        subscription.setType("flickr");
        subscription.setUrl("http://api.flickr.com/services/feeds/photos_public.gne?id=24662369@N07&lang=en-us&format=atom");

        subscriptions.post(subscription.getTitle(), subscription);

        subscriptions.getAll();
    }

    @Test
    public void testUpdateSubscription() throws Exception {
        SubscriptionConfig subscription = new SubscriptionConfig();
        subscription.setTitle("Test 02");
        subscription.setType("flickr");
        subscription.setUrl("http://localhost/xxx");

        subscriptions.post(subscription.getTitle(), subscription);

        try {
            subscriptions.get(subscription.getTitle());
        } catch(NotFoundException nf) {
            subscriptions.delete(subscription.getTitle());
        }

        subscriptions.post(subscription.getTitle(), subscription);

        subscription.setUrl("http://api.flickr.com/services/feeds/photos_public.gne?id=24662369@N07&lang=en-us&format=atom");

        subscriptions.post(subscription.getTitle(), subscription);

        SubscriptionConfig subscritionRead = subscriptions.get(subscription.getTitle());

        Assert.assertEquals(subscription.getTitle(), subscritionRead.getTitle());
    }
}
