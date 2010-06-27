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

package org.apache.photark.jcr;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jcr.LoginException;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import org.apache.jackrabbit.core.TransientRepository;
import org.oasisopen.sca.annotation.Destroy;
import org.oasisopen.sca.annotation.EagerInit;
import org.oasisopen.sca.annotation.Init;
import org.oasisopen.sca.annotation.Property;
import org.oasisopen.sca.annotation.Scope;

/**
 *  A JCR Repository Manager that controls access to JCR session
 *  This is a singleton component controlled by the SCA Runtime via scope composite 
 */
@Scope("COMPOSITE")
@EagerInit
public class JCRRepositoryManager {
    private static final Logger logger = Logger.getLogger(JCRRepositoryManager.class.getName());
    
    /**Default Name of the repository home directory */
    private final String REPO_HOME_DEFAULT = "photark";
    
    private String repositoryHome;
    /** JCR Repository **/
    private static Repository repository;
    /** JCR Repository Session **/
    private static Session session;

    public JCRRepositoryManager() throws IOException {
        logger.log(Level.INFO,"JCRRepositoryManager Constructor : " + this.hashCode());
    }
 
    @Init
    public void init() {
       initializeRepository();
    }
    
    @Property
    public void setRepositoryHome(String repositoryHome){
    	if(repositoryHome != null && !repositoryHome.equals("")){
    		this.repositoryHome = repositoryHome;
    	}else{
    		logger.log(Level.WARNING,"Setting Default Repository Home:" + REPO_HOME_DEFAULT);
    		this.repositoryHome = REPO_HOME_DEFAULT;
    	}
    }

    public String getRepositoryHome(){
    	return repositoryHome;
    }
    
    @Destroy
    public void destroy() {
        logger.log(Level.INFO,"Shutting down JCR repository");
        
        if (session != null) {
            session.logout();
        }
        
        session = null;
        repository = null;
    }
    
    /**
     * Retrieve current JCR Session
     * 
     * @return A valid Session object
     * @throws RepositoryException
     * @throws LoginException
     */
    public Session getSession() throws LoginException, RepositoryException {
        if( session == null) {
            initializeRepository();
        }
        return session;
    }
    
    /**
     * Initialize the JCR Repository
     */
    private synchronized void initializeRepository() {
        logger.fine("Initializing JCR repository");
        try {
            if( repository == null) {
                repository = new TransientRepository(new File(repositoryHome));
            }
            session = repository.login(new SimpleCredentials("photarkUser", "passwordDoesNotMatter".toCharArray()));                
        } catch (Exception e) {
            logger.log(Level.INFO, ">>>Error initializing JCR repository : " + e.getMessage(), e);
        } 
    }
}