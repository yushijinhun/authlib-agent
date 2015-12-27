package yushijinhun.authlibagent.web.exception;

public class YggdrasilIllegalArgumentException extends YggdrasilException {

	private static final long serialVersionUID = 1L;

	@Override
	public String getErrorName() {
		return "IllegalArgumentException";
	}

	@Override
	public int getResponseCode() {
		return 400;
	}

}
