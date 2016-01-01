package yushijinhun.authlibagent.api.web.response;

import java.io.Serializable;
import java.util.UUID;
import yushijinhun.authlibagent.api.web.GameProfile;

public class RefreshResponse implements Serializable {

	private static final long serialVersionUID = 1L;

	private UUID accessToken;
	private GameProfile selectedProfile;

	public RefreshResponse(UUID accessToken, GameProfile selectedProfile) {
		this.accessToken = accessToken;
		this.selectedProfile = selectedProfile;
	}

	public UUID getAccessToken() {
		return accessToken;
	}

	public GameProfile getSelectedProfile() {
		return selectedProfile;
	}

}
