package yushijinhun.authlibagent.backend.service;

import java.io.UnsupportedEncodingException;
import java.rmi.RemoteException;
import java.security.interfaces.RSAPrivateKey;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import yushijinhun.authlibagent.api.web.ForbiddenOperationException;
import yushijinhun.authlibagent.api.web.SignatureKeyChangeCallback;
import yushijinhun.authlibagent.api.web.WebBackend;
import yushijinhun.authlibagent.api.web.response.AuthenticateResponse;
import yushijinhun.authlibagent.api.web.response.GameProfileResponse;
import yushijinhun.authlibagent.backend.api.AccountManager;
import yushijinhun.authlibagent.backend.api.AlreadyDeletedException;
import yushijinhun.authlibagent.backend.api.GameProfile;
import yushijinhun.authlibagent.backend.api.HostAccessManager;
import yushijinhun.authlibagent.backend.api.YggdrasilAccount;
import yushijinhun.authlibagent.commons.AccessPolicy;
import static yushijinhun.authlibagent.commons.UUIDUtils.*;

@Component("webBackend")
public class WebBackendImpl implements WebBackend {

	private static final String MSG_INVALID_USERNAME_OR_PASSWORD = "Invalid credentials. Invalid username or password.";
	private static final String MSG_INVALID_TOKEN = "Invalid token.";
	private static final String MSG_SELECTING_PROFILE_NOT_SUPPORTED = "Access token already has a profile assigned.";

	// TODO after mojang implements selecting profiles, change this to the official message.
	private static final String MSG_INVALID_PROFILE = "Invalid profile.";

	private static final String MSG_ACCOUNT_BANNED = "Account has been banned.";
	private static final String MSG_PROFILE_BANNED = "Game profile has been banned.";

	private static final Logger LOGGER = LogManager.getFormatterLogger();

	@Autowired
	private AccountManager accountManager;

	@Autowired
	private HostAccessManager hostAccessManager;

	@Autowired
	private KeyServerService keyServerService;

	@Value("#{config['feature.allowSelectingProfile']}")
	private boolean allowSelectingProfile;

	@Value("#{config['feature.includeProfilesInRefresh']}")
	private boolean includeProfilesInRefresh;

	private Set<SignatureKeyChangeCallback> keyListeners = Collections.newSetFromMap(new ConcurrentHashMap<>());
	private KeyChangeListener keyChangeListener;

	@Transactional
	@Override
	public AuthenticateResponse authenticate(String username, String password, UUID clientToken) throws ForbiddenOperationException, RemoteException {
		try {
			YggdrasilAccount account = accountManager.lookupAccount(username);
			if (account == null || !account.isPasswordValid(password)) {
				throw new ForbiddenOperationException(MSG_INVALID_USERNAME_OR_PASSWORD);
			}
			if (account.isBanned()) {
				throw new ForbiddenOperationException(MSG_ACCOUNT_BANNED);
			}
			UUID accessToken = account.createToken(clientToken);
			return createAuthenticateResponse(account, accessToken, true);
		} catch (AlreadyDeletedException e) {
			throw new ForbiddenOperationException(MSG_INVALID_USERNAME_OR_PASSWORD, e);
		}
	}

	@Transactional
	@Override
	public AuthenticateResponse refresh(UUID accessToken, UUID clientToken) throws ForbiddenOperationException, RemoteException {
		return selectProfile(accessToken, clientToken, null);
	}

