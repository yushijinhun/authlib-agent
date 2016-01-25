package yushijinhun.authlibagent.service;

import java.security.interfaces.RSAPrivateKey;

public interface KeyServerService {

	RSAPrivateKey getKey();

	void setKey(RSAPrivateKey key);

	void addKeyChangeListener(KeyChangeListener l);

	void removeKeyChangeListener(KeyChangeListener l);

}
