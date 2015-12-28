package yushijinhun.authlibagent.web.exception;

public class YggdrasilUnsupportedMediaTypeException extends YggdrasilException {

	private static final long serialVersionUID = 1L;

	public YggdrasilUnsupportedMediaTypeException() {
	}

	public YggdrasilUnsupportedMediaTypeException(String message, Throwable cause) {
		super(message, cause);
	}

	public YggdrasilUnsupportedMediaTypeException(String message) {
		super(message);
	}

	public YggdrasilUnsupportedMediaTypeException(Throwable cause) {
		super(cause);
	}

	@Override
	public String getErrorName() {
		return "Unsupported Media Type";
	}

	@Override
	public String getErrorMessage() {
		return "The server is refusing to service the request because the entity of the request is in a format not supported by the requested resource for the requested method";
	}

	@Override
	public int getResponseCode() {
		return 400;
	}

}
