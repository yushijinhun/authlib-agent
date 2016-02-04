package yushijinhun.authlibagent.dao;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;
import yushijinhun.authlibagent.model.Token;
import java.util.HashMap;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import static java.util.stream.Collectors.toSet;
import static yushijinhun.authlibagent.util.HexUtils.bytesToHex;

@Component
public class TokenRepositoryImpl implements TokenRepository {

	/*
	 * Redis结构:
	 * 
	 * a) 对accessToken->Token的存储:
	 * "A"+accessToken -> <hash>{
	 * 	"c": clientToken
	 * 	"o": owner
	 *     "p": selectedProfile
	 *  	"t": lastRefreshTime
	 *  	"a": createTime // 即通过authenticate请求创建时的时间, 不会因为refresh重置
	 * }
	 * 
	 * b) 对clientToken->Tokens的存储:
	 * "C"+clientToken -> <set>[
	 * 	accessToken...
	 * ]
	 * 
	 * c) 对account->Tokens的存储:
	 * "O"+account -> <set>[
	 * 	accessToken...
	 * ]
	 * 
	 * d) 用于对accessToken进行过期处理的存储:
	 *  // expire: $tokenExpireTime
	 *  "X"+accessToken->""
	 * 
	 * 当redis通知一个d)的过期事件时, 则移除a),b), c)中的映射.
	 * 同时, 为了防止redis删除了d)但没有通知客户端, 每经过$tokenExpireScanCycle秒,
	 * 将自动遍历所有token, 测试过期.
	 */

	/* 
	* Token 生命周期:
	* 
	* |----1. 有效----|----2. 暂时失效----|3. 无效
	* |------------------------------------------------------>Time
	* 
	* 当一个token被创建时, 将处于有效状态.
	* 经过$tokenPreExpireTime秒后, token状态转为暂时失效,
	* 此时进行validate请求返回false, 但仍可以refresh, 经refresh之后分配新的token将处于有效状态.
	* 如果不进行refresh, 该token将在创建后$tokenExpireTime秒后失效.
	* 当一个token自通过authenticate创建后, 经过$tokenMaxLivingTime之秒后(refresh不会重置该值), 也将自动失效.
	*/

	private static final Logger LOGGER = LogManager.getFormatterLogger();

	private static final String PREFIX_ACCESS_TOKEN = "A";
	private static final String PREFIX_CLIENT_TOKEN = "C";
	private static final String PREFIX_ACCOUNT = "O";
	private static final String PREFIX_EXPIRE = "X";
	private static final String KEY_CLIENT_TOKEN = "c";
	private static final String KEY_OWNER = "o";
	private static final String KEY_SELECTED_PROFILE = "p";
	private static final String KEY_LAST_REFRESH_TIME = "t";
	private static final String KEY_CREATE_TIME = "a";

	@Autowired
	private RedisMessageListenerContainer container;

	@Autowired
	private RedisTemplate<String, String> template;

	@Resource(name = "redisTemplate")
	private HashOperations<String, String, String> hashOps;

	@Resource(name = "redisTemplate")
	private SetOperations<String, String> setOps;

	@Resource(name = "redisTemplate")
	private ValueOperations<String, String> valOps;

	// Unit: second
	@Value("#{config['expire.token.time']}")
	private long tokenExpireTime;

	// Unit: second
	@Value("#{config['expire.token.maxLiving']}")
	private long tokenMaxLivingTime;

	// Unit: second
	@Value("#{config['expire.token.scanCycle']}")
	private long tokenExpireScanCycle;

	@Value("#{config['security.maxTokensPerAccounts']}")
	private int maxTokensPerAccounts;

	@Value("#{config['security.extraTokensToDelete']}")
	private int extraTokensToDelete;

	private MessageListener expiredListener;

	private Thread expireScanThread;

	@PostConstruct
	private void registerExpiredEventListener() {
		expiredListener = new MessageListener() {

			@Override
			public void onMessage(Message message, byte[] pattern) {
				byte[] body = message.getBody();
				if (body == null) {
					return;
				}

				String key;
				try {
					key = new String(message.getBody(), "UTF-8");
				} catch (UnsupportedEncodingException e) {
					LOGGER.debug(() -> "failed to decode message body: " + bytesToHex(body), e);
					return;
				}

				if (!key.startsWith(PREFIX_EXPIRE)) {
					return;
				}

				String accessToken = key.substring(PREFIX_EXPIRE.length());
				template.delete(keyAccessToken(accessToken));
				Map<String, String> values = hashOps.entries(keyAccessToken(accessToken));
				if (values != null && !values.isEmpty()) {
					setOps.remove(keyClientToken(values.get(KEY_CLIENT_TOKEN)), accessToken);
					setOps.remove(keyAccount(values.get(KEY_OWNER)), accessToken);
				}
			}
		};
		container.addMessageListener(expiredListener, new PatternTopic("__keyevent@*__:expired"));
	}

	@PreDestroy
	private void unregisterExpiredEventListener() {
		container.removeMessageListener(expiredListener);
	}

	@PostConstruct
	private void startExpireScanThread() {
		expireScanThread = new Thread(() -> {
			while (true) {
				try {
					Thread.sleep(tokenExpireScanCycle * 1000);
				} catch (InterruptedException e) {
					return;
				}

				template.keys(PREFIX_ACCESS_TOKEN + "*").forEach(this::testExpire);
			}
		});
		expireScanThread.setDaemon(true);
		expireScanThread.start();
	}

	@PreDestroy
	private void stopExpireScanThread() {
		expireScanThread.interrupt();
	}

