package yushijinhun.authlibagent.dao;

import java.util.Set;
import java.util.UUID;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import yushijinhun.authlibagent.api.AlreadyDeletedException;
import yushijinhun.authlibagent.api.IDCollisionException;
import yushijinhun.authlibagent.api.PlayerTexture;
import yushijinhun.authlibagent.util.TokenPair;

@Repository("account_repository")
public class AccountRepositoryImpl implements AccountRepository {

	@Qualifier("session_factory")
	private SessionFactory database;

	@Transactional
	@Override
	public void newAccount(String id) throws IDCollisionException {
		// TODO Auto-generated method stub

	}

	@Transactional
	@Override
	public void deleteAccount(String id) throws AlreadyDeletedException {
		// TODO Auto-generated method stub

	}

	@Transactional
	@Override
	public String getEncryptedPassword(String id) throws AlreadyDeletedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Transactional
	@Override
	public void setEncryptedPassword(String id, String password) throws AlreadyDeletedException {
		// TODO Auto-generated method stub

	}

	@Transactional
	@Override
	public TokenPair getTokenByAccount(String id) throws AlreadyDeletedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Transactional
	@Override
	public String getAccountByClientToken(UUID clientToken) {
		// TODO Auto-generated method stub
		return null;
	}

	@Transactional
	@Override
	public void setToken(String id, TokenPair token) throws AlreadyDeletedException {
		// TODO Auto-generated method stub

	}

	@Transactional
	@Override
	public boolean isAccountBanned(String id) throws AlreadyDeletedException {
		// TODO Auto-generated method stub
		return false;
	}

	@Transactional
	@Override
	public void setAccountBanned(String id, boolean banned) throws AlreadyDeletedException {
		// TODO Auto-generated method stub

	}

	@Transactional
	@Override
	public void createProfile(String ownerId, UUID uuid, String name) throws IDCollisionException, AlreadyDeletedException {
		// TODO Auto-generated method stub

	}

	@Transactional
	@Override
	public void deleteProfile(UUID uuid) throws AlreadyDeletedException {
		// TODO Auto-generated method stub

	}

	@Transactional
	@Override
	public void renameProfile(UUID uuid, String newName) throws IDCollisionException, AlreadyDeletedException {
		// TODO Auto-generated method stub

	}

	@Override
	public UUID getProfileByName(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getProfileName(UUID uuid) throws AlreadyDeletedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isProfileBanned(UUID uuid) throws AlreadyDeletedException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setProfileBanned(UUID uuid, boolean banned) throws AlreadyDeletedException {
		// TODO Auto-generated method stub

	}

	@Override
	public PlayerTexture getTexture(UUID uuid) throws AlreadyDeletedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setTexture(UUID uuid, PlayerTexture texture) throws AlreadyDeletedException {
		// TODO Auto-generated method stub

	}

	@Override
	public String getProfileOwner(UUID uuid) throws AlreadyDeletedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<UUID> getProfiles(String accountId) throws AlreadyDeletedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UUID getDefaultProfile(String accountId) throws AlreadyDeletedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setDefaultProfile(UUID uuid) throws AlreadyDeletedException {
		// TODO Auto-generated method stub

	}

}
