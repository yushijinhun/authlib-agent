package yushijinhun.authlibagent.backend.service.rmi;

import java.security.interfaces.RSAPrivateKey;
import yushijinhun.authlibagent.backend.api.YggdrasilBackend;
import yushijinhun.authlibagent.backend.service.SignatureService;

public interface YggdrasilBackendLocal extends YggdrasilBackend {

	@Override
	AccountManagerLocal getAccountManager();

	@Override
	HostAccessManagerLocal getGameServerAccessManager();

	@Override
	void setSignatureKey(RSAPrivateKey key);

	SignatureService getSignatureService();

}
