package yushijinhun.authlibagent.service.rmi;

import java.security.interfaces.RSAPrivateKey;
import yushijinhun.authlibagent.api.YggdrasilBackend;
import yushijinhun.authlibagent.service.SignatureService;

public interface YggdrasilBackendLocal extends YggdrasilBackend {

	@Override
	AccountManagerLocal getAccountManager();

	@Override
	HostAccessManagerLocal getGameServerAccessManager();

	@Override
	void setSignatureKey(RSAPrivateKey key);

	SignatureService getSignatureService();

}
