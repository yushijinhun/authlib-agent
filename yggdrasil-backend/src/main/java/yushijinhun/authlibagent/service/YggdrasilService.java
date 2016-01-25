package yushijinhun.authlibagent.service;

import java.util.UUID;
import yushijinhun.authlibagent.util.AccessPolicy;
import yushijinhun.authlibagent.web.yggdrasil.api.ForbiddenOperationException;
import yushijinhun.authlibagent.web.yggdrasil.api.response.AuthenticateResponse;
import yushijinhun.authlibagent.web.yggdrasil.api.response.GameProfileResponse;

public interface YggdrasilService {

	AuthenticateResponse authenticate(String username, String password, String clientToken) throws ForbiddenOperationException;

	AuthenticateResponse refresh(String accessToken, String clientToken) throws ForbiddenOperationException;

	AuthenticateResponse selectProfile(String accessToken, String clientToken, UUID profile) throws ForbiddenOperationException;

	boolean validate(String accessToken, String clientToken);

	boolean validate(String accessToken);

	void invalidate(String accessToken, String clientToken) throws ForbiddenOperationException;

	void signout(String username, String password) throws ForbiddenOperationException;

	void joinServer(String accessToken, UUID profile, String serverid) throws ForbiddenOperationException;

	GameProfileResponse hasJoinServer(String playername, String serverid);

	GameProfileResponse lookupProfile(UUID profile);

	GameProfileResponse lookupProfile(String name);

	AccessPolicy getServerAccessPolicy(String host);

}
