package yushijinhun.authlibagent.service;

import java.security.GeneralSecurityException;
import yushijinhun.authlibagent.service.annotation.ErrorMessage;

@ErrorMessage(error = "ForbiddenOperationException", message = "The server is unable to sign the data")
public class SignatureException extends GeneralSecurityException {

	private static final long serialVersionUID = 1L;

	public SignatureException() {
	}

	public SignatureException(String message, Throwable cause) {
		super(message, cause);
	}

	public SignatureException(String msg) {
		super(msg);
	}

	public SignatureException(Throwable cause) {
		super(cause);
	}

}
