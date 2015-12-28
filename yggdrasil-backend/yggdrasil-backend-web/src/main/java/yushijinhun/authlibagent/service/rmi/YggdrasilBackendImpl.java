package yushijinhun.authlibagent.service.rmi;

import java.security.interfaces.RSAPrivateKey;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import yushijinhun.authlibagent.service.SignatureService;

@Component("backend_access")
public class YggdrasilBackendImpl implements YggdrasilBackendLocal {

	@Qualifier("account_manager")
	private AccountManagerLocal accountManager;

	@Qualifier("host_access_manager")
	private HostAccessManagerLocal hostAccessManager;

	@Qualifier("signature_service")
	private SignatureService signatureService;

	@Override
	public AccountManagerLocal getAccountManager() {
		return accountManager;
	}

	@Override
	public HostAccessManagerLocal getGameServerAccessManager() {
		return hostAccessManager;
	}

	@Override
	public void setSignatureKey(RSAPrivateKey key) {
		signatureService.setSignatureKey(key);
	}

}
