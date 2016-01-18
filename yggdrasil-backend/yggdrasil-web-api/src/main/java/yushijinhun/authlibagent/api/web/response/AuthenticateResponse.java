package yushijinhun.authlibagent.api.web.response;

import java.io.Serializable;
import java.util.Map;
import java.util.UUID;

public class AuthenticateResponse implements Serializable {

	private static final long serialVersionUID = 1L;

	private UUID accessToken;
	private GameProfileResponse selectedProfile;
	private GameProfileResponse[] profiles;
	private String userid;
	private Map<String, String> properties;

	public AuthenticateResponse(UUID accessToken, GameProfileResponse selectedProfile, GameProfileResponse[] profiles, String userid) {
		this(accessToken, selectedProfile, profiles, userid, null);
	}

	public AuthenticateResponse(UUID accessToken, GameProfileResponse selectedProfile, GameProfileResponse[] profiles, String userid, Map<String, String> properties) {
		this.accessToken = accessToken;
		this.selectedProfile = selectedProfile;
		this.profiles = profiles;
		this.userid = userid;
		this.properties = properties;
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

	public String getUserid() {
		return userid;
	}

	public Map<String, String> getProperties() {
		return properties;
	}

}
