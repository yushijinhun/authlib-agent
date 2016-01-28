package yushijinhun.authlibagent.service;

import yushijinhun.authlibagent.model.Account;
import yushijinhun.authlibagent.model.Token;

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
	Account loginWithToken(String accessToken, String clientToken) throws ForbiddenOperationException;

	/**
	 * 通过token登录。
	 * <p>
	 * 因为此方法不验证clientToken，安全性较低，所以不推荐使用该方法。
	 * 
	 * @param accessToken accessToken
	 * @return 账号
	 * @throws ForbiddenOperationException 如果token有误，或者该用户被禁止登录
	 */
	Account loginWithToken(String accessToken) throws ForbiddenOperationException;

	/**
	 * 创建一个token。
	 * <p>
	 * 创建新的token后，原来和该账号和该clientToken对应的token将失效。
	 * 
	 * @param account 账号
	 * @param clientToken clientToken，如果为null，则随机生成一个
	 * @return 新的token
	 */
	Token createToken(String account, String clientToken);

	/**
	 * 创建一个token。
	 * <p>
	 * 该方法同{@link #createToken(String, String)}，但clientToken为随机生成的。
	 * 
	 * @param account 账号
	 * @return token
	 */
	Token createToken(String account);

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
	 * 验证该token是否有效。
	 * 
	 * @param accessToken accessToken
	 * @param clientToken clientToken
	 * @return 如果有效则为true，无效则false
	 */
	boolean isTokenAvailable(String accessToken, String clientToken);

	/**
	 * 验证该token是否有效。
	 * 
	 * @param accessToken accessToken
	 * @return 如果有效则为true，无效则false
	 */
	boolean isTokenAvailable(String accessToken);

}
