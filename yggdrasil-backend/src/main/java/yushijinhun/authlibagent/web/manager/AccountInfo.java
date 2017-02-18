package yushijinhun.authlibagent.web.manager;

import static com.google.common.base.Strings.nullToEmpty;
import static java.util.stream.Collectors.toSet;
import java.io.Serializable;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import yushijinhun.authlibagent.model.Account;
import yushijinhun.authlibagent.model.GameProfile;

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

	public AccountInfo() {}

	public AccountInfo(Account account) {
		setId(account.getId());
		setBanned(account.isBanned());
		setTwitchToken(nullToEmpty(account.getTwitchToken()));
		setProfiles(getAccountProfiles(account));
	}

	public static Set<UUID> getAccountProfiles(Account account) {
		Set<GameProfile> profiles = account.getProfiles();
		// profiles will be null if the account is in transient status
		return profiles == null ? Collections.emptySet() : profiles.stream().map(p -> UUID.fromString(p.getUuid())).collect(toSet());
	}

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

}
