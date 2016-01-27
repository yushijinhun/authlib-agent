package yushijinhun.authlibagent.service;

import static yushijinhun.authlibagent.util.RandomUtils.getSecureRandom;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import javax.annotation.PostConstruct;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import yushijinhun.authlibagent.util.KeyUtils;

@Component
public class SignatureServiceImpl implements SignatureService {

	private static final Logger LOGGER = LogManager.getFormatterLogger();

	private volatile RSAPrivateKey key;

	@PostConstruct
	private void loadLocalKey() {
		byte[] binKey;
		try (InputStream in = getClass().getResourceAsStream("/signature_key.der")) {
			if (in == null) {
				LOGGER.info("no local key found");
				return;
			}
			binKey = IOUtils.toByteArray(in);
		} catch (IOException e) {
			LOGGER.warn("an i/o exception occurred when loading private key", e);
			return;
		}

		RSAPrivateKey newkey;
		try {
			newkey = KeyUtils.fromPKCS8(binKey);
		} catch (GeneralSecurityException e) {
			LOGGER.warn("cannot construct rsa private key", e);
			return;
		}
		setKey(newkey);
	}

	@Override
	public RSAPrivateKey getKey() {
		return key;
	}

	@Override
	public void setKey(RSAPrivateKey key) {
		LOGGER.info("new signature key: " + key);
		this.key = key;
	}

	@Override
	public byte[] sign(byte[] data) throws GeneralSecurityException {
		if (key == null) {
			throw new InvalidKeyException("no key to sign with");
		}
		Signature signature = Signature.getInstance("SHA1withRSA");
		signature.initSign(key, getSecureRandom());
		signature.update(data);
		return signature.sign();
	}

}
