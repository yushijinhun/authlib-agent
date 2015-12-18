package yushijinhun.authlibagent.service;

import java.security.interfaces.RSAPrivateKey;

public interface SignatureService {

	byte[] sign(byte[] data) throws SignatureException;

	void setSignatureKey(RSAPrivateKey key);

}
