package yushijinhun.authlibagent.web.yggdrasil.servlet;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import org.json.JSONObject;
import yushijinhun.authlibagent.service.ForbiddenOperationException;

@WebServlet("/yggdrasil/validate")
public class ValidateServlet extends YggdrasilPostServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected JSONObject process(JSONObject req, HttpServletRequest rawReq) throws Exception {
		String accessToken = req.getString("accessToken");
		String clientToken = req.optString("clientToken", null);

		boolean valid;
		if (clientToken == null) {
			valid = backend.validate(accessToken);
		} else {
			valid = backend.validate(accessToken, clientToken);
		}

		if (valid) {
			return null;
		} else {
			throw new ForbiddenOperationException("Invalid token");
		}
	}

}
