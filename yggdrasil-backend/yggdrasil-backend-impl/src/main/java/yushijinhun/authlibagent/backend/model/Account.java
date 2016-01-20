package yushijinhun.authlibagent.backend.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

@Entity
public class Account implements Serializable {

	private static final long serialVersionUID = 1L;

	private String id;
	private String password;
	private boolean banned;
	private Set<GameProfile> profiles = new HashSet<>();
	private GameProfile selectedProfile;
	private Set<Token> tokens = new HashSet<>();

	@Id
	@Column(nullable = false, unique = true)
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

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

	@OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	public Set<GameProfile> getProfiles() {
		return profiles;
	}

	public void setProfiles(Set<GameProfile> profiles) {
		this.profiles = profiles;
	}

	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn
	public GameProfile getSelectedProfile() {
		return selectedProfile;
	}

	public void setSelectedProfile(GameProfile selectedProfile) {
		this.selectedProfile = selectedProfile;
	}

	@OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	public Set<Token> getTokens() {
		return tokens;
	}

	public void setTokens(Set<Token> tokens) {
		this.tokens = tokens;
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
		if (obj instanceof Account) {
			Account another = (Account) obj;
			return Objects.equals(getId(), another.getId());
		}
		return false;
	}

}
