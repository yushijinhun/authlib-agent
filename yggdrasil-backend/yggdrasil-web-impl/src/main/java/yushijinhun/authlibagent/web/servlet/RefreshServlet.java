package yushijinhun.authlibagent.web.servlet;

import java.util.UUID;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import org.json.JSONObject;
import yushijinhun.authlibagent.api.web.response.AuthenticateResponse;
import static yushijinhun.authlibagent.commons.UUIDUtils.*;

@WebServlet("/yggdrasil/refresh")
public class RefreshServlet extends YggdrasilPostServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected JSONObject process(JSONObject req, HttpServletRequest rawReq) throws Exception {
		UUID clientToken = toUUID(req.getString("clientToken"));
		UUID accessToken = toUUID(req.getString("accessToken"));

		AuthenticateResponse auth;

		JSONObject newProfileReq = req.optJSONObject("selectedProfile");
		if (newProfileReq == null) {
			// refresh
			auth = backend.refresh(accessToken, clientToken);
		} else {
			// select profile
			UUID newProfile = toUUID(newProfileReq.getString("id"));
			auth = backend.selectProfile(accessToken, clientToken, newProfile);
		}

		return serializer.serializeAuthenticateResponse(auth, clientToken);
	}

}
