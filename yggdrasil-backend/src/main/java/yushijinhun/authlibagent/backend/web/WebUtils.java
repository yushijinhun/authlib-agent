package yushijinhun.authlibagent.backend.web;

import javax.ws.rs.BadRequestException;

public final class WebUtils {

	public static void requireNonNullBody(Object body) {
		if (body == null) {
			throw new BadRequestException("body cannot be empty");
		}
	}

	private WebUtils() {
	}
}
