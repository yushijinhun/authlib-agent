package yushijinhun.authlibagent.api.web;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.interfaces.RSAPrivateKey;
import java.util.UUID;
import yushijinhun.authlibagent.api.util.AccessPolicy;
import yushijinhun.authlibagent.api.web.response.AuthenticateResponse;
import yushijinhun.authlibagent.api.web.response.GameProfileResponse;

public interface WebBackend extends Remote {

	AuthenticateResponse authenticate(String username, String password, String clientToken) throws ForbiddenOperationException, RemoteException;

	AuthenticateResponse refresh(String accessToken, String clientToken) throws ForbiddenOperationException, RemoteException;

	AuthenticateResponse selectProfile(String accessToken, String clientToken, UUID profile) throws ForbiddenOperationException, RemoteException;

	boolean validate(String accessToken, String clientToken) throws RemoteException;

	boolean validate(String accessToken) throws RemoteException;

	void invalidate(String accessToken, String clientToken) throws ForbiddenOperationException, RemoteException;

	void signout(String username, String password) throws ForbiddenOperationException, RemoteException;

	void joinServer(String accessToken, UUID profile, String serverid) throws ForbiddenOperationException, RemoteException;

	GameProfileResponse hasJoinServer(String playername, String serverid) throws RemoteException;

	GameProfileResponse lookupProfile(UUID profile) throws RemoteException;

	GameProfileResponse lookupProfile(String name) throws RemoteException;

	AccessPolicy getServerAccessPolicy(String host) throws RemoteException;

	void addSignatureKeyListener(SignatureKeyChangeCallback listener) throws RemoteException;

	void removeSignatureKeyListener(SignatureKeyChangeCallback listener) throws RemoteException;

	RSAPrivateKey getSignatureKey() throws RemoteException;

}
