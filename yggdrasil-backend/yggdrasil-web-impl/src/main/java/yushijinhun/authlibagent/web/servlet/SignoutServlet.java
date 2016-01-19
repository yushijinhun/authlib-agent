package yushijinhun.authlibagent.web.servlet;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import org.json.JSONObject;
import yushijinhun.authlibagent.web.YggdrasilPostServlet;

@WebServlet("/yggdrasil/signout")
public class SignoutServlet extends YggdrasilPostServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected JSONObject process(JSONObject req, HttpServletRequest rawReq) throws Exception {
		String username = req.getString("username");
		String password = req.getString("password");
		backend.signout(username, password);
		return null;
	}

}
