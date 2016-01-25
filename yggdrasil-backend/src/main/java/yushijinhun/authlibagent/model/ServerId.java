package yushijinhun.authlibagent.model;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class ServerId implements Serializable {

	private static final long serialVersionUID = 1L;

	private String serverId;
	private GameProfile profile;
	private long createTime;

	@Id
	@Column(nullable = false, unique = true)
	public String getServerId() {
		return serverId;
	}

	public void setServerId(String serverId) {
		this.serverId = serverId;
	}

	@ManyToOne(cascade = CascadeType.REFRESH, optional = false)
	@JoinColumn
	public GameProfile getProfile() {
		return profile;
	}

	public void setProfile(GameProfile profile) {
		this.profile = profile;
	}

	@Column(nullable = false)
	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
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
