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

import com.dyuproject.openid.OpenIdServletFilter;
import com.dyuproject.openid.OpenIdUser;
import com.dyuproject.openid.RelyingParty;
import com.dyuproject.openid.YadisDiscovery;
import com.dyuproject.openid.ext.AxSchemaExtension;
import com.dyuproject.openid.ext.SRegExtension;
import com.dyuproject.util.http.UrlEncodedParameterMap;
import org.apache.photark.security.authorization.AccessList;
import org.apache.photark.security.authorization.services.AccessManager;
import org.oasisopen.sca.annotation.Reference;
import org.oasisopen.sca.annotation.Scope;
import org.oasisopen.sca.annotation.Service;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Map;

import static org.apache.photark.security.utils.Constants.*;

/**
 * OpenID Authentication Service Impl. If authenticated, goes to the home page. If not, goes to the login page.
 */
@Service(Servlet.class)
@Scope("COMPOSITE")
public class OpenIDAuthenticationServiceImpl extends HttpServlet implements Servlet {
    private static AccessManager accessManager;

    static {
        RelyingParty.getInstance()
                .addListener(new SRegExtension()
                        .addExchange("email")
                        .addExchange("country")
                        .addExchange("language")
                )
                .addListener(new AxSchemaExtension()
                        .addExchange("email")
                        .addExchange("country")
                        .addExchange("language")
                )
                .addListener(new RelyingParty.Listener() {
                    public void onDiscovery(OpenIdUser user, HttpServletRequest request) {
                        System.err.println("discovered user: " + user.getClaimedId());
                    }

                    public void onPreAuthenticate(OpenIdUser user, HttpServletRequest request,
                                                  UrlEncodedParameterMap params) {
                        System.err.println("pre-authenticate user: " + user.getClaimedId());
                    }

                    public void onAuthenticate(OpenIdUser user, HttpServletRequest request) {
                        System.err.println("newly authenticated user: " + user.getIdentity());

                        //Invalidating the Super Admin user
                        request.getSession().invalidate();

                        String email = null;

                        Map<String, String> sreg = SRegExtension.remove(user);
                        Map<String, String> axschema = AxSchemaExtension.remove(user);
                        if (sreg != null && !sreg.isEmpty()) {
                            System.err.println("sreg: " + sreg);
                            user.setAttribute("info", sreg);
                            email = sreg.get("email");
                        } else if (axschema != null && !axschema.isEmpty()) {
                            System.err.println("axschema: " + axschema);
                            user.setAttribute("info", axschema);
                            email = axschema.get("email");
                        }
                        //Creating the accessList for the newly authenticated user

                        if (email == null) {
                            email = "";
                        }
                        AccessList accessList = accessManager.createAccessList(user.getIdentity(), email);
                        request.getSession().setAttribute(ACCESS_LIST, accessList);
                        if (!accessManager.isUserStoredInList(accessList.getUserId(), REGISTERED_USER_LIST)) {
                            if (accessManager.isUserStoredInList(accessList.getUserId(), BLOCKED_USER_LIST)) {
                                request.getSession().setAttribute(USER_NEED_TO_REGISTER, "blocked");
                            } else {
                                request.getSession().setAttribute(USER_NEED_TO_REGISTER, "true");
                            }
                        }

                    }

                    public void onAccess(OpenIdUser user, HttpServletRequest request) {
                        System.err.println("user access: " + user.getIdentity());
                        System.err.println("info: " + user.getAttribute("info"));
                    }
                });
    }

    RelyingParty _relyingParty = RelyingParty.getInstance();

    @Reference(name = "accessmanager")
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
        String loginWith = request.getParameter("loginWith");
        if (loginWith != null) {
            // If the ui supplies a LoginWithGoogle or LoginWithYahoo link/button, 
            // this will speed up the openid process by skipping discovery. 
            // The override is done by adding the OpenIdUser to the request attribute.
            if (loginWith.equals("google")) {
                OpenIdUser user = OpenIdUser.populate("https://www.google.com/accounts/o8/id",
                        YadisDiscovery.IDENTIFIER_SELECT,
                        "https://www.google.com/accounts/o8/ud");
                request.setAttribute(OpenIdUser.ATTR_NAME, user);

            } else if (loginWith.equals("yahoo")) {
                OpenIdUser user = OpenIdUser.populate("http://yahoo.com/",
                        YadisDiscovery.IDENTIFIER_SELECT,
                        "https://open.login.yahooapis.com/openid/op/auth");
                request.setAttribute(OpenIdUser.ATTR_NAME, user);
            }
        }

