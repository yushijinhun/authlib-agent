package yushijinhun.authlibagent.service;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import yushijinhun.authlibagent.dao.ServerIdRepository;
import yushijinhun.authlibagent.dao.TokenRepository;
import yushijinhun.authlibagent.model.AccessPolicy;
import yushijinhun.authlibagent.model.AccessRule;
import yushijinhun.authlibagent.model.Account;
import yushijinhun.authlibagent.model.GameProfile;
import yushijinhun.authlibagent.model.Token;
import yushijinhun.authlibagent.web.yggdrasil.AuthenticateResponse;
import yushijinhun.authlibagent.web.yggdrasil.GameProfileResponse;
import static org.hibernate.criterion.Restrictions.eq;
import static yushijinhun.authlibagent.util.UUIDUtils.toUUID;
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

	@Value("#{config['feature.includeProfilesInRefresh']}")
	private boolean includeProfilesInRefresh;

	@Value("#{config['feature.autoSelectedUniqueProfile']}")
	private boolean autoSelectedUniqueProfile;

	@Value("#{config['feature.clearSelectedProfileInLogin']}")
	private boolean clearSelectedProfileInLogin;

	@Value("#{config['access.policy.default']}")
	private String defaultPolicy;

	@Transactional
	@Override
	public AuthenticateResponse authenticate(String username, String password, String clientToken) throws ForbiddenOperationException {
		Account account = loginService.loginWithPassword(username, password);

		Session session = sessionFactory.getCurrentSession();
		if (clearSelectedProfileInLogin) {
			account.setSelectedProfile(null);
		}
		if (autoSelectedUniqueProfile && account.getProfiles().size() == 1) {
			account.setSelectedProfile(account.getProfiles().iterator().next());
		}
		session.update(account);

		if (account.getSelectedProfile() != null && account.getSelectedProfile().isBanned()) {
			throw new ForbiddenOperationException(MSG_PROFILE_BANNED);
		}

		Token token = loginService.createToken(username, clientToken);
		return createAuthenticateResponse(account, token, true);
	}

	@Transactional
	@Override
	public AuthenticateResponse refresh(String accessToken, String clientToken) throws ForbiddenOperationException {
		return selectProfile(accessToken, clientToken, null);
	}

	@Transactional
	@Override
	public AuthenticateResponse selectProfile(String accessToken, String clientToken, UUID profileUUID) throws ForbiddenOperationException {
		Account account = loginService.loginWithToken(accessToken, clientToken);

		if (profileUUID != null) {
			// select profile
			if (!allowSelectingProfile) {
				throw new IllegalArgumentException(MSG_SELECTING_PROFILE_NOT_SUPPORTED);
			}

			Session session = sessionFactory.getCurrentSession();
			GameProfile profile = session.get(GameProfile.class, profileUUID.toString());
			if (profile == null || !profile.getOwner().equals(account)) {
				throw new ForbiddenOperationException(MSG_INVALID_PROFILE);
			}
			if (profile.isBanned()) {
				throw new ForbiddenOperationException(MSG_PROFILE_BANNED);
			}

			account.setSelectedProfile(profile);
			session.update(account);
		} else if (account.getSelectedProfile() != null && account.getSelectedProfile().isBanned()) {
			// check if current profile is banned
			throw new ForbiddenOperationException(MSG_PROFILE_BANNED);
		}

		tokenRepo.delete(accessToken);
		Token token = loginService.createToken(account.getId(), clientToken);
		return createAuthenticateResponse(account, token, includeProfilesInRefresh);
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
		loginService.loginWithPassword(username, password);
		loginService.revokeAllTokens(username);
	}

	@Transactional(readOnly = true)
	@Override
	public void joinServer(String accessToken, UUID profileUUID, String serverid) throws ForbiddenOperationException {
		Session session = sessionFactory.getCurrentSession();

		Account account = loginService.loginWithToken(accessToken);
		GameProfile profile = session.get(GameProfile.class, profileUUID.toString());
		if (profile == null || !account.equals(profile.getOwner())) {
			throw new ForbiddenOperationException(MSG_INVALID_PROFILE);
		}

		if (profile.isBanned()) {
			throw new ForbiddenOperationException(MSG_PROFILE_BANNED);
		}

		serveridRepo.createServerId(serverid, profileUUID);
	}

	@Transactional(readOnly = true)
	@Override
	public GameProfileResponse hasJoinServer(String playername, String serverid) {
		Session session = sessionFactory.getCurrentSession();

		UUID profileUUID = serveridRepo.getOwner(serverid);
		if (profileUUID != null) {
			GameProfile profile = session.get(GameProfile.class, profileUUID.toString());
			if (profile != null && playername.equals(profile.getName())) {
				serveridRepo.deleteServerId(serverid);
				return createGameProfileResponse(profile, true);
			}
		}

		return null;
	}

	@Transactional(readOnly = true)
	@Override
	public GameProfileResponse lookupProfile(UUID profileUUID) {
		Session session = sessionFactory.getCurrentSession();
		return createGameProfileResponse(session.get(GameProfile.class, profileUUID.toString()), true);
	}

	@Transactional(readOnly = true)
	@Override
	public GameProfileResponse lookupProfile(String name) {
		Session session = sessionFactory.getCurrentSession();
		@SuppressWarnings("unchecked")
		List<GameProfile> profiles = session.createCriteria(GameProfile.class).add(eq("name", name)).setCacheable(true).list();
		if (profiles.isEmpty()) {
			return null;
		} else {
			return createGameProfileResponse(profiles.get(0), true);
		}
	}

	@Transactional(readOnly = true)
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

	private GameProfileResponse createGameProfileResponse(GameProfile profile, boolean withTexture) {
		if (profile == null) {
			return null;
		}
		return new GameProfileResponse(toUUID(profile.getUuid()), profile.getName(), withTexture ? profile.getTexture() : null);
	}

	private GameProfileResponse[] createGameProfileResponses(Set<GameProfile> profiles, boolean withTexture) {
		GameProfileResponse[] responses = new GameProfileResponse[profiles.size()];
		int index = 0;
		for (GameProfile profile : profiles) {
			responses[index++] = createGameProfileResponse(profile, withTexture);
		}
		return responses;
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

	private AuthenticateResponse createAuthenticateResponse(Account account, Token token, boolean withProfiles) {
		GameProfileResponse selectedProfile = createGameProfileResponse(account.getSelectedProfile(), false);
		GameProfileResponse[] profiles = withProfiles ? createGameProfileResponses(account.getProfiles(), false) : null;
		String userid = getUserid(account);
		Map<String, String> properties = getUserProperties(account);
		return new AuthenticateResponse(token.getClientToken(), token.getAccessToken(), selectedProfile, profiles, userid, properties);
	}

}
