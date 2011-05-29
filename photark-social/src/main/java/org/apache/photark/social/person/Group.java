package org.apache.photark.social.person;

public class Group {
	private String id; // ID representing this group
	private String title; // title of the group
	private String description; // description of the group
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
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
}
