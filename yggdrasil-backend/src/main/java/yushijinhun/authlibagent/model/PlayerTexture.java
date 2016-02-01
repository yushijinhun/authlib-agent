package yushijinhun.authlibagent.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * 玩家的皮肤/披风
 * 
 * @author yushijinhun
 */
public class PlayerTexture implements Serializable {

	private static final long serialVersionUID = 1L;

	private TextureModel model;
	private String skin;
	private String cape;
	private String elytra;

	/**
	 * 创建一个PlayerTexture。
	 * 
	 * @param model 模型类型
	 * @param skin 皮肤的url，如果没有则为null
	 * @param cape 披风的url，如果没有则为null
	 * @param elytra 翅鞘(?)的url, 如果没有则为null
	 */
	public PlayerTexture(TextureModel model, String skin, String cape, String elytra) {
		Objects.requireNonNull(model);
		this.model = model;
		this.skin = skin;
		this.cape = cape;
		this.elytra = elytra;
	}

	/**
	 * 获取玩家皮肤的模型类型。
	 * 
	 * @return 模型类型
	 */
	public TextureModel getModel() {
		return model;
	}

	/**
	 * 获取玩家皮肤的url，如果没有则为null。
	 * 
	 * @return 玩家皮肤的url，如果没有则为null
	 */
	public String getSkin() {
		return skin;
	}

	/**
	 * 获取玩家披风的url，如果没有则为null。
	 * 
	 * @return 玩家披风的url，如果没有则为null
	 */
	public String getCape() {
		return cape;
	}

	public String getElytra() {
		return elytra;
	}

	public void setElytra(String elytra) {
		this.elytra = elytra;
	}

	@Override
	public int hashCode() {
		return Objects.hash(model, skin, cape);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof PlayerTexture) {
			PlayerTexture another = (PlayerTexture) obj;
			return model == another.model && Objects.equals(skin, another.skin) && Objects.equals(cape, another.cape);
		}
		return false;
	}

}
