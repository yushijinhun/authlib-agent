package yushijinhun.authlibagent.web.servlet;

import java.util.UUID;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import org.json.JSONArray;
import org.json.JSONObject;
import yushijinhun.authlibagent.api.web.ForbiddenOperationException;
import yushijinhun.authlibagent.api.web.response.AuthenticateResponse;
import yushijinhun.authlibagent.api.web.response.GameProfileResponse;
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

		AuthenticateResponse authResp = backend.authenticate(username, password, clientToken);
		JSONObject resp = new JSONObject();
		resp.put("accessToken", unsign(authResp.getAccessToken()));
		resp.put("clientToken", unsign(clientToken));

		JSONObject userEntry = new JSONObject();
		userEntry.put("id", username);
		resp.put("user", userEntry);

		resp.put("selectedProfile", profileSerializer.serialize(authResp.getSelectedProfile()));

		JSONArray profilesEntry = new JSONArray();
		for (GameProfileResponse profile : authResp.getProfiles()) {
			profilesEntry.put(profileSerializer.serialize(profile));
		}
		resp.put("availableProfiles", profilesEntry);

		return resp;
	}

	private void checkAgent(JSONObject req) throws ForbiddenOperationException {
		JSONObject agent = req.getJSONObject("agent");
		if (!"Minecraft".equals(agent.getString("name")) || agent.getInt("version") != 1) {
			throw new ForbiddenOperationException("Invalid agent");
		}
	}

}
