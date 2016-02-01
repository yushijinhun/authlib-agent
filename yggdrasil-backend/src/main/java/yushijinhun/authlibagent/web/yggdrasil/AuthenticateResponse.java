package yushijinhun.authlibagent.web.yggdrasil;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;
import yushijinhun.authlibagent.model.GameProfile;

public class AuthenticateResponse implements Serializable {

	private static final long serialVersionUID = 1L;

	private String clientToken;
	private String accessToken;
	private GameProfile selectedProfile;
	private Set<GameProfile> profiles;
	private String userid;
	private Map<String, String> properties;

	public AuthenticateResponse(String clientToken, String accessToken, GameProfile selectedProfile, Set<GameProfile> profiles, String userid, Map<String, String> properties) {
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

	public GameProfile getSelectedProfile() {
		return selectedProfile;
	}

	public void setSelectedProfile(GameProfile selectedProfile) {
		this.selectedProfile = selectedProfile;
	}

	public Set<GameProfile> getProfiles() {
		return profiles;
	}

	public void setProfiles(Set<GameProfile> profiles) {
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
