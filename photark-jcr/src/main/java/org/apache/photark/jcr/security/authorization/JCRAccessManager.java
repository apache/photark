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

package org.apache.photark.jcr.security.authorization;

import org.apache.photark.jcr.JCRRepositoryManager;
import org.apache.photark.security.authorization.services.AccessManager;
import org.oasisopen.sca.annotation.Reference;
import org.oasisopen.sca.annotation.Remotable;
import org.oasisopen.sca.annotation.Scope;

@Remotable
@Scope("COMPOSITE")
public class JCRAccessManager implements AccessManager {
	private static String accessList;
	private JCRRepositoryManager repositoryManager;

	public JCRAccessManager(){

	}

	@Reference(name="repositoryManager")
	protected void setRepositoryManager(JCRRepositoryManager repositoryManager) {
		this.repositoryManager = repositoryManager;
	}

	public synchronized String creatAccessList(String accessList) {
		this.accessList = accessList;
		return accessList;
	}
}
