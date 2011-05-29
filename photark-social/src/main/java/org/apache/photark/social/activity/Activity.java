package org.apache.photark.social.activity;

import java.util.Date;

public class Activity {

	private String body; // content of the activity
	private String id; // ID associated with this activity
	// private Array<MediaItem>
	private Date postedTime;
	private String title; // primary text of an activity
	private String url; // url representing this activity
	private String userId; // ID of the user to whom this activity is belong to

	public void setBody(String body) {
		this.body = body;
	}

	public String getBody() {
		return body;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public Date getPostedTime() {
		return postedTime;
	}

	public void setPostedTime(Date postedTime) {
		this.postedTime = postedTime;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

}
