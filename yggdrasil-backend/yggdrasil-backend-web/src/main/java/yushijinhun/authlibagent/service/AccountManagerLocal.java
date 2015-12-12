package yushijinhun.authlibagent.service;

import java.util.UUID;
import yushijinhun.authlibagent.api.AccountManager;
import yushijinhun.authlibagent.api.GameProfile;
import yushijinhun.authlibagent.api.IDCollisionException;
import yushijinhun.authlibagent.api.YggdrasilAccount;

public interface AccountManagerLocal extends AccountManager {

	@Override
	GameProfile lookupGameProfile(UUID uuid);

	@Override
	GameProfile lookupGameProfile(String name);

	@Override
	YggdrasilAccount lookupAccount(String id);

	@Override
	YggdrasilAccount createAccount(String id) throws IDCollisionException;

}
