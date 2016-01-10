package yushijinhun.authlibagent.backend.service;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import yushijinhun.authlibagent.backend.api.SignatureKeyManager;

@Component("keyServerService")
public class KeyServerServiceImpl implements KeyServerService, SignatureKeyManager {

	private static final Logger LOGGER = LogManager.getFormatterLogger();

	private volatile RSAPrivateKey key;
	private Set<KeyChangeListener> listeners = Collections.newSetFromMap(new ConcurrentHashMap<>());

	public KeyServerServiceImpl() {
		loadLocalKey();
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
			setKey((RSAPrivateKey) key);
		} else {
			LOGGER.warn("unable to cast " + key + " to a rsa private key");
			return;
		}
	}

	@Override
	public RSAPrivateKey getKey() {
		return key;
	}

	@Override
	public void setKey(RSAPrivateKey key) {
		LOGGER.info("new signature key: " + key);
		this.key = key;
		listeners.forEach(l -> l.onChange(key));
	}

	@Override
	public void addKeyChangeListener(KeyChangeListener l) {
		listeners.add(l);
	}

	@Override
	public void removeKeyChangeListener(KeyChangeListener l) {
		listeners.remove(l);
	}

}
