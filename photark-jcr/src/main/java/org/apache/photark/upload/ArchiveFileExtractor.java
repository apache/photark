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

package org.apache.photark.upload;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.io.IOUtils;
import org.apache.photark.Picture;


public class ArchiveFileExtractor 
{
	private String entryTypes[];
		
	public ArchiveFileExtractor(String [] entryTypes){
		this.entryTypes = entryTypes;
	}
		
	public List<Picture> extractArchive(InputStream inStream){
		ArchiveStreamFactory streamFactory = new ArchiveStreamFactory();
		List<Picture> pictures = new ArrayList<Picture>();
		try 
		{
			ArchiveInputStream archiveInputStream = streamFactory.createArchiveInputStream(inStream);
			ArchiveEntry entry = null;
			while((entry = archiveInputStream.getNextEntry())!= null){
				if(!entry.isDirectory() && isEntryTypeAllowd(entry)){
					byte buf[] = IOUtils.toByteArray(archiveInputStream);
					InputStream inputStream = new ByteArrayInputStream(buf);
					Picture picture = new Picture(entry.getName(), new Date(), inputStream);
					pictures.add(picture);
				}
			}
		} 
		catch (ArchiveException e) 
		{
			e.printStackTrace();
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		return pictures;
	}
	
	
	/**
	 * Test whether this stream is of archive type or not
	 * 
	 * @param inStream InputStream
	 * 
	 * @return boolean
	 */
	public boolean isArchive(InputStream inStream){
		ArchiveStreamFactory streamFactory = new ArchiveStreamFactory();
		try {
			streamFactory.createArchiveInputStream(inStream);
			return true;
		} catch (ArchiveException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	
	/**
	 * Test weather the extension of given entry is allowed or not
	 * 
	 * @param entry ArchiveEntry
	 * 
	 * @return boolean
	 */
	private boolean isEntryTypeAllowd(ArchiveEntry entry){
		String entryName = entry.getName();
		for(String entryType : entryTypes){
			if(entryName.endsWith(entryType))
				return true;
		}
		return false;
	}
}