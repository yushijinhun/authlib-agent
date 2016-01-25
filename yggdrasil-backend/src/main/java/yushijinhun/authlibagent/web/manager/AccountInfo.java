package yushijinhun.authlibagent.web.manager;

import java.io.Serializable;
import java.util.Set;
import java.util.UUID;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@XmlRootElement(name = "account")
public class AccountInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	private String id;

	/**
	 * The plain password, only exists in the request from the client (if the client wants to change the password)
	 */
	private String password;

	/**
	 * Null by default
	 */
	private Boolean banned;

	/**
	 * Null by default, empty string for no token
	 */
	private String twitchToken;

	/**
	 * Only exists in the request to the client
	 */
	private Set<UUID> profiles;

	/**
	 * Null by default, empty for no selected profile, use UUID.fromString() to decode this
	 */
	private String selectedProfile;

	@XmlElement
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@XmlElement
	public Boolean getBanned() {
		return banned;
	}

	public void setBanned(Boolean banned) {
		this.banned = banned;
	}

	@XmlElement
	@JsonInclude(Include.NON_NULL)
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@XmlElement
	public String getTwitchToken() {
		return twitchToken;
	}

	public void setTwitchToken(String twitchToken) {
		this.twitchToken = twitchToken;
	}

	@XmlElement
	public Set<UUID> getProfiles() {
		return profiles;
	}

	public void setProfiles(Set<UUID> profiles) {
		this.profiles = profiles;
	}

	@XmlElement
	public String getSelectedProfile() {
		return selectedProfile;
	}

	public void setSelectedProfile(String selectedProfile) {
		this.selectedProfile = selectedProfile;
	}

}
