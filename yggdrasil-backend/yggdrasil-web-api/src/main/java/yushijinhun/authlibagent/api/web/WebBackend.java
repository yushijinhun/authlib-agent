package yushijinhun.authlibagent.api.web;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.UUID;
import yushijinhun.authlibagent.api.web.response.AuthenticateResponse;
import yushijinhun.authlibagent.api.web.response.RefreshResponse;
import yushijinhun.authlibagent.commons.AccessPolicy;

public interface WebBackend extends Remote {

	AuthenticateResponse authenticate(String username, String password, UUID clientToken) throws ForbiddenOperationException, RemoteException;

	RefreshResponse refresh(UUID accessToken, UUID clientToken) throws ForbiddenOperationException, RemoteException;

	RefreshResponse selectProfile(UUID accessToken, UUID clientToken) throws ForbiddenOperationException, RemoteException;

	boolean validate(UUID accessToken, UUID clientToken) throws RemoteException;

	boolean validate(UUID accessToken) throws RemoteException;

	void invalidate(UUID accessToken, UUID clientToken) throws ForbiddenOperationException, RemoteException;

	void signout(String username, String password) throws ForbiddenOperationException, RemoteException;

	void joinServer(UUID accessToken, UUID profile, String serverid) throws ForbiddenOperationException, RemoteException;

	boolean hasJoinServer(String username, String serverid) throws RemoteException;

	GameProfile lookupProfile(UUID profile) throws RemoteException;

	AccessPolicy getServerAccessPolicy(String host) throws RemoteException;

	void setSignatureKeyListener(SignatureKeyChangeCallback listener) throws RemoteException;

}
