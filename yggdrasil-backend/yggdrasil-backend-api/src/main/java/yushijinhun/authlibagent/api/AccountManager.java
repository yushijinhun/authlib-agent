package yushijinhun.authlibagent.api;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.UUID;

/**
 * 管理账户
 * 
 * @author yushijinhun
 */
public interface AccountManager extends Remote {

	/**
	 * 根据uuid查询角色。
	 * 
	 * @param uuid uuid
	 * @return 查询到的角色，如果uuid为null或没有角色与给定uuid对应则返回null
	 * @throws RemoteException 如果RMI调用期间出现异常
	 */
	GameProfile lookupGameProfile(UUID uuid) throws RemoteException;

	/**
	 * 根据角色名查询角色。
	 * <p>
	 * 因为改名机制，所以不推荐使用角色名作为标识。
	 * 
	 * @param name 角色名
	 * @return 查询到的角色，如果name为null或没有角色与给定name对应则返回null
	 * @throws RemoteException 如果RMI调用期间出现异常
	 */
	GameProfile lookupGameProfile(String name) throws RemoteException;

	/**
	 * 根据账户id查询账户。
	 * 
	 * @param id 账户id
	 * @return 查询到的账户，如果id为null或没有角色与给定id对应则返回null
	 * @throws RemoteException 如果RMI调用期间出现异常
	 */
	YggdrasilAccount lookupAccount(String id) throws RemoteException;

	/**
	 * 创建一个账户。
	 * <p>
	 * 注意：捕获 {@link IDCollisionException} 时，同时要注意处理ServerException里包装了 {@link IDCollisionException} 的情况。
	 * 
	 * @param id 账户id，须满足 {@link YggdrasilValidate#isValidId(String)}，必须保证唯一
	 * @return 创建的账户
	 * @throws IDCollisionException 如果id发生冲突
	 * @throws RemoteException 如果RMI调用期间出现异常
	 */
	YggdrasilAccount createAccount(String id) throws IDCollisionException, RemoteException;

}
