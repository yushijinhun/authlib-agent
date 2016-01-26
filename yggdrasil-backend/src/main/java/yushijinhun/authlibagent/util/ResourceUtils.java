package yushijinhun.authlibagent.util;

import javax.ws.rs.BadRequestException;

public final class ResourceUtils {
	
	public static final String APPLICATION_JSON_UTF8 = "application/json; charset=utf-8";

	public static void requireNonNullBody(Object body) {
		if (body == null) {
			throw new BadRequestException("body cannot be empty");
		}
	}
	
	private ResourceUtils() {
	}

}
