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
import org.apache.photark.security.authorization.Permission;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


@Service(Servlet.class)
@Scope("COMPOSITE")
public class SecurityServiceImpl extends HttpServlet implements Servlet /*SecurityService*/ {

    /**
     *
     */
    private static final long serialVersionUID = -6452934544772432330L;
    private AccessManager accessManager;
    JSONRPCSecurityManager jsonSecurityManager=new JSONRPCSecurityManager();

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
        PrintWriter out = response.getWriter();
        StringBuffer sb = new StringBuffer();

        if ("getUserInfo".equalsIgnoreCase(request.getParameter("request")) && (request.getSession().getAttribute("accessList") != null && !request.getSession().getAttribute("accessList").equals(""))) {
            AccessList accessList = (AccessList) request.getSession().getAttribute("accessList");
            String userId = accessList.getUserId();
            StringBuffer ssb = createJSONUser(request);
            if (accessManager.isUserStoredInRole(userId, "registeredUserRole")) {
                request.getSession().setAttribute("toRigester", "false");

                sb.append("{registered:'true'," + ssb + "}");
                //  response.sendRedirect(request.getContextPath() + "/admin/upload.html");
            } else {
                sb.append("{registered:'false'," + ssb + "}");

            }
            send(out, sb);
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
                sb.append("OK");
                //sb.append(",unRegistered=false");
            }
            send(out, sb);
            accessList = accessManager.createAccessList(userId, request.getParameter("email"));
            request.getSession().removeAttribute("accessList");
            request.getSession().setAttribute("accessList", accessList);
        } else if ("getUser".equalsIgnoreCase(request.getParameter("request"))) {
            sb.append("{" + createJSONUser(request) + "}");
            send(out, sb);
        } else if ("getJSONAccessList".equalsIgnoreCase(request.getParameter("request"))) {
            if (request.getSession().getAttribute("accessList") == null) {
                AccessList accessList = accessManager.createAccessList("UnRegisteredUser", "");
                request.getSession().setAttribute("accessList", accessList);
            }
            sb.append("{" + jsonSecurityManager.getJSONAccessList(request) + "}");
            send(out, sb);
        } else {
            response.sendRedirect(request.getContextPath() + "/home/authenticate");
        }


    }


//    private AccessList getAccessList(String token) {  //todo
//        Object[] accessListAndToken = AccessManager.accessTokenMap.get(token.substring(0,token.length()-25));
//        return (AccessList )accessListAndToken[0] ;
//    }

    private void send(PrintWriter out, StringBuffer sb) {
        out.write(sb.toString());
        out.flush();
        out.close();
    }

    private StringBuffer createJSONUser(HttpServletRequest request) {
        StringBuffer sb = new StringBuffer();
        if (request.getSession().getAttribute("accessList") != null && request.getSession().getAttribute("accessList") != "") {

            AccessList accessList = (AccessList) request.getSession().getAttribute("accessList");

            String userId = accessList.getUserId();
            if (userId.equals("SuperAdmin")) {

                sb.append("user:{userId:'" + userId +
                        "',userInfo:{realName:'" +
                        "',displayName:'" + userId +
                        "',email:'" +
                        "',website:'" + "'}}");

            } else {
                User user;
                user = accessManager.getUser(userId);
                UserInfo userInfo = user.getUserInfo();


                sb.append("user:{userId:'" + userId +
                        "',userInfo:{realName:'" + userInfo.getRealName() +
                        "',displayName:'" + userInfo.getDisplayName() +
                        "',email:'" + userInfo.getEmail() +
                        "',website:'" + userInfo.getWebsite() + "'}}");
            }


        } else {
            sb.append("user:{userId:'null'}");
        }
        return sb;
    }


}
