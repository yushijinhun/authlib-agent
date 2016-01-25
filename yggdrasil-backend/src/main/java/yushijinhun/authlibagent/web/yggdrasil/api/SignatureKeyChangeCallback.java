package yushijinhun.authlibagent.web.yggdrasil.api;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.interfaces.RSAPrivateKey;

public interface SignatureKeyChangeCallback extends Remote {

	void call(RSAPrivateKey newKey) throws RemoteException;

}
