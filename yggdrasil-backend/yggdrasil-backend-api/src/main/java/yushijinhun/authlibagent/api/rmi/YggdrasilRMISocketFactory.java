package yushijinhun.authlibagent.api.rmi;

import java.io.IOException;
import java.net.Socket;
import java.rmi.server.RMIClientSocketFactory;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

/**
 * 用于建立加密的RMI连接
 * 
 * @author yushijinhun
 */
class YggdrasilRMISocketFactory implements RMIClientSocketFactory {

	private SSLContext ctx;

	public YggdrasilRMISocketFactory(KeyStore trustKeyStore, KeyStore clientKeyStore, String clientKeyPassword) throws NoSuchAlgorithmException, UnrecoverableKeyException, KeyStoreException, KeyManagementException {
		ctx = SSLContext.getInstance("TSL");
		KeyManagerFactory keyManager = KeyManagerFactory.getInstance("SunX509");
		TrustManagerFactory trustManager = TrustManagerFactory.getInstance("SunX509");
		keyManager.init(clientKeyStore, clientKeyPassword.toCharArray());
		trustManager.init(trustKeyStore);
		ctx.init(keyManager.getKeyManagers(), trustManager.getTrustManagers(), new SecureRandom());
	}

	@Override
	public Socket createSocket(String host, int port) throws IOException {
		return ctx.getSocketFactory().createSocket(host, port);
	}

}
