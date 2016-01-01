package yushijinhun.authlibagent.backend.api;

/**
 * 封装了yggdasil-backend对一些参数的验证
 * 
 * @author yushijinhun
 */
public final class YggdrasilValidate {
	
	/**
	 * 判断是否为一个有效的id。
	 * <p>
	 * 一个有效id须满足：<br>
	 * 1. 不为null<br>
	 * 2. 开头结尾不能有空白字符<br>
	 * 3. 不能为空字符串
	 * 
	 * @param id 要测试的id
	 * @return 有效返回true，无效返回false
	 */
	public static boolean isValidId(String id){
		if (id==null){
			return false;
		}
		if (id.length()==0){
			return false;
		}
		if (!id.trim().equals(id)){
			return false;
		}
		return true;
	}

	
	private YggdrasilValidate() {
	}
	
}
