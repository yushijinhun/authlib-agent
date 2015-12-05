package yushijinhun.authlibagent.api.rmi;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import yushijinhun.authlibagent.api.YggdrasilBackend;

/**
 * 用于建立和yggdrasil-backend的连接
 * 
 * @author yushijinhun
 */
public final class BackendNaming {

	/**
	 * 连接到指定后端。
	 * 
	 * @param host 后端的ip
	 * @param port 端口
	 * @param name RMI服务名
	 * @param trustKeyStore 后端证书
	 * @param clientKeyStore 客户端证书，用于向后端验证身份
	 * @param clientKeyPassword 客户端证书的密码
	 * @return 后端的RMI引用
	 * @throws GeneralSecurityException 如果发生安全异常
	 * @throws RemoteException 如果无法绑定到远程对象
	 * @throws IOException 如果发生I/O异常
	 * @throws NotBoundException 如果RMI服务名不存在
	 */
	public static YggdrasilBackend lookup(String host, int port, String name, KeyStore trustKeyStore, KeyStore clientKeyStore, String clientKeyPassword) throws GeneralSecurityException, RemoteException, IOException, NotBoundException {
		Registry registry = LocateRegistry.getRegistry(host, port, new YggdrasilRMISocketFactory(trustKeyStore, clientKeyStore, clientKeyPassword));
		return (YggdrasilBackend) registry.lookup(name);
	}

	private BackendNaming() {
	}

}
