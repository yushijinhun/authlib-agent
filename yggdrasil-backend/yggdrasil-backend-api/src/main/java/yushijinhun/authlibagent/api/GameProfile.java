package yushijinhun.authlibagent.api;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.UUID;

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
	 * 删除该角色。
	 * 
	 * @throws AlreadyDeletedException 如果角色已被删除
	 * @throws RemoteException 如果RMI调用期间出现异常
	 */
	void delete() throws AlreadyDeletedException, RemoteException;

}
