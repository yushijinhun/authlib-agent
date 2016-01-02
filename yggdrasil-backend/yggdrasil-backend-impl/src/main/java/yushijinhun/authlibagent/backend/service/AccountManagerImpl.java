package yushijinhun.authlibagent.backend.service;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import yushijinhun.authlibagent.backend.api.AccountManager;
import yushijinhun.authlibagent.backend.api.AlreadyDeletedException;
import yushijinhun.authlibagent.backend.api.GameProfile;
import yushijinhun.authlibagent.backend.api.IDCollisionException;
import yushijinhun.authlibagent.backend.api.YggdrasilAccount;
import yushijinhun.authlibagent.backend.dao.AccountRepository;
import yushijinhun.authlibagent.backend.util.TokenPair;
import yushijinhun.authlibagent.commons.PlayerTexture;
import static yushijinhun.authlibagent.commons.RandomUtils.*;
import static yushijinhun.authlibagent.commons.UUIDUtils.*;
import static java.util.stream.Collectors.*;

@Component("account_manager")
public class AccountManagerImpl implements AccountManager {

	@Qualifier("account_repository")
	private AccountRepository repo;

	@Qualifier("password_sha512_salt")
	private PasswordAlgorithm pwdAlg;

	private class GameProfileImpl implements GameProfile {

		UUID uuid;

		GameProfileImpl(UUID uuid) {
			this.uuid = uuid;
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
		public AccountImpl getOwner() throws AlreadyDeletedException {
			return new AccountImpl(repo.getProfileOwner(uuid));
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
		}

		@Override
		public String getId() {
			return id;
		}

		@Override
		public Set<GameProfileImpl> getProfiles() throws AlreadyDeletedException {
			return repo.getProfiles(id).stream().map(GameProfileImpl::new).collect(toSet());
		}

		@Override
		public GameProfileImpl getSelectedProfile() throws AlreadyDeletedException {
			return new GameProfileImpl(repo.getDefaultProfile(id));
		}

		@Override
		public void revokeToken() throws AlreadyDeletedException {
			repo.setToken(id, null);
		}

		@Override
		public String createToken(String clientTokenStr) throws AlreadyDeletedException {
			UUID clientToken = toUUID(clientTokenStr);
			UUID accessToken = randomUUID();
			repo.setToken(id, new TokenPair(clientToken, accessToken));
			return unsign(accessToken);
		}

		@Override
		public boolean isTokenValid(String clientToken, String accessToken) throws AlreadyDeletedException {
			return new TokenPair(toUUID(clientToken), toUUID(accessToken)).equals(repo.getTokenByAccount(id));
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
		public GameProfileImpl createGameProfile(UUID uuid, String name) throws AlreadyDeletedException, IDCollisionException {
			repo.createProfile(id, uuid, name);
			return new GameProfileImpl(uuid);
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
	public GameProfileImpl lookupGameProfile(UUID uuid) {
		if (uuid == null || !repo.doesProfileExist(uuid)) {
			return null;
		}
		return new GameProfileImpl(uuid);
	}

	@Override
	public GameProfileImpl lookupGameProfile(String name) {
		if (name == null) {
			return null;
		}
		UUID uuid = repo.getProfileByName(name);
		if (uuid == null) {
			return null;
		}
		return new GameProfileImpl(uuid);
	}

	@Override
	public AccountImpl lookupAccount(String id) {
		if (id == null || !repo.doesAccountExist(id)) {
			return null;
		}
		return new AccountImpl(id);
	}

	@Override
	public AccountImpl createAccount(String id) throws IDCollisionException {
		Objects.requireNonNull(id);
		repo.newAccount(id);
		return new AccountImpl(id);
	}

}
