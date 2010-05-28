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

package org.apache.photark.security.authentication;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.photark.security.authorization.AccessManager;
import org.oasisopen.sca.annotation.Property;
import org.oasisopen.sca.annotation.Scope;
import org.oasisopen.sca.annotation.Service;

/**
 * Authorization Filter. This will only allow authenticated user 
 * to access to upload.html and redirect others to OpenID authentication
 * 
 * 
 * 
 */
//@Service(Filter.class)
//@Scope("COMPOSITE")
public class AuthorizationFilter implements Filter {
        private static final Logger logger = Logger.getLogger(AuthorizationFilter.class.getName());
    
	/**Default Name of the Redirect Page */
	//private final String REDIRECT_PAGE_DEFAULT = "photark";

	//@Property(name = "redirectPage", required = true)
	private String redirectPage;

    	/*@Property
    	public void setRedirectPage(String redirectPage) {
    	    if (redirectPage != null && !redirectPage.equals("")) {
    		this.redirectPage = redirectPage;
    	    } else {
    		logger.log(Level.WARNING, "Setting Default Redirect Page to Upload.html:"
		    + REDIRECT_PAGE_DEFAULT);
    		this.redirectPage = REDIRECT_PAGE_DEFAULT;
    	    }
    	}

    public String getRedirectPage() {
	return redirectPage;
    }
	*/
	/** Filter should be configured with an redirect page. */
	public void init(FilterConfig FilterConfig) throws ServletException {
		if (FilterConfig != null) {
		    redirectPage = FilterConfig.getInitParameter("redirect_page");
		}
	}
	
	public void destroy() {
		// TODO Auto-generated method stub
	}
	
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws ServletException, IOException {
		
		HttpServletRequest httpReq = (HttpServletRequest) request;
		HttpServletResponse httpResp = (HttpServletResponse) response;
		
		AccessManager am = new AccessManager();
		
		if ((String)httpReq.getSession().getAttribute("accessList") != null && !((String)httpReq.getSession().getAttribute("accessList")).equals("")) {
			System.err.println( (String)httpReq.getSession().getAttribute("accessList") +" Accessing Admin page");
			chain.doFilter(request, response);
		} else {
			httpResp.sendRedirect(httpReq.getContextPath() + redirectPage);
		}

	}

	/*public void init(FilterConfig filterConfig) throws ServletException {
	    // TODO Auto-generated method stub
	    
	}*/
}
