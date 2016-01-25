package yushijinhun.authlibagent.service;

import java.security.interfaces.RSAPrivateKey;

public interface KeyChangeListener {

	void onChange(RSAPrivateKey newKey);

}
