package yushijinhun.authlibagent.web;

import java.security.GeneralSecurityException;

public interface SignatureService {

	byte[] sign(byte[] data) throws GeneralSecurityException;

}
