package yushijinhun.authlibagent.backend.service;

import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.security.interfaces.RSAPrivateKey;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import yushijinhun.authlibagent.api.web.ForbiddenOperationException;
import yushijinhun.authlibagent.api.web.SignatureKeyChangeCallback;
import yushijinhun.authlibagent.api.web.WebBackend;
import yushijinhun.authlibagent.api.web.response.AuthenticateResponse;
import yushijinhun.authlibagent.api.web.response.GameProfileResponse;
import yushijinhun.authlibagent.api.web.response.RefreshResponse;
import yushijinhun.authlibagent.backend.api.AccountManager;
import yushijinhun.authlibagent.backend.api.AlreadyDeletedException;
import yushijinhun.authlibagent.backend.api.GameProfile;
import yushijinhun.authlibagent.backend.api.HostAccessManager;
import yushijinhun.authlibagent.backend.api.YggdrasilAccount;
import yushijinhun.authlibagent.commons.AccessPolicy;

@Component("web_backend")
public class WebBackendImpl implements WebBackend {

	private static final String MSG_INVALID_USERNAME_OR_PASSWORD = "Invalid credentials. Invalid username or password.";
	private static final String MSG_INVALID_TOKEN = "Invalid token.";

	// TODO after mojang implements selecting profiles, change this to the official message.
	private static final String MSG_INVALID_PROFILE = "Invalid profile.";

	private static final Logger LOGGER = LogManager.getFormatterLogger();

	@Qualifier("account_manager")
	private AccountManager accountManager;

	@Qualifier("host_access_manager")
	private HostAccessManager hostAccessManager;

	@Qualifier("key_server_service")
	private KeyServerService keyServerService;

	private Set<SignatureKeyChangeCallback> keyListeners = Collections.newSetFromMap(new ConcurrentHashMap<>());
	private KeyChangeListener keyChangeListener;

	@Value("#{config['rmi.web_backend']}")
	private String rmiUri;

	@Transactional
	@Override
	public AuthenticateResponse authenticate(String username, String password, UUID clientToken) throws ForbiddenOperationException, RemoteException {
		try {
			YggdrasilAccount account = accountManager.lookupAccount(username);
			if (account == null || !account.isPasswordValid(password)) {
				throw new ForbiddenOperationException(MSG_INVALID_USERNAME_OR_PASSWORD);
			}
			UUID accessToken = account.createToken(clientToken);
			GameProfileResponse selectedProfile = createGameProfileResponse(account.getSelectedProfile(), false);
			GameProfileResponse[] profiles = createGameProfileResponses(account.getProfiles(), false);
			return new AuthenticateResponse(accessToken, selectedProfile, profiles);
		} catch (AlreadyDeletedException e) {
			throw new ForbiddenOperationException(MSG_INVALID_USERNAME_OR_PASSWORD, e);
		}
	}

	@Transactional
	@Override
	public RefreshResponse refresh(UUID accessToken, UUID clientToken) throws ForbiddenOperationException, RemoteException {
		return selectProfile(accessToken, clientToken, null);
	}

	@Transactional
	@Override
	public RefreshResponse selectProfile(UUID accessToken, UUID clientToken, UUID profileUUID) throws ForbiddenOperationException, RemoteException {
		try {
			YggdrasilAccount account = accountManager.lookupAccount(accessToken);
			if (account == null || !account.isTokenValid(clientToken, accessToken)) {
				throw new ForbiddenOperationException(MSG_INVALID_TOKEN);
			}
			UUID newAccessToken = account.createToken(clientToken);
			if (profileUUID != null) {
				GameProfile profile = accountManager.lookupGameProfile(profileUUID);
				if (profile == null || !profile.getOwner().equals(account)) {
					throw new ForbiddenOperationException(MSG_INVALID_PROFILE);
				} else {
					profile.setToDefault();
				}
			}
			GameProfileResponse selectedProfile = createGameProfileResponse(account.getSelectedProfile(), false);
			return new RefreshResponse(newAccessToken, selectedProfile);
		} catch (AlreadyDeletedException e) {
			throw new ForbiddenOperationException(MSG_INVALID_TOKEN, e);
		}
	}

	@Transactional
	@Override
	public boolean validate(UUID accessToken, UUID clientToken) throws RemoteException {
		try {
			YggdrasilAccount account = accountManager.lookupAccount(accessToken);
			return account != null && account.isTokenValid(clientToken, accessToken);
		} catch (AlreadyDeletedException e) {
			LOGGER.info("an AlreadyDeletedException has thrown during validate request", e);
			return false;
		}
	}

	@Transactional
	@Override
	public boolean validate(UUID accessToken) throws RemoteException {
		YggdrasilAccount account = accountManager.lookupAccount(accessToken);
		return account != null;
	}

	@Transactional
	@Override
	public void invalidate(UUID accessToken, UUID clientToken) throws ForbiddenOperationException, RemoteException {
		try {
			YggdrasilAccount account = accountManager.lookupAccount(accessToken);
			if (account == null || !account.isTokenValid(clientToken, accessToken)) {
				throw new ForbiddenOperationException(MSG_INVALID_TOKEN);
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
			profile.setServerAuthenticationID(serverid);
		} catch (AlreadyDeletedException e) {
			throw new ForbiddenOperationException(MSG_INVALID_TOKEN, e);
		}
	}

	@Transactional
	@Override
	public boolean hasJoinServer(String playername, String serverid) throws RemoteException {
		try {
			GameProfile profile = accountManager.lookupGameProfile(playername);
			if (profile == null || !serverid.equals(profile.getServerAuthenticationID())) {
				return false;
			}
			return true;
		} catch (AlreadyDeletedException e) {
			LOGGER.info("an AlreadyDeletedException has thrown during hasJoinServer request", e);
			return false;
		}
	}

	@Transactional
	@Override
	public GameProfileResponse lookupProfile(UUID profileUUID) throws RemoteException {
		try {
			return createGameProfileResponse(accountManager.lookupGameProfile(profileUUID), true);
		} catch (AlreadyDeletedException e) {
			LOGGER.info("an AlreadyDeletedException has thrown during lookupProfile request", e);
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

	@PostConstruct
	private void rmiBind() throws MalformedURLException, RemoteException, AlreadyBoundException {
		LOGGER.info("web backend bind: %s", rmiUri);
		Naming.bind(rmiUri, this);
	}

	@PreDestroy
	private void rmiUnbind() throws RemoteException, MalformedURLException, NotBoundException {
		LOGGER.info("web backend unbind: %s", rmiUri);
		Naming.unbind(rmiUri);
	}

	private GameProfileResponse createGameProfileResponse(GameProfile profile, boolean withTexture) throws AlreadyDeletedException, RemoteException {
		if (profile == null) {
			throw null;
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

}
