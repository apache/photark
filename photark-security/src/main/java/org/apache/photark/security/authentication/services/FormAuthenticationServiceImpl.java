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

package org.apache.photark.security.authentication.services;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.photark.security.authorization.AccessList;
import org.apache.photark.security.authorization.services.AccessManager;
import org.oasisopen.sca.annotation.Reference;
import org.oasisopen.sca.annotation.Scope;
import org.oasisopen.sca.annotation.Service;

import com.dyuproject.openid.RelyingParty;

/**
 * Form Authentication Service Impl authenticates Super Admin.
 * 
 * 
 */
@Service(Servlet.class)
@Scope("COMPOSITE")
public class FormAuthenticationServiceImpl extends HttpServlet implements Servlet {

	private AccessManager accessManager;
    /**
     * 
     */
    private static final long serialVersionUID = -6462488654757190805L;

    public FormAuthenticationServiceImpl() {

    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
	    throws IOException, ServletException {
	doPost(request, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
	    throws IOException, ServletException {

	
	//check whether the Super Admin is authenticated
	if (request.getUserPrincipal() != null) {
	    
	    //Invalidating the OpenID authentication
	    RelyingParty.getInstance().invalidate(request, response);
	    
	    //Creating the accessList
	    AccessList accessList=accessManager.createAccessList("SuperAdmin","");
	    request.getSession().setAttribute("accessList", accessList);
	    System.err.println("Super Admin authenticated");

	    response.sendRedirect(request.getContextPath() + "/admin/upload.html");
	    
	} else {
	    // if not Authenticated as Super Admin redirect to OpenID login
	    //But this is always false
	    response.sendRedirect(request.getContextPath() + "/home/authenticate");
	}

    }
    
    
    @Reference(name="accessmanager")
	protected void setAccessService(AccessManager accessManager) {
		this.accessManager = accessManager;
	}
}