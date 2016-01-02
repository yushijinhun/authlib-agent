package yushijinhun.authlibagent.backend.service;

import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.security.interfaces.RSAPrivateKey;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
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

	@Value("#{config['rmi.backend']}")
	private String rmiUri;

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

	@PostConstruct
	private void rmiBind() throws MalformedURLException, RemoteException, AlreadyBoundException {
		Naming.bind(rmiUri, this);
	}

	@PreDestroy
	private void rmiUnbind() throws RemoteException, MalformedURLException, NotBoundException {
		Naming.unbind(rmiUri);
	}

}
