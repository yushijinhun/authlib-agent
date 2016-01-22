package yushijinhun.authlibagent.backend.web;

import java.io.Serializable;
import java.util.UUID;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import yushijinhun.authlibagent.commons.TextureModel;

@XmlRootElement(name = "profile")
public class ProfileInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	private UUID uuid;
	private String name;
	private String owner;
	private Boolean banned;

	/**
	 * Null by default, empty for no skin
	 */
	private String skin;

	/**
	 * Null by default, empty for no cape
	 */
	private String cape;

	private TextureModel textureModel;

	@XmlElement
	public UUID getUuid() {
		return uuid;
	}

	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	@XmlElement
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlElement
	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	@XmlElement
	public Boolean getBanned() {
		return banned;
	}

	public void setBanned(Boolean banned) {
		this.banned = banned;
	}

	@XmlElement
	public String getSkin() {
		return skin;
	}

	public void setSkin(String skin) {
		this.skin = skin;
	}

	@XmlElement
	public String getCape() {
		return cape;
	}

	public void setCape(String cape) {
		this.cape = cape;
	}

	@XmlElement
	public TextureModel getTextureModel() {
		return textureModel;
	}

	public void setTextureModel(TextureModel textureModel) {
		this.textureModel = textureModel;
	}

}
