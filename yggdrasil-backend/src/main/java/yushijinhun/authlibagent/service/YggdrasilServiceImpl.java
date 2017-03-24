package yushijinhun.authlibagent.service;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import yushijinhun.authlibagent.dao.ServerIdRepository;
import yushijinhun.authlibagent.dao.TokenRepository;
import yushijinhun.authlibagent.model.AccessPolicy;
import yushijinhun.authlibagent.model.AccessRule;
import yushijinhun.authlibagent.model.Account;
import yushijinhun.authlibagent.model.GameProfile;
import yushijinhun.authlibagent.model.Token;
import yushijinhun.authlibagent.util.TokenAuthResult;
import yushijinhun.authlibagent.web.yggdrasil.AuthenticateResponse;
import static org.hibernate.criterion.Restrictions.eq;
import static yushijinhun.authlibagent.util.UUIDUtils.unsign;

@Component
public class YggdrasilServiceImpl implements YggdrasilService {

	private static final String MSG_INVALID_PROFILE = "Invalid profile.";
	private static final String MSG_PROFILE_BANNED = "Game profile has been banned.";
	private static final String MSG_SELECTING_PROFILE_NOT_SUPPORTED = "Access token already has a profile assigned.";

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	private LoginService loginService;

	@Autowired
	private ServerIdRepository serveridRepo;

	@Autowired
	private TokenRepository tokenRepo;

	@Value("#{config['feature.allowSelectingProfile']}")
	private boolean allowSelectingProfile;

	@Value("#{config['feature.autoSelectedUniqueProfile']}")
	private boolean autoSelectedUniqueProfile;

	@Value("#{config['access.policy.default']}")
	private String defaultPolicy;

	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	@Override
	public AuthenticateResponse authenticate(String username, String password, String clientToken) throws ForbiddenOperationException {
		Account account = loginService.loginWithPassword(username, password, false);
		UUID selectedProfileUUID = null;
		GameProfile selectedProfile = null;

		if (autoSelectedUniqueProfile && account.getProfiles().size() == 1) { // select the unique profile
			selectedProfile = account.getProfiles().iterator().next();
			if (selectedProfile.isBanned()) {
				throw new ForbiddenOperationException(MSG_PROFILE_BANNED);
			}
			selectedProfileUUID = UUID.fromString(selectedProfile.getUuid());
		}

		Token token = loginService.createToken(username, selectedProfileUUID, clientToken);
		return createAuthenticateResponse(account, token, selectedProfile);
	}

	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	@Override
	public AuthenticateResponse refresh(String accessToken, String clientToken) throws ForbiddenOperationException {
		return selectProfile(accessToken, clientToken, null);
	}

	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	@Override
	public AuthenticateResponse selectProfile(String accessToken, String clientToken, UUID profileUUID) throws ForbiddenOperationException {
		TokenAuthResult result = loginService.loginWithToken(accessToken, clientToken);
		Account account = result.getAccount();

		UUID selectedProfileUUID = result.getToken().getSelectedProfile();

		if (profileUUID != null) {
			// select profile
			if (!allowSelectingProfile) {
				throw new IllegalArgumentException(MSG_SELECTING_PROFILE_NOT_SUPPORTED);
			}
			selectedProfileUUID = profileUUID;
		}

		GameProfile selectedProfile = null;
		if (selectedProfileUUID != null) {
			// check if the selected profile is banned
			Session session = sessionFactory.getCurrentSession();
			selectedProfile = session.get(GameProfile.class, selectedProfileUUID.toString());
			if (selectedProfile == null || !selectedProfile.getOwner().equals(account)) {
				throw new ForbiddenOperationException(MSG_INVALID_PROFILE);
			}
			if (selectedProfile.isBanned()) {
				throw new ForbiddenOperationException(MSG_PROFILE_BANNED);
			}
		}

		tokenRepo.delete(accessToken);

		Token token = loginService.createToken(account.getId(), selectedProfileUUID, clientToken, result.getToken().getCreateTime());
		return createAuthenticateResponse(account, token, selectedProfile);
	}

	@Override
	public boolean validate(String accessToken, String clientToken) {
		return loginService.isTokenAvailable(accessToken, clientToken);
	}

	@Override
	public boolean validate(String accessToken) {
		return loginService.isTokenAvailable(accessToken);
	}

	@Override
	public void invalidate(String accessToken, String clientToken) throws ForbiddenOperationException {
		loginService.revokeToken(accessToken, clientToken);
	}

	@Override
	public void signout(String username, String password) throws ForbiddenOperationException {
		loginService.loginWithPassword(username, password, false);
		loginService.revokeAllTokens(username);
	}

	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	@Override
	public void joinServer(String accessToken, UUID profileUUID, String serverid) throws ForbiddenOperationException {
		Session session = sessionFactory.getCurrentSession();

		Account account = loginService.loginWithToken(accessToken).getAccount();
		GameProfile profile = session.get(GameProfile.class, profileUUID.toString());
		if (profile == null || !account.equals(profile.getOwner())) {
			throw new ForbiddenOperationException(MSG_INVALID_PROFILE);
		}

		if (profile.isBanned()) {
			throw new ForbiddenOperationException(MSG_PROFILE_BANNED);
		}

		serveridRepo.createServerId(serverid, profileUUID);
	}

	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	@Override
	public GameProfile hasJoinServer(String playername, String serverid) {
		Session session = sessionFactory.getCurrentSession();

		UUID profileUUID = serveridRepo.getOwner(serverid);
		if (profileUUID != null) {
			GameProfile profile = session.get(GameProfile.class, profileUUID.toString());
			if (profile != null && playername.equals(profile.getName())) {
				serveridRepo.deleteServerId(serverid);
				return profile;
			}
		}

		return null;
	}

	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	@Override
	public GameProfile lookupProfile(UUID profileUUID) {
		Session session = sessionFactory.getCurrentSession();
		return session.get(GameProfile.class, profileUUID.toString());
	}

	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	@Override
	public GameProfile lookupProfile(String name) {
		Session session = sessionFactory.getCurrentSession();
		@SuppressWarnings("unchecked")
		List<GameProfile> profiles = session.createCriteria(GameProfile.class).add(eq("name", name)).list();
		if (profiles.isEmpty()) {
			return null;
		} else {
			return profiles.get(0);
		}
	}

	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	@Override
	public AccessPolicy getServerAccessPolicy(String host) {
		Session session = sessionFactory.getCurrentSession();
		AccessRule rule = session.get(AccessRule.class, host);
		if (rule == null) {
			rule = session.get(AccessRule.class, AccessRule.DEFAULT_RULE_KEY);
		}

		if (rule == null) {
			return AccessPolicy.valueOf(defaultPolicy);
		} else {
			return rule.getPolicy();
		}
	}

	private String getUserid(Account account) {
		try {
			return unsign(UUID.nameUUIDFromBytes(account.getId().getBytes("UTF-8")));
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException("utf-8 not supported", e);
		}
	}

	private Map<String, String> getUserProperties(Account account) {
		Map<String, String> properties = new HashMap<>();

		// twitch
		if (account.getTwitchToken() != null) {
			properties.put("twitch_access_token", account.getTwitchToken());
		}

		return properties;
	}

	private AuthenticateResponse createAuthenticateResponse(Account account, Token token, GameProfile selectedProfileObj) {
		String userid = getUserid(account);
		Map<String, String> properties = getUserProperties(account);
		return new AuthenticateResponse(token.getClientToken(), token.getAccessToken(), selectedProfileObj, account.getProfiles(), userid, properties);
	}

}
