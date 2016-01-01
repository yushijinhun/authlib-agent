package yushijinhun.authlibagent.backend.service.rmi;

import java.util.UUID;
import yushijinhun.authlibagent.backend.api.AlreadyDeletedException;
import yushijinhun.authlibagent.backend.api.GameProfile;
import yushijinhun.authlibagent.backend.api.IDCollisionException;
import yushijinhun.authlibagent.commons.PlayerTexture;

public interface GameProfileLocal extends GameProfile {

	@Override
	UUID getUUID();

	@Override
	String getName() throws AlreadyDeletedException;

	@Override
	void setName(String name) throws AlreadyDeletedException, IDCollisionException;

	@Override
	AccountLocal getOwner() throws AlreadyDeletedException;

	@Override
	boolean isBanned() throws AlreadyDeletedException;

	@Override
	void setBanned(boolean banned) throws AlreadyDeletedException;

	@Override
	PlayerTexture getTexture() throws AlreadyDeletedException;

	@Override
	void setTexture(PlayerTexture texture) throws AlreadyDeletedException;

	@Override
	public void setToDefault() throws AlreadyDeletedException;

	@Override
	void delete() throws AlreadyDeletedException;

}
