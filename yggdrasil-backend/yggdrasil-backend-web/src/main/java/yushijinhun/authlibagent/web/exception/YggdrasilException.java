package yushijinhun.authlibagent.web.exception;

abstract public class YggdrasilException extends Exception {

	private static final long serialVersionUID = 1L;

	public YggdrasilException() {
	}

	public YggdrasilException(String message, Throwable cause) {
		super(message, cause);
	}

	public YggdrasilException(String message) {
		super(message);
	}

	public YggdrasilException(Throwable cause) {
		super(cause);
	}

	public String getErrorName() {
		return getClass().getSimpleName();
	}

	public String getErrorMessage() {
		return getMessage();
	}

	public String getErrorCause() {
		return null;
	}

	abstract public int getResponseCode();

}
