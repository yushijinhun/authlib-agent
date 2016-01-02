package yushijinhun.authlibagent.backend.service;

import java.security.interfaces.RSAPrivateKey;

public interface KeyChangeListener {

	void onChange(RSAPrivateKey newKey);

}
