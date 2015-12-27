package yushijinhun.authlibagent.web.exception;

public class YggdrasilForbiddenOperationException extends YggdrasilException {

	private static final long serialVersionUID = 1L;

	@Override
	public String getErrorName() {
		return "ForbiddenOperationException";
	}

	@Override
	public int getResponseCode() {
		return 403;
	}

}