	@Transactional
	@Override
	public AuthenticateResponse selectProfile(UUID accessToken, UUID clientToken, UUID profileUUID) throws ForbiddenOperationException, RemoteException {
		try {
			YggdrasilAccount account = accountManager.lookupAccount(accessToken);
			if (account == null || !account.isTokenValid(clientToken, accessToken)) {
				throw new ForbiddenOperationException(MSG_INVALID_TOKEN);
			}
			if (account.isBanned()) {
				throw new ForbiddenOperationException(MSG_ACCOUNT_BANNED);
			}
			UUID newAccessToken = account.createToken(clientToken);

			// select profile
			if (profileUUID != null) {
				if (!allowSelectingProfile) {
					throw new IllegalArgumentException(MSG_SELECTING_PROFILE_NOT_SUPPORTED);
				}

				GameProfile profile = accountManager.lookupGameProfile(profileUUID);
				if (profile == null || !profile.getOwner().equals(account)) {
					throw new ForbiddenOperationException(MSG_INVALID_PROFILE);
				} else {
					profile.setToDefault();
				}
			}

			return createAuthenticateResponse(account, newAccessToken, includeProfilesInRefresh);
		} catch (AlreadyDeletedException e) {
			throw new ForbiddenOperationException(MSG_INVALID_TOKEN, e);
		}
	}

	@Transactional
	@Override
	public boolean validate(UUID accessToken, UUID clientToken) throws RemoteException {
		try {
			YggdrasilAccount account = accountManager.lookupAccount(accessToken);
			return account != null && !account.isBanned() && account.isTokenValid(clientToken, accessToken);
		} catch (AlreadyDeletedException e) {
			LOGGER.debug("an AlreadyDeletedException has thrown during validate request", e);
			return false;
		}
	}

	@Transactional
	@Override
	public boolean validate(UUID accessToken) throws RemoteException {
		YggdrasilAccount account = accountManager.lookupAccount(accessToken);
		try {
			return account != null && !account.isBanned();
		} catch (AlreadyDeletedException e) {
			LOGGER.debug("an AlreadyDeletedException has thrown during validate request", e);
			return false;
		}
	}

	@Transactional
	@Override
	public void invalidate(UUID accessToken, UUID clientToken) throws ForbiddenOperationException, RemoteException {
		try {
			YggdrasilAccount account = accountManager.lookupAccount(accessToken);
			if (account == null || !account.isTokenValid(clientToken, accessToken)) {
				throw new ForbiddenOperationException(MSG_INVALID_TOKEN);
			}
			if (account.isBanned()) {
				throw new ForbiddenOperationException(MSG_ACCOUNT_BANNED);
			}
			account.revokeToken();
		} catch (AlreadyDeletedException e) {
			throw new ForbiddenOperationException(MSG_INVALID_TOKEN, e);
		}
	}

	@Transactional
	@Override
	public void signout(String username, String password) throws ForbiddenOperationException, RemoteException {
		try {
			YggdrasilAccount account = accountManager.lookupAccount(username);
			if (account == null || !account.isPasswordValid(password)) {
				throw new ForbiddenOperationException(MSG_INVALID_USERNAME_OR_PASSWORD);
			}
			if (account.isBanned()) {
				throw new ForbiddenOperationException(MSG_ACCOUNT_BANNED);
			}
			account.revokeToken();
		} catch (AlreadyDeletedException e) {
			throw new ForbiddenOperationException(MSG_INVALID_USERNAME_OR_PASSWORD, e);
		}
	}

	@Transactional
	@Override
	public void joinServer(UUID accessToken, UUID profileUUID, String serverid) throws ForbiddenOperationException, RemoteException {
		try {
			YggdrasilAccount account = accountManager.lookupAccount(accessToken);
			GameProfile profile = accountManager.lookupGameProfile(profileUUID);
			if (account == null || profile == null || !account.equals(profile.getOwner())) {
				throw new ForbiddenOperationException(MSG_INVALID_TOKEN);
			}
			if (account.isBanned()) {
				throw new ForbiddenOperationException(MSG_ACCOUNT_BANNED);
			}
			if (profile.isBanned()) {
				throw new ForbiddenOperationException(MSG_PROFILE_BANNED);
			}
			profile.setServerAuthenticationID(serverid);
		} catch (AlreadyDeletedException e) {
			throw new ForbiddenOperationException(MSG_INVALID_TOKEN, e);
		}
	}

