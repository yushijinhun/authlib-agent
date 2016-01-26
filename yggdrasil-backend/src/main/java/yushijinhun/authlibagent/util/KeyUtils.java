package yushijinhun.authlibagent.util;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

public final class KeyUtils {

	public static RSAPrivateKey fromPKCS8(byte[] pkcs8) throws InvalidKeyException, InvalidKeySpecException, NoSuchAlgorithmException {
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(pkcs8);
		PrivateKey key = keyFactory.generatePrivate(keySpec);

		if (key instanceof RSAPrivateKey) {
			return (RSAPrivateKey) key;
		} else {
			throw new InvalidKeyException("Cannot cast " + key + " to a RSA private key");
		}
	}

	private KeyUtils() {
	}
}
