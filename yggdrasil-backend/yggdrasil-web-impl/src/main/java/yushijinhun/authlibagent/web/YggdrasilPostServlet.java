package yushijinhun.authlibagent.web;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

abstract public class YggdrasilPostServlet extends YggdrasilServlet {

	private static final long serialVersionUID = 1L;

	private static final String MSG_REQUEST_FORMAT_INVALID = "The server is refusing to service the request because the entity of the request is in a format not supported by the requested resource for the requested method";

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		handleRequest(req, resp);
	}

	@Override
	protected JSONObject process(HttpServletRequest req) throws Exception {
		String contentType = req.getContentType();
		if (!contentType.startsWith("application/json")) {
			throw new UnsupportedMediaTypeException(MSG_REQUEST_FORMAT_INVALID);
		}

		try {
			JSONObject json = new JSONObject(new JSONTokener(req.getReader()));
			return process(json, req);
		} catch (JSONException e) {
			throw new UnsupportedMediaTypeException(MSG_REQUEST_FORMAT_INVALID);
		}
	}

	abstract protected JSONObject process(JSONObject req, HttpServletRequest rawReq) throws Exception;

}