	@Transactional
	@Override
	public GameProfileResponse hasJoinServer(String playername, String serverid) throws RemoteException {
		try {
			GameProfile profile = accountManager.lookupGameProfile(playername);
			if (profile == null || profile.getOwner().isBanned() || profile.isBanned() || !serverid.equals(profile.getServerAuthenticationID())) {
				return null;
			}
			return createGameProfileResponse(profile, true);
		} catch (AlreadyDeletedException e) {
			LOGGER.debug("an AlreadyDeletedException has thrown during hasJoinServer request", e);
			return null;
		}
	}

	@Transactional
	@Override
	public GameProfileResponse lookupProfile(UUID profileUUID) throws RemoteException {
		try {
			return createGameProfileResponse(accountManager.lookupGameProfile(profileUUID), true);
		} catch (AlreadyDeletedException e) {
			LOGGER.debug("an AlreadyDeletedException has thrown during lookupProfile request", e);
			return null;
		}
	}

	@Transactional
	@Override
	public GameProfileResponse lookupProfile(String name) throws RemoteException {
		try {
			return createGameProfileResponse(accountManager.lookupGameProfile(name), true);
		} catch (AlreadyDeletedException e) {
			LOGGER.debug("an AlreadyDeletedException has thrown during lookupProfile request", e);
			return null;
		}
	}

	@Transactional
	@Override
	public AccessPolicy getServerAccessPolicy(String host) throws RemoteException {
		AccessPolicy policy = hostAccessManager.getHostPolicy(host);
		if (policy == null) {
			policy = hostAccessManager.getDefaultPolicy();
		}
		return policy;
	}

	@Override
	public void addSignatureKeyListener(SignatureKeyChangeCallback listener) throws RemoteException {
		keyListeners.add(listener);
	}

	@Override
	public void removeSignatureKeyListener(SignatureKeyChangeCallback listener) throws RemoteException {
		keyListeners.remove(listener);
	}

	@Override
	public RSAPrivateKey getSignatureKey() throws RemoteException {
		return keyServerService.getKey();
	}

	@PostConstruct
	private void registerKeyListener() {
		keyChangeListener = key -> keyListeners.forEach(l -> {
			try {
				l.call(key);
			} catch (Throwable e) {
				LOGGER.warn("exception during posting key change listener to " + l, e);
			}
		});
		keyServerService.addKeyChangeListener(keyChangeListener);
	}

	@PreDestroy
	private void unregisterKeyListener() {
		keyServerService.removeKeyChangeListener(keyChangeListener);
	}

	private GameProfileResponse createGameProfileResponse(GameProfile profile, boolean withTexture) throws AlreadyDeletedException, RemoteException {
		if (profile == null) {
			return null;
		}
		return new GameProfileResponse(profile.getUUID(), profile.getName(), withTexture ? profile.getTexture() : null);
	}

	private GameProfileResponse[] createGameProfileResponses(Set<GameProfile> profiles, boolean withTexture) throws AlreadyDeletedException, RemoteException {
		GameProfileResponse[] responses = new GameProfileResponse[profiles.size()];
		int index = 0;
		for (GameProfile profile : profiles) {
			responses[index++] = createGameProfileResponse(profile, withTexture);
		}
		return responses;
	}

	private String getUserid(YggdrasilAccount account) throws RemoteException {
		try {
			return unsign(UUID.nameUUIDFromBytes(account.getId().getBytes("UTF-8")));
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException("utf-8 not supported", e);
		}
	}

	private Map<String, String> getUserProperties(YggdrasilAccount account) {
		// TODO: Add twich support
		return null;
	}

	private AuthenticateResponse createAuthenticateResponse(YggdrasilAccount account, UUID accessToken, boolean withProfiles) throws AlreadyDeletedException, RemoteException {
		GameProfileResponse selectedProfile = createGameProfileResponse(account.getSelectedProfile(), false);
		GameProfileResponse[] profiles = withProfiles ? createGameProfileResponses(account.getProfiles(), false) : null;
		String userid = getUserid(account);
		Map<String, String> properties = getUserProperties(account);
		return new AuthenticateResponse(accessToken, selectedProfile, profiles, userid, properties);
	}

}
