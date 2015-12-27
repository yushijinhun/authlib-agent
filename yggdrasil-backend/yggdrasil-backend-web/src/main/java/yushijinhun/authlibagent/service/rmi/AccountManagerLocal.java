package yushijinhun.authlibagent.service.rmi;

import java.util.UUID;
import yushijinhun.authlibagent.api.AccountManager;
import yushijinhun.authlibagent.api.IDCollisionException;

public interface AccountManagerLocal extends AccountManager {

	@Override
	GameProfileLocal lookupGameProfile(UUID uuid);

	@Override
	GameProfileLocal lookupGameProfile(String name);

	@Override
	AccountLocal lookupAccount(String id);

	@Override
	AccountLocal createAccount(String id) throws IDCollisionException;

}
