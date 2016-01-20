package yushijinhun.authlibagent.backend.model;

import java.io.Serializable;
import java.util.Objects;

public class ServerId implements Serializable {

	private static final long serialVersionUID = 1L;

	private String serverId;
	private GameProfile profile;

	public String getServerId() {
		return serverId;
	}

	public void setServerId(String serverId) {
		this.serverId = serverId;
	}

	public GameProfile getProfile() {
		return profile;
	}

	public void setProfile(GameProfile profile) {
		this.profile = profile;
	}

	@Override
	public int hashCode() {
		return Objects.hash(serverId);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof ServerId) {
			ServerId another = (ServerId) obj;
			return Objects.equals(getServerId(), another.getServerId());
		}
		return false;
	}

}
