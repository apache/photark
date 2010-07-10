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

/**
 * Model representing a Permission
 */
public class Permission implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 115956810128294635L;
	public String permission;
	private String desc;

    public Permission(String permission,String description){
		this.permission = permission;
        this.desc = description;
	}
	
	/**
	 * 
	 * @param permission String
	 */
	public Permission(String permission){
		this.permission = permission;
	}
	
	
	/**
	 * 
	 * @param description String
	 */
	public void setDescription(String description){
		this.desc = description;
	}
	
	
	/**
	 * 
	 * @return String
	 */
	public String getPermission(){
		return permission;
	}
	
	
	/**
	 * 
	 * @return String
	 */
	public String getPermissionDesc(){
		return desc;
	}

    public boolean equals(Object obj){
		if(!(obj instanceof Permission))
			return false;

		Permission permission = (Permission)obj;
        return this.getPermission().equals(permission.getPermission()) ;

    }
     public int hashCode() {
        return permission.hashCode();
    }
}
