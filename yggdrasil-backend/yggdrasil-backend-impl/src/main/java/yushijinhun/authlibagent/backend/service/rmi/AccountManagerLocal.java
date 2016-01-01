package yushijinhun.authlibagent.backend.service.rmi;

import java.util.UUID;
import yushijinhun.authlibagent.backend.api.AccountManager;
import yushijinhun.authlibagent.backend.api.IDCollisionException;

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
