package yushijinhun.authlibagent.backend.api;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.interfaces.RSAPrivateKey;

/**
 * 管理用于数字签名的RSA密钥
 * 
 * @author yushijinhun
 */
public interface SignatureKeyManager extends Remote {

	/**
	 * 获得用于数字签名的RSA密钥。
	 * 
	 * @return rsa密钥，可以为null
	 * @throws RemoteException 如果RMI调用期间出现异常
	 */
	RSAPrivateKey getKey() throws RemoteException;

	/**
	 * 设置用于数字签名的RSA密钥。
	 * <p>
	 * key可以为null，但所有要求数字签名的业务都将失败。<br>
	 * 该key不会被保存，在webapp destroy后key将丢失。
	 * 
	 * @param key rsa密钥
	 * @throws RemoteException 如果RMI调用期间出现异常
	 */
	void setKey(RSAPrivateKey key) throws RemoteException;

}
