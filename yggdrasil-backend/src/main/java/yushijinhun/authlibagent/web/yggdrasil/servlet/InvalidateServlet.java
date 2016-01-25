package yushijinhun.authlibagent.web.yggdrasil.servlet;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import org.json.JSONObject;

@WebServlet("/yggdrasil/invalidate")
public class InvalidateServlet extends YggdrasilPostServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected JSONObject process(JSONObject req, HttpServletRequest rawReq) throws Exception {
		String clientToken = req.getString("clientToken");
		String accessToken = req.getString("accessToken");
		backend.invalidate(accessToken, clientToken);
		return null;
	}

}
