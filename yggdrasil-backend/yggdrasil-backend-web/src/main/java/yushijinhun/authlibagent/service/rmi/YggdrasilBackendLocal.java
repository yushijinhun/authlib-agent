package yushijinhun.authlibagent.service.rmi;

import java.security.interfaces.RSAPrivateKey;
import yushijinhun.authlibagent.api.AccountManager;
import yushijinhun.authlibagent.api.HostAccessManager;
import yushijinhun.authlibagent.api.YggdrasilBackend;

public interface YggdrasilBackendLocal extends YggdrasilBackend {

	@Override
	AccountManager getAccountManager();

	@Override
	HostAccessManager getGameServerAccessManager();

	@Override
	void setSignatureKey(RSAPrivateKey key);

}
