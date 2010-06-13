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

package org.apache.photark.security.authorization.services;

import org.apache.photark.security.authorization.AccessList;
import org.apache.photark.security.authorization.User;
import org.apache.photark.security.authorization.UserInfo;
import org.oasisopen.sca.annotation.Reference;
import org.oasisopen.sca.annotation.Scope;
import org.oasisopen.sca.annotation.Service;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;


@Service(Servlet.class)
@Scope("COMPOSITE")
public class SecurityServiceImpl extends HttpServlet implements Servlet /*SecurityService*/ {

    /**
     *
     */
    private static final long serialVersionUID = -6452934544772432330L;
    private AccessManager accessManager;

    @Reference(name = "accessmanager")
    protected void setAccessService(AccessManager accessManager) {
        this.accessManager = accessManager;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        doPost(request, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");

        StringBuffer sb = new StringBuffer();

        if ("getUserInfo".equalsIgnoreCase(request.getParameter("request"))) {
            AccessList accessList = (AccessList) request.getSession().getAttribute("accessList");
            String userId = accessList.getUserId();
            User user;
            user = accessManager.getUser(userId);
            UserInfo userInfo = user.getUserInfo();
            if (accessManager.isUserStoredInRole(userId, "registeredUserRole")) {
                request.getSession().setAttribute("toRigester", "false");
                /*	sb.append("userId="+ userId);
                    sb.append(",displayName=" + userInfo.getDisplayName());
                    sb.append(",email=" + userInfo.getEmail());
                    sb.append(",realName=" + userInfo.getRealName());
                    sb.append(",webSite=" + userInfo.getWebsite());*/
                sb.append("registered,").append(userId).append(",").append(userInfo.getRealName()).append(",").append(userInfo.getDisplayName()).append(",").append(userInfo.getEmail()).append(",").append(userInfo.getWebsite());

            } else {
                /*sb.append("userId="+ userId);
                    sb.append(",unRegistered=false");*/
                sb.append("unRegistered,").append(userId).append(",").append(userInfo.getRealName()).append(",").append(userInfo.getDisplayName()).append(",").append(userInfo.getEmail()).append(",").append(userInfo.getWebsite());
            }

        } else if ("setUserInfo".equalsIgnoreCase(request.getParameter("request"))) {
            AccessList accessList = (AccessList) request.getSession().getAttribute("accessList");
            String userId = accessList.getUserId();
            User user;
            if (request.getParameter("displayName") != null && !request.getParameter("displayName").trim().equals("")) {
                request.getSession().setAttribute("toRigester", "false");
                user = new User(userId);
                UserInfo userInfo = new UserInfo(request.getParameter("displayName"),
                        request.getParameter("email"),
                        request.getParameter("realName"),
                        request.getParameter("webSite"));
                user.setUserInfo(userInfo);

                if (accessManager.isUserStoredInRole(userId, "unRegisteredUserRole")) {
                    accessManager.removeUserFromRole(userId, "unRegisteredUserRole");
                }
                if (!accessManager.isUserStoredInRole(userId, "registeredUserRole")) {
                    accessManager.addUserToRole(user, "registeredUserRole");
                }
                //sb.append("userId="+ userId);
                //sb.append(",unRegistered=false");
            }
        } else if ("getUser".equalsIgnoreCase(request.getParameter("request"))) {
            if (request.getSession().getAttribute("accessList") != null && request.getSession().getAttribute("accessList") != "") {

                AccessList accessList = (AccessList) request.getSession().getAttribute("accessList");

                String userId = accessList.getUserId();
                if(userId.equals("SuperAdmin")){

                sb.append("{user:{userId:'" + userId +
                        "',userInfo:{realName:'" +
                        "',displayName:'"+userId + 
                        "',email:'" +
                        "',website:'" +  "'}}}");

                }   else{
                    User user;
                user = accessManager.getUser(userId);
                UserInfo userInfo = user.getUserInfo();

                /*	sb.append("userId="+ userId);
              sb.append(",displayName=" + userInfo.getDisplayName());
              sb.append(",email=" + userInfo.getEmail());
              sb.append(",realName=" + userInfo.getRealName());
              sb.append(",webSite=" + userInfo.getWebsite());*/
                sb.append("{user:{userId:'" + userId +
                        "',userInfo:{realName:'" + userInfo.getRealName() +
                        "',displayName:'" + userInfo.getDisplayName() +
                        "',email:'" + userInfo.getEmail() +
                        "',website:'" + userInfo.getWebsite() + "'}}}");
                }


            }else{
                 sb.append("{user:{userId:'null'}}");
            }
        }
        PrintWriter out = response.getWriter();
        out.write(sb.toString());
        out.flush();
        out.close();

    }
}