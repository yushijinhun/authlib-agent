package yushijinhun.authlibagent.dao;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import yushijinhun.authlibagent.api.AlreadyDeletedException;
import yushijinhun.authlibagent.api.IDCollisionException;
import yushijinhun.authlibagent.api.PlayerTexture;
import yushijinhun.authlibagent.dao.pojo.AccountDao;
import yushijinhun.authlibagent.dao.pojo.GameProfileDao;
import yushijinhun.authlibagent.util.TokenPair;
import static org.hibernate.criterion.Restrictions.*;
import static yushijinhun.authlibagent.util.UUIDUtils.*;
import static java.util.stream.Collectors.*;

@Repository("account_repository")
@Transactional
public class AccountRepositoryImpl implements AccountRepository {

	private static final Logger LOGGER = LogManager.getFormatterLogger();

	@Qualifier("session_factory")
	private SessionFactory database;

	@Override
	public void newAccount(String id) throws IDCollisionException {
		Session session = database.getCurrentSession();
		if (session.get(AccountDao.class, id) != null) {
			throw new IDCollisionException("account '" + id + "' already exists");
		}
		AccountDao account = new AccountDao();
		account.setId(id);
		session.save(account);
	}

	@Override
	public void deleteAccount(String id) throws AlreadyDeletedException {
		Session session = database.getCurrentSession();
		AccountDao account = lookupAccount(id, session);
		session.delete(account);
	}

	@Override
	@Transactional(readOnly = true)
	public String getEncryptedPassword(String id) throws AlreadyDeletedException {
		Session session = database.getCurrentSession();
		AccountDao account = lookupAccount(id, session);
		return account.getPassword();
	}

	@Override
	public void setEncryptedPassword(String id, String password) throws AlreadyDeletedException {
		Session session = database.getCurrentSession();
		AccountDao account = lookupAccount(id, session);
		account.setPassword(password);
		session.update(account);
	}

	@Override
	@Transactional(readOnly = true)
	public TokenPair getTokenByAccount(String id) throws AlreadyDeletedException {
		Session session = database.getCurrentSession();
		AccountDao account = lookupAccount(id, session);
		return new TokenPair(toUUID(account.getClientToken()), toUUID(account.getAccessToken()));
	}

	@Override
	@Transactional(readOnly = true)
	public String getAccountByClientToken(UUID clientToken) {
		Session session = database.getCurrentSession();

		@SuppressWarnings("unchecked")
		List<AccountDao> results = session.createCriteria(AccountDao.class).add(eq("clientToken", unsign(clientToken))).list();

		if (results.isEmpty()) {
			return null;
		}
		if (results.size() > 1) {
			LOGGER.warn("%d accounts have the same client token '%s'", results.size(), clientToken);
		}

		return results.get(0).getId();
	}

	@Override
	public void setToken(String id, TokenPair token) throws AlreadyDeletedException {
		Session session = database.getCurrentSession();
		AccountDao account = lookupAccount(id, session);
		account.setClientToken(unsign(token.getClientToken()));
		account.setAccessToken(unsign(token.getAccessToken()));
		session.update(account);
	}

	@Override
	@Transactional(readOnly = true)
	public boolean isAccountBanned(String id) throws AlreadyDeletedException {
		Session session = database.getCurrentSession();
		AccountDao account = lookupAccount(id, session);
		return account.isBanned();
	}

	@Override
	public void setAccountBanned(String id, boolean banned) throws AlreadyDeletedException {
		Session session = database.getCurrentSession();
		AccountDao account = lookupAccount(id, session);
		account.setBanned(banned);
		session.update(account);
	}

	@Override
	public void createProfile(String ownerId, UUID uuid, String name) throws IDCollisionException, AlreadyDeletedException {
		Session session = database.getCurrentSession();
		AccountDao account = lookupAccount(ownerId, session);

		if (session.get(GameProfileDao.class, uuid) != null) {
			throw new IDCollisionException("uuid collision '" + uuid + "'");
		}
		if (!session.createCriteria(GameProfileDao.class).add(eq("name", name)).list().isEmpty()) {
			throw new IDCollisionException("name collision '" + name + "'");
		}

		GameProfileDao profile = new GameProfileDao();
		profile.setUuid(uuid);
		profile.setName(name);
		profile.setOwner(account);
		session.save(profile);
		session.update(account);
	}

