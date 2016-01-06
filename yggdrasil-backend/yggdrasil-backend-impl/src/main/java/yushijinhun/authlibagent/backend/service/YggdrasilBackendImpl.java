package yushijinhun.authlibagent.backend.service;

import java.security.interfaces.RSAPrivateKey;
import javax.annotation.Resource;
import org.springframework.stereotype.Component;
import yushijinhun.authlibagent.backend.api.AccountManager;
import yushijinhun.authlibagent.backend.api.HostAccessManager;
import yushijinhun.authlibagent.backend.api.YggdrasilBackend;

@Component("backend_access")
public class YggdrasilBackendImpl implements YggdrasilBackend {

	@Resource(name = "account_manager")
	private AccountManager accountManager;

	@Resource(name = "host_access_manager")
	private HostAccessManager hostAccessManager;

	@Resource(name = "key_server_service")
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
