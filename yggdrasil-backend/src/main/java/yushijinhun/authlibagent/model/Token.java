package yushijinhun.authlibagent.model;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class Token implements Serializable {

	private static final long serialVersionUID = 1L;

	private String accessToken;
	private String clientToken;
	private String owner;
	private UUID selectedProfile;

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getClientToken() {
		return clientToken;
	}

	public void setClientToken(String clientToken) {
		this.clientToken = clientToken;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public UUID getSelectedProfile() {
		return selectedProfile;
	}

	public void setSelectedProfile(UUID selectedProfile) {
		this.selectedProfile = selectedProfile;
	}

	@Override
	public int hashCode() {
		return Objects.hash(accessToken);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof Token) {
			Token another = (Token) obj;
			return Objects.equals(getAccessToken(), another.getAccessToken());
		}
		return false;
	}

}
