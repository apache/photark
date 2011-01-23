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

package org.apache.photark.subscription;

import org.apache.photark.Album;

/**
 * Model representing a subscription where images are loded from
 * Subscriptions can be local (e.g fileSystem) or remote (e.g Flickr, Picasa, etc)
 *
 * @version $Rev$ $Date$
 */
public class SubscriptionConfig extends Album {

    private static final long serialVersionUID = 1839829824832424269L;

    private String id;
    private String title;
    private String type;
    private String url;
    private String username;
    private String password;

    /**
     * Get subscription ID
     * @return
     */
    public String getId() {
        return id;
    }

    /**
     * Set subscription ID
     * @param id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Get subscription title
     *
     * @return the subscription title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Set subscription title
     *
     * @param title the subscription title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Get subscription type
     *
     * @return the subscription title
     */
    public String getType() {
        return type;
    }

    /**
     * Set subscription type
     *
     * @param type the subscription type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Get subscription url (e.g Local filesystem or remote Flickr Album)
     *
     * @return the subscription url
     */
    public String getUrl() {
        return url;
    }

    /**
     *  Set subscription url (e.g Local filesystem or remote Flickr Album)
     *
     * @param url the subscription url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Get subscription username for authentication/authorization
     * @return
     */
    public String getUsername() {
        return username;
    }

    /**
     * Set subscription username for authentication/authorization
     * @param username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Get subscription password for authentication/authorization
     * @return
     */
    public String getPassword() {
        return password;
    }

    /**
     * Set subscription password for authentication/authorization
     * @param password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "Subscription [title=" + title + ", type=" + type + ", remoteLocation=" + url + "]";
    }


}
