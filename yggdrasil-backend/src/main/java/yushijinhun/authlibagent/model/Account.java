package yushijinhun.authlibagent.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
public class Account implements Serializable {

	private static final long serialVersionUID = 1L;

	private String id;
	private String password;
	private boolean banned;
	private Set<GameProfile> profiles = new HashSet<>();

	// third-part tokens
	private String twitchToken;

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

	@OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
	public Set<GameProfile> getProfiles() {
		return profiles;
	}

	public void setProfiles(Set<GameProfile> profiles) {
		this.profiles = profiles;
	}

	public String getTwitchToken() {
		return twitchToken;
	}

	public void setTwitchToken(String twitchToken) {
		this.twitchToken = twitchToken;
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
