package yushijinhun.authlibagent.web.manager;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "login-param")
public class LoginParam implements Serializable {

	private static final long serialVersionUID = 1L;

	private String username;
	private String password;
	private boolean ignoreBanned = false;

	@XmlElement
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@XmlElement
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@XmlElement
	public boolean isIgnoreBanned() {
		return ignoreBanned;
	}

	public void setIgnoreBanned(boolean ignoreBanned) {
		this.ignoreBanned = ignoreBanned;
	}

}
