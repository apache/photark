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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Model representing a Role
 */
public class Role implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -7560129536060718311L;
    private String roleName;
    private  List<Permission> permissions = new ArrayList<Permission>();


    public String getRoleName() {
        return roleName;
    }

    /**
     * @param roleName String
     */
    public Role(String roleName) {
        this.roleName = roleName;
    }

    /**
     * @param permission Permission
     */
    public void setPermission( Permission permission) {
        if (!this.permissions.contains(permission)) {
          this.permissions.add(  permission) ;
        }

    }

    public void setPermission(List<Permission> permission) {
        this.permissions.addAll(permission);
    }

    /**
     * @return List<Permission>
     */
    public List<Permission> getPermissions() {
        return permissions;
    }
}
