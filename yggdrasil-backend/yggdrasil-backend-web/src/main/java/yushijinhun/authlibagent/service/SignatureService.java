package yushijinhun.authlibagent.service;

import java.security.interfaces.RSAPrivateKey;

public interface SignatureService {

	/**
	 * 为给定数据使用 SHA1withRSA (UTF-8) 算法签名。
	 * 
	 * @param data 要签名的数据
	 * @return 签名值
	 * @throws SignatureException 如果出现签名时出现异常
	 */
	byte[] sign(byte[] data) throws SignatureException;

	/**
	 * 更换签名所用的密钥。
	 * <p>
	 * key可以为null，但所有要求数字签名的业务都将失败。
	 * 
	 * @param key 用于签名的新密钥
	 */
	void setSignatureKey(RSAPrivateKey key);

}
