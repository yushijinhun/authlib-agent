package yushijinhun.authlibagent.api;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Set;
import java.util.UUID;

/**
 * 游戏账户
 * <p>
 * 一个账户可以拥有多个角色。账户使用string id进行标识，id必须满足 {@link YggdrasilValidate#isValidId(String)}。
 * 
 * @author yushijinhun
 */
public interface YggdrasilAccount extends Remote {

	/**
	 * 获取此账号的id。
	 * <p>
	 * 注意：捕获 {@link AlreadyDeletedException} 时，同时要注意处理ServerException里包装了 {@link AlreadyDeletedException} 的情况。
	 * 
	 * @return 账号的id
	 * @throws AlreadyDeletedException 如果账号已被删除
	 * @throws RemoteException 如果RMI调用期间出现异常
	 */
	String getId() throws AlreadyDeletedException, RemoteException;

	/**
	 * 获取此账户所拥有的角色。
	 * <p>
	 * 注意：捕获 {@link AlreadyDeletedException} 时，同时要注意处理ServerException里包装了 {@link AlreadyDeletedException} 的情况。
	 * 
	 * @return 一个无序的，不可修改的角色集合
	 * @throws AlreadyDeletedException 如果账号已被删除
	 * @throws RemoteException 如果RMI调用期间出现异常
	 */
	Set<GameProfile> getProfiles() throws AlreadyDeletedException, RemoteException;

	/**
	 * 吊销该账户的token。
	 * <p>
	 * 注意：捕获 {@link AlreadyDeletedException} 时，同时要注意处理ServerException里包装了 {@link AlreadyDeletedException} 的情况。
	 * 
	 * @throws AlreadyDeletedException 如果账号已被删除
	 * @throws RemoteException 如果RMI调用期间出现异常
	 */
	void revokeToken() throws AlreadyDeletedException, RemoteException;

	/**
	 * 为该账户重新分配一个token。
	 * <p>
	 * 该token可能随时会失效。<br>
	 * 注意：捕获 {@link AlreadyDeletedException} 时，同时要注意处理ServerException里包装了 {@link AlreadyDeletedException} 的情况。
	 * 
	 * @param clientToken 客户端token，不能为null
	 * @return 一个新的accessToken（访问token）
	 * @throws AlreadyDeletedException 如果账号已被删除
	 * @throws RemoteException 如果RMI调用期间出现异常
	 */
	String createToken(String clientToken) throws AlreadyDeletedException, RemoteException;

	/**
	 * 验证token是否有效。
	 * <p>
	 * 注意：捕获 {@link AlreadyDeletedException} 时，同时要注意处理ServerException里包装了 {@link AlreadyDeletedException} 的情况。
	 * 
	 * @param clientToken 客户端token
	 * @param accessToken 访问token
	 * @return 有效返回true，无效返回false
	 * @throws AlreadyDeletedException 如果账号已被删除
	 * @throws RemoteException 如果RMI调用期间出现异常
	 */
	boolean isTokenValid(String clientToken, String accessToken) throws AlreadyDeletedException, RemoteException;

	/**
	 * 更改改账户的密码。
	 * <p>
	 * password可以为null，在这种情况下，用户将不能登录。<br>
	 * 安全保证：密码将使用sha512+salt的方法存储。<br>
	 * 注意：捕获 {@link AlreadyDeletedException} 时，同时要注意处理ServerException里包装了 {@link AlreadyDeletedException} 的情况。
	 * 
	 * @param password 密码
	 * @throws AlreadyDeletedException 如果账号已被删除
	 * @throws RemoteException 如果RMI调用期间出现异常
	 */
	void setPassword(String password) throws AlreadyDeletedException, RemoteException;

	/**
	 * 验证密码是否有效。
	 * <p>
	 * 如果password为null，该方法将始终返回false。<br>
	 * 注意：捕获 {@link AlreadyDeletedException} 时，同时要注意处理ServerException里包装了 {@link AlreadyDeletedException} 的情况。
	 * 
	 * @param password 密码
	 * @return 密码有效则true，无效则false
	 * @throws AlreadyDeletedException 如果账号已被删除
	 * @throws RemoteException 如果RMI调用期间出现异常
	 */
	boolean isPasswordValid(String password) throws AlreadyDeletedException, RemoteException;

	/**
	 * 获取该账户是否被封禁。
	 * <p>
	 * 如果一个账户被封禁，该账户将无法登录，其角色将无法加入服务器。<br>
	 * 注意：捕获 {@link AlreadyDeletedException} 时，同时要注意处理ServerException里包装了 {@link AlreadyDeletedException} 的情况。
	 * 
	 * @return 如果账户被封禁返回true，否则false
	 * @throws AlreadyDeletedException 如果账号已被删除
	 * @throws RemoteException 如果RMI调用期间出现异常
	 */
	boolean isBanned() throws AlreadyDeletedException, RemoteException;

	/**
	 * 将该账户封禁/解封。
	 * <p>
	 * 注意：捕获 {@link AlreadyDeletedException} 时，同时要注意处理ServerException里包装了 {@link AlreadyDeletedException} 的情况。
	 * 
	 * @param banned true则封禁，false则解封
	 * @throws AlreadyDeletedException 如果账号已被删除
	 * @throws RemoteException 如果RMI调用期间出现异常
	 */
	void setBanned(boolean banned) throws AlreadyDeletedException, RemoteException;

	/**
	 * 为该账户添加一个角色。
	 * <p>
	 * 注意：捕获 {@link IDCollisionException} 和 {@link AlreadyDeletedException} 时，同时要注意处理ServerException里包装了
	 * {@link IDCollisionException} 和 {@link AlreadyDeletedException} 的情况。
	 * 
	 * @param uuid 角色uuid，不能为null，要求保证唯一
	 * @param name 角色名，须满足 {@link YggdrasilValidate#isValidId(String)}，要求保证唯一
	 * @return 创建的角色
	 * @throws AlreadyDeletedException 如果账号已被删除
	 * @throws IDCollisionException 若uuid或name发生冲突
	 * @throws RemoteException 如果RMI调用期间出现异常
	 */
	GameProfile createGameProfile(UUID uuid, String name) throws AlreadyDeletedException, IDCollisionException, RemoteException;

	/**
	 * 删除该账户。
	 * <p>
	 * 注意：捕获 {@link AlreadyDeletedException} 时，同时要注意处理ServerException里包装了 {@link AlreadyDeletedException} 的情况。
	 * 
	 * @throws AlreadyDeletedException 如果账号已被删除
	 * @throws RemoteException 如果RMI调用期间出现异常
	 */
	void delete() throws AlreadyDeletedException, RemoteException;

}
