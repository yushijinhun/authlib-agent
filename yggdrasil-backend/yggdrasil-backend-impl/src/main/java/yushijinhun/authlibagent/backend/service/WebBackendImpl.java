package yushijinhun.authlibagent.backend.service;

import java.rmi.RemoteException;
import java.security.interfaces.RSAPrivateKey;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import yushijinhun.authlibagent.api.web.ForbiddenOperationException;
import yushijinhun.authlibagent.api.web.SignatureKeyChangeCallback;
import yushijinhun.authlibagent.api.web.WebBackend;
import yushijinhun.authlibagent.api.web.response.AuthenticateResponse;
import yushijinhun.authlibagent.api.web.response.GameProfileResponse;
import yushijinhun.authlibagent.api.web.response.RefreshResponse;
import yushijinhun.authlibagent.backend.api.YggdrasilBackend;
import yushijinhun.authlibagent.commons.AccessPolicy;

@Component("web_backend")
public class WebBackendImpl implements WebBackend {

	@Qualifier("backend_access")
	private YggdrasilBackend backend;

	@Qualifier("signature_service")
	private SignatureService signatureService;

	@Transactional
	@Override
	public AuthenticateResponse authenticate(String username, String password, UUID clientToken) throws ForbiddenOperationException, RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Transactional
	@Override
	public RefreshResponse refresh(UUID accessToken, UUID clientToken) throws ForbiddenOperationException, RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Transactional
	@Override
	public RefreshResponse selectProfile(UUID accessToken, UUID clientToken) throws ForbiddenOperationException, RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Transactional
	@Override
	public boolean validate(UUID accessToken, UUID clientToken) throws RemoteException {
		// TODO Auto-generated method stub
		return false;
	}

	@Transactional
	@Override
	public boolean validate(UUID accessToken) throws RemoteException {
		// TODO Auto-generated method stub
		return false;
	}

	@Transactional
	@Override
	public void invalidate(UUID accessToken, UUID clientToken) throws ForbiddenOperationException, RemoteException {
		// TODO Auto-generated method stub

	}

	@Transactional
	@Override
	public void signout(String username, String password) throws ForbiddenOperationException, RemoteException {
		// TODO Auto-generated method stub

	}

	@Transactional
	@Override
	public void joinServer(UUID accessToken, UUID profile, String serverid) throws ForbiddenOperationException, RemoteException {
		// TODO Auto-generated method stub

	}

	@Transactional
	@Override
	public boolean hasJoinServer(String playername, String serverid) throws RemoteException {
		// TODO Auto-generated method stub
		return false;
	}

	@Transactional
	@Override
	public GameProfileResponse lookupProfile(UUID profile) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Transactional
	@Override
	public AccessPolicy getServerAccessPolicy(String host) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addSignatureKeyListener(SignatureKeyChangeCallback listener) throws RemoteException {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeSignatureKeyListener(SignatureKeyChangeCallback listener) throws RemoteException {
		// TODO Auto-generated method stub

	}

	@Override
	public RSAPrivateKey getSignatureKey() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

}
