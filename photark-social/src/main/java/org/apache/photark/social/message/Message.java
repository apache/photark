package org.apache.photark.social.message;

import java.util.Date;
import java.util.List;

public class Message {

	private String body; // main text of the message
	private String id; // unique ID of this message
	private String inReplyTo; // ID of the message for which this message is a
								// reply/comment
	private List<String> recepients; // list of person IDs
	private List<String> replies; // list of message IDs which are replies for
									// this message
	private String senderId; // ID of the person who sent the message
	private String status; // status of the message (NEW, READ, DELETED)
	private Date timeSent; // the time message was sent
	private String title; // title of the message
	private Date updated; // last updated time of this message
	private String url; // url for this message
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getInReplyTo() {
		return inReplyTo;
	}
	public void setInReplyTo(String inReplyTo) {
		this.inReplyTo = inReplyTo;
	}
	public List<String> getRecepients() {
		return recepients;
	}
	public void setRecepients(List<String> recepients) {
		this.recepients = recepients;
	}
	public List<String> getReplies() {
		return replies;
	}
	public void setReplies(List<String> replies) {
		this.replies = replies;
	}
	public String getSenderId() {
		return senderId;
	}
	public void setSenderId(String senderId) {
		this.senderId = senderId;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Date getTimeSent() {
		return timeSent;
	}
	public void setTimeSent(Date timeSent) {
		this.timeSent = timeSent;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Date getUpdated() {
		return updated;
	}
	public void setUpdated(Date updated) {
		this.updated = updated;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
}
