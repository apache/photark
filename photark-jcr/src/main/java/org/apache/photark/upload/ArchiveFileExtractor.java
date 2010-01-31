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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;

public class ArchiveFileExtractor 
{
	private String entryTypes[];
	private String albumDir;
		
	public ArchiveFileExtractor(String albumDir, String [] entryTypes){
		this.albumDir = albumDir;
		this.entryTypes = entryTypes;
	}
		
	public List<String> extractArchive(InputStream inStream){
		ArchiveStreamFactory streamFactory = new ArchiveStreamFactory();
		List<String> pictures = new ArrayList<String>();
		try 
		{
			ArchiveInputStream archiveInputStream = streamFactory.createArchiveInputStream(inStream);
			ArchiveEntry entry = null;
			 byte[] buffer = new byte[2048];
			while((entry = archiveInputStream.getNextEntry())!= null){
				System.out.println("entry:"+entry.getName());
				if(!entry.isDirectory() && isEntryTypeAllowd(entry)){
					String outpath = albumDir + File.separator + entry.getName();
					File file = new File(outpath);
					FileOutputStream outputStream = null;
					 try{
						 outputStream = new FileOutputStream(file);
					 	 int len = 0;
					 	 while ((len = archiveInputStream.read(buffer)) > 0){
					 		 outputStream.write(buffer, 0, len);
					 	  }
					 	pictures.add(entry.getName());
					 }
					finally{
						if(outputStream!=null) outputStream.close();
					}
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
	 * Test wether the extension of given entry is allowed or not
	 * @param entry
	 * @return
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