package yushijinhun.authlibagent.dao;

import java.util.Map;
import java.util.Set;
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
	 *  // expire: $expire.token.time
	 *  "X"+accessToken->""
	 * 
	 * 当redis通知一个d)的过期事件时, 则移除a),b), c)中的映射.
	 */

	private static final Logger LOGGER = LogManager.getFormatterLogger();

	private static final String PREFIX_ACCESS_TOKEN = "A";
	private static final String PREFIX_CLIENT_TOKEN = "C";
	private static final String PREFIX_ACCOUNT = "O";
	private static final String PREFIX_EXPIRE = "X";
	private static final String KEY_CLIENT_TOKEN = "c";
	private static final String KEY_OWNER = "o";

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

	@Value("#{config['security.maxTokensPerAccounts']}")
	private int maxTokensPerAccounts;

	@Value("#{config['security.extraTokensToDelete']}")
	private int extraTokensToDelete;

	private MessageListener expiredListener;

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

	@Override
	public Token get(String accessToken) {
		// notify the accessToken to be expired
		valOps.get(keyExpire(accessToken));

		Map<String, String> values = hashOps.entries(keyAccessToken(accessToken));
		if (values == null || values.isEmpty()) {
			return null;
		}
		Token token = new Token();
		token.setAccessToken(accessToken);
		token.setClientToken(values.get(KEY_CLIENT_TOKEN));
		token.setOwner(values.get(KEY_OWNER));
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

		int accountTokens = setOps.size(accountKey).intValue();
		if (accountTokens > maxTokensPerAccounts) {
			// token limit reached

			// remove $extraTokensToDelete more tokens every time,
			int tokensToDelete = accountTokens - maxTokensPerAccounts + extraTokensToDelete;
			for (String expiredAccessToken : setOps.randomMembers(accountKey, tokensToDelete)) {
				delete(expiredAccessToken);
			}
		}

		// add token
		hashOps.putAll(keyAccessToken(accessToken), values);

		// link
		setOps.add(keyClientToken(clientToken), accessToken);
		setOps.add(accountKey, accessToken);
		valOps.set(expireKey, "");

		template.expire(expireKey, tokenExpireTime, TimeUnit.SECONDS);
	}

	@Override
	public void delete(String accessToken) {
		Map<String, String> values = hashOps.entries(keyAccessToken(accessToken));
		if (values == null || values.isEmpty()) {
			return;
		}

		// unlink
		setOps.remove(keyClientToken(values.get(KEY_CLIENT_TOKEN)), accessToken);
		setOps.remove(keyAccount(values.get(KEY_OWNER)), accessToken);
		template.delete(keyExpire(accessToken));

		// delete token
		template.delete(keyAccessToken(accessToken));
	}

	@Override
	public void deleteByAccount(String account) {
		String accountKey = keyAccount(account);
		Set<String> accessTokens = setOps.members(accountKey);
		if (accessTokens == null || accessTokens.isEmpty()) {
			return;
		}

		// unlink expire keys
		template.delete(accessTokens.stream().map(this::keyExpire).collect(toSet()));
		// unlink account -> accessTokens
		template.delete(accountKey);
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
