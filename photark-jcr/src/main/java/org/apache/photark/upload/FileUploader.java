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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.photark.Picture;

public class FileUploader {
	
	private String entryTypes [] = {".jpg",".jpeg",".png",".gif"}; 
	
	public FileUploader(){
	}
	
	
	public List<Picture> uploadFile(InputStream inStream, String fileName) throws IOException{
		
		List<Picture> pictures = new ArrayList<Picture>();
		
		if(isArchive(inStream)){
			ArchiveFileExtractor archiveFileExtractor = new ArchiveFileExtractor(entryTypes);
			pictures = archiveFileExtractor.extractArchive(inStream);
		}
		else{
			// this is a picture file and not the archive file
			Picture picture = new Picture(fileName, new Date(), inStream);
			pictures.add(picture);
		}
		return pictures;
	}

	
	/**
	 * Test whether this stream is of archive or not
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
}