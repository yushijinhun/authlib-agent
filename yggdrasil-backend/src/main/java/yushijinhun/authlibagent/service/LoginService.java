package yushijinhun.authlibagent.service;

import java.util.UUID;
import yushijinhun.authlibagent.model.Account;
import yushijinhun.authlibagent.model.Token;
import yushijinhun.authlibagent.util.TokenAuthResult;

public interface LoginService {

	/**
	 * 通过用户名和密码登录。
	 * 
	 * @param username 用户名
	 * @param password 密码
	 * @return 账号
	 * @throws ForbiddenOperationException 如果用户名或密码有误，或者该用户被禁止登录
	 */
	Account loginWithPassword(String username, String password) throws ForbiddenOperationException;

	/**
	 * 通过token登录。
	 * 
	 * @param accessToken accessToken
	 * @param clientToken clientToken
	 * @return 账号
	 * @throws ForbiddenOperationException 如果token有误，或者该用户被禁止登录
	 */
	TokenAuthResult loginWithToken(String accessToken, String clientToken) throws ForbiddenOperationException;

	/**
	 * 通过token登录。
	 * <p>
	 * 因为此方法不验证clientToken，安全性较低，所以不推荐使用该方法。
	 * 
	 * @param accessToken accessToken
	 * @return 账号
	 * @throws ForbiddenOperationException 如果token有误，或者该用户被禁止登录
	 */
	TokenAuthResult loginWithToken(String accessToken) throws ForbiddenOperationException;

	/**
	 * 创建一个token, 一般在新创建token时调用该方法.
	 * 
	 * @param account 账号
	 * @param selectedProfile 选中的profile
	 * @param clientToken clientToken，如果为null，则随机生成一个
	 * @return 新的token
	 */
	Token createToken(String account, UUID selectedProfile, String clientToken);

	/**
	 * 创建一个token, 一般在重分配token时调用该方法.
	 * 
	 * @param account 账号
	 * @param selectedProfile 选中的profile
	 * @param clientToken clientToken，如果为null，则随机生成一个
	 * @param createTime token的createTime
	 * @return 新的token
	 */
	Token createToken(String account, UUID selectedProfile, String clientToken, long createTime);

	/**
	 * 吊销token。
	 * 
	 * @param accessToken accessToken
	 * @param clientToken clientToken
	 * @throws ForbiddenOperationException 如果所给的token无效
	 */
	void revokeToken(String accessToken, String clientToken) throws ForbiddenOperationException;

	/**
	 * 吊销token。
	 * <p>
	 * 因为此方法不验证clientToken，安全性较低，所以不推荐使用该方法。
	 * 
	 * @param accessToken accessToken
	 * @throws ForbiddenOperationException 如果所给的token无效
	 */
	void revokeToken(String accessToken) throws ForbiddenOperationException;

	/**
	 * 吊销所给账号的所有token。
	 * 
	 * @param account 账号
	 */
	void revokeAllTokens(String account);

	/**
	 * 验证该token是否有效, 当token处于暂时失效时, 该方法返回false.
	 * 
	 * @param accessToken accessToken
	 * @param clientToken clientToken
	 * @return 如果有效则为true，无效则false
	 */
	boolean isTokenAvailable(String accessToken, String clientToken);

	/**
	 * 验证该token是否有效, 当token处于暂时失效时, 该方法返回false.
	 * 
	 * @param accessToken accessToken
	 * @return 如果有效则为true，无效则false
	 */
	boolean isTokenAvailable(String accessToken);

}
