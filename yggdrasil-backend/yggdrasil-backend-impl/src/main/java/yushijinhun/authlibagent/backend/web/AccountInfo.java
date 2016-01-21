package yushijinhun.authlibagent.backend.web;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

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
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getTwitchToken() {
		return twitchToken;
	}

	public void setTwitchToken(String twitchToken) {
		this.twitchToken = twitchToken;
	}

}
