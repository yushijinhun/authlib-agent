package yushijinhun.authlibagent.util;

import javax.ws.rs.BadRequestException;

public final class ResourceUtils {
	
	public static void requireNonNullBody(Object body) {
		if (body == null) {
			throw new BadRequestException("body cannot be empty");
		}
	}
	
	private ResourceUtils() {
	}

}
