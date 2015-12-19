package yushijinhun.authlibagent.service;

import java.util.Set;
import java.util.UUID;
import yushijinhun.authlibagent.api.AlreadyDeletedException;
import yushijinhun.authlibagent.api.IDCollisionException;
import yushijinhun.authlibagent.api.PlayerTexture;
import yushijinhun.authlibagent.util.TokenPair;

public interface AccountService {

	// account methods

	void newAccount(String id) throws IDCollisionException;

	void deleteAccount(String id) throws AlreadyDeletedException;

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

	// ============

}
