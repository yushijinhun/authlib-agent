package yushijinhun.authlibagent.backend.service;

import java.security.interfaces.RSAPrivateKey;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import yushijinhun.authlibagent.backend.api.AccountManager;
import yushijinhun.authlibagent.backend.api.HostAccessManager;
import yushijinhun.authlibagent.backend.api.YggdrasilBackend;

@Component("backend_access")
public class YggdrasilBackendImpl implements YggdrasilBackend {

	@Qualifier("account_manager")
	private AccountManager accountManager;

	@Qualifier("host_access_manager")
	private HostAccessManager hostAccessManager;

	@Qualifier("key_server_service")
	private KeyServerService keyServerService;

	@Override
	public AccountManager getAccountManager() {
		return accountManager;
	}

	@Override
	public HostAccessManager getGameServerAccessManager() {
		return hostAccessManager;
	}

	@Override
	public void setSignatureKey(RSAPrivateKey key) {
		keyServerService.setKey(key);
	}

}