        String errorMsg = OpenIdServletFilter.DEFAULT_ERROR_MSG;
        try {
            OpenIdUser user = _relyingParty.discover(request);
            if (user == null) {
                if (RelyingParty.isAuthResponse(request)) {
                    // authentication timeout                    
                    response.sendRedirect(request.getRequestURI());
                } else {
                    // set error msg if the openid_identifier is not resolved.
                    if (request.getParameter(_relyingParty.getIdentifierParameter()) != null) {
                        request.setAttribute(OpenIdServletFilter.ERROR_MSG_ATTR, errorMsg);//TODO error pages, massage not passed to front end
                        // request.getRequestDispatcher("/home/error.html").forward(request, response);
                        response.sendRedirect(request.getContextPath() + "/home/error.html");
                    } else {
                        // new user
                        //request.getRequestDispatcher("/home/login.html").forward(request, response);
                        response.sendRedirect(request.getContextPath() + "/home/login.html");
                    }
                }
                return;
            }

            if (user.isAuthenticated()) {
                // user already authenticated
                // request.getRequestDispatcher("/home/home.jsp").forward(request, response);
                //added by suho
                // the original entry
                if (request.getSession().getAttribute(USER_NEED_TO_REGISTER) != null
                        && request.getSession().getAttribute(USER_NEED_TO_REGISTER).equals("true")) {
                    // for registering purposes
                    //  request.getRequestDispatcher("/home/registration.html").forward(request, response);
                    response.sendRedirect(request.getContextPath() + "/home/registration.html");
                } else if (request.getSession().getAttribute(USER_NEED_TO_REGISTER) != null
                        && request.getSession().getAttribute(USER_NEED_TO_REGISTER).equals("blocked")) {

                    // for registering purposes
                    //  request.getRequestDispatcher("/home/registration.html").forward(request, response);
                    response.sendRedirect(request.getContextPath() + "/logout");
                } else {
//                    if (request.getSession().getAttribute("accessList") != null) {
//                        AccessList accessList = (AccessList) request.getSession().getAttribute("accessList");
//                        if (accessList.getUserId().equals("UnRegisteredUser")) {
//                            response.sendRedirect(request.getContextPath() + "/logout");
//                            return;
//                        }
//                    }
                    response.sendRedirect(request.getContextPath() + "/admin/upload.html");
                }

                return;
            }

            if (user.isAssociated() && RelyingParty.isAuthResponse(request)) {
                // verify authentication
                if (_relyingParty.verifyAuth(user, request, response)) {
                    // authenticated                    
                    // redirect to home to remove the query params instead of doing:
                    // request.getRequestDispatcher("/home.jsp").forward(request, response);
                    // send to the authorization filter to loop back
                    response.sendRedirect(request.getContextPath() + "/home/authenticate");
                } else {
                    // failed verification
                    //request.getRequestDispatcher("/home/login.jsp").forward(request, response);
                    // request.getRequestDispatcher("/home/error.html").forward(request, response);
                    response.sendRedirect(request.getContextPath() + "/home/error.html");
                }
                return;
            }

            // associate and authenticate user
            StringBuffer url = request.getRequestURL();
            String trustRoot = url.substring(0, url.indexOf("/", 9));
            String realm = url.substring(0, url.lastIndexOf("/"));
            String returnTo = url.toString();
            if (_relyingParty.associateAndAuthenticate(user, request, response, trustRoot, realm,
                    returnTo)) {
                // successful association
                return;
            }
        }
        catch (UnknownHostException uhe) {
            System.err.println("not found");
            errorMsg = OpenIdServletFilter.ID_NOT_FOUND_MSG;//TODO error pages, massage not passed to front end
        }
        catch (FileNotFoundException fnfe) {
            System.err.println("could not be resolved");
            errorMsg = OpenIdServletFilter.DEFAULT_ERROR_MSG;//TODO error pages, massage not passed to front end
        }
        catch (Exception e) {
            e.printStackTrace();
            errorMsg = OpenIdServletFilter.DEFAULT_ERROR_MSG;//TODO error pages, massage not passed to front end
        }
        request.setAttribute(OpenIdServletFilter.ERROR_MSG_ATTR, errorMsg);//TODO error pages, massage not passed to front end
        //request.getRequestDispatcher("/home/login.html").forward(request, response);
        response.sendRedirect(request.getContextPath() + "/home/login.html");
    }

}
