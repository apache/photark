package org.apache.photark.social.person;

import org.apache.photark.social.exception.PhotArkSocialException;

public interface PersonManager {
	/**
	 * Persists the Person data object
	 * @param personId ID of the Person data object
	 * @param person   Person data object
	 * @throws PhotArkSocialException
	 */
	void savePerson(String personId, Person person) throws PhotArkSocialException;
	
	/**
	 * Updates the Person data object with updated fields
	 * @param personId  ID of the Person data object
	 * @param person	Person data object with filed to update
	 * @throws PhotArkSocialException
	 */
	
	void updatePerson(String personId, Person person) throws PhotArkSocialException;
	
	/**
	 * Removes a Person data object from persistence
	 * @param personId	ID of the Person data object to be removed
	 * @throws PhotArkSocialException
	 */
	void removePerson(String personId) throws PhotArkSocialException;
	
	/**
	 * Retrieves the Person data object for the given ID
	 * @param personId ID of the Person data object to be retrieved
	 * @return the Person data object for the given ID
	 * @throws PhotArkSocialException
	 */
	
	Person getPerson(String personId) throws PhotArkSocialException;
	
	/**
	 * Retrieves the Person data object for the given ID with specified fields
	 * @param personId 	personId ID of the Person data object to be retrieved
	 * @param fields	the fields of the Person data object to be retrieved
	 * @return 	the Person data object for the given ID with the specified fields
	 * @throws PhotArkSocialException
	 */
	
	Person getPerson(String personId, String[] fields) throws PhotArkSocialException;
	
	/**
	 * Retrieves array of Person data objects for the given person IDs and group
	 * @param personIds 	String array of IDs of the persons
	 * @param groupId	the ID of the group the persons belongs to; optional
	 * @param fields	the fields of the Person data object to be retrieved
	 * @return	an array of Person data objects
	 * @throws PhotArkSocialException
	 */
	Person[] getPeople(String[] personIds, String groupId,String[] fields) throws PhotArkSocialException;
	
	
}
