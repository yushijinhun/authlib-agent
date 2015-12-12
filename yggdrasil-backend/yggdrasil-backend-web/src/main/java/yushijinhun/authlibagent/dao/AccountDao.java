package yushijinhun.authlibagent.dao;

import java.io.Serializable;
import java.util.Objects;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import yushijinhun.authlibagent.api.GameProfile;

@Entity
public class AccountDao implements Serializable {

	private static final long serialVersionUID = 1L;

	private String id;
	private String password;
	private boolean banned;
	private String clientToken;
	private String accessToken;
	private Set<GameProfile> profiles;

	@Id
	@Column(nullable = false, unique = true)
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Column(length = Short.MAX_VALUE)
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Column(nullable = false)
	public boolean isBanned() {
		return banned;
	}

	public void setBanned(boolean banned) {
		this.banned = banned;
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

	@OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	public Set<GameProfile> getProfiles() {
		return profiles;
	}

	public void setProfiles(Set<GameProfile> profiles) {
		this.profiles = profiles;
	}

	@Override
	public int hashCode() {
		return Objects.hash(getId());
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof AccountDao) {
			AccountDao another = (AccountDao) obj;
			return Objects.equals(getId(), another.getId());
		}
		return false;
	}

}
