package yushijinhun.authlibagent.backend.service;

import java.rmi.RemoteException;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import yushijinhun.authlibagent.backend.api.AccountManager;
import yushijinhun.authlibagent.backend.api.AlreadyDeletedException;
import yushijinhun.authlibagent.backend.api.GameProfile;
import yushijinhun.authlibagent.backend.api.IDCollisionException;
import yushijinhun.authlibagent.backend.api.YggdrasilAccount;
import yushijinhun.authlibagent.backend.dao.AccountRepository;
import yushijinhun.authlibagent.backend.util.Cache;
import yushijinhun.authlibagent.backend.util.TokenPair;
import yushijinhun.authlibagent.commons.PlayerTexture;
import static yushijinhun.authlibagent.commons.RandomUtils.*;
import static java.util.stream.Collectors.*;
import static yushijinhun.authlibagent.backend.util.RMIUtils.*;

@Component("accountManager")
public class AccountManagerImpl implements AccountManager {

	@Autowired
	private AccountRepository repo;

	@Autowired
	private PasswordAlgorithm pwdAlg;

	private Cache<String, YggdrasilAccount> accounts = new Cache<>(AccountImpl::new);
	private Cache<UUID, GameProfile> profiles = new Cache<>(GameProfileImpl::new);

	private class GameProfileImpl implements GameProfile {

		UUID uuid;

		GameProfileImpl(UUID uuid) {
			this.uuid = uuid;
			exportRemoteObject(this);
		}

		@Override
		public UUID getUUID() {
			return uuid;
		}

		@Override
		public String getName() throws AlreadyDeletedException {
			return repo.getProfileName(uuid);
		}

		@Override
		public void setName(String name) throws AlreadyDeletedException, IDCollisionException {
			repo.renameProfile(uuid, name);
		}

		@Override
		public YggdrasilAccount getOwner() throws AlreadyDeletedException {
			return lookupAccount(repo.getProfileOwner(uuid));
		}

		@Override
		public boolean isBanned() throws AlreadyDeletedException {
			return repo.isProfileBanned(uuid);
		}

		@Override
		public void setBanned(boolean banned) throws AlreadyDeletedException {
			repo.setProfileBanned(uuid, banned);
		}

		@Override
		public PlayerTexture getTexture() throws AlreadyDeletedException {
			return repo.getTexture(uuid);
		}

		@Override
		public void setTexture(PlayerTexture texture) throws AlreadyDeletedException {
			repo.setTexture(uuid, texture);
		}

		@Override
		public void setToDefault() throws AlreadyDeletedException {
			repo.setDefaultProfile(uuid);
		}

		@Override
		public String getServerAuthenticationID() throws AlreadyDeletedException, RemoteException {
			return repo.getServerId(uuid);
		}

		@Override
		public void setServerAuthenticationID(String serverid) throws AlreadyDeletedException, RemoteException {
			repo.setServerId(uuid, serverid);
		}

		@Override
		public void delete() throws AlreadyDeletedException {
			repo.deleteProfile(uuid);
		}

		@Override
		public String toString() {
			return "<" + uuid + ">";
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == this) {
				return true;
			}
			if (obj instanceof GameProfileImpl) {
				GameProfileImpl another = (GameProfileImpl) obj;
				return uuid.equals(another.uuid);
			}
			return false;
		}

		@Override
		public int hashCode() {
			return Objects.hash(uuid);
		}

	}

	private class AccountImpl implements YggdrasilAccount {

		String id;

		AccountImpl(String id) {
			this.id = id;
			exportRemoteObject(this);
		}

		@Override
		public String getId() {
			return id;
		}

		@Override
		public Set<GameProfile> getProfiles() throws AlreadyDeletedException {
			return repo.getProfiles(id).stream().map(AccountManagerImpl.this::lookupGameProfile).collect(toSet());
		}

		@Override
		public GameProfile getSelectedProfile() throws AlreadyDeletedException {
			return lookupGameProfile(repo.getDefaultProfile(id));
		}

		@Override
		public void revokeToken() throws AlreadyDeletedException {
			repo.setToken(id, null);
		}

		@Override
		public UUID createToken(UUID clientToken) throws AlreadyDeletedException {
			UUID accessToken = randomUUID();
			repo.setToken(id, new TokenPair(clientToken, accessToken));
			return accessToken;
		}

		@Override
		public boolean isTokenValid(UUID clientToken, UUID accessToken) throws AlreadyDeletedException {
			return new TokenPair(clientToken, accessToken).equals(repo.getTokenByAccount(id));
		}

		@Override
		public void setPassword(String password) throws AlreadyDeletedException {
			String hash = pwdAlg.hash(password);
			repo.setEncryptedPassword(id, hash);
		}

		@Override
		public boolean isPasswordValid(String password) throws AlreadyDeletedException {
			String hash = repo.getEncryptedPassword(id);
			return pwdAlg.verify(password, hash);
		}

		@Override
		public boolean isBanned() throws AlreadyDeletedException {
			return repo.isAccountBanned(id);
		}

		@Override
		public void setBanned(boolean banned) throws AlreadyDeletedException {
			repo.setAccountBanned(id, banned);
		}

		@Override
		public GameProfile createGameProfile(UUID uuid, String name) throws AlreadyDeletedException, IDCollisionException {
			repo.createProfile(id, uuid, name);
			return lookupGameProfile(uuid);
		}

		@Override
		public void delete() throws AlreadyDeletedException {
			repo.deleteAccount(id);
		}

		@Override
		public String toString() {
			return "<" + id + ">";
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == this) {
				return true;
			}
			if (obj instanceof AccountImpl) {
				AccountImpl another = (AccountImpl) obj;
				return id.equals(another.id);
			}
			return false;
		}

		@Override
		public int hashCode() {
			return Objects.hash(id);
		}

	}

	@Override
	public GameProfile lookupGameProfile(UUID uuid) {
		if (uuid == null || !repo.doesProfileExist(uuid)) {
			profiles.remove(uuid);
			return null;
		}
		return profiles.get(uuid);
	}

	@Override
	public GameProfile lookupGameProfile(String name) {
		if (name == null) {
			return null;
		}
		UUID uuid = repo.getProfileByName(name);
		if (uuid == null) {
			return null;
		}
		return profiles.get(uuid);
	}

	@Override
	public YggdrasilAccount lookupAccount(String id) {
		if (id == null || !repo.doesAccountExist(id)) {
			return null;
		}
		return accounts.get(id);
	}

	@Override
	public YggdrasilAccount createAccount(String id) throws IDCollisionException {
		Objects.requireNonNull(id);
		repo.newAccount(id);
		return accounts.get(id);
	}

	@Override
	public YggdrasilAccount lookupAccount(UUID accessToken) throws RemoteException {
		if (accessToken == null) {
			return null;
		}
		String id = repo.getAccountByAccessToken(accessToken);
		if (id == null) {
			return null;
		}
		return accounts.get(id);
	}

}
