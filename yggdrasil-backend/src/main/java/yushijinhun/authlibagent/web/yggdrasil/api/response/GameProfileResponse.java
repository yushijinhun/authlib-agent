package yushijinhun.authlibagent.web.yggdrasil.api.response;

import java.io.Serializable;
import java.util.UUID;
import yushijinhun.authlibagent.util.PlayerTexture;

public class GameProfileResponse implements Serializable {

	private static final long serialVersionUID = 1L;

	private UUID uuid;
	private String name;
	private PlayerTexture texture;

	public GameProfileResponse(UUID uuid, String name) {
		this(uuid, name, null);
	}

	public GameProfileResponse(UUID uuid, String name, PlayerTexture texture) {
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
