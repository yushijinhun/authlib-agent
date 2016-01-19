package yushijinhun.authlibagent.web.servlet;

import java.util.UUID;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import org.json.JSONObject;
import yushijinhun.authlibagent.api.web.ForbiddenOperationException;
import static yushijinhun.authlibagent.commons.UUIDUtils.toUUID;

@WebServlet("/yggdrasil/validate")
public class ValidateServlet extends YggdrasilPostServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected JSONObject process(JSONObject req, HttpServletRequest rawReq) throws Exception {
		UUID accessToken = toUUID(req.getString("accessToken"));
		String clientTokenStr = req.optString("clientToken", null);
		UUID clientToken = clientTokenStr == null ? null : toUUID(clientTokenStr);

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
