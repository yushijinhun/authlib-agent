package yushijinhun.authlibagent.api;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.interfaces.RSAPrivateKey;

/**
 * 和后端通信的主接口
 * 
 * @author yushijinhun
 */
public interface YggdrasilBackend extends Remote {

	/**
	 * 获得账户管理器。
	 * 
	 * @return 账户管理器
	 * @throws RemoteException 如果RMI调用期间出现异常
	 */
	AccountManager getAccountManager() throws RemoteException;

	/**
	 * 获取对于mc服务端验证登录的访问控制器。
	 * <p>
	 * 只有被该访问控制器允许的主机才能验证玩家的登录是否有效。<br>
	 * 使用白名单模式并将mc服务端ip加入允许列表可以防止其他mc服务端使用该yggdasil-backend进行验证。<br>
	 * 该访问规则将会被持久化到数据库中。
	 * 
	 * @return 对于mc服务端验证登录的访问控制器
	 * @throws RemoteException 如果RMI调用期间出现异常
	 */
	HostAccessManager getGameServerAccessManager() throws RemoteException;

	/**
	 * 设置用于数字签名的RSA密钥。
	 * <p>
	 * key可以为null，但所有要求数字签名的业务都将失败。<br>
	 * 该key不会被保存，在webapp destroy后key将丢失。
	 * 
	 * @param key rsa密钥
	 * @throws RemoteException 如果RMI调用期间出现异常
	 */
	void setSignatureKey(RSAPrivateKey key) throws RemoteException;

}
