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

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 */
public class JSONRPCSecurityManager {
    private static Map<String, Object[]> accessTokenMap = new HashMap<String, Object[]>();

    public static boolean isUserExist(String userId) {
        return accessTokenMap.containsKey(userId);
    }

    public static AccessList getAccessList(String userId) {
        Object[] accessListAndToken = accessTokenMap.get(userId);
        return (AccessList) accessListAndToken[0];

    }

    public static String getSecurityToken(String userId) {
        Object[] accessListAndToken = accessTokenMap.get(userId);
        return (String) accessListAndToken[1];

    }

    public static AccessList getAccessListFromSecurityToken(String token) {
        Object[] accessListAndToken = accessTokenMap.get(userIdFromSecurityToken(token));
        return (AccessList) accessListAndToken[0];

    }

    public static String userIdFromSecurityToken(String token) { 
        String userId=    token.substring(0, token.length() - 25);    //don't use this anywhere else
        getSecurityToken(userId);
        if(token.equals(getSecurityToken(userId))){
            return  userId;
        }
        return "UnRegisteredUser";

    }

    public static void putAccessListAndToken(AccessList accessList, String token) {
        accessTokenMap.put(accessList.getUserId(), new Object[]{accessList, token});

    }

    public static void removeAccessListAndToken(String userId) {
        accessTokenMap.remove(userId);

    }

    public String getJSONAccessList(HttpServletRequest request) {

        AccessList accessList = (AccessList) request.getSession().getAttribute("accessList");
        String token;
        if (JSONRPCSecurityManager.isUserExist(accessList.getUserId())) {
            token = JSONRPCSecurityManager.getSecurityToken(accessList.getUserId());
        } else {
            token = createAccessToken(accessList.getUserId());
            JSONRPCSecurityManager.putAccessListAndToken(accessList, token);
        }
         String jsonPermission = "|";
        for (String key:accessList.getPermissions().keySet()) {
            List permissions = accessList.getPermissions().get(key);
            if("_default".equals(key)) {
            for (Object permission : permissions) {
                jsonPermission +=  ((Permission) permission).getPermission()+"|" ;
            }
            }else{
                for (Object permission : permissions) {
                jsonPermission += key+"."+ ((Permission) permission).getPermission()+"|" ;
                }
            }

        }
            return "userId:'" + accessList.getUserId() +
                    "',token:'" + token +
                    "',permissions:'" + jsonPermission + "'";

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



}