	@Override
	public Token get(String accessToken) {
		// notify the accessToken to be expired
		valOps.get(keyExpire(accessToken));

		Map<String, String> values = hashOps.entries(keyAccessToken(accessToken));
		if (values == null || values.isEmpty() || testExpire(accessToken, values)) {
			return null;
		}

		Token token = new Token();
		token.setAccessToken(accessToken);
		token.setClientToken(values.get(KEY_CLIENT_TOKEN));
		token.setOwner(values.get(KEY_OWNER));
		token.setLastRefreshTime(Long.parseLong(values.get(KEY_LAST_REFRESH_TIME)));
		token.setCreateTime(Long.parseLong(values.get(KEY_CREATE_TIME)));
		String selectedProfile = values.get(KEY_SELECTED_PROFILE);
		token.setSelectedProfile(selectedProfile.isEmpty() ? null : UUID.fromString(selectedProfile));
		return token;
	}

	@Override
	public Set<Token> getByClientToken(String clientToken) {
		return getTokens(setOps.members(keyClientToken(clientToken)));
	}

	@Override
	public Set<Token> getByAccount(String account) {
		return getTokens(setOps.members(keyAccount(account)));
	}

	@Override
	public void put(Token token) {
		String accessToken = token.getAccessToken();
		String clientToken = token.getClientToken();
		String owner = token.getOwner();

		String expireKey = keyExpire(accessToken);
		String accountKey = keyAccount(owner);

		Map<String, String> values = new HashMap<>();
		values.put(KEY_CLIENT_TOKEN, clientToken);
		values.put(KEY_OWNER, owner);
		values.put(KEY_LAST_REFRESH_TIME, String.valueOf(token.getLastRefreshTime()));
		values.put(KEY_CREATE_TIME, String.valueOf(token.getCreateTime()));
		UUID selectedProfile = token.getSelectedProfile();
		values.put(KEY_SELECTED_PROFILE, selectedProfile == null ? "" : selectedProfile.toString());

		int accountTokens = setOps.size(accountKey).intValue();
		if (accountTokens > maxTokensPerAccounts) {
			// token limit reached

			// remove $extraTokensToDelete more tokens every time,
			int tokensToDelete = accountTokens - maxTokensPerAccounts + extraTokensToDelete;
			for (int i = 0; i < tokensToDelete; i++) {
				String expiredAccessToken = setOps.pop(accountKey);
				if (expiredAccessToken != null) {
					String expiredClientToken = hashOps.get(keyAccessToken(expiredAccessToken), KEY_CLIENT_TOKEN);
					if (expiredClientToken != null) {
						setOps.remove(keyClientToken(expiredClientToken), expiredAccessToken);
					}
					template.delete(keyExpire(expiredAccessToken));
					template.delete(keyAccessToken(expiredAccessToken));
				}
			}
		}

		valOps.set(expireKey, "");
		template.expire(expireKey, tokenExpireTime, TimeUnit.SECONDS);
		hashOps.putAll(keyAccessToken(accessToken), values);
		setOps.add(keyClientToken(clientToken), accessToken);
		setOps.add(accountKey, accessToken);
	}

	@Override
	public void delete(String accessToken) {
		Map<String, String> values = hashOps.entries(keyAccessToken(accessToken));
		if (values == null || values.isEmpty()) {
			return;
		}

		setOps.remove(keyAccount(values.get(KEY_OWNER)), accessToken);
		setOps.remove(keyClientToken(values.get(KEY_CLIENT_TOKEN)), accessToken);
		template.delete(keyExpire(accessToken));
		template.delete(keyAccessToken(accessToken));
	}

	@Override
	public void deleteByAccount(String account) {
		String accountKey = keyAccount(account);
		Set<String> accessTokens = setOps.members(accountKey);
		if (accessTokens == null || accessTokens.isEmpty()) {
			return;
		}

		// unlink account -> accessTokens
		template.delete(accountKey);
		// unlink expire keys
		template.delete(accessTokens.stream().map(this::keyExpire).collect(toSet()));
		// unlink clientToken -> accessTokens
		for (String accessToken : accessTokens) {
			String clientToken = hashOps.get(keyAccessToken(accessToken), KEY_CLIENT_TOKEN);
			if (clientToken != null) {
				setOps.remove(keyClientToken(clientToken), accessToken);
			}
		}
		// delete tokens
		template.delete(accessTokens);
	}

	private Set<Token> getTokens(Set<String> accessTokens) {
		if (accessTokens == null || accessTokens.isEmpty()) {
			return Collections.emptySet();
		}
		return accessTokens.stream().map(this::get).collect(toSet());
	}

	private boolean testExpire(String accessToken) {
		Map<String, String> values = hashOps.entries(keyAccessToken(accessToken));
		if (values == null || values.isEmpty()) {
			return false;
		}
		return testExpire(accessToken, values);
	}

	private boolean testExpire(String accessToken, Map<String, String> values) {
		long createTime = Long.parseLong(values.get(KEY_CREATE_TIME));
		long lastRefreshTime = Long.parseLong(values.get(KEY_LAST_REFRESH_TIME));
		long now = System.currentTimeMillis();
		if (createTime + tokenMaxLivingTime * 1000 < now || lastRefreshTime + tokenExpireTime * 1000 < now) {
			// reached max living time
			delete(accessToken);
			return true;
		}
		return false;
	}

	private String keyAccessToken(String accessToken) {
		return PREFIX_ACCESS_TOKEN + accessToken;
	}

	private String keyClientToken(String clientToken) {
		return PREFIX_CLIENT_TOKEN + clientToken;
	}

	private String keyAccount(String account) {
		return PREFIX_ACCOUNT + account;
	}

	private String keyExpire(String accessToken) {
		return PREFIX_EXPIRE + accessToken;
	}

}
