package org.apache.photark.social.message;

import java.util.List;
import java.util.Set;

import org.apache.photark.social.exception.PhotArkSocialException;
import org.apache.photark.social.util.FilterOptions;

public class MessageManagerImpl implements MessageManager{

	public void createMessage(String userId, String msgCollId, Message message)
			throws PhotArkSocialException {
		// TODO Auto-generated method stub
		
	}

	public void createMessageCollection(String userId,
			MessageCollection messageColl) throws PhotArkSocialException {
		// TODO Auto-generated method stub
		
	}

	public void deleteMessageCollection(String userId, String msgCollId)
			throws PhotArkSocialException {
		// TODO Auto-generated method stub
		
	}

	public void deleteMessages(String userId, String msgCollId,
			List<String> msgIds) throws PhotArkSocialException {
		// TODO Auto-generated method stub
		
	}

	public List<MessageCollection> getMessageCollections(String userId,
			Set<String> fileds, FilterOptions filters)
			throws PhotArkSocialException {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Message> getMessages(String userId, String msgCollId,
			Set<String> fields, List<String> msgIds, FilterOptions filters)
			throws PhotArkSocialException {
		// TODO Auto-generated method stub
		return null;
	}

	public void modifyMessage(String userId, String msgCollId, String msgId,
			Message message) throws PhotArkSocialException {
		// TODO Auto-generated method stub
		
	}

	public void modifyMessageCollections(String userId,
			MessageCollection messageCollection) throws PhotArkSocialException {
		// TODO Auto-generated method stub
		
	}

}
