package yushijinhun.authlibagent.service;

public class ForbiddenOperationException extends Exception {

	private static final long serialVersionUID = 1L;

	public ForbiddenOperationException() {
	}

	public ForbiddenOperationException(String message, Throwable cause) {
		super(message, cause);
	}

	public ForbiddenOperationException(String message) {
		super(message);
	}

	public ForbiddenOperationException(Throwable cause) {
		super(cause);
	}

}
