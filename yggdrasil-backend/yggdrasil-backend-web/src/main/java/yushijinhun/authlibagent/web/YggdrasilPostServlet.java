package yushijinhun.authlibagent.web;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import yushijinhun.authlibagent.web.exception.YggdrasilException;
import yushijinhun.authlibagent.web.exception.YggdrasilUnsupportedMediaTypeException;

abstract public class YggdrasilPostServlet extends YggdrasilServlet {

	private static final long serialVersionUID = 1L;

	abstract protected JSONObject process(JSONObject req, HttpServletRequest httpReq) throws YggdrasilException;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		handleRequest(req, resp);
	}

	@Override
	protected JSONObject process(HttpServletRequest req, HttpServletResponse resp) throws IOException, YggdrasilException {
		String contentType = req.getContentType();
		if (!"application/json".equals(contentType)) {
			throw new YggdrasilUnsupportedMediaTypeException(contentType);
		}

		JSONObject jsonReq;
		try {
			jsonReq = new JSONObject(new JSONTokener(req.getReader()));
		} catch (JSONException e) {
			throw new YggdrasilUnsupportedMediaTypeException(e);
		}

		return process(jsonReq, req);
	}

}
