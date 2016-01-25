package yushijinhun.authlibagent.web.yggdrasil;

import java.security.GeneralSecurityException;

public interface SignatureService {

	byte[] sign(byte[] data) throws GeneralSecurityException;

}
