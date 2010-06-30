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

package org.apache.photark.security.authorization;

import java.io.Serializable;
import java.util.*;


/**
 * Immutable AccessList Object responsible for storing permissions of the user.
 * 
 */

public class AccessList implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6849853208649971131L;
	/** */
	private String userId="";
	/** */
	private Map<String, List<Permission>> permissions= new HashMap<String, List<Permission>>();
	

	/**
	 * 
	 * @param userId String
	 * 
	 * @param permissions  List<String>
	 */
	public AccessList(String userId, Map<String, List<Permission>>  permissions){
        this.permissions = Collections.unmodifiableMap(permissions);
		this.userId = userId;
	}
	
	public AccessList(){ 
			
	}
	
	/**
	 * 
	 * @return List<String>
	 */
	public  Map<String, List<Permission>>  getPermissions(){
		return permissions;
	}

	
	/**
	 * 
	 * @return String
	 */
	public String getUserId(){
		return userId;
	}
	
	
	/**
	 * 
	 */
	public boolean equals(Object obj){
		if(!(obj instanceof AccessList))
			return false;
		
		AccessList accessList = (AccessList)obj;
        return accessList.userId.equals(userId) && isPermissionsEqual(accessList.permissions);

    }
	
	
	/**
	 * 
	 * @param permissionList List<String>
	 * 
	 * @return boolean
     */
    private boolean isPermissionsEqual(Map<String, List<Permission>> permissionList) {
        if (permissionList != null && permissions != null) {
            if (permissionList.size() == permissions.size()) {
                for (String permission : permissionList.keySet()) {
                    if (!permissions.keySet().contains(permission))
                        return false;
                    for (Permission aPermission : permissionList.get(permission)) {
                        if (permissions.get(permission).contains(aPermission))
                            return false;
                    }
                }
                return true;
            } else
                return false;
        }
        return false;
    }
	
	
	/**
	 * 
	 */
    public int hashCode() {
        int hash = 1;
        hash = hash * 7 + (userId.equals("") ? 0 : userId.hashCode());
        for (String permission : permissions.keySet()) {
            hash = hash * 7 + (permission == null ? 0 : permission.hashCode());
            for (Permission aPermission : permissions.get(permission)) {
                hash = hash * 7 + (aPermission == null ? 0 : aPermission.hashCode());
            }
        }
        return hash;
    }
}