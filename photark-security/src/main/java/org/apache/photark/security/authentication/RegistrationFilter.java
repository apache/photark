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

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;


/**
 * Authorization Filter. This will only allow authenticated user to access to
 * upload.html and redirect others to OpenID authentication
 * 
 * 
 * 
 */

public class RegistrationFilter implements Filter {
	private static final Logger logger = Logger
			.getLogger(RegistrationFilter.class.getName());

	private String redirectPage;

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
		
		if (httpReq.getSession().getAttribute("toRigester") != null
				&& httpReq.getSession().getAttribute("toRigester").equals("true")) {
			//httpReq.getSession().setAttribute("toRigester", "false");
			chain.doFilter(request, response);
		} else {
			// httpResp.sendRedirect(httpReq.getContextPath() +"/admin/upload.html");
			httpResp.sendRedirect(httpReq.getContextPath() + redirectPage);
		}

	}

}
