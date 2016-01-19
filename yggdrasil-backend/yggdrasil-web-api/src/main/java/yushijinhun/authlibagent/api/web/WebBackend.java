package yushijinhun.authlibagent.api.web;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.interfaces.RSAPrivateKey;
import java.util.UUID;
import yushijinhun.authlibagent.api.web.response.AuthenticateResponse;
import yushijinhun.authlibagent.api.web.response.GameProfileResponse;
import yushijinhun.authlibagent.commons.AccessPolicy;

public interface WebBackend extends Remote {

	AuthenticateResponse authenticate(String username, String password, UUID clientToken) throws ForbiddenOperationException, RemoteException;

	AuthenticateResponse refresh(UUID accessToken, UUID clientToken) throws ForbiddenOperationException, RemoteException;

	AuthenticateResponse selectProfile(UUID accessToken, UUID clientToken, UUID profile) throws ForbiddenOperationException, RemoteException;

	boolean validate(UUID accessToken, UUID clientToken) throws RemoteException;

	boolean validate(UUID accessToken) throws RemoteException;

	void invalidate(UUID accessToken, UUID clientToken) throws ForbiddenOperationException, RemoteException;

	void signout(String username, String password) throws ForbiddenOperationException, RemoteException;

	void joinServer(UUID accessToken, UUID profile, String serverid) throws ForbiddenOperationException, RemoteException;

	GameProfileResponse hasJoinServer(String playername, String serverid) throws RemoteException;

	GameProfileResponse lookupProfile(UUID profile) throws RemoteException;

	GameProfileResponse lookupProfile(String name) throws RemoteException;

	AccessPolicy getServerAccessPolicy(String host) throws RemoteException;

	void addSignatureKeyListener(SignatureKeyChangeCallback listener) throws RemoteException;

	void removeSignatureKeyListener(SignatureKeyChangeCallback listener) throws RemoteException;

	RSAPrivateKey getSignatureKey() throws RemoteException;

}
