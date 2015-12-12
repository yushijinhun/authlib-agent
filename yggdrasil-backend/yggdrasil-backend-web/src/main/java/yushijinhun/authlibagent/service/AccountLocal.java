package yushijinhun.authlibagent.service;

import java.util.Set;
import java.util.UUID;
import yushijinhun.authlibagent.api.AlreadyDeletedException;
import yushijinhun.authlibagent.api.GameProfile;
import yushijinhun.authlibagent.api.IDCollisionException;
import yushijinhun.authlibagent.api.YggdrasilAccount;

public interface AccountLocal extends YggdrasilAccount {

	@Override
	String getId() throws AlreadyDeletedException;

	@Override
	Set<GameProfile> getProfiles() throws AlreadyDeletedException;

	@Override
	void revokeToken() throws AlreadyDeletedException;

	@Override
	String createToken(String clientToken) throws AlreadyDeletedException;

	@Override
	boolean isTokenValid(String clientToken, String accessToken) throws AlreadyDeletedException;

	@Override
	void setPassword(String password) throws AlreadyDeletedException;

	@Override
	boolean isPasswordValid(String password) throws AlreadyDeletedException;

	@Override
	boolean isBanned() throws AlreadyDeletedException;

	@Override
	void setBanned(boolean banned) throws AlreadyDeletedException;

	@Override
	GameProfile createGameProfile(UUID uuid, String name) throws AlreadyDeletedException, IDCollisionException;

	@Override
	void delete() throws AlreadyDeletedException;

}
