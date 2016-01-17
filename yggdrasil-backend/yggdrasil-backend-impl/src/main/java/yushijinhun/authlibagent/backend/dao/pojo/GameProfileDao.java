package yushijinhun.authlibagent.backend.dao.pojo;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import yushijinhun.authlibagent.commons.TextureModel;

@Entity
public class GameProfileDao implements Serializable {

	private static final long serialVersionUID = 1L;

	private String uuid;
	private String name;
	private AccountDao owner;
	private boolean banned = false;
	private String skin = null;
	private String cape = null;
	private TextureModel textureModel = TextureModel.STEVE;
	private String serverId;

	@Id
	@Column(nullable = false, unique = true)
	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	@Column(nullable = false, unique = true)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@ManyToOne(cascade = CascadeType.REFRESH, optional = false)
	@JoinColumn
	public AccountDao getOwner() {
		return owner;
	}

	public void setOwner(AccountDao owner) {
		this.owner = owner;
	}

	@Column(nullable = false)
	public boolean isBanned() {
		return banned;
	}

	public void setBanned(boolean banned) {
		this.banned = banned;
	}

	public String getSkin() {
		return skin;
	}

	public void setSkin(String skin) {
		this.skin = skin;
	}

	public String getCape() {
		return cape;
	}

	public void setCape(String cape) {
		this.cape = cape;
	}

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	public TextureModel getTextureModel() {
		return textureModel;
	}

	public void setTextureModel(TextureModel textureModel) {
		this.textureModel = textureModel;
	}

	public String getServerId() {
		return serverId;
	}

	public void setServerId(String serverId) {
		this.serverId = serverId;
	}

	@Override
	public int hashCode() {
		return Objects.hash(getUuid());
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof GameProfileDao) {
			GameProfileDao another = (GameProfileDao) obj;
			return Objects.equals(getUuid(), another.getUuid());
		}
		return false;
	}

}
