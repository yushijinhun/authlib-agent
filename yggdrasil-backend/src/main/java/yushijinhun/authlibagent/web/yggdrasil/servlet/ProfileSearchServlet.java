package yushijinhun.authlibagent.web.yggdrasil.servlet;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import yushijinhun.authlibagent.model.GameProfile;
import yushijinhun.authlibagent.service.MojangYggdrasilService;

@WebServlet("/yggdrasil/profilerepo/minecraft")
public class ProfileSearchServlet extends YggdrasilPostServlet {

	private static final long serialVersionUID = 1L;

	@Autowired
	private MojangYggdrasilService mojangService;

	@Override
	protected JSONArray process(JSONArray req, HttpServletRequest rawReq) throws Exception {
		JSONArray resp = new JSONArray();
		for (Object o : req) {
			String name = (String) o;
			GameProfile profile = backend.lookupProfile(name);
			if (profile != null) {
				resp.put(serializer.serializeGameProfile(profile, false));
			} else {
				mojangService.profileSearch(name).ifPresent(resp::put);
			}
		}
		return resp;
	}

}
