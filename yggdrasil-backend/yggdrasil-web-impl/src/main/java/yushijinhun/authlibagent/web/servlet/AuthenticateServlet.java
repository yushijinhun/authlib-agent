package yushijinhun.authlibagent.web.servlet;

import java.util.UUID;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import org.json.JSONObject;
import yushijinhun.authlibagent.api.web.ForbiddenOperationException;
import yushijinhun.authlibagent.api.web.response.AuthenticateResponse;
import yushijinhun.authlibagent.web.YggdrasilPostServlet;
import static yushijinhun.authlibagent.commons.UUIDUtils.*;
import static yushijinhun.authlibagent.commons.RandomUtils.*;

@WebServlet("/yggdrasil/authenticate")
public class AuthenticateServlet extends YggdrasilPostServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected JSONObject process(JSONObject req, HttpServletRequest rawReq) throws Exception {
		checkAgent(req);
		String username = req.getString("username");
		String password = req.getString("password");
		String clientTokenStr = req.optString("clientToken");
		UUID clientToken;
		if (clientTokenStr == null) {
			clientToken = randomUUID();
		} else {
			clientToken = toUUID(clientTokenStr);
		}

		AuthenticateResponse auth = backend.authenticate(username, password, clientToken);
		return serializer.serializeAuthenticateResponse(auth, clientToken);
	}

	private void checkAgent(JSONObject req) throws ForbiddenOperationException {
		JSONObject agent = req.getJSONObject("agent");
		if (!"Minecraft".equals(agent.getString("name")) || agent.getInt("version") != 1) {
			throw new ForbiddenOperationException("Invalid agent");
		}
	}

}
