package org.apache.photark.social.person;

import java.util.Date;
import java.util.List;

public class Person {
	
	private String aboutMe;
	private List<String> activities;
	private String address;
	private Date birthday;
	private String displayName;
	private List<String> emails;
	private String firstName;
	private String gender;
	private String id;
	private String lastName;
	private String nickname;
	private List<String> organizations;
	private List<String> phoneNumbers;
	private String thumbnailUrl;
	public void setAboutMe(String aboutMe) {
		this.aboutMe = aboutMe;
	}
	public String getAboutMe() {
		return aboutMe;
	}
	public void setActivities(List<String> activities) {
		this.activities = activities;
	}
	public List<String> getActivities() {
		return activities;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getAddress() {
		return address;
	}
	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}
	public Date getBirthday() {
		return birthday;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setEmails(List<String> emails) {
		this.emails = emails;
	}
	public List<String> getEmails() {
		return emails;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getGender() {
		return gender;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getId() {
		return id;
	}
	public void setOrganizations(List<String> organizations) {
		this.organizations = organizations;
	}
	public List<String> getOrganizations() {
		return organizations;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getNickname() {
		return nickname;
	}
	public void setPhoneNumbers(List<String> phoneNumbers) {
		this.phoneNumbers = phoneNumbers;
	}
	public List<String> getPhoneNumbers() {
		return phoneNumbers;
	}
	public void setThumbnailUrl(String thumbnailUrl) {
		this.thumbnailUrl = thumbnailUrl;
	}
	public String getThumbnailUrl() {
		return thumbnailUrl;
	}
	

}
