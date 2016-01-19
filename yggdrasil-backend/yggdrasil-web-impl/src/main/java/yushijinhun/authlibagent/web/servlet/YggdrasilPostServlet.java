package yushijinhun.authlibagent.web.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import yushijinhun.authlibagent.web.UnsupportedMediaTypeException;

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
			JSONTokener tokener = new JSONTokener(req.getReader());
			char next = tokener.nextClean();
			tokener.back();
			if (next == '{') {
				JSONObject json = new JSONObject(tokener);
				return process(json, req);
			} else if (next == '[') {
				JSONArray json = new JSONArray(tokener);
				return process(json, req);
			} else {
				throw new UnsupportedMediaTypeException(MSG_REQUEST_FORMAT_INVALID);
			}
		} catch (JSONException e) {
			throw new UnsupportedMediaTypeException(MSG_REQUEST_FORMAT_INVALID, e);
		}
	}

	protected JSONObject process(JSONObject req, HttpServletRequest rawReq) throws Exception {
		throw new UnsupportedMediaTypeException(MSG_REQUEST_FORMAT_INVALID);
	}

	protected JSONObject process(JSONArray req, HttpServletRequest rawReq) throws Exception {
		throw new UnsupportedMediaTypeException(MSG_REQUEST_FORMAT_INVALID);
	}

}
