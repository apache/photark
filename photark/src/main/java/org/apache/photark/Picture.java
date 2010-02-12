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

package org.apache.photark;

import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * Model representing an album image
 */
public class Picture {
	private String name;
	private String description;
	private Date creationDate;
	private InputStream inStream;
	
	private List<Properties> imageProperties;
	
	public Picture(String name, Date createDate){
		this.name = name;
		this.creationDate = createDate;
	}
	
	public Picture(String name, Date createDate, InputStream inStream){
		this(name, createDate);
		this.inStream = inStream;
	}
	
	
	public String getName(){
		return name;
	}
	
	public Date getCreationDate(){
		return creationDate;
	}
	
	public InputStream getInputStream(){
		return inStream;
	}
	
}