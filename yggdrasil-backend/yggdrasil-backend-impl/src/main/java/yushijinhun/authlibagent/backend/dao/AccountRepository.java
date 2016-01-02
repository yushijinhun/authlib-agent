package yushijinhun.authlibagent.backend.dao;

import java.util.Set;
import java.util.UUID;
import yushijinhun.authlibagent.backend.api.AlreadyDeletedException;
import yushijinhun.authlibagent.backend.api.IDCollisionException;
import yushijinhun.authlibagent.backend.util.TokenPair;
import yushijinhun.authlibagent.commons.PlayerTexture;

public interface AccountRepository {

	// account methods

	void newAccount(String id) throws IDCollisionException;

	void deleteAccount(String id) throws AlreadyDeletedException;

	boolean doesAccountExist(String id);

	String getEncryptedPassword(String id) throws AlreadyDeletedException;

	void setEncryptedPassword(String id, String password) throws AlreadyDeletedException;

	TokenPair getTokenByAccount(String id) throws AlreadyDeletedException;

	String getAccountByClientToken(UUID clientToken);

	void setToken(String id, TokenPair token) throws AlreadyDeletedException;

	boolean isAccountBanned(String id) throws AlreadyDeletedException;

	void setAccountBanned(String id, boolean banned) throws AlreadyDeletedException;

	// ============

	// profile methods

	void createProfile(String ownerId, UUID uuid, String name) throws IDCollisionException, AlreadyDeletedException;

	void deleteProfile(UUID uuid) throws AlreadyDeletedException;

	void renameProfile(UUID uuid, String newName) throws IDCollisionException, AlreadyDeletedException;

	boolean doesProfileExist(UUID uuid);

	UUID getProfileByName(String name);

	String getProfileName(UUID uuid) throws AlreadyDeletedException;

	boolean isProfileBanned(UUID uuid) throws AlreadyDeletedException;

	void setProfileBanned(UUID uuid, boolean banned) throws AlreadyDeletedException;

	PlayerTexture getTexture(UUID uuid) throws AlreadyDeletedException;

	void setTexture(UUID uuid, PlayerTexture texture) throws AlreadyDeletedException;

	String getProfileOwner(UUID uuid) throws AlreadyDeletedException;

	Set<UUID> getProfiles(String accountId) throws AlreadyDeletedException;

	UUID getDefaultProfile(String accountId) throws AlreadyDeletedException;

	void setDefaultProfile(UUID uuid) throws AlreadyDeletedException;

	String getServerId(UUID profile) throws AlreadyDeletedException;

	void setServerId(UUID profile, String serverid) throws AlreadyDeletedException;

	// ============

}