	@Override
	public void deleteProfile(UUID uuid) throws AlreadyDeletedException {
		Session session = database.getCurrentSession();
		GameProfileDao profile = lookupProfile(uuid, session);
		AccountDao account = profile.getOwner();
		account.getProfiles().remove(profile);
		session.delete(profile);
		session.update(account);
	}

	@Override
	public void renameProfile(UUID uuid, String newName) throws IDCollisionException, AlreadyDeletedException {
		Session session = database.getCurrentSession();
		GameProfileDao profile = lookupProfile(uuid, session);

		if (!session.createCriteria(GameProfileDao.class).add(eq("name", newName)).list().isEmpty()) {
			throw new IDCollisionException("name collision '" + newName + "'");
		}

		profile.setName(newName);
		session.update(profile);
	}

	@Override
	@Transactional(readOnly = true)
	public UUID getProfileByName(String name) {
		Session session = database.getCurrentSession();

		@SuppressWarnings("unchecked")
		List<GameProfileDao> results = session.createCriteria(GameProfileDao.class).add(eq("name", name)).list();

		if (results.isEmpty()) {
			return null;
		}
		if (results.size() != 1) {
			LOGGER.warn("%d profiles have the same name '%s'", results.size(), name);
		}

		return results.get(0).getUuid();
	}

	@Override
	@Transactional(readOnly = true)
	public String getProfileName(UUID uuid) throws AlreadyDeletedException {
		Session session = database.getCurrentSession();
		GameProfileDao profile = lookupProfile(uuid, session);
		return profile.getName();
	}

	@Override
	@Transactional(readOnly = true)
	public boolean isProfileBanned(UUID uuid) throws AlreadyDeletedException {
		Session session = database.getCurrentSession();
		GameProfileDao profile = lookupProfile(uuid, session);
		return profile.isBanned();
	}

	@Override
	public void setProfileBanned(UUID uuid, boolean banned) throws AlreadyDeletedException {
		Session session = database.getCurrentSession();
		GameProfileDao profile = lookupProfile(uuid, session);
		profile.setBanned(banned);
		session.update(profile);
	}

	@Override
	@Transactional(readOnly = true)
	public PlayerTexture getTexture(UUID uuid) throws AlreadyDeletedException {
		Session session = database.getCurrentSession();
		GameProfileDao profile = lookupProfile(uuid, session);
		return new PlayerTexture(profile.getTextureModel(), profile.getSkin(), profile.getCape());
	}

	@Override
	public void setTexture(UUID uuid, PlayerTexture texture) throws AlreadyDeletedException {
		Session session = database.getCurrentSession();
		GameProfileDao profile = lookupProfile(uuid, session);
		profile.setTextureModel(texture.getModel());
		profile.setSkin(texture.getSkin());
		profile.setCape(texture.getCape());
		session.update(profile);
	}

	@Override
	@Transactional(readOnly = true)
	public String getProfileOwner(UUID uuid) throws AlreadyDeletedException {
		Session session = database.getCurrentSession();
		GameProfileDao profile = lookupProfile(uuid, session);
		return profile.getOwner().getId();
	}

	@Override
	@Transactional(readOnly = true)
	public Set<UUID> getProfiles(String accountId) throws AlreadyDeletedException {
		Session session = database.getCurrentSession();
		AccountDao account = lookupAccount(accountId, session);
		return account.getProfiles().stream().map(GameProfileDao::getUuid).collect(toSet());
	}

	@Override
	@Transactional(readOnly = true)
	public UUID getDefaultProfile(String accountId) throws AlreadyDeletedException {
		Session session = database.getCurrentSession();
		AccountDao account = lookupAccount(accountId, session);
		return account.getSelectedProfile().getUuid();
	}

	@Override
	public void setDefaultProfile(UUID uuid) throws AlreadyDeletedException {
		Session session = database.getCurrentSession();
		GameProfileDao profile = lookupProfile(uuid, session);
		AccountDao account = profile.getOwner();
		account.setSelectedProfile(profile);
		session.update(account);
	}

	private AccountDao lookupAccount(String id, Session session) throws AlreadyDeletedException {
		AccountDao account = session.get(AccountDao.class, id);
		if (account == null) {
			throw new AlreadyDeletedException("account '" + id + "' does not exist");
		}
		return account;
	}

	private GameProfileDao lookupProfile(UUID uuid, Session session) throws AlreadyDeletedException {
		GameProfileDao profile = session.get(GameProfileDao.class, uuid);
		if (profile == null) {
			throw new AlreadyDeletedException("profile '" + uuid + "' does not exist");
		}
		return profile;
	}

}
