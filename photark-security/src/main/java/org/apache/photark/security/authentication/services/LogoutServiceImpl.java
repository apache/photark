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
import static org.apache.photark.security.utils.Constants.*;

/**
 * Logout Service Impl. This will logout all kind of Authenticated users
 */
@Service(Servlet.class)
@Scope("COMPOSITE")
public class LogoutServiceImpl extends HttpServlet {

    private AccessManager accessManager;
    /**
     *
     */
    private static final long serialVersionUID = 5282044123210612195L;

    public LogoutServiceImpl() {

    }
    @Reference(name="accessmanager")
	protected void setAccessService(AccessManager accessManager) {
		this.accessManager = accessManager;
	}

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        doPost(request, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        if (request.getSession().getAttribute(ACCESS_LIST) != null) {
            String userId = ((AccessList) request.getSession().getAttribute(ACCESS_LIST)).getUserId();
            System.err.print(userId);
            accessManager.removeAccessListAndToken(userId);

        }
        // Removing the AccessList
        request.getSession().setAttribute(ACCESS_LIST, "");
        boolean blocked = false;
        if (request.getSession().getAttribute(USER_NEED_TO_REGISTER) != null&&request.getSession().getAttribute(USER_NEED_TO_REGISTER).equals("blocked")) {
            blocked = true;
        }
        request.getSession().setAttribute(USER_NEED_TO_REGISTER, "false");
        // invalidating the Authenticated OpenID User
        RelyingParty.getInstance().invalidate(request, response);
        // invalidating the Authenticated Super Admin User
        request.getSession().invalidate();

        System.err.println(" logged out");
        if (blocked) {
            response.sendRedirect(request.getContextPath() + "/home/blocked.html");
        } else {
            // Redirect to Gallery
            response.sendRedirect(request.getContextPath() + "/");
        }
    }

}
