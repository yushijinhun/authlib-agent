package yushijinhun.authlibagent.service.rmi;

import java.util.Set;
import java.util.UUID;
import yushijinhun.authlibagent.api.AlreadyDeletedException;
import yushijinhun.authlibagent.api.IDCollisionException;
import yushijinhun.authlibagent.api.YggdrasilAccount;

public interface AccountLocal extends YggdrasilAccount {

	@Override
	String getId();

	@Override
	Set<GameProfileLocal> getProfiles() throws AlreadyDeletedException;

	@Override
	public GameProfileLocal getSelectedProfile() throws AlreadyDeletedException;

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
	GameProfileLocal createGameProfile(UUID uuid, String name) throws AlreadyDeletedException, IDCollisionException;

	@Override
	void delete() throws AlreadyDeletedException;

}
