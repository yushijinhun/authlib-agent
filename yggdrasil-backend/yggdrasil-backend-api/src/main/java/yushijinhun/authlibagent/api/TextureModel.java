package yushijinhun.authlibagent.api;

/**
 * 描述一个皮肤的模型类型
 * 
 * @author yushijinhun
 */
public enum TextureModel {

	/**
	 * Steve（默认格式）皮肤
	 */
	STEVE("default"),

	/**
	 * Alex（细胳膊）皮肤
	 */
	ALEX("alex");

	private String modelName;

	TextureModel(String modelName) {
		this.modelName = modelName;
	}

	/**
	 * 返回该模型在mc内的名称。
	 * 
	 * @return 该模型在mc内的名称
	 */
	public String getModelName() {
		return modelName;
	}

}
