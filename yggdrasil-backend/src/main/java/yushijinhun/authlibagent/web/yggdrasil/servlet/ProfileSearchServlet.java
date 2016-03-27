package yushijinhun.authlibagent.web.yggdrasil.servlet;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import org.json.JSONArray;
import yushijinhun.authlibagent.model.GameProfile;

@WebServlet("/yggdrasil/profilerepo/minecraft")
public class ProfileSearchServlet extends YggdrasilPostServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected JSONArray process(JSONArray req, HttpServletRequest rawReq) throws Exception {
		JSONArray resp = new JSONArray();
		for (Object o : req) {
			String name = (String) o;
			GameProfile profile = backend.lookupProfile(name);
			if (profile != null) {
				resp.put(serializer.serializeGameProfile(profile, false));
			}
		}
		return resp;
	}

}
