package yushijinhun.authlibagent.service;

import java.security.GeneralSecurityException;
import java.security.interfaces.RSAPrivateKey;

public interface SignatureService {

	RSAPrivateKey getKey();

	void setKey(RSAPrivateKey key);

	byte[] sign(byte[] data) throws GeneralSecurityException;

}
