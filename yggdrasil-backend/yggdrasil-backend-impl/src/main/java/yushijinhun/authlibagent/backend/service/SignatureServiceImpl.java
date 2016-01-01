package yushijinhun.authlibagent.backend.service;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import javax.management.openmbean.InvalidKeyException;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component("signature_service")
public class SignatureServiceImpl implements SignatureService {

	private static final Logger LOGGER = LogManager.getFormatterLogger();

	private volatile RSAPrivateKey signKey;

	public SignatureServiceImpl() {
		loadLocalKey();
	}

	@Override
	public byte[] sign(byte[] data) throws GeneralSecurityException {
		RSAPrivateKey key = signKey;
		if (key == null) {
			throw new InvalidKeyException("no key to sign with");
		}

		Signature signature = null;
		signature = Signature.getInstance("SHA1withRSA");
		signature.initSign(signKey);
		signature.update(data);
		return signature.sign();
	}

	@Override
	public void setSignatureKey(RSAPrivateKey key) {
		LOGGER.info("new signature key: " + key);
		this.signKey = key;
	}

	private void loadLocalKey() {
		byte[] binKey;
		try (InputStream in = getClass().getResourceAsStream("/signature_key.der")) {
			if (in == null) {
				LOGGER.info("no local key found");
				return;
			}
			binKey = IOUtils.toByteArray(in);
		} catch (IOException e) {
			LOGGER.warn("an i/o exception occurred when loading local key", e);
			return;
		}

		PrivateKey key;
		try {
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(binKey);
			key = keyFactory.generatePrivate(keySpec);
		} catch (GeneralSecurityException e) {
			LOGGER.warn("unable to construct rsa private key", e);
			return;
		}

		if (key instanceof RSAPrivateKey) {
			setSignatureKey((RSAPrivateKey) key);
		} else {
			LOGGER.warn("unable to cast " + key + " to a rsa private key");
			return;
		}
	}

}
