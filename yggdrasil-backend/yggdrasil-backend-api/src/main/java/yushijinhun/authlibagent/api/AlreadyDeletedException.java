package yushijinhun.authlibagent.api;

/**
 * 当操作一个不存在的事务对象时抛出。
 * 
 * @author yushijinhun
 */
public class AlreadyDeletedException extends Exception {

	private static final long serialVersionUID = 1L;

	public AlreadyDeletedException() {
	}

	public AlreadyDeletedException(String message) {
		super(message);
	}

}
