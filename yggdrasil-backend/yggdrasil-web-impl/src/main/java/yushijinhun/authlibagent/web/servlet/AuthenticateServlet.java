package yushijinhun.authlibagent.web.servlet;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import org.json.JSONObject;
import yushijinhun.authlibagent.api.web.response.AuthenticateResponse;

@WebServlet("/authenticate")
public class AuthenticateServlet extends YggdrasilPostServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected JSONObject process(JSONObject req, HttpServletRequest rawReq) throws Exception {
		String username = req.getString("username");
		String password = req.getString("password");
		String clientToken = req.optString("clientToken", null);

		AuthenticateResponse auth = backend.authenticate(username, password, clientToken);
		return serializer.serializeAuthenticateResponse(auth);
	}

}
