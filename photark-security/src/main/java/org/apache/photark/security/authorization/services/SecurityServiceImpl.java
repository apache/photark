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

import org.apache.photark.security.authorization.*;
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

import static org.apache.photark.security.utils.Constants.*;


@Service(Servlet.class)
@Scope("COMPOSITE")
public class SecurityServiceImpl extends HttpServlet implements Servlet /*SecurityService*/ {

    /**
     *
     */
    private static final long serialVersionUID = -6452934544772432330L;
    private AccessManager accessManager;
//     private boolean userInit =false;
    //JSONRPCSecurityManager jsonSecurityManager = new JSONRPCSecurityManager();

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
//            AccessList accessList;
        if (!(request.getSession().getAttribute(ACCESS_LIST) != null && !request.getSession().getAttribute(ACCESS_LIST).equals(""))) {
            AccessList accessList = accessManager.createAccessList(GUEST, "");
            request.getSession().setAttribute(ACCESS_LIST, accessList);
//
        }

        PrintWriter out = response.getWriter();
        StringBuffer sb = new StringBuffer();

        if ("getUserInfo".equalsIgnoreCase(request.getParameter("request"))) {
            String userId = ((AccessList) request.getSession().getAttribute(ACCESS_LIST)).getUserId();
            StringBuffer ssb = createJSONUser(request);
            if (accessManager.isUserStoredInList(userId, REGISTERED_USER_LIST)) {
                request.getSession().setAttribute(USER_NEED_TO_REGISTER, "false");

                sb.append("{registered:'true'," + ssb + "}");
                //  response.sendRedirect(request.getContextPath() + "/admin/upload.html");
            } else {
                sb.append("{registered:'false'," + ssb + "}");

            }
            send(out, sb);
        } else if ("setUserInfo".equalsIgnoreCase(request.getParameter("request"))) {
            String userId = ((AccessList) request.getSession().getAttribute(ACCESS_LIST)).getUserId();
            User user;
            if (request.getParameter(USER_DISPLAY_NAME) != null && !request.getParameter(USER_DISPLAY_NAME).trim().equals("")) {
                request.getSession().setAttribute(USER_NEED_TO_REGISTER, "false");
                user = new User(userId);
                UserInfo userInfo = new UserInfo(request.getParameter(USER_DISPLAY_NAME),
                        request.getParameter(USER_EMAIL),
                        request.getParameter(USER_REAL_NAME),
                        request.getParameter(USER_WEBSITE));
                user.setUserInfo(userInfo);

                if (accessManager.isUserStoredInList(userId, UNREGISTERED_USER_LIST)) {
                    accessManager.removeUserFromList(userId, UNREGISTERED_USER_LIST);
                }
                if (!accessManager.isUserStoredInList(userId, REGISTERED_USER_LIST)) {
                    accessManager.addUserToList(user, REGISTERED_USER_LIST);
                }
                sb.append("OK");
            }
            AccessList accessList = accessManager.createAccessList(userId, request.getParameter(USER_EMAIL));
            request.getSession().setAttribute(ACCESS_LIST, accessList);
            send(out, sb);
        } else if ("getUser".equalsIgnoreCase(request.getParameter("request"))) {
            sb.append("{" + createJSONUser(request) + "}");
            send(out, sb);
        } else if ("getJSONAccessList".equalsIgnoreCase(request.getParameter("request"))) {

            sb.append("{" + getJSONAccessList(request) + "}");
            send(out, sb);
        } else {
            response.sendRedirect(request.getContextPath() + "/home/authenticate");
        }


    }

    private String createAccessToken(String userId) {
        Random randomGenerator = new Random();
        String token = "";
        for (int i = 0; i < 25; i++) {
            int n = randomGenerator.nextInt(36);
            if (n < 10) {
                token += (n); // digit 0-9
            } else {
                token += (char) (n - 10 + 'A'); // alpha A-Z
            }
        }
        //  System.out.println(token);

        return userId + token;
    }

    public String getJSONAccessList(HttpServletRequest request) {
        AccessList accessList = (AccessList) request.getSession().getAttribute(ACCESS_LIST);
//        if (!userInit) {
//            accessManager.putAccessListAndToken(accessManager.createAccessList(GUEST, ""), createAccessToken(GUEST));
//            userInit = true;
//        }
        String token;
        if (accessManager.isUserActive(accessList.getUserId())) {
            token = accessManager.getSecurityTokenFromUserId(accessList.getUserId());
            accessList = accessManager.getAccessListFromUserId(accessList.getUserId());
            request.getSession().setAttribute(ACCESS_LIST, accessList);
            accessManager.putAccessListAndToken(accessList, token);
        } else {
            token = createAccessToken(accessList.getUserId());
            accessManager.putAccessListAndToken(accessList, token);
        }


        //    JSONRPCSecurityManager.putAccessListAndToken(accessList,token);
        String jsonPermission = "|";
        for (String albumName : accessList.getPermissions().keySet()) {
            List permissions = accessList.getPermissions().get(albumName);
            for (Object permission : permissions) {
                jsonPermission += albumName + "." + ((Permission) permission).getPermission() + "|";
            }
        }
        return "userId:'" + accessList.getUserId() +
                "',token:'" + token +
                "',permissions:'" + jsonPermission + "'";

    }


    private void send(PrintWriter out, StringBuffer sb) {
        out.write(sb.toString());
        out.flush();
        out.close();
    }

    private StringBuffer createJSONUser(HttpServletRequest request) {
        StringBuffer sb = new StringBuffer();
        if (request.getSession().getAttribute(ACCESS_LIST) != null && request.getSession().getAttribute(ACCESS_LIST) != "") {

            AccessList accessList = (AccessList) request.getSession().getAttribute(ACCESS_LIST);

            String userId = accessList.getUserId();
            if (userId.equals(SUPER_ADMIN)) {

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
