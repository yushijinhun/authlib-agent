package yushijinhun.authlibagent.backend.api;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.UUID;
import yushijinhun.authlibagent.commons.PlayerTexture;

/**
 * 游戏角色
 * <p>
 * 每个角色的name和uuid必须唯一。name可变，但uuid不可变。应该使用uuid作为角色的标识符。
 * 
 * @author yushijinhun
 */
public interface GameProfile extends Remote {

	/**
	 * 获取该角色的uuid。uuid为角色的唯一标识符。
	 * <p>
	 * 此uuid与角色在游戏中的uuid相同。
	 * 
	 * @return 角色的uuid
	 * @throws RemoteException 如果RMI调用期间出现异常
	 */
	UUID getUUID() throws RemoteException;

	/**
	 * 获取角色名。
	 * <p>
	 * 该角色名将在游戏中显示。
	 * 
	 * @return 角色名
	 * @throws AlreadyDeletedException 如果角色已被删除
	 * @throws RemoteException 如果RMI调用期间出现异常
	 */
	String getName() throws AlreadyDeletedException, RemoteException;

	/**
	 * 设置角色名。
	 * <p>
	 * 该方法相当于正版换名机制，必须确保角色名唯一。
	 * 
	 * @param name 角色名，必须满足 {@link YggdrasilValidate#isValidId(String)}，必须保证唯一
	 * @throws AlreadyDeletedException 如果角色已被删除
	 * @throws IDCollisionException 若发生角色名冲突
	 * @throws RemoteException 如果RMI调用期间出现异常
	 */
	void setName(String name) throws AlreadyDeletedException, IDCollisionException, RemoteException;

	/**
	 * 获取该角色所属账号。
	 * 
	 * @return 该角色所属账号
	 * @throws AlreadyDeletedException 如果角色已被删除
	 * @throws RemoteException 如果RMI调用期间出现异常
	 */
	YggdrasilAccount getOwner() throws AlreadyDeletedException, RemoteException;

	/**
	 * 获取该角色是否被封禁。
	 * <p>
	 * 如果一个角色被封禁，将无法加入服务器。<br>
	 * 注意：这里的封禁不代表在服务器上被ban，而是对其验证请求不予处理。
	 * 
	 * @return 如果角色被封禁返回true，否则false
	 * @throws AlreadyDeletedException 如果角色已被删除
	 * @throws RemoteException 如果RMI调用期间出现异常
	 */
	boolean isBanned() throws AlreadyDeletedException, RemoteException;

	/**
	 * 将该角色封禁/解封。
	 * 
	 * @param banned true则封禁，false则解封
	 * @throws AlreadyDeletedException 如果角色已被删除
	 * @throws RemoteException 如果RMI调用期间出现异常
	 */
	void setBanned(boolean banned) throws AlreadyDeletedException, RemoteException;

	/**
	 * 获取角色的皮肤和披风，没有则返回一个skin和cape都为null的PlayerTexture。
	 * 
	 * @return 角色的皮肤和披风
	 * @throws AlreadyDeletedException 如果角色已被删除
	 * @throws RemoteException 如果RMI调用期间出现异常
	 */
	PlayerTexture getTexture() throws AlreadyDeletedException, RemoteException;

	/**
	 * 设置角色的皮肤和披风。
	 * 
	 * @param texture 角色的皮肤和披风，不能为null
	 * @throws AlreadyDeletedException 如果角色已被删除
	 * @throws RemoteException 如果RMI调用期间出现异常
	 */
	void setTexture(PlayerTexture texture) throws AlreadyDeletedException, RemoteException;

	/**
	 * 将该角色设置为其所属账户默认。
	 * 
	 * @throws AlreadyDeletedException 如果角色已被删除
	 * @throws RemoteException 如果RMI调用期间出现异常
	 */
	void setToDefault() throws AlreadyDeletedException, RemoteException;

	/**
	 * 获取该角色用于进行"正版"验证而生成的临时id。
	 * <p>
	 * "正版"验证进服流程：<br>
	 * 0. 客户端请求加入服务端<br>
	 * 1. 客户端要求服务端生成一个临时id<br>
	 * 2. 客户端将该临时id和自己的accessToken发送给验证服务器<br>
	 * 3. 验证服务器确认该accessToken有效后将临时id存储起来<br>
	 * 4. 服务端将这个临时id和玩家名发送给验证服务器，要求验证服务器对比该临时id和存储的临时id是否一致<br>
	 * 5. 若两个临时id一致，则进服成功<br>
	 * 
	 * @return 该角色用于进行"正版"验证而生成的临时id
	 * @throws AlreadyDeletedException 如果角色已被删除
	 * @throws RemoteException 如果RMI调用期间出现异常
	 */
	String getServerAuthenticationID() throws AlreadyDeletedException, RemoteException;

	/**
	 * 设置该角色用于进行"正版"验证而生成的临时id。
	 * 
	 * @param authenticationId 该角色用于进行"正版"验证而生成的临时id
	 * @throws AlreadyDeletedException 如果角色已被删除
	 * @throws RemoteException 如果RMI调用期间出现异常
	 */
	void setServerAuthenticationID(String authenticationId) throws AlreadyDeletedException, RemoteException;

	/**
	 * 删除该角色。
	 * 
	 * @throws AlreadyDeletedException 如果角色已被删除
	 * @throws RemoteException 如果RMI调用期间出现异常
	 */
	void delete() throws AlreadyDeletedException, RemoteException;

}
