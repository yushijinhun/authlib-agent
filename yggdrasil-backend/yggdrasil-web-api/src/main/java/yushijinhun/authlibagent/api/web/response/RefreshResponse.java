package yushijinhun.authlibagent.api.web.response;

import java.io.Serializable;
import java.util.UUID;

public class RefreshResponse implements Serializable {

	private static final long serialVersionUID = 1L;

	private UUID accessToken;
	private GameProfileResponse selectedProfile;

	public RefreshResponse(UUID accessToken, GameProfileResponse selectedProfile) {
		this.accessToken = accessToken;
		this.selectedProfile = selectedProfile;
	}

	public UUID getAccessToken() {
		return accessToken;
	}

	public GameProfileResponse getSelectedProfile() {
		return selectedProfile;
	}

}
