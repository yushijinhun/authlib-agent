package yushijinhun.authlibagent.api;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * 用于管理针对主机的访问政策
 * 
 * @author yushijinhun
 */
public interface HostAccessManager extends Remote {

	/**
	 * 获得对给定主机的访问政策。
	 * 
	 * @param host 主机ip或域名
	 * @return 访问政策，null则为默认
	 * @throws RemoteException 如果RMI调用期间出现异常
	 */
	AccessPolicy getHostPolicy(String host) throws RemoteException;

	/**
	 * 设置对给定主机访问政策。
	 * 
	 * @param host 主机ip或域名
	 * @param policy 访问政策，null则设为默认
	 * @throws RemoteException 如果RMI调用期间出现异常
	 */
	void setHostPolicy(String host, AccessPolicy policy) throws RemoteException;

	/**
	 * 获得默认的访问政策。
	 * 
	 * @return 访问政策，null则为默认
	 * @throws RemoteException 如果RMI调用期间出现异常
	 */
	AccessPolicy getDefaultPolicy() throws RemoteException;

	/**
	 * 设置默认的访问政策。
	 * 
	 * @param policy 访问政策
	 * @throws RemoteException 如果RMI调用期间出现异常
	 */
	void setDefaultPolicy(AccessPolicy policy) throws RemoteException;

}
