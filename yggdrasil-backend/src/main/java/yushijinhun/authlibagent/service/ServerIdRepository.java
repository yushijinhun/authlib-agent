package yushijinhun.authlibagent.service;

import java.util.UUID;

public interface ServerIdRepository {

	/**
	 * 创建/覆盖一个serverid.
	 * 
	 * @param serverid serverid
	 * @param profile 属主
	 */
	void createServerId(String serverid, UUID profile);

	/**
	 * 删除serverid.
	 * 
	 * @param serverid serverid
	 */
	void deleteServerId(String serverid);

	/**
	 * 获得给定serverid的属主.
	 * 
	 * @param serverid serverid
	 * @return serverid的属主, 没有则为null
	 */
	UUID getOwner(String serverid);

}
