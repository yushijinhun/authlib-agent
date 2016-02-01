package yushijinhun.authlibagent.model;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
public class GameProfile implements Serializable {

	private static final long serialVersionUID = 1L;

	private String uuid;
	private String name;
	private Account owner;
	private boolean banned = false;
	private String skin = null;
	private String cape = null;
	private String elytra = null;
	private TextureModel textureModel = TextureModel.STEVE;

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

	@ManyToOne(cascade = CascadeType.REFRESH, optional = false, fetch = FetchType.EAGER)
	@JoinColumn
	public Account getOwner() {
		return owner;
	}

	public void setOwner(Account owner) {
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

	public String getElytra() {
		return elytra;
	}

	public void setElytra(String elytra) {
		this.elytra = elytra;
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
		if (obj instanceof GameProfile) {
			GameProfile another = (GameProfile) obj;
			return Objects.equals(getUuid(), another.getUuid());
		}
		return false;
	}

}
