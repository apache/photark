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

package org.apache.photark.ui;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.CollectingAlertHandler;
import com.gargoylesoftware.htmlunit.DefaultCredentialsProvider;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlOption;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
import com.gargoylesoftware.htmlunit.html.HtmlTable;


public class IE8TestBrowser {

	public static WebClient webClient;

	@BeforeClass
	public static void setUp(){
		webClient = new WebClient(BrowserVersion.INTERNET_EXPLORER_8);
	}

	@Test
	public void testGallery() throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {
		final HtmlPage page = webClient.getPage("http://localhost:8080/photark/");
		final HtmlTable table = page.getHtmlElementById("tableGallery");
		
		//testing the static part of the gallery
		final String pageAsXml = page.asXml();
		assertTrue(pageAsXml.contains("<a href=\"javascript:displayGallery()\" onmouseover=\"document.index.src=index_on.src\" onmouseout=\"document.index.src=index_off.src\" onmousedown=\"beforeClick();\">"));
		assertTrue(pageAsXml.contains("<body onload=\"initGallery()\">"));
		final String pageAsText = page.asText();
		assertTrue(pageAsText.contains("Apache PhotArk Gallery"));
		
		Thread.sleep(3000);
		//testing the dynamic part of the gallery
		
		//albums loaded
		assertTrue( table.getRow(1).getCell(0).asText().contains("boston"));
		//Gallery pictures loaded
		assertTrue( table.getRow(3).getCell(0).asXml().contains("http://localhost:8080/photark/gallery/vegas/dsc00860.jpg")); 
		
		//clicking on an image
		assertTrue(page.getElementById("albumImage").getAttribute("src").contains("space.gif"));
		DomNodeList<HtmlElement> ele= table.getRow(3).getCell(0).getElementsByTagName("a");
		final HtmlAnchor anchor =(HtmlAnchor) ele.get(0);
		final HtmlPage page2= anchor.click(); 
		Thread.sleep(3000);
		//checking whether there are images in the album
		assertTrue( page2.getElementById("albumImage").getAttribute("src").contains("/photark/gallery/vegas/dsc00860.jpg"));

	}

	@Test
	public void testAdmin() throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {
		//passing credentials
		final List collectedAlerts = new ArrayList();
		webClient.setAlertHandler(new CollectingAlertHandler(collectedAlerts));
		((DefaultCredentialsProvider) webClient.getCredentialsProvider()).addCredentials("admin", "password"); 

		final HtmlPage page = webClient.getPage("http://localhost:8080/photark/admin/upload.html");
		Thread.sleep(3000);
		
		//testing the loaded page
		HtmlSelect select = page.getHtmlElementById("selectAlbum"); 
		assertTrue(select.asText().contains("New Album"));
		assertTrue(page.<HtmlElement>getHtmlElementById("cancelBtnAlbumDesc").getAttribute("style").contains("display: none;"));
		assertTrue(page.<HtmlElement>getHtmlElementById("deleteAlbum").getAttribute("style").contains("display: none;"));
		assertTrue(page.<HtmlElement>getHtmlElementById("btnAlbumDesc").getAttribute("style").contains("display: none;"));

		assertFalse(page.<HtmlElement>getHtmlElementById("albumCover").asXml().contains("photark/gallery/boston/dsc00376.jpg"));
		assertFalse(page.<HtmlElement>getHtmlElementById("newAlbumLabel").getAttribute("style").contains("display: none;"));
		assertFalse(page.<HtmlElement>getHtmlElementById("newAlbumName").getAttribute("style").contains("display: none;"));
		assertFalse(page.<HtmlElement>getHtmlElementById("adminTableGallery").asXml().contains("photark/gallery/boston/dsc00376.jpg"));
		assertFalse(page.<HtmlElement>getHtmlElementById("adminTableGallery").asXml().contains("photark/gallery/boston/dsc00368.jpg"));

		//selecting an album from drop down
		HtmlOption option = select.getOption(1);
		option.click();
		Thread.sleep(3000);
		//testing whether the expected changes has happened
		assertTrue(select.asText().contains("boston"));
		assertTrue(page.<HtmlElement>getHtmlElementById("albumCover").asXml().contains("photark/gallery/boston/dsc"));
		
		assertTrue(page.<HtmlElement>getHtmlElementById("adminTableGallery").asXml().contains("photark/gallery/boston/dsc00376.jpg"));
		assertTrue(page.<HtmlElement>getHtmlElementById("adminTableGallery").asXml().contains("photark/gallery/boston/dsc00368.jpg"));
		
		assertTrue(page.<HtmlElement>getHtmlElementById("newAlbumLabel").getAttribute("style").contains("display: none;"));
		assertTrue(page.<HtmlElement>getHtmlElementById("newAlbumName").getAttribute("style").contains("display: none;"));
		assertTrue(page.<HtmlElement>getHtmlElementById("cancelBtnAlbumDesc").getAttribute("style").contains("display: none;"));
		assertFalse(page.<HtmlElement>getHtmlElementById("deleteAlbum").getAttribute("style").contains("display: none;"));
		assertFalse(page.<HtmlElement>getHtmlElementById("btnAlbumDesc").getAttribute("style").contains("display: none;"));

		//checking the changes by clicking the edit album description button
		assertTrue(page.<HtmlElement>getHtmlElementById("albumDescription").getAttribute("readonly").contains("readonly"));
		page.<HtmlElement>getHtmlElementById("btnAlbumDesc").click();
		assertTrue(page.getFocusedElement().getAttribute("id").contains("albumDescription"));
		assertFalse(page.<HtmlElement>getHtmlElementById("cancelBtnAlbumDesc").getAttribute("style").contains("display: none;"));
		assertFalse(page.<HtmlElement>getHtmlElementById("albumDescription").getAttribute("readonly").contains("readonly"));
		page.<HtmlElement>getHtmlElementById("cancelBtnAlbumDesc").click();
		assertTrue(page.<HtmlElement>getHtmlElementById("albumDescription").getAttribute("readonly").contains("readonly"));
		assertTrue(page.<HtmlElement>getHtmlElementById("cancelBtnAlbumDesc").getAttribute("style").contains("display: none;"));

	}

}