package yushijinhun.authlibagent.backend.api;

/**
 * 当要求唯一的字段发生冲突时抛出。
 * 
 * @author yushijinhun
 */
public class IDCollisionException extends Exception {

	private static final long serialVersionUID = 1L;

	public IDCollisionException() {
	}

	public IDCollisionException(String message) {
		super(message);
	}

}
