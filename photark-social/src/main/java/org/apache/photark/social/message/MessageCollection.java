package org.apache.photark.social.message;

import java.util.Date;
import java.util.List;

public class MessageCollection {
	public static String ALL="@all";
	public static String OUTBOX="@outbox";
	
	private String id; // unique ID of the message collection
	private String title; //title of the message collection
	private int totalCount; // total number of messages for this collection
	private int unreadCount; //total number of unread messages
	private Date lastUpdated; // the last time this message collection was modified
	private List<String> urls; // the URLs related to the message collection
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public int getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}
	public int getUnreadCount() {
		return unreadCount;
	}
	public void setUnreadCount(int unreadCount) {
		this.unreadCount = unreadCount;
	}
	public Date getLastUpdated() {
		return lastUpdated;
	}
	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}
	public List<String> getUrls() {
		return urls;
	}
	public void setUrls(List<String> urls) {
		this.urls = urls;
	}
	

}
