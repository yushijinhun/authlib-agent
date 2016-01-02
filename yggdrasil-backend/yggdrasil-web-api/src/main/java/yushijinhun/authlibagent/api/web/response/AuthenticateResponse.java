package yushijinhun.authlibagent.api.web.response;

import java.io.Serializable;
import java.util.UUID;

public class AuthenticateResponse implements Serializable {

	private static final long serialVersionUID = 1L;

	private UUID accessToken;
	private GameProfileResponse selectedProfile;
	private GameProfileResponse[] profiles;

	public AuthenticateResponse(UUID accessToken, GameProfileResponse selectedProfile, GameProfileResponse[] profiles) {
		this.accessToken = accessToken;
		this.selectedProfile = selectedProfile;
		this.profiles = profiles;
	}

	public UUID getAccessToken() {
		return accessToken;
	}

	public GameProfileResponse getSelectedProfile() {
		return selectedProfile;
	}

	public GameProfileResponse[] getProfiles() {
		return profiles;
	}

}
