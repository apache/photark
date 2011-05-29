package org.apache.photark.social.exception;

public class PhotArkSocialException extends Throwable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5053119486166764904L;

	  public PhotArkSocialException() {
	    }

	    public PhotArkSocialException(String message) {
	        super(message);
	    }

	    public PhotArkSocialException(Throwable cause) {
	        super(cause);
	    }

	    public PhotArkSocialException(String message, Throwable cause) {
	        super(message, cause);
	    }

}
