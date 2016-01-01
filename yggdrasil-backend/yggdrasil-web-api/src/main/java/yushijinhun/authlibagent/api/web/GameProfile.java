package yushijinhun.authlibagent.api.web;

import java.io.Serializable;
import java.util.UUID;
import yushijinhun.authlibagent.commons.PlayerTexture;

public class GameProfile implements Serializable {

	private static final long serialVersionUID = 1L;

	private UUID uuid;
	private String name;
	private PlayerTexture texture;

	public GameProfile(UUID uuid, String name) {
		this(uuid, name, null);
	}

	public GameProfile(UUID uuid, String name, PlayerTexture texture) {
		this.uuid = uuid;
		this.name = name;
		this.texture = texture;
	}

	public UUID getUUID() {
		return uuid;
	}

	public String getName() {
		return name;
	}

	public PlayerTexture getTexture() {
		return texture;
	}

}
