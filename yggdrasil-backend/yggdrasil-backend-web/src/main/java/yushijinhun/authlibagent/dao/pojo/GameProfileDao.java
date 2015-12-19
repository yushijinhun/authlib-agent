package yushijinhun.authlibagent.dao.pojo;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import yushijinhun.authlibagent.api.TextureModel;

@Entity
public class GameProfileDao implements Serializable {

	private static final long serialVersionUID = 1L;

	private UUID uuid;
	private String name;
	private AccountDao owner;
	private boolean banned = false;
	private String skin = null;
	private String cape = null;
	private TextureModel textureModel = TextureModel.STEVE;

	@Id
	@Column(nullable = false, unique = true)
	public UUID getUuid() {
		return uuid;
	}

	public void setUuid(UUID uuid) {
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
