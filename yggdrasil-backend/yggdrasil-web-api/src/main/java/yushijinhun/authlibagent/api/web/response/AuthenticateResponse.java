package yushijinhun.authlibagent.api.web.response;

import java.io.Serializable;
import java.util.UUID;
import yushijinhun.authlibagent.api.web.GameProfile;

public class AuthenticateResponse implements Serializable {

	private static final long serialVersionUID = 1L;

	private UUID accessToken;
	private GameProfile selectedProfile;
	private GameProfile[] profiles;

	public AuthenticateResponse(UUID accessToken, GameProfile selectedProfile, GameProfile[] profiles) {
		this.accessToken = accessToken;
		this.selectedProfile = selectedProfile;
		this.profiles = profiles;
	}

	public UUID getAccessToken() {
		return accessToken;
	}

	public GameProfile getSelectedProfile() {
		return selectedProfile;
	}

	public GameProfile[] getProfiles() {
		return profiles;
	}

}
