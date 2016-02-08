package yushijinhun.authlibagent.web.yggdrasil.servlet;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import org.json.JSONArray;
import org.json.JSONObject;

@WebServlet("/yggdrasil/profilerepo/minecraft")
public class ProfileSearchServlet extends YggdrasilPostServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected JSONObject process(JSONArray req, HttpServletRequest rawReq) throws Exception {
		JSONObject resp = new JSONObject();
		JSONArray profilesResp = new JSONArray();
		for (Object o : req) {
			String name = (String) o;
			profilesResp.put(serializer.serializeGameProfile(backend.lookupProfile(name), false));
		}
		resp.put("profiles", profilesResp);
		return resp;
	}

}
