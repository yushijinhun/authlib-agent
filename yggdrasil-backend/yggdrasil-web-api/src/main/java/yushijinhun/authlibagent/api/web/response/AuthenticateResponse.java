package yushijinhun.authlibagent.api.web.response;

import java.io.Serializable;
import java.util.Map;

public class AuthenticateResponse implements Serializable {

	private static final long serialVersionUID = 1L;

	private String clientToken;
	private String accessToken;
	private GameProfileResponse selectedProfile;
	private GameProfileResponse[] profiles;
	private String userid;
	private Map<String, String> properties;

	public AuthenticateResponse(String clientToken, String accessToken, GameProfileResponse selectedProfile, GameProfileResponse[] profiles, String userid, Map<String, String> properties) {
		this.clientToken = clientToken;
		this.accessToken = accessToken;
		this.selectedProfile = selectedProfile;
		this.profiles = profiles;
		this.userid = userid;
		this.properties = properties;
	}

	public String getClientToken() {
		return clientToken;
	}

	public void setClientToken(String clientToken) {
		this.clientToken = clientToken;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public GameProfileResponse getSelectedProfile() {
		return selectedProfile;
	}

	public void setSelectedProfile(GameProfileResponse selectedProfile) {
		this.selectedProfile = selectedProfile;
	}

	public GameProfileResponse[] getProfiles() {
		return profiles;
	}

	public void setProfiles(GameProfileResponse[] profiles) {
		this.profiles = profiles;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public Map<String, String> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}

}
