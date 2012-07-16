package de.mukis.tvs.core;

public class ProjectException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1502798363137318575L;
	
	public ProjectException() {
		super();
	}
	
	public ProjectException(String msg) {
		super(msg);
	}

	public ProjectException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ProjectException(String message, Throwable cause) {
		super(message, cause);
	}

	public ProjectException(Throwable cause) {
		super(cause);
	}
	
	

}
