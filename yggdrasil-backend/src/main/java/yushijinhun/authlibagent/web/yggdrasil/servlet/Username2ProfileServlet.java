package yushijinhun.authlibagent.web.yggdrasil.servlet;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import org.json.JSONObject;
import yushijinhun.authlibagent.model.GameProfile;

@WebServlet("/yggdrasil/profiles/minecraft/*")
public class Username2ProfileServlet extends YggdrasilGetServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected JSONObject process(HttpServletRequest req) throws Exception {
		String path = req.getPathInfo();
		if (path == null) {
			return null;
		}

		// substring() - delete the / at the beginning
		String username = path.substring(1);
		GameProfile profile = backend.lookupProfile(username);

		return serializer.serializeGameProfile(profile, true);
	}

}
