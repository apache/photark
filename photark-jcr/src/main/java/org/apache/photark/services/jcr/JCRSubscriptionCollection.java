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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.photark.services.SubscriptionCollection;
import org.apache.photark.subscription.SubscriptionConfig;
import org.apache.tuscany.sca.data.collection.Entry;
import org.apache.tuscany.sca.data.collection.NotFoundException;
import org.oasisopen.sca.annotation.Destroy;
import org.oasisopen.sca.annotation.Init;
import org.oasisopen.sca.annotation.Reference;
import org.oasisopen.sca.annotation.Scope;
import org.oasisopen.sca.annotation.Service;

/**
 * Collection representing all subscriptions either local (e.g fileSystem) or remote (e.g Flickr, Picasa, etc)
 * This also serves as a intermediary cache, as it removes the requirement to read from the repository every single time
 * 
 * @version $Rev$ $Date$
 */
@Scope("COMPOSITE")
@Service(SubscriptionCollection.class)
public class JCRSubscriptionCollection implements SubscriptionCollection {
    private static final Logger logger = Logger.getLogger(JCRSubscriptionCollection.class.getName());
    
    private JCRRepositoryManager repositoryManager;
    
    private Map<String, SubscriptionConfig> subscriptions = new HashMap<String, SubscriptionConfig>();

    public JCRSubscriptionCollection() {
        
    }
    
    @Reference(name="repositoryManager")
    protected void setRepositoryManager(JCRRepositoryManager repositoryManager) {
        this.repositoryManager = repositoryManager;
    }

    @Init
    public void init() {
        try {
            Session session = repositoryManager.getSession();
            Node rootNode = getSubscriptionRootNode(session);
            NodeIterator subscriptionNodes = rootNode.getNodes();
            while (subscriptionNodes.hasNext()) {
                Node subscriptionNode = subscriptionNodes.nextNode();
                if (subscriptionNode.getPath().equals("/jcr:system")) {
                    continue;
                }

                SubscriptionConfig subscription = fromNode(subscriptionNode);
                if (!subscriptions.containsKey(subscription.getTitle())) {
                    subscriptions.put(subscription.getTitle(),subscription);
                }
            }

        } catch (Exception e) {
            logger.log(Level.FINE,"Error initializing Subscription Collection: " + e.getMessage(), e);
            e.printStackTrace();
        } 
    }
    
    @Destroy
    public void destroy() {

    }
    
    public Entry<String, SubscriptionConfig>[] getAll() {
        Entry<String, SubscriptionConfig>[] entries = new Entry[subscriptions.size()];
        int i = 0;
        for (Map.Entry<String, SubscriptionConfig> e: subscriptions.entrySet()) {
            entries[i++] = new Entry<String, SubscriptionConfig>(e.getKey(), e.getValue());
        }
        return entries;
    }

    public SubscriptionConfig get(String key) throws NotFoundException {
        SubscriptionConfig subscription = subscriptions.get(key);
        if (subscription == null) {
            throw new NotFoundException(key);
        } else {
            return subscription;
        }

    }

    public String post(String key, SubscriptionConfig subscription) {
        if (subscription.getTitle() == null && subscription.getTitle().length() == 0) {
            key = "subscription-" + UUID.randomUUID().toString();
            subscription.setTitle(key);
        }

        try {
            Session session = repositoryManager.getSession();
            Node rootNode = getSubscriptionRootNode(session);
            if (rootNode.hasNode(key)) {
                logger.info("This subscription is already in gallery");
                return key;
            }
            
            // save the new subscription to the jcr
            Node subscriptionNode = rootNode.addNode(key);
            fromSubscription(subscriptionNode, subscription);
            session.save();
            
            // add the new subscription to the subscription Map
            subscriptions.put(key, subscription);
        } catch (RepositoryException e) {
            logger.log(Level.FINE,"Error adding new subscription : " + e.getMessage(), e);
            e.printStackTrace();
        } 
        
        return key;
    }

    public void put(String key, SubscriptionConfig subscription) throws NotFoundException {
        try {
            Session session = repositoryManager.getSession();
            Node rootNode = getSubscriptionRootNode(session);
            if (! rootNode.hasNode(key)) {
                throw new NotFoundException("Subscription not found:" + key);
            }
            
            // save the subscription to the jcr
            Node subscriptionNode = rootNode.getNode(key);
            fromSubscription(subscriptionNode, subscription);
            session.save();
            
            // update the new subscription to the subscription Map
            subscriptions.put(key, subscription);
        } catch (RepositoryException e) {
            logger.log(Level.FINE,"Error updating new subscription : " + e.getMessage(), e);
            e.printStackTrace();
        }     
    }

    public void delete(String key) throws NotFoundException {
        try {
            Session session = repositoryManager.getSession();
            Node rootNode = getSubscriptionRootNode(session);
            if (! rootNode.hasNode(key)) {
                throw new NotFoundException("Subscription not found:" + key);
            }
            
            // remove the subscription from the jcr
            Node subscriptionNode = rootNode.getNode(key);
            subscriptionNode.remove();
            session.save();

            subscriptions.remove(key);
            
        } catch (RepositoryException e) {
            logger.log(Level.FINE,"Error removing new subscription : " + e.getMessage(), e);
            e.printStackTrace();
        }     
    }

    public Entry<String, SubscriptionConfig>[] query(String query) {
        throw new UnsupportedOperationException("Not implemented");
    }
    

    /**
     * Utility methods 
     */
    
    
    
    /**
     * 
     * @return
     * @throws RepositoryException
     */
    private Node getSubscriptionRootNode(Session session) throws RepositoryException {
        Node rootNode = null;
        try {
            rootNode = session.getRootNode().getNode("subscriptions"); 
        } catch (javax.jcr.PathNotFoundException pnf) {
            rootNode = session.getRootNode().addNode("subscriptions");
        }
        
        return rootNode;
    }
    
    private SubscriptionConfig fromNode(Node subscriptionNode) {
        SubscriptionConfig subscription = null;
        try {
            subscription = new SubscriptionConfig();
            subscription.setTitle(subscriptionNode.getProperty("title").getValue().toString());
            subscription.setType(subscriptionNode.getProperty("type").getValue().toString());
            subscription.setUrl(subscriptionNode.getProperty("url").getValue().toString());
        } catch(Exception e) {
            logger.log(Level.WARNING, "Can't read subscription node :" + e.getMessage(), e);
        }
        
        return subscription;
    }
    
    private void fromSubscription(Node subscriptionNode, SubscriptionConfig subscription) {
        try {
            subscriptionNode.setProperty("title", subscription.getTitle());
            subscriptionNode.setProperty("type", subscription.getType());
            subscriptionNode.setProperty("url", subscription.getUrl());
        } catch(Exception e) {
            logger.log(Level.WARNING, "Can't save subscription node :" + e.getMessage(), e);
        }
    }

}
